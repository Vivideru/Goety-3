package com.Polarice3.Goety.common.loot;


import net.minecraft.core.registries.BuiltInRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class AddItemLootModifier extends LootModifier {

    public static final Supplier<MapCodec<AddItemLootModifier>> CODEC = () ->
            RecordCodecBuilder.mapCodec(inst ->
                    inst.group(
                                    LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(lm -> lm.conditions),
                                    Codec.floatRange(0, Float.MAX_VALUE).fieldOf("chance").forGetter((lm) -> lm.chance),
                                    Codec.BOOL.fieldOf("replace").forGetter((configuration) -> configuration.replace),
                                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(lm -> lm.addedItem),
                                    Codec.intRange(0, Integer.MAX_VALUE).fieldOf("count").forGetter((lm) -> lm.count)
                                    )
                            .apply(inst, AddItemLootModifier::new));

    private final float chance;
    private final boolean replace;
    private final Item addedItem;
    private final int count;

    protected AddItemLootModifier(LootItemCondition[] conditionsIn, float chance, boolean replace, Item addedItemIn, int count) {
        // NeoForge 1.21 global loot modifiers are MapCodec-based and LootModifier now owns condition matching.
        super(conditionsIn);
        this.addedItem = addedItemIn;
        this.replace = replace;
        this.count = count;
        this.chance = chance;
    }

    @NotNull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() < this.chance) {
            if (this.replace) {
                generatedLoot.clear();
            }
            generatedLoot.add(new ItemStack(this.addedItem, this.count));
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
