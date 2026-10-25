package com.Vivideru.Goety.common.world.structures.crypt;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rules applied while the jigsaw placer assembles a crypt structure. The crypt is layered: the hidden entrance mound stands on
 * level 0, every path leaves it through one or two plain corridors and then a Library, whose lower floor is 33 blocks
 * deeper. Three of the Library's lower doors are developed in place; the one pointing away from the tower stays open for
 * the next structure, which starts there with its own corridor and (on level 1) its own Library down to level 2.
 * Hooked through the same mixins as the Wind Shrine rules; every method is a no-op without a crypt session on the thread.
 */
public final class CryptPlacementRules {
    private static final Logger LOGGER = LogUtils.getLogger();

    /** What a structure of the crypt is for; decides which pieces it may hold. */
    public enum Kind { MAIN, STAIRS, BOSS, VAULT, DEEP }

    /** Chunks only keep references to structure starts this many chunks away; pieces beyond are never placed. */
    private static final int REFERENCE_CHUNKS = 8;
    /** Parent depth from which the Library may follow the plain corridors (after one or two of them). */
    private static final int STAIRS_FROM_DEPTH = 1;
    /** Parent depth from which only the Library is accepted on the entry level. */
    private static final int STAIRS_FORCED_DEPTH = 3;
    private static final int TERMINAL_FROM_DEPTH = 1;
    /** Libraries (staircases down) the main dungeon places: the other tower doors end in a few corridors. */
    private static final int MAIN_LIBRARIES = 2;
    /** Blocks of leeway on the sector's sides, so paths can turn near the tower where the sector is narrow. */
    private static final int SECTOR_SLACK = 20;
    private static final int DEAD_END_FROM_DEPTH = 2;
    private static final int MAX_MAIN_PIECES = 36;
    private static final int MAX_WING_PIECES = 27;
    private static final int LEVEL_TOLERANCE = 2;
    private static final String CAP = "crypt/end_cap";
    private static final String DEAD_END = "crypt/dead_end";
    private static final String STRAIGHT = "crypt/corridor_straight";
    private static final String CORNER = "crypt/corridor_corner";
    private static final String LIBRARY = "crypt/library";
    private static final ThreadLocal<Session> SESSION = new ThreadLocal<>();
    private static final ThreadLocal<Rotation> FORCED_ROTATION = new ThreadLocal<>();
    /**
     * Rooms a single structure may hold at most this many times; corridors are unlimited. Dead ends close the open doors
     * once the structure is full, so without a limit they would end almost every branch: past it, the walls close them.
     */
    private static final Map<String, Integer> ROOM_LIMITS = Map.ofEntries(
            Map.entry("crypt/dead_end", 2),
            Map.entry("crypt/sculk_graveyard", 1),
            Map.entry("crypt/study_hall", 1),
            Map.entry("crypt/catacomb", 3),
            Map.entry("crypt/spider_hall", 3),
            Map.entry("crypt/trap_hall", 3),
            Map.entry("crypt/spider_nest", 3),
            Map.entry("crypt/treasure_cross", 2),
            Map.entry("crypt/stair_hall", 2),
            Map.entry("crypt/spawner_shaft", 2),
            Map.entry("crypt/wind_lift", 2));
    /** Height between two levels: the drop of a Library's stairs. */
    private static final int LEVEL_SPACING = 33;
    /** Stair halls, stairways and wind lifts may move a branch up or down, but every door of the entry level stays within this many blocks of it. */
    private static final int LEVEL_BAND = 12;
    /** Below the first Library the branches climb or drop further: their doors stay within this many blocks of their level. */
    private static final int DEEP_LEVEL_BAND = 24;
    /** Pieces keep this many blocks above the bottom of the world, clear of the bedrock. */
    private static final int FLOOR_MARGIN = 5;
    /** Height of the doors above the bottom of a corridor or a start piece. */
    private static final int DOOR_HEIGHT = 3;
    /** Lowest and highest door of each template, relative to its bottom, read once from its jigsaws. */
    private static final Map<String, int[]> DOOR_HEIGHTS = new java.util.concurrent.ConcurrentHashMap<>();

    /** Stair halls, stairways, spawner shafts and wind lifts: the pieces that climb or drop within a level. */
    private static final List<String> VERTICAL = List.of("crypt/stair_hall", "crypt/stairway", "crypt/spawner_shaft", "crypt/wind_lift");
    /** Pieces that must follow a vertical piece before the next one. */
    private static final int STAIR_GAP = 2;

