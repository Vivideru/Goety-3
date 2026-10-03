package com.Vivideru.Goety.common.world.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public final class WindShrineExits {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int COUNT = 8;
    private static final String CAP = "connector_end";
    private static final List<String> DOOR_NAMES = List.of("goety:wind_shrine/entrance", "goety:wind_shrine/connector", "goety:wind_shrine/connector_ground");

    private record Candidate(WindShrinePlans.Door door, StructurePiece cap, double distance) {
    }

    private WindShrineExits() {
    }

    public static void openExits(List<StructurePiece> pieces, StructureTemplateManager templates, long seed, BlockPos center) {
        for (Candidate candidate : choose(pieces, templates, seed, center)) {
            if (candidate == null) {
                continue;
            }
            if (candidate.cap() != null) {
                pieces.remove(candidate.cap());
            }
            BoundingBox zone = landingZone(candidate.door());
            pieces.removeIf(piece -> isCap(piece) && piece.getBoundingBox().intersects(zone));
        }
    }

    private static boolean isCap(StructurePiece piece) {
        return piece instanceof PoolElementStructurePiece pool && pool.getElement().toString().contains(CAP);
    }

    public static List<Optional<WindShrinePlans.Door>> doors(List<StructurePiece> pieces, StructureTemplateManager templates, long seed, BlockPos center) {
        List<Optional<WindShrinePlans.Door>> result = new ArrayList<>();
        for (Candidate candidate : choose(pieces, templates, seed, center)) {
            result.add(candidate == null ? Optional.empty() : Optional.of(candidate.door()));
        }
        return result;
    }

    private static Candidate[] choose(List<StructurePiece> pieces, StructureTemplateManager templates, long seed, BlockPos center) {
        Candidate[] best = new Candidate[COUNT];
        List<Candidate> outward = new ArrayList<>();
        for (Candidate candidate : candidates(pieces, templates, seed)) {
            double dx = candidate.door().jigsaw().getX() - center.getX();
            double dz = candidate.door().jigsaw().getZ() - center.getZ();
            Direction facing = candidate.door().facing();
            if (dx * facing.getStepX() + dz * facing.getStepZ() > 0.0D) {
                if (landingFree(pieces, candidate)) {
                    outward.add(candidate);
                }
            }
        }
        double sectorWidth = 2.0D * Math.PI / COUNT;
        for (int pass = 0; pass < 3; pass++) {
            List<Candidate> pool = outward;
            double limit = pass == 0 ? sectorWidth * 0.7D : Math.PI;
            for (int sector = 0; sector < COUNT; sector++) {
                if (best[sector] != null) {
                    continue;
                }
                double bearing = -Math.PI + (sector + 0.5D) * sectorWidth;
                double farthest = 0.0D;
                for (Candidate candidate : pool) {
                    if (!taken(best, candidate) && Math.abs(angleBetween(bearing, angleOf(candidate, center))) <= limit) {
                        farthest = Math.max(farthest, candidate.distance());
                    }
                }
                Candidate chosen = null;
                double chosenDeviation = Double.MAX_VALUE;
                for (Candidate candidate : pool) {
                    if (taken(best, candidate) || pass == 0 && candidate.distance() < farthest * OUTER_SHARE || landingsCross(best, candidate) || bearingClash(best, candidate, center)) {
                        continue;
                    }
                    double deviation = Math.abs(angleBetween(bearing, angleOf(candidate, center)));
                    if (deviation <= limit && (deviation < chosenDeviation || deviation == chosenDeviation && candidate.distance() > chosen.distance())) {
                        chosen = candidate;
                        chosenDeviation = deviation;
                    }
                }
                best[sector] = chosen;
            }
        }
        if (LOGGER.isDebugEnabled()) {
            List<Candidate> all = candidates(pieces, templates, seed);
            StringBuilder report = new StringBuilder();
            for (int sector = 0; sector < COUNT; sector++) {
                report.append(' ').append((char) ('a' + sector)).append('=').append(best[sector] == null ? "none" : best[sector].door().target().toShortString() + '>' + best[sector].door().facing());
            }
            int blockedShown = 0;
            StringBuilder blocked = new StringBuilder();
            for (Candidate candidate : all) {
                double dx = candidate.door().jigsaw().getX() - center.getX();
                double dz = candidate.door().jigsaw().getZ() - center.getZ();
                Direction facing = candidate.door().facing();
                if (dx * facing.getStepX() + dz * facing.getStepZ() > 0.0D && !landingFree(pieces, candidate) && blockedShown++ < 12) {
                    blocked.append("\n    ").append(candidate.door().target().toShortString()).append('>').append(facing).append(" blocked by ").append(blockerOf(pieces, candidate));
                }
            }
            LOGGER.debug("Wind Shrine exits: {} candidates, {} outward with a free landing; doors:{}{}", all.size(), outward.size(), report, blocked);
        }
        return best;
    }

    private static String blockerOf(List<StructurePiece> pieces, Candidate candidate) {
        BoundingBox zone = landingZone(candidate.door());
        for (StructurePiece piece : pieces) {
            BoundingBox box = piece.getBoundingBox();
            if (!isCap(piece) && !(box.getXSpan() <= 3 && box.getZSpan() <= 3) && box.intersects(zone)) {
                String name = piece instanceof PoolElementStructurePiece pool ? pool.getElement().toString().replace("Single[Left[goety:wind_shrine/", "").replace("]]", "") : piece.getClass().getSimpleName();
                return name + " " + box + " (zone " + zone + ")";
            }
        }
        return "?";
    }

    private static BoundingBox landingZone(WindShrinePlans.Door door) {
        BlockPos target = door.target();
        Direction facing = door.facing();
        BlockPos near = target.relative(facing, 2);
        BlockPos far = target.relative(facing, LANDING_LENGTH);
        int halfWidth = 2;
        return new BoundingBox(Math.min(near.getX(), far.getX()) - (facing.getAxis() == Direction.Axis.Z ? halfWidth : 0), target.getY() - LANDING_DEPTH, Math.min(near.getZ(), far.getZ()) - (facing.getAxis() == Direction.Axis.X ? halfWidth : 0),
                Math.max(near.getX(), far.getX()) + (facing.getAxis() == Direction.Axis.Z ? halfWidth : 0), target.getY() + 8, Math.max(near.getZ(), far.getZ()) + (facing.getAxis() == Direction.Axis.X ? halfWidth : 0));
    }

    private static boolean landingFree(List<StructurePiece> pieces, Candidate candidate) {
        BoundingBox zone = landingZone(candidate.door());
        for (StructurePiece piece : pieces) {
            if (isCap(piece)) {
                continue;
            }
            BoundingBox box = piece.getBoundingBox();
            if (box.getXSpan() <= 3 && box.getZSpan() <= 3) {
                continue;
            }
            if (box.intersects(zone)) {
                return false;
            }
        }
        return true;
    }

    private static final int LANDING_LENGTH = 14;
    private static final int LANDING_DEPTH = 8;

    private static final double OUTER_SHARE = 0.6D;

    private static double angleOf(Candidate candidate, BlockPos center) {
        return Math.atan2(candidate.door().jigsaw().getZ() - center.getZ(), candidate.door().jigsaw().getX() - center.getX());
    }

    private static double angleBetween(double a, double b) {
        double d = b - a;
        while (d > Math.PI) {
            d -= 2.0D * Math.PI;
        }
        while (d < -Math.PI) {
            d += 2.0D * Math.PI;
        }
        return d;
    }

    private static boolean bearingClash(Candidate[] chosen, Candidate candidate, BlockPos center) {
        double bearing = angleOf(candidate, center);
        for (Candidate other : chosen) {
            if (other != null && Math.abs(angleBetween(bearing, angleOf(other, center))) < MIN_DOOR_BEARING) {
                return true;
            }
        }
        return false;
    }

    private static final double MIN_DOOR_BEARING = Math.toRadians(22.0D);

    private static boolean landingsCross(Candidate[] chosen, Candidate candidate) {
        BoundingBox zone = landingZone(candidate.door());
        for (Candidate other : chosen) {
            if (other != null && landingZone(other.door()).intersects(zone)) {
                return true;
            }
        }
        return false;
    }

    private static boolean taken(Candidate[] chosen, Candidate candidate) {
        for (Candidate other : chosen) {
            if (other != null && other.door().jigsaw().equals(candidate.door().jigsaw())) {
                return true;
            }
        }
        return false;
    }

    private static List<Candidate> candidates(List<StructurePiece> pieces, StructureTemplateManager templates, long seed, BlockPos... ignored) {
        List<Candidate> result = new ArrayList<>();
        BlockPos center = center(pieces);
        for (StructurePiece piece : pieces) {
            if (!(piece instanceof PoolElementStructurePiece poolPiece)) {
                continue;
            }
            BoundingBox box = piece.getBoundingBox();
            if (box.getXSpan() <= 3 && box.getZSpan() <= 3 || box.getXSpan() <= 2 || box.getZSpan() <= 2) {
                continue;
            }
            String name = poolPiece.getElement().toString();
            List<StructureTemplate.StructureBlockInfo> jigsaws = poolPiece.getElement().getShuffledJigsawBlocks(templates, poolPiece.getPosition(), poolPiece.getRotation(), RandomSource.create(seed));
            if (name.contains(CAP)) {
                for (StructureTemplate.StructureBlockInfo info : jigsaws) {
                    Direction facing = JigsawBlock.getFrontFacing(info.state());
                    if (!facing.getAxis().isVertical()) {
                        BlockPos doorPos = info.pos().relative(facing);
                        result.add(new Candidate(new WindShrinePlans.Door(doorPos, facing.getOpposite()), piece, distance(doorPos, center)));
                    }
                }
                continue;
            }
            for (StructureTemplate.StructureBlockInfo info : jigsaws) {
                Direction facing = JigsawBlock.getFrontFacing(info.state());
                if (facing.getAxis().isVertical() || info.nbt() == null || !DOOR_NAMES.contains(info.nbt().getString("name"))) {
                    continue;
                }
                BlockPos target = info.pos().relative(facing);
                if (!occupied(pieces, target)) {
                    result.add(new Candidate(new WindShrinePlans.Door(info.pos(), facing), null, distance(info.pos(), center)));
                }
            }
        }
        return result;
    }

    private static boolean occupied(List<StructurePiece> pieces, BlockPos pos) {
        for (StructurePiece piece : pieces) {
            if (piece.getBoundingBox().isInside(pos)) {
                return true;
            }
        }
        return false;
    }

    public static BlockPos center(List<StructurePiece> pieces) {
        return pieces.isEmpty() ? BlockPos.ZERO : pieces.get(0).getBoundingBox().getCenter();
    }

    private static double distance(BlockPos a, BlockPos b) {
        double dx = a.getX() - b.getX();
        double dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }
}
