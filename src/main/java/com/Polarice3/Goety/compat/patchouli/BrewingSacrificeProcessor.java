package com.Polarice3.Goety.compat.patchouli;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.common.effects.brew.BrewEffects;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class BrewingSacrificeProcessor implements IComponentProcessor {
    protected BrewEffect brewEffect;
    private String extraText = "";

    @Override
    public void setup(Level level, IVariableProvider variables) {
        String effectId = variables.get("recipe", level.registryAccess()).asString();
        this.brewEffect = new BrewEffects().getBrewEffect(effectId);
        if (variables.has("text")) {
            this.extraText = variables.get("text", level.registryAccess()).asString();
        }
    }

    @Override
    public IVariable process(Level level, String key) {
        if (this.brewEffect == null)
            return IVariable.empty();

        if (key.startsWith("input")) {
            ItemStack itemStack;
            EntityType<?> entityType = new BrewEffects().getSacrificeFromEffect(this.brewEffect.getEffectID());
            if (entityType == null) {
                // Patchouli may request a sacrifice page for effects that only have a catalyst in 1.21 data; keep the book loadable.
                ItemStack catalyst = new BrewEffects().getCatalystFromEffect(this.brewEffect.getEffectID());
                return catalyst != null ? IVariable.from(catalyst, level.registryAccess()) : IVariable.empty();
            }
            Item item = SpawnEggItem.byId(entityType);
            if (item != null){
                itemStack = new ItemStack(item);
            } else {
                itemStack = new ItemStack(ModItems.JEI_DUMMY_REQUIRE_SACRIFICE.get());
                // ItemStack custom names are stored as data components in 1.21.
                itemStack.set(DataComponents.CUSTOM_NAME, entityType.getDescription());
            }
            return IVariable.from(itemStack, level.registryAccess());
        }

        if (key.startsWith("capacityExtra")) {
            if (this.brewEffect.getCapacityExtra() > 0) {
                return IVariable.wrap(I18n.get("jei.goety.capacityUse", this.brewEffect.getCapacityExtra()), level.registryAccess());
            }
        }

        if (key.startsWith("soulCost")) {
            return IVariable.wrap(I18n.get("jei.goety.single.soulcost", this.brewEffect.getSoulCost()), level.registryAccess());
        }

        if (key.startsWith("duration")) {
            if (this.brewEffect.getDuration() > 40) {
                return IVariable.wrap(I18n.get("jei.goety.single.duration", StringUtil.formatTickDuration(this.brewEffect.getDuration(), 20.0F)), level.registryAccess());
            } else {
                return IVariable.wrap(I18n.get("jei.goety.instant.duration"), level.registryAccess());
            }
        }

        if (key.startsWith("linger")){
            if (!this.brewEffect.canLinger()){
                return IVariable.wrap(I18n.get("jei.goety.linger"), level.registryAccess());
            }
        }

        if (key.startsWith("output")) {
            return IVariable.wrap(this.brewEffect.getDescriptionId(), level.registryAccess());
        }

        if (key.startsWith("text")){
            return IVariable.wrap(this.extraText, level.registryAccess());
        }

        return IVariable.empty();
    }
}