    private enum Reason { RESERVED, BEYOND, SECTOR, BACKWARD, REACH, FOREIGN, LEVEL, STAIRS_EARLY, STAIRS_TWICE, ENTRY_PATH, FULL, UNFINISHABLE, LIMIT, EARLY_DEAD_END, STRAIGHT, TERMINAL, HEIGHT, STAIRS_CLOSE }

    private static boolean isVertical(String name) {
        for (String vertical : VERTICAL) {
            if (name.contains(vertical)) {
                return true;
            }
        }
        return false;
    }

    /** The outward side of a placed Library: nothing of the lower level may cross the plane of its far face, so the next structure has room there. */
    private record Reserved(Direction away, int plane, int levelY) {
        boolean crossedBy(BoundingBox box) {
            // The plane lies on the level under the Library, whose pieces may climb or drop up to DEEP_LEVEL_BAND blocks; none of them
            // crosses it, other levels are free.
            if (box.minY() > this.levelY + DEEP_LEVEL_BAND + LEVEL_TOLERANCE + 1 || box.maxY() < this.levelY - DEEP_LEVEL_BAND - 1) {
                return false;
            }
            return switch (this.away) {
                case EAST -> box.maxX() > this.plane;
                case WEST -> box.minX() < this.plane;
                case SOUTH -> box.maxZ() > this.plane;
                case NORTH -> box.minZ() < this.plane;
                default -> false;
            };
        }
    }

    private static final class Session {
        final Kind kind;
        final String label;
        final BoundingBox reach;
        final List<BoundingBox> foreign = new ArrayList<>();
        @Nullable
        final String terminal;
        final int maxDepth;
        final int maxPieces;
        final Map<String, Integer> placedRooms = new HashMap<>();
        final List<Reserved> reserved = new ArrayList<>();
        /** The direction each accepted piece belongs to: the tower door its branch left through, or the wing's door. */
        final Map<BoundingBox, Direction> paths = new HashMap<>();
        /** For each accepted piece, how many pieces separate it from the last vertical piece of its branch (capped at STAIR_GAP). */
        final Map<BoundingBox, Integer> sinceStairs = new HashMap<>();
        /** Vertical pieces already attached to each piece: two of them on the same room would be a single piece apart. */
        final Map<BoundingBox, Integer> verticalChildren = new HashMap<>();
        /** The level of the structure's start: 0 for the main dungeon, the wing's level for a wing. */
        final int baseLevel;
        final int minBuildY;
        /** Each accepted piece's level counted from the start's: one more under a Library's lower doors. */
        final Map<BoundingBox, Integer> levels = new HashMap<>();
        int candidateLevel;
        int libraries;
        @Nullable
        BlockPos towerCenter;
        /** Wings: the direction from the tower the whole wing keeps to, and the plane of its start door it never goes back behind. */
        @Nullable
        Direction direction;
        int startPlane = Integer.MIN_VALUE;
        int entryLevelY = Integer.MIN_VALUE;
        boolean terminalPlaced;
        boolean stairsPlaced;
        int depth;
        String parentName = "";
        @Nullable
        BoundingBox parentBox;
        Direction parentDirection = Direction.NORTH;
        String candidateName = "";
        @Nullable
        AABB candidateBounds;
        @Nullable
        int[] candidateDoors;
        int placed;
        final int[] rejected = new int[Reason.values().length];

        Session(Kind kind, String label, ChunkPos startChunk, @Nullable String terminal, int maxDepth, int maxPieces, @Nullable BlockPos towerCenter, int baseLevel, int minBuildY) {
            this.kind = kind;
            this.baseLevel = baseLevel;
            this.minBuildY = minBuildY;
            this.label = label;
            this.terminal = terminal;
            this.maxDepth = maxDepth;
            this.maxPieces = maxPieces;
            this.towerCenter = towerCenter;
            this.reach = new BoundingBox(SectionPos.sectionToBlockCoord(startChunk.x - REFERENCE_CHUNKS), Integer.MIN_VALUE / 2, SectionPos.sectionToBlockCoord(startChunk.z - REFERENCE_CHUNKS),
                    SectionPos.sectionToBlockCoord(startChunk.x + REFERENCE_CHUNKS) + 15, Integer.MAX_VALUE / 2, SectionPos.sectionToBlockCoord(startChunk.z + REFERENCE_CHUNKS) + 15);
        }

