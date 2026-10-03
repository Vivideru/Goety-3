package com.Vivideru.Goety.common.world.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class WindShrinePieceProfiles {
    public record Profile(int chain, int closingHeight, int closingXSpan, int closingZSpan) {
        public static final Profile CLOSED = new Profile(0, 0, 0, 0);

        public boolean open() {
            return this.chain > 0;
        }
    }

    private static final Map<String, Profile> CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Integer> HORIZONTAL_EXITS = new ConcurrentHashMap<>();
    private static final Map<String, Integer> FOOTPRINTS = new ConcurrentHashMap<>();

    private WindShrinePieceProfiles() {
    }

    public static int horizontalExits(StructurePoolElement element, StructureTemplateManager templates) {
        return HORIZONTAL_EXITS.computeIfAbsent(element.toString(), key -> {
            int count = 0;
            for (StructureTemplate.StructureBlockInfo info : element.getShuffledJigsawBlocks(templates, BlockPos.ZERO, Rotation.NONE, RandomSource.create(0L))) {
                if (!JigsawBlock.getFrontFacing(info.state()).getAxis().isVertical()) {
                    count++;
                }
            }
            return count;
        });
    }

    public static int footprint(StructurePoolElement element, StructureTemplateManager templates) {
        return FOOTPRINTS.computeIfAbsent(element.toString(), key -> {
            BoundingBox box = element.getBoundingBox(templates, BlockPos.ZERO, Rotation.NONE);
            return Math.max(box.getXSpan(), box.getZSpan());
        });
    }

    public static Profile of(StructurePoolElement element, Registry<StructureTemplatePool> pools, StructureTemplateManager templates) {
        String key = element.toString();
        Profile cached = CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        Profile profile = compute(element, pools, templates, new HashSet<>());
        CACHE.put(key, profile);
        return profile;
    }

    private static Profile compute(StructurePoolElement element, Registry<StructureTemplatePool> pools, StructureTemplateManager templates, Set<String> visiting) {
        String key = element.toString();
        Profile cached = CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        visiting.add(key);
        int chain = 0;
        int height = 0;
        int xSpan = 0;
        int zSpan = 0;
        for (StructureTemplate.StructureBlockInfo info : element.getShuffledJigsawBlocks(templates, BlockPos.ZERO, Rotation.NONE, RandomSource.create(0L))) {
            if (info.nbt() == null || JigsawBlock.getFrontFacing(info.state()) != Direction.UP) {
                continue;
            }
            Profile closer = shortestCloser(info.nbt().getString("target"), ResourceLocation.tryParse(info.nbt().getString("pool")), pools, templates, visiting);
            if (closer != null) {
                chain = Math.max(chain, closer.chain());
                height = Math.max(height, closer.closingHeight());
                xSpan = Math.max(xSpan, closer.closingXSpan());
                zSpan = Math.max(zSpan, closer.closingZSpan());
            }
        }
        visiting.remove(key);
        return chain == 0 ? Profile.CLOSED : new Profile(chain, height, xSpan, zSpan);
    }

    private static Profile shortestCloser(String target, ResourceLocation poolId, Registry<StructureTemplatePool> pools, StructureTemplateManager templates, Set<String> visiting) {
        if (poolId == null) {
            return null;
        }
        StructureTemplatePool pool = pools.get(poolId);
        if (pool == null) {
            return null;
        }
        Profile best = null;
        Set<String> seen = new HashSet<>();
        StructureTemplatePool current = pool;
        for (int hop = 0; hop < 3 && current != null; hop++) {
            for (StructurePoolElement candidate : current.getShuffledTemplates(RandomSource.create(0L))) {
                if (candidate == EmptyPoolElement.INSTANCE || !seen.add(candidate.toString()) || visiting.contains(candidate.toString()) || !attaches(candidate, target, templates)) {
                    continue;
                }
                BoundingBox box = candidate.getBoundingBox(templates, BlockPos.ZERO, Rotation.NONE);
                Profile above = compute(candidate, pools, templates, visiting);
                Profile total = new Profile(1 + above.chain(), box.getYSpan() + above.closingHeight(), Math.max(box.getXSpan(), above.closingXSpan()), Math.max(box.getZSpan(), above.closingZSpan()));
                if (best == null || total.chain() < best.chain()) {
                    best = total;
                } else if (total.chain() == best.chain()) {
                    best = new Profile(best.chain(), Math.max(best.closingHeight(), total.closingHeight()), Math.max(best.closingXSpan(), total.closingXSpan()), Math.max(best.closingZSpan(), total.closingZSpan()));
                }
            }
            StructureTemplatePool fallback = current.getFallback().value();
            current = fallback == current ? null : fallback;
        }
        return best;
    }

    private static boolean attaches(StructurePoolElement candidate, String target, StructureTemplateManager templates) {
        for (StructureTemplate.StructureBlockInfo info : candidate.getShuffledJigsawBlocks(templates, BlockPos.ZERO, Rotation.NONE, RandomSource.create(0L))) {
            if (info.nbt() != null && JigsawBlock.getFrontFacing(info.state()) == Direction.DOWN && target.equals(info.nbt().getString("name"))) {
                return true;
            }
        }
        return false;
    }
}
