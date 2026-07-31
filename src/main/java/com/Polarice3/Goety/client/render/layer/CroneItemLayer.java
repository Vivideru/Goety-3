package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.client.render.model.CroneModel;
import com.Polarice3.Goety.common.entities.hostile.cultists.Crone;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.BrewUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.component.DataComponents;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;
import java.util.Optional;

public class CroneItemLayer<T extends Crone> extends CrossedArmsItemLayer<T, CroneModel<T>> {
   private final ItemInHandRenderer itemInHandRenderer;

   public CroneItemLayer(RenderLayerParent<T, CroneModel<T>> p_234926_, ItemInHandRenderer p_234927_) {
      super(p_234926_, p_234927_);
      this.itemInHandRenderer = p_234927_;
   }

   public void render(PoseStack p_117685_, MultiBufferSource p_117686_, int p_117687_, T p_117688_, float p_117689_, float p_117690_, float p_117691_, float p_117692_, float p_117693_, float p_117694_) {
      ItemStack itemstack = this.getVisibleItem(p_117688_);
      if (!p_117688_.isDrinkingPotion() && (itemstack.is(Items.POTION) || itemstack.is(ModItems.BREW.get()))) {
         return;
      }
      ItemStack renderStack = this.getRenderItem(itemstack, p_117688_);
      p_117685_.pushPose();
      if (itemstack.is(Items.POTION) || itemstack.is(ModItems.BREW.get())) {
         this.getParentModel().getHead().translateAndRotate(p_117685_);
         this.getParentModel().getNose().translateAndRotate(p_117685_);
         p_117685_.translate(0.0625D, 0.25D, 0.0D);
         p_117685_.mulPose(Axis.ZP.rotationDegrees(180.0F));
         p_117685_.mulPose(Axis.XP.rotationDegrees(140.0F));
         p_117685_.mulPose(Axis.ZP.rotationDegrees(10.0F));
         p_117685_.translate(0.0D, (double)-0.4F, (double)0.4F);
      }

      // The Crone keeps her crossed-arm mesh while drinking, so the bottle needs to sit farther forward than the vanilla witch item pose.
      p_117685_.translate(0.0F, 0.4F, -0.18F);
      p_117685_.mulPose(Axis.XP.rotationDegrees(180.0F));
      if (this.isBrewFallback(itemstack, p_117688_)) {
         // The vanilla potion fallback uses a flat inventory-facing model, so rotate it toward the Crone's front after the witch drinking pose.
         p_117685_.mulPose(Axis.YP.rotationDegrees(180.0F));
         // After the fallback bottle is turned to face front, positive local Z keeps it clear of the Crone's face geometry.
         p_117685_.translate(0.0F, 0.0F, 0.16F);
      }
      this.itemInHandRenderer.renderItem(p_117688_, renderStack, ItemDisplayContext.GROUND, false, p_117685_, p_117686_, p_117687_);
      p_117685_.popPose();
   }

   private ItemStack getVisibleItem(T crone) {
      ItemStack offhand = crone.getOffhandItem();
      // Crones drink from the offhand in 1.21, while CrossedArmsItemLayer only renders the main hand.
      if (offhand.is(Items.POTION) || offhand.is(ModItems.BREW.get())) {
         return offhand;
      }
      return crone.getMainHandItem();
   }

   private ItemStack getRenderItem(ItemStack itemstack, T crone) {
      // Crone drinks custom brews; render a vanilla bottle fallback so the drinking animation is visible even if the brew model is not.
      if (this.isBrewFallback(itemstack, crone)) {
         ItemStack renderStack = new ItemStack(Items.POTION);
         if (itemstack.is(ModItems.BREW.get())) {
            // The fallback must carry the brew color because vanilla potion item rendering tints only from POTION_CONTENTS.
            renderStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(BrewUtils.getColor(itemstack)), List.of()));
         }
         return renderStack;
      }
      return itemstack;
   }

   private boolean isBrewFallback(ItemStack itemstack, T crone) {
      return itemstack.is(ModItems.BREW.get()) || itemstack.isEmpty() && crone.isDrinkingPotion();
   }
}