        /** The direction the current parent's branch runs in. */
        Direction pathDirection() {
            if (this.direction != null) {
                return this.direction;
            }
            if (this.depth == 0 || this.parentBox == null) {
                return this.parentDirection;
            }
            Direction known = this.paths.get(this.parentBox);
            if (known != null) {
                return known;
            }
            return this.towerCenter != null ? awayFromTower(this.towerCenter, this.parentBox.getCenter()) : this.parentDirection;
        }

        void accept(BoundingBox box, Direction path) {
            this.placed++;
            int parentGap = this.parentBox == null ? STAIR_GAP : this.sinceStairs.getOrDefault(this.parentBox, STAIR_GAP);
            this.sinceStairs.put(box, isVertical(this.candidateName) ? 0 : Math.min(STAIR_GAP, parentGap + 1));
            if (this.parentBox != null && isVertical(this.candidateName)) {
                this.verticalChildren.merge(this.parentBox, 1, Integer::sum);
            }
            this.levels.put(box, this.candidateLevel);
            if (path.getAxis().isHorizontal()) {
                this.paths.put(box, path);
            }
        }

        /** True while the parent still lies on the level the structure entered at. */
        boolean parentOnEntryLevel() {
            return this.parentBox != null && this.entryLevelY != Integer.MIN_VALUE && Math.abs(this.parentBox.minY() - this.entryLevelY) <= LEVEL_TOLERANCE;
        }

        boolean descends() {
            return this.kind == Kind.MAIN || this.kind == Kind.STAIRS;
        }

        /** The Library is wanted now: entry level, after the plain corridors, and (for a wing) not placed yet. */
        boolean stairsWanted() {
            return this.descends() && this.parentOnEntryLevel() && this.depth >= STAIRS_FROM_DEPTH && !(this.kind == Kind.STAIRS && this.stairsPlaced);
        }

        boolean terminalAllowed() {
            return this.terminal != null && !this.terminalPlaced && this.depth >= TERMINAL_FROM_DEPTH && this.depth < this.maxDepth;
        }
    }

    private CryptPlacementRules() {
    }

    public static void beginMain(ChunkPos startChunk, int maxDepth, int minBuildY) {
        SESSION.set(new Session(Kind.MAIN, "dungeon", startChunk, null, maxDepth, MAX_MAIN_PIECES, null, 0, minBuildY));
    }

    /** A wing keeps clear of everything planned before it; {@code terminal} is the room a boss or vault wing must place exactly once. */
    public static void beginWing(String label, ChunkPos startChunk, List<BoundingBox> obstacles, Kind kind, @Nullable String terminal, int maxDepth, BlockPos towerCenter, Direction direction,
                                 int level, int minBuildY) {
        Session session = new Session(kind, label, startChunk, terminal, maxDepth, MAX_WING_PIECES, towerCenter, level, minBuildY);
        session.direction = direction;
        session.foreign.addAll(obstacles);
        SESSION.set(session);
    }

    /** What an assembly achieved; structures retry with another seed until the outcome satisfies their kind. */
    public record Outcome(int pieces, int libraries, boolean terminalPlaced) {
        public static final Outcome NONE = new Outcome(0, 0, false);

        public boolean satisfies(Kind kind) {
            return switch (kind) {
                case MAIN -> this.libraries >= MAIN_LIBRARIES;
                case STAIRS -> this.libraries >= 1;
                case BOSS, VAULT -> this.terminalPlaced;
                case DEEP -> this.pieces >= 8;
            };
        }

        public boolean betterThan(Outcome other, Kind kind) {
            if (this.satisfies(kind) != other.satisfies(kind)) {
                return this.satisfies(kind);
            }
            if (this.libraries != other.libraries) {
                return this.libraries > other.libraries;
            }
            return this.pieces > other.pieces;
        }
    }

