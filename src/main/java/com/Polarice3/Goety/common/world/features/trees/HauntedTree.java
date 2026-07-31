package com.Polarice3.Goety.common.world.features.trees;

import com.Polarice3.Goety.common.world.features.ConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class HauntedTree {
    // In 1.21 the third short-constructor feature is flower-only; haunted trees need the normal tree slot so bonemeal works without nearby flowers.
    public static final TreeGrower GROWER = new TreeGrower("haunted_tree", Optional.empty(), Optional.of(ConfiguredFeatures.SAPLING_HAUNTED_TREE), Optional.empty());
}
