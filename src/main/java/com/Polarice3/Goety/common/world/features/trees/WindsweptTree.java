package com.Polarice3.Goety.common.world.features.trees;

import com.Polarice3.Goety.common.world.features.ConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class WindsweptTree {
    public static final TreeGrower GROWER = new TreeGrower("windswept_tree", 0.2F, Optional.empty(), Optional.empty(), Optional.of(ConfiguredFeatures.WINDSWEPT_TREE), Optional.of(ConfiguredFeatures.WINDSWEPT_TREE_2), Optional.empty(), Optional.empty());
}
