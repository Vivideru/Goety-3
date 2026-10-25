package com.Polarice3.Goety.common.world.features.trees;

import com.Polarice3.Goety.common.world.features.ConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class MarshTree {
    public static final TreeGrower GROWER = new TreeGrower("marsh_tree", 0.1F, Optional.empty(), Optional.empty(), Optional.of(ConfiguredFeatures.SAPLING_MARSH_TREE), Optional.of(ConfiguredFeatures.SAPLING_FANCY_MARSH_TREE), Optional.empty(), Optional.empty());
}
