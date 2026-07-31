package com.Polarice3.Goety.data;


import net.minecraft.core.registries.BuiltInRegistries;
import com.Polarice3.Goety.Goety;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Goety.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (Item item : BuiltInRegistries.ITEM) {
            if (BuiltInRegistries.ITEM.getKey(item) != null) {
                ResourceLocation resourceLocation = BuiltInRegistries.ITEM.getKey(item);
                if (resourceLocation != null) {
                    if (item instanceof SpawnEggItem && resourceLocation.getNamespace().equals(Goety.MOD_ID)) {
                        getBuilder(resourceLocation.getPath())
                                .parent(getExistingFile(ResourceLocation.parse("item/template_spawn_egg")));
                    }
                }
            }
        }
    }
}
