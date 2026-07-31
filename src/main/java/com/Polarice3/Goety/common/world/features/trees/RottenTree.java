package com.Polarice3.Goety.common.world.features.trees;

import com.Polarice3.Goety.common.world.features.ConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class RottenTree {
    public static final TreeGrower GROWER = new TreeGrower("rotten_tree", 0.1F, Optional.empty(), Optional.empty(), Optional.of(ConfiguredFeatures.SAPLING_ROTTEN_TREE), Optional.of(ConfiguredFeatures.SAPLING_FANCY_ROTTEN_TREE), Optional.empty(), Optional.empty());
}