    public static Outcome end() {
        Session session = SESSION.get();
        if (session == null) {
            return Outcome.NONE;
        }
        if (LOGGER.isDebugEnabled()) {
            StringBuilder summary = new StringBuilder();
            for (Reason reason : Reason.values()) {
                if (session.rejected[reason.ordinal()] > 0) {
                    summary.append(' ').append(reason.name().toLowerCase()).append('=').append(session.rejected[reason.ordinal()]);
                }
            }
            LOGGER.debug("Crypt {} assembled: {} pieces, libraries={} terminal={}, rejected:{}", session.label, session.placed, session.libraries, session.terminalPlaced, summary);
        }
        SESSION.remove();
        return new Outcome(session.placed, session.libraries, session.terminalPlaced);
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

    public static void beginParent(PoolElementStructurePiece parent, int depth) {
        Session session = SESSION.get();
        if (session == null) {
            return;
        }
        session.depth = depth;
        session.parentName = parent.getElement().toString();
        session.parentBox = parent.getBoundingBox();
        if (depth == 0) {
            session.entryLevelY = session.parentBox.minY();
            if (session.kind != Kind.MAIN && session.direction != null) {
                session.startPlane = switch (session.direction) {
                    case EAST -> session.parentBox.minX();
                    case WEST -> session.parentBox.maxX();
                    case SOUTH -> session.parentBox.minZ();
                    default -> session.parentBox.maxZ();
                };
            }
            if (session.towerCenter == null) {
                session.towerCenter = session.parentBox.getCenter();
            }
        }
    }

    public static void parentDirection(Direction direction) {
        Session session = SESSION.get();
        if (session != null) {
            session.parentDirection = direction;
        }
    }

    public static void candidate(StructurePoolElement element, net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager templates) {
        Session session = SESSION.get();
        if (session != null) {
            session.candidateName = element.toString();
            session.candidateDoors = doorHeights(session.candidateName, templates);
        }
    }

    /** The lowest and highest jigsaw of a single-template element, relative to its bottom; null when it cannot be read. */
    @Nullable
    private static int[] doorHeights(String element, net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager templates) {
        int start = element.indexOf("goety:");
        if (start < 0) {
            return null;
        }
        int end = start;
        while (end < element.length() && (Character.isLetterOrDigit(element.charAt(end)) || "_:/.-".indexOf(element.charAt(end)) >= 0)) {
            end++;
        }
        String id = element.substring(start, end);
        int[] cached = DOOR_HEIGHTS.get(id);
        if (cached != null) {
            return cached.length == 0 ? null : cached;
        }
        int[] heights = new int[0];
        net.minecraft.resources.ResourceLocation location = net.minecraft.resources.ResourceLocation.tryParse(id);
        if (location != null) {
            java.util.Optional<net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate> template = templates.get(location);
            if (template.isPresent()) {
                int low = Integer.MAX_VALUE;
                int high = Integer.MIN_VALUE;
                for (net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo info : template.get().filterBlocks(BlockPos.ZERO,
                        new net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings(), net.minecraft.world.level.block.Blocks.JIGSAW)) {
                    low = Math.min(low, info.pos().getY());
                    high = Math.max(high, info.pos().getY());
                }
                if (low != Integer.MAX_VALUE) {
                    heights = new int[]{low, high};
                }
            }
        }
        DOOR_HEIGHTS.put(id, heights);
        return heights.length == 0 ? null : heights;
    }

    /**
     * Whether a piece would put one of its doors too far above or below its level: LEVEL_BAND blocks on the entry level,
     * DEEP_LEVEL_BAND below the first Library. Levels lie LEVEL_SPACING blocks apart from the door height of the start piece.
     */
    private static boolean outOfLevel(Session session, BoundingBox box) {
        if (session.candidateDoors == null || session.entryLevelY == Integer.MIN_VALUE) {
            return false;
        }
        int reference = session.entryLevelY + DOOR_HEIGHT;
        int low = box.minY() + session.candidateDoors[0];
        int high = box.minY() + session.candidateDoors[1];
        int door = reference - session.candidateLevel * LEVEL_SPACING;
        int band = session.baseLevel + session.candidateLevel == 0 ? LEVEL_BAND : DEEP_LEVEL_BAND;
        return low < door - band || high > door + band;
    }

    public static void candidateBounds(AABB bounds) {
        Session session = SESSION.get();
        if (session != null) {
            session.candidateBounds = bounds;
        }
    }

    /** Orders the candidates: the Library or the terminal room first when it is wanted, dropped when it cannot be placed here. */
    public static List<StructurePoolElement> reweight(List<StructurePoolElement> candidates) {
        Session session = SESSION.get();
        if (session == null) {
            return candidates;
        }
        List<StructurePoolElement> first = new ArrayList<>();
        List<StructurePoolElement> others = new ArrayList<>();
        boolean stairsWanted = session.stairsWanted();
        boolean stairsFirst = stairsWanted && (session.depth >= STAIRS_FROM_DEPTH + 1 || session.parentBox != null && Mth.getSeed(session.parentBox.minX(), session.parentBox.minY(), session.parentBox.minZ()) % 2L == 0L);
        boolean terminalWanted = session.terminalAllowed();
        // Straight corridors and dead ends are tried only when nothing else fits; the path to the first Library keeps its own rule.
        boolean lastResort = !(session.descends() && session.parentOnEntryLevel());
        List<StructurePoolElement> late = new ArrayList<>();
        for (StructurePoolElement element : candidates) {
            String name = element.toString();
            if (name.contains(LIBRARY)) {
                if (stairsWanted) {
                    (stairsFirst ? first : others).add(element);
                }
            } else if (session.terminal != null && name.contains(session.terminal)) {
                if (terminalWanted) {
                    first.add(element);
                }
            } else if (lastResort && (name.contains(STRAIGHT) || name.contains(DEAD_END))) {
                late.add(element);
            } else {
                others.add(element);
            }
        }
        first.addAll(others);
        first.addAll(late);
        return first;
    }

    private static boolean reject(Session session, Reason reason) {
        session.rejected[reason.ordinal()]++;
        return true;
    }

    /** The side of a Library that faces away from the tower: its lower door there is kept for the next structure. */
    private static Direction awayFromTower(BlockPos tower, BlockPos center) {
        int dx = center.getX() - tower.getX();
        int dz = center.getZ() - tower.getZ();
        if (Math.abs(dx) >= Math.abs(dz)) {
            return dx >= 0 ? Direction.EAST : Direction.WEST;
        }
        return dz >= 0 ? Direction.SOUTH : Direction.NORTH;
    }

    /** True when the point lies in the 90-degree sector of the given direction as seen from the tower. */
    private static boolean inSector(BlockPos tower, BlockPos point, Direction direction) {
        int dx = point.getX() - tower.getX();
        int dz = point.getZ() - tower.getZ();
        int along = dx * direction.getStepX() + dz * direction.getStepZ();
        int across = direction.getAxis() == Direction.Axis.X ? Math.abs(dz) : Math.abs(dx);
        return along >= 1 && across <= along + SECTOR_SLACK;
    }

    /** True when the box reaches back behind the plane of the wing's start door. */
    private static boolean behindStart(BoundingBox box, Direction direction, int plane) {
        return switch (direction) {
            case EAST -> box.minX() < plane;
            case WEST -> box.maxX() > plane;
            case SOUTH -> box.minZ() < plane;
            default -> box.maxZ() > plane;
        };
    }

    private static boolean isPlainCorridor(String name) {
        return name.contains(STRAIGHT) || name.contains(CORNER);
    }

    /** Called with the placer's own collision verdict; returns true when the candidate must be rejected. */
    public static boolean rejectOrCollides(boolean collides) {
        Session session = SESSION.get();
        if (session == null || session.candidateBounds == null || collides) {
            return collides;
        }
        String name = session.candidateName;
        AABB aabb = session.candidateBounds;
        BoundingBox box = new BoundingBox(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ));
        Direction path = session.pathDirection();
        if (session.parentBox != null && session.parentName.contains(LIBRARY) && session.parentDirection.getAxis().isHorizontal()
                && box.minY() <= session.parentBox.minY() + LEVEL_TOLERANCE && session.parentDirection == path) {
            return reject(session, Reason.RESERVED);
        }
        if (CryptExits.isCapName(name)) {
            return false;
        }
        if (box.minX() < session.reach.minX() || box.maxX() > session.reach.maxX() || box.minZ() < session.reach.minZ() || box.maxZ() > session.reach.maxZ()) {
            return reject(session, Reason.REACH);
        }
        for (BoundingBox other : session.foreign) {
            if (other.intersects(box)) {
                return reject(session, Reason.FOREIGN);
            }
        }
        if (name.contains("crypt/entrance")) {
            session.placed++;
            return false;
        }
        boolean terminalRoom = session.terminal != null && name.contains(session.terminal);
        // Every branch keeps to its own sector from the tower, so the four directions never meet; a wing also never turns back behind
        // its start door. The terminal room of a wing (the Skull Lord chamber, the Vault) is so wide that it is judged by overlaps alone.
        if (session.towerCenter != null && session.parentBox != null && !terminalRoom) {
            if (path.getAxis().isHorizontal() && !inSector(session.towerCenter, box.getCenter(), path)) {
                return reject(session, Reason.SECTOR);
            }
            if (session.direction != null && session.startPlane != Integer.MIN_VALUE && session.depth > 0 && behindStart(box, session.direction, session.startPlane)) {
                return reject(session, Reason.BACKWARD);
            }
        }
        // A piece takes its parent's level, one more when it hangs from the lower doors of a Library.
        int parentLevel = session.parentBox == null ? 0 : session.levels.getOrDefault(session.parentBox, 0);
        boolean underLibrary = session.parentBox != null && session.parentName.contains(LIBRARY) && box.minY() < (session.parentBox.minY() + session.parentBox.maxY()) / 2;
        session.candidateLevel = parentLevel + (underLibrary ? 1 : 0);
        if (box.minY() < session.minBuildY + FLOOR_MARGIN || !name.contains(LIBRARY) && outOfLevel(session, box)) {
            return reject(session, Reason.HEIGHT);
        }
        if (isVertical(name) && session.parentBox != null && (session.sinceStairs.getOrDefault(session.parentBox, STAIR_GAP) < STAIR_GAP
                || session.verticalChildren.getOrDefault(session.parentBox, 0) > 0)) {
            return reject(session, Reason.STAIRS_CLOSE);
        }
        if (name.contains(LIBRARY)) {
            if (!session.descends() || !session.parentOnEntryLevel()) {
                return reject(session, Reason.LEVEL);
            }
            if (session.depth < STAIRS_FROM_DEPTH) {
                return reject(session, Reason.STAIRS_EARLY);
            }
            if (session.kind == Kind.STAIRS && session.stairsPlaced) {
                return reject(session, Reason.STAIRS_TWICE);
            }
            if (session.kind == Kind.MAIN && session.libraries >= MAIN_LIBRARIES) {
                return reject(session, Reason.LIMIT);
            }
            session.stairsPlaced = true;
            session.libraries++;
            session.accept(box, path);
            if (session.towerCenter != null) {
                Direction away = path.getAxis().isHorizontal() ? path : awayFromTower(session.towerCenter, box.getCenter());
                int plane = switch (away) {
                    case EAST -> box.maxX();
                    case WEST -> box.minX();
                    case SOUTH -> box.maxZ();
                    default -> box.minZ();
                };
                session.reserved.add(new Reserved(away, plane, box.minY()));
                LOGGER.debug("Crypt {}: Library at depth {} box {} keeps its {} door", session.label, session.depth + 1, box, away);
            }
            return false;
        }
        for (Reserved reserved : session.reserved) {
            if (reserved.crossedBy(box)) {
                return reject(session, Reason.BEYOND);
            }
        }
        boolean deadEnd = name.contains(DEAD_END);
        boolean stairsDone = session.kind == Kind.MAIN && session.libraries >= MAIN_LIBRARIES;
        if (session.descends() && session.parentOnEntryLevel() && !(stairsDone && deadEnd)) {
            if (!isPlainCorridor(name) || session.depth >= STAIRS_FORCED_DEPTH) {
                return reject(session, Reason.ENTRY_PATH);
            }
            session.accept(box, path);
            return false;
        }
        boolean terminal = session.terminal != null && name.contains(session.terminal);
        if (terminal) {
            if (!session.terminalAllowed()) {
                return reject(session, Reason.TERMINAL);
            }
        } else if (!deadEnd) {
            if (session.depth >= session.maxDepth) {
                return reject(session, Reason.UNFINISHABLE);
            }
            if (session.placed >= session.maxPieces) {
                return reject(session, Reason.FULL);
            }
        }
        if (deadEnd && session.depth < DEAD_END_FROM_DEPTH) {
            return reject(session, Reason.EARLY_DEAD_END);
        }
        for (Map.Entry<String, Integer> limit : ROOM_LIMITS.entrySet()) {
            if (name.contains(limit.getKey())) {
                int count = session.placedRooms.getOrDefault(limit.getKey(), 0);
                if (count >= limit.getValue()) {
                    return reject(session, Reason.LIMIT);
                }
                session.placedRooms.put(limit.getKey(), count + 1);
            }
        }
        if (name.contains(STRAIGHT) && session.parentName.contains(STRAIGHT)) {
            return reject(session, Reason.STRAIGHT);
        }
        if (terminal) {
            session.terminalPlaced = true;
        }
        session.accept(box, path);
        return false;
    }
}
