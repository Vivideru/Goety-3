package com.Vivideru.Goety.common.world.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.core.Registry;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public final class WindShrinePlacementRules {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String STAIRS = "stairs";
    private static final int DESCENT_PER_DEPTH = 2;
    private static final double INWARD_TOLERANCE = 6.0D;
    private static final int DEAD_END_DEPTH = 2;
    private static final double INWARD_COSINE = -0.85D;
    private static final double WEDGE = Math.toRadians(40.0D);
    private static final int MAX_WING_PIECES = 210;
    private static final ThreadLocal<Session> SESSION = new ThreadLocal<>();
    private static final ThreadLocal<Rotation> FORCED_ROTATION = new ThreadLocal<>();

    private record StairBox(BoundingBox box, Direction runs) {
        private static final int PIECE_HEIGHT = 11;

        int envelopeTop(BoundingBox other) {
            int length = this.runs.getAxis() == Direction.Axis.X ? this.box.getXSpan() : this.box.getZSpan();
            int nearest = switch (this.runs) {
                case EAST -> Math.max(other.minX(), this.box.minX()) - this.box.minX();
                case WEST -> this.box.maxX() - Math.min(other.maxX(), this.box.maxX());
                case SOUTH -> Math.max(other.minZ(), this.box.minZ()) - this.box.minZ();
                default -> this.box.maxZ() - Math.min(other.maxZ(), this.box.maxZ());
            };
            double f = length <= 1 ? 1.0D : Mth.clamp(nearest / (double) (length - 1), 0.0D, 1.0D);
            int drop = Math.max(0, this.box.getYSpan() - PIECE_HEIGHT);
            return this.box.minY() + PIECE_HEIGHT + (int) Math.round(drop * (1.0D - f));
        }

        boolean clearOf(BoundingBox other) {
            return !this.box.intersects(other) || other.minY() > this.envelopeTop(other);
        }
    }

    private static final class Session {
        final boolean wing;
        final int summitY;
        final int maxDepth;
        final BlockPos center;
        BlockPos doorPos;
        final List<BoundingBox> accepted = new ArrayList<>();
        final List<BoundingBox> foreign = new ArrayList<>();
        final List<StairBox> stairs = new ArrayList<>();
        AABB freeBounds;
        BoundingBox parentBox;
        int parentDepth;
        int doorFloorY = Integer.MIN_VALUE;
        Direction parentDirection = Direction.NORTH;
        String candidateName = "";
        String startName = "";
        WindShrinePieceProfiles.Profile candidateProfile = WindShrinePieceProfiles.Profile.CLOSED;
        int placed;
        int loggedCollisions;
        final int[] rejected = new int[Reason.values().length];

        Session(boolean wing, int summitY, int maxDepth, BlockPos center) {
            this.wing = wing;
            this.summitY = summitY;
            this.maxDepth = maxDepth;
            this.center = center;
        }
    }

    private enum Reason { COLLISION, FOREIGN, WEDGE, WING_FULL, UNFINISHABLE, CLOSING_SPACE, STAIRS_CLIMB, TOWER_HEIGHT, WING_CLIMB, DESCENT_QUOTA, INWARD, FOOTPRINT }

    private static boolean reject(Session session, Reason reason) {
        session.rejected[reason.ordinal()]++;
        return true;
    }

    private WindShrinePlacementRules() {
    }

    public static void beginShrine(int summitY, int maxDepth) {
        SESSION.set(new Session(false, summitY, maxDepth, null));
    }

    public static void beginWing(WindShrinePlans.Plan parent, int index, int maxDepth) {
        Session session = new Session(true, parent.summitY(), maxDepth, parent.center());
        session.foreign.addAll(parent.obstaclesFor(index));
        session.doorPos = parent.door(index).map(WindShrinePlans.Door::target).orElse(null);
        SESSION.set(session);
    }

    public static void end() {
        Session session = SESSION.get();
        if (session != null && LOGGER.isDebugEnabled()) {
            StringBuilder summary = new StringBuilder();
            for (Reason reason : Reason.values()) {
                if (session.rejected[reason.ordinal()] > 0) {
                    summary.append(' ').append(reason.name().toLowerCase()).append('=').append(session.rejected[reason.ordinal()]);
                }
            }
            LOGGER.debug("Wind Shrine {} assembled from {}: {} pieces accepted, rejected:{}", session.wing ? "wing" : "ring", session.startName, session.placed, summary);
        }
        SESSION.remove();
    }

    public static void forceRotation(Rotation rotation) {
        FORCED_ROTATION.set(rotation);
    }

    public static void clearForcedRotation() {
        FORCED_ROTATION.remove();
    }

    public static Rotation forcedRotationOr(Rotation random) {
        Rotation forced = FORCED_ROTATION.get();
        return forced == null ? random : forced;
    }

    public static void beginParent(PoolElementStructurePiece parent, int depth, VoxelShape free, StructureTemplateManager templates) {
        Session session = SESSION.get();
        if (session != null) {
            if (session.wing && depth == 0 && LOGGER.isDebugEnabled()) {
                StringBuilder jigsaws = new StringBuilder();
                for (net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo info : parent.getElement().getShuffledJigsawBlocks(templates, parent.getPosition(), parent.getRotation(), RandomSource.create(0L))) {
                    Direction facing = net.minecraft.world.level.block.JigsawBlock.getFrontFacing(info.state());
                    BlockPos target = info.pos().relative(facing);
                    jigsaws.append(' ').append(info.nbt() == null ? "?" : info.nbt().getString("name")).append('@').append(info.pos().toShortString()).append('>').append(facing).append(parent.getBoundingBox().isInside(target) ? "(interior)" : "");
                }
                LOGGER.debug("Wind Shrine wing start {} rot {} box {} free {} jigsaws:{}", parent.getElement(), parent.getRotation(), parent.getBoundingBox(), free == null ? "?" : free.bounds(), jigsaws);
            }
            session.parentBox = parent.getBoundingBox();
            session.parentDepth = depth;
            remember(session, session.parentBox);
            if (depth == 0 && session.startName.isEmpty()) {
                session.startName = parent.getElement().toString() + " " + parent.getBoundingBox();
                session.freeBounds = free == null ? null : free.bounds();
                if (session.startName.contains(STAIRS)) {
                    session.stairs.add(new StairBox(parent.getBoundingBox(), runsAwayFrom(session.doorPos, parent.getBoundingBox())));
                }
            }
            if (session.wing && depth == 0 && session.doorFloorY == Integer.MIN_VALUE) {
                session.doorFloorY = session.parentBox.minY() + (session.parentBox.getYSpan() - 11) + 4;
            }
        }
    }

    public static void parentDirection(Direction direction) {
        Session session = SESSION.get();
        if (session != null) {
            session.parentDirection = direction;
        }
    }

    public static void candidate(StructurePoolElement element, Registry<StructureTemplatePool> pools, StructureTemplateManager templates) {
        Session session = SESSION.get();
        if (session != null) {
            session.candidateName = element.toString();
            session.candidateProfile = WindShrinePieceProfiles.of(element, pools, templates);
        }
    }

    public static List<StructurePoolElement> reweight(List<StructurePoolElement> candidates, RandomSource random, Registry<StructureTemplatePool> pools, StructureTemplateManager templates) {
        Session session = SESSION.get();
        if (session == null || session.parentBox == null) {
            return candidates;
        }
        if (!session.wing) {
            return candidates;
        }
        double lowness = lowness(session);
        if (lowness > 0.0D) {
            List<StructurePoolElement> thinned = new ArrayList<>();
            List<StructurePoolElement> wideRoofs = new ArrayList<>();
            for (StructurePoolElement element : candidates) {
                String name = element.toString();
                double keep = 1.0D;
                int copies = 1;
                if (name.contains("big_tower_base")) {
                    keep = 1.0D - 0.9D * lowness;
                } else if (name.contains("medium_tower_base")) {
                    keep = 1.0D - 0.5D * lowness;
                } else if (name.contains("small_tower_base")) {
                    copies = 1 + (int) Math.round(2.0D * lowness);
                } else if (name.contains("tower_floor")) {
                    keep = 1.0D - 0.8D * lowness;
                }
                if (random.nextDouble() >= keep) {
                    continue;
                }
                boolean wideRoof = name.contains("roof") && WindShrinePieceProfiles.footprint(element, templates) >= 11 && random.nextDouble() < lowness;
                for (int i = 0; i < copies; i++) {
                    (wideRoof ? wideRoofs : thinned).add(element);
                }
            }
            wideRoofs.addAll(thinned);
            candidates = wideRoofs;
        }
        if (session.parentDepth <= DEAD_END_DEPTH) {
            List<StructurePoolElement> open = new ArrayList<>();
            List<StructurePoolElement> deadEnds = new ArrayList<>();
            for (StructurePoolElement element : candidates) {
                (WindShrinePieceProfiles.horizontalExits(element, templates) > 0 ? open : deadEnds).add(element);
            }
            if (!open.isEmpty()) {
                open.addAll(deadEnds);
                candidates = open;
            }
        }
        if (session.doorFloorY != Integer.MIN_VALUE) {
            int required = session.doorFloorY - 4 - DESCENT_PER_DEPTH * (session.parentDepth + 2);
            boolean behind = session.parentBox.minY() > required + 4;
            List<StructurePoolElement> stairsOnly = new ArrayList<>();
            for (StructurePoolElement element : candidates) {
                if (element.toString().contains(STAIRS)) {
                    stairsOnly.add(element);
                }
            }
            if (behind && !stairsOnly.isEmpty()) {
                return stairsOnly;
            }
        }
        List<StructurePoolElement> result = new ArrayList<>(candidates);
        for (StructurePoolElement element : candidates) {
            if (element.toString().contains(STAIRS)) {
                for (int i = 0; i < 2; i++) {
                    result.add(random.nextInt(result.size() + 1), element);
                }
            }
        }
        return result;
    }

    public static boolean rejectOrCollides(boolean collides, VoxelShape candidateShape) {
        Session session = SESSION.get();
        if (session == null) {
            return collides;
        }
        AABB aabb = candidateShape.bounds();
        BoundingBox box = new BoundingBox(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ));
        boolean stackedCandidate = session.parentDirection.getAxis().isVertical();
        if (collides) {
            if (stackedCandidate && overlapsOnlyStairs(session, box, aabb)) {
                collides = false;
            } else {
                return reject(session, Reason.COLLISION);
            }
        }
        for (BoundingBox other : session.foreign) {
            if (other.intersects(box)) {
                return reject(session, Reason.FOREIGN);
            }
        }
        WindShrinePieceProfiles.Profile profile = session.candidateProfile;
        if (profile.open()) {
            int depth = session.parentDepth + 1;
            if (depth + profile.chain() > session.maxDepth + 1) {
                return reject(session, Reason.UNFINISHABLE);
            }
            BoundingBox closing = new BoundingBox(box.minX(), box.maxY() + 1, box.minZ(), box.maxX(), box.maxY() + profile.closingHeight(), box.maxZ());
            for (BoundingBox other : session.foreign) {
                if (other.intersects(closing)) {
                    return reject(session, Reason.CLOSING_SPACE);
                }
            }
            for (BoundingBox other : session.accepted) {
                if (other.intersects(closing)) {
                    StairBox stair = stairFor(session, other);
                    if (stair == null || !stair.clearOf(closing)) {
                        return reject(session, Reason.CLOSING_SPACE);
                    }
                }
            }
        }
        boolean stairs = session.candidateName.contains(STAIRS);
        if (stairs && session.parentBox != null && box.minY() >= session.parentBox.minY() - 2) {
            return reject(session, Reason.STAIRS_CLIMB);
        }
        boolean stacked = session.parentDirection.getAxis().isVertical();
        if (stacked && !session.candidateName.contains("main_tower") && !session.candidateName.contains("roof") && !session.candidateName.contains("mobs/")) {
            int baseY = box.minY();
            for (BoundingBox other : session.accepted) {
                if (sharesGround(box, other)) {
                    baseY = Math.min(baseY, other.minY());
                }
            }
            int limit = 21 - (int) Math.round(10.0D * lowness(session));
            if (box.minY() - baseY >= limit) {
                return reject(session, Reason.TOWER_HEIGHT);
            }
        }
        if (session.wing && session.parentBox != null && !stacked) {
            if (box.minY() > session.parentBox.minY() + 1) {
                return reject(session, Reason.WING_CLIMB);
            }
            boolean closingPiece = session.candidateName.contains("connector_end") || session.candidateName.contains("roof") || session.candidateName.contains("bottom") || session.candidateName.contains("mobs/");
            if (session.placed >= MAX_WING_PIECES && !closingPiece) {
                return reject(session, Reason.WING_FULL);
            }
            if (session.doorPos != null && !closingPiece) {
                double doorBearing = Math.atan2(session.doorPos.getZ() - session.center.getZ(), session.doorPos.getX() - session.center.getX());
                BlockPos boxCenter = box.getCenter();
                double bearing = Math.atan2(boxCenter.getZ() - session.center.getZ(), boxCenter.getX() - session.center.getX());
                double delta = Math.abs(Math.atan2(Math.sin(bearing - doorBearing), Math.cos(bearing - doorBearing)));
                if (delta > WEDGE) {
                    return reject(session, Reason.WEDGE);
                }
            }
            boolean cap = session.candidateName.contains("connector_end") || session.candidateName.contains("roof") || session.candidateName.contains("bottom") || session.candidateName.contains("mobs/");
            if (!cap && session.doorFloorY != Integer.MIN_VALUE && box.minY() > session.doorFloorY - 4 - DESCENT_PER_DEPTH * (session.parentDepth + 1)) {
                return reject(session, Reason.DESCENT_QUOTA);
            }
            BlockPos parentCenter = session.parentBox.getCenter();
            double radialX = parentCenter.getX() - session.center.getX();
            double radialZ = parentCenter.getZ() - session.center.getZ();
            double radialLength = Math.sqrt(radialX * radialX + radialZ * radialZ);
            if (radialLength > 1.0D) {
                double cosine = (radialX * session.parentDirection.getStepX() + radialZ * session.parentDirection.getStepZ()) / radialLength;
                if (cosine < INWARD_COSINE) {
                    return reject(session, Reason.INWARD);
                }
            }
            double limit = session.doorPos != null ? horizontalDistance(session.doorPos, session.center) - INWARD_TOLERANCE : radialLength - INWARD_TOLERANCE;
            if (horizontalDistance(box.getCenter(), session.center) < limit) {
                return reject(session, Reason.INWARD);
            }
        }
        boolean slender = box.getXSpan() <= 3 && box.getZSpan() <= 3;
        boolean bridge = session.candidateName.contains("bridge") || session.candidateName.contains("connector_end") || slender;
        if (!stacked && !bridge) {
            for (BoundingBox other : session.accepted) {
                boolean verticallyApart = box.maxY() < other.minY() || other.maxY() < box.minY();
                if (verticallyApart && sharesGround(box, other)) {
                    return reject(session, Reason.FOOTPRINT);
                }
            }
            for (BoundingBox other : session.foreign) {
                boolean otherSlender = other.getXSpan() <= 3 && other.getZSpan() <= 3;
                if (!otherSlender && sharesGround(box, other)) {
                    return reject(session, Reason.FOOTPRINT);
                }
            }
        }
        remember(session, box);
        if (stairs) {
            session.stairs.add(new StairBox(box, session.parentDirection));
        }
        session.placed++;
        return false;
    }

    private static double lowness(Session session) {
        if (!session.wing || session.doorFloorY == Integer.MIN_VALUE || session.parentBox == null) {
            return 0.0D;
        }
        return Mth.clamp((session.doorFloorY - session.parentBox.minY() - 8) / 40.0D, 0.0D, 1.0D);
    }

    private static boolean overlapsOnlyStairs(Session session, BoundingBox box, AABB aabb) {
        if (session.freeBounds == null || !session.freeBounds.contains(aabb.minX, aabb.minY, aabb.minZ) || !session.freeBounds.contains(aabb.maxX, aabb.maxY, aabb.maxZ)) {
            return false;
        }
        boolean touchedStair = false;
        for (BoundingBox other : session.accepted) {
            if (other.intersects(box)) {
                StairBox stair = stairFor(session, other);
                if (stair == null || !stair.clearOf(box)) {
                    return false;
                }
                touchedStair = true;
            }
        }
        for (BoundingBox other : session.foreign) {
            if (other.intersects(box)) {
                return false;
            }
        }
        return touchedStair;
    }

    private static StairBox stairFor(Session session, BoundingBox box) {
        for (StairBox stair : session.stairs) {
            if (stair.box().equals(box)) {
                return stair;
            }
        }
        return null;
    }

    private static Direction runsAwayFrom(BlockPos door, BoundingBox box) {
        if (door == null) {
            return Direction.NORTH;
        }
        BlockPos center = box.getCenter();
        int dx = door.getX() - center.getX();
        int dz = door.getZ() - center.getZ();
        if (Math.abs(dx) >= Math.abs(dz)) {
            return dx < 0 ? Direction.EAST : Direction.WEST;
        }
        return dz < 0 ? Direction.SOUTH : Direction.NORTH;
    }

    private static double horizontalDistance(BlockPos a, BlockPos b) {
        double dx = a.getX() - b.getX();
        double dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    private static boolean sharesGround(BoundingBox a, BoundingBox b) {
        return Math.min(a.maxX(), b.maxX()) >= Math.max(a.minX(), b.minX()) && Math.min(a.maxZ(), b.maxZ()) >= Math.max(a.minZ(), b.minZ());
    }

    private static void remember(Session session, BoundingBox box) {
        for (BoundingBox other : session.accepted) {
            if (other.equals(box)) {
                return;
            }
        }
        session.accepted.add(box);
    }
}
