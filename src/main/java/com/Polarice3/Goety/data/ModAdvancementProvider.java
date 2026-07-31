package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.AdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AdvancementProvider {

    public ModAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new ModAdvancementGenerator()));
    }

    public static class ModAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {

        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
            AdvancementHolder advancement = Advancement.Builder.advancement().build(Goety.location("goety/craft_empty_focus"));
            addFocuses(Advancement.Builder.advancement()).parent(advancement).display(
                            ModItems.FOCUS_PACK.get(),
                            Component.translatable("advancements.goety.craft_all_focus.title"),
                            Component.translatable("advancements.goety.craft_all_focus.description"),
                            null, AdvancementType.CHALLENGE, true, true, false)
                    .rewards(AdvancementRewards.Builder.experience(100))
                    .save(saver, "goety:goety/craft_all_focus");
        }
    }

    private static Advancement.Builder addFocuses(Advancement.Builder p_248814_) {
        ModItems.ITEMS.getEntries().stream().map(holder -> holder.get()).forEach(item ->
        {
            if (item instanceof MagicFocus){
                p_248814_.addCriterion(item.getDescriptionId(), InventoryChangeTrigger.TriggerInstance.hasItems(item));
            }
        });

        return p_248814_;
    }
}
