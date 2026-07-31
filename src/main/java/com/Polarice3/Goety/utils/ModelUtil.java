package com.Polarice3.Goety.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class ModelUtil {
    public static Optional<Vec3> getThirdPersonPlayerHandPosition(Player player, EntityRenderDispatcher renderDispatcher, float yaw, float partialTick, HumanoidArm arm, Vec3 offset) {
        if (player instanceof AbstractClientPlayer clientPlayer && renderDispatcher.getRenderer(clientPlayer) instanceof PlayerRenderer renderer) {
            PoseStack stack = new PoseStack();
            PlayerModel<AbstractClientPlayer> model = renderer.getModel();
            setupPlayerModelProperties(clientPlayer, model);

            // animate the player model
            // copied from LivingEntityRenderer
            model.attackTime = clientPlayer.getAttackAnim(partialTick);
            boolean shouldSit = clientPlayer.isPassenger() && clientPlayer.getVehicle() != null && clientPlayer.getVehicle().shouldRiderSit();
            model.riding = shouldSit;
            float yBodyRot = Mth.rotLerp(partialTick, clientPlayer.yBodyRotO, clientPlayer.yBodyRot);
            float yHeadRot = Mth.rotLerp(partialTick, clientPlayer.yHeadRotO, clientPlayer.yHeadRot);
            float rotDiff = yHeadRot - yBodyRot;
            if (shouldSit && clientPlayer.getVehicle() instanceof LivingEntity livingentity) {
                yBodyRot = Mth.rotLerp(partialTick, livingentity.yBodyRotO, livingentity.yBodyRot);
                rotDiff = yHeadRot - yBodyRot;
                float diff = Mth.wrapDegrees(rotDiff);
                if (diff < -85.0F) {
                    diff = -85.0F;
                }
                if (diff >= 85.0F) {
                    diff = 85.0F;
                }
                yBodyRot = yHeadRot - diff;
                if (diff * diff > 2500.0F) {
                    yBodyRot += diff * 0.2F;
                }
                rotDiff = yHeadRot - yBodyRot;
            }
            float xRot = Mth.lerp(partialTick, clientPlayer.xRotO, clientPlayer.getXRot());
            if (LivingEntityRenderer.isEntityUpsideDown(clientPlayer)) {
                xRot *= -1.0F;
                rotDiff *= -1.0F;
            }
            rotDiff = Mth.wrapDegrees(rotDiff);
            float age = clientPlayer.tickCount + partialTick;
            float walkSpeed = 0.0F;
            float walkPos = 0.0F;
            if (!shouldSit && clientPlayer.isAlive()) {
                walkSpeed = clientPlayer.walkAnimation.speed(partialTick);
                walkPos = clientPlayer.walkAnimation.position(partialTick);
                if (walkSpeed > 1.0F) {
                    walkSpeed = 1.0F;
                }
            }
            model.prepareMobModel(clientPlayer, walkPos, walkSpeed, partialTick);
            model.setupAnim(clientPlayer, walkPos, walkSpeed, age, rotDiff, xRot);

            stack.translate(
                    Mth.lerp(partialTick, player.xo, player.getX()),
                    Mth.lerp(partialTick, player.yo, player.getY()),
                    Mth.lerp(partialTick, player.zo, player.getZ())
            );
            stack.mulPose(new Quaternionf().rotationY((-yaw + 180.0F) * Mth.DEG_TO_RAD));
            stack.scale(-1, -1, 1);
            // PlayerRenderer#scale is protected in 1.21; vanilla's player scale is a fixed 0.9375 factor.
            stack.scale(0.9375F, 0.9375F, 0.9375F);
            stack.translate(0, -1.5f, 0);
            model.translateToHand(arm, stack);

            Vector4f vec = new Vector4f((float) offset.x(), (float) offset.y(), (float) offset.z(), 1).mul(stack.last().pose());
            Vec3 pos = new Vec3(vec.x(), vec.y(), vec.z());
            Vec3 subtract = pos.subtract(player.position());
            return Optional.of(player.position().add(subtract.scale(player.getScale())));
        }
        return Optional.empty();
    }

    private static void setupPlayerModelProperties(AbstractClientPlayer player, PlayerModel<AbstractClientPlayer> model) {
        // PlayerRenderer#setModelProperties became private in 1.21, so mirror vanilla's setup before computing hand transforms.
        if (player.isSpectator()) {
            model.setAllVisible(false);
            model.head.visible = true;
            model.hat.visible = true;
        } else {
            model.setAllVisible(true);
            model.hat.visible = player.isModelPartShown(PlayerModelPart.HAT);
            model.jacket.visible = player.isModelPartShown(PlayerModelPart.JACKET);
            model.leftPants.visible = player.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            model.rightPants.visible = player.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            model.leftSleeve.visible = player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            model.rightSleeve.visible = player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            model.crouching = player.isCrouching();
            HumanoidModel.ArmPose mainArmPose = getArmPose(player, InteractionHand.MAIN_HAND);
            HumanoidModel.ArmPose offArmPose = getArmPose(player, InteractionHand.OFF_HAND);
            if (mainArmPose.isTwoHanded()) {
                offArmPose = player.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
            }

            if (player.getMainArm() == HumanoidArm.RIGHT) {
                model.rightArmPose = mainArmPose;
                model.leftArmPose = offArmPose;
            } else {
                model.rightArmPose = offArmPose;
                model.leftArmPose = mainArmPose;
            }
        }
    }

    private static HumanoidModel.ArmPose getArmPose(AbstractClientPlayer player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        }
        if (player.getUsedItemHand() == hand && player.getUseItemRemainingTicks() > 0) {
            UseAnim useanim = itemstack.getUseAnimation();
            if (useanim == UseAnim.BLOCK) {
                return HumanoidModel.ArmPose.BLOCK;
            }
            if (useanim == UseAnim.BOW) {
                return HumanoidModel.ArmPose.BOW_AND_ARROW;
            }
            if (useanim == UseAnim.SPEAR) {
                return HumanoidModel.ArmPose.THROW_SPEAR;
            }
            if (useanim == UseAnim.CROSSBOW && hand == player.getUsedItemHand()) {
                return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
            }
            if (useanim == UseAnim.SPYGLASS) {
                return HumanoidModel.ArmPose.SPYGLASS;
            }
            if (useanim == UseAnim.TOOT_HORN) {
                return HumanoidModel.ArmPose.TOOT_HORN;
            }
            if (useanim == UseAnim.BRUSH) {
                return HumanoidModel.ArmPose.BRUSH;
            }
        } else if (!player.swinging && itemstack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack)) {
            return HumanoidModel.ArmPose.CROSSBOW_HOLD;
        }
        HumanoidModel.ArmPose neoForgeArmPose = net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.of(itemstack).getArmPose(player, hand, itemstack);
        return neoForgeArmPose != null ? neoForgeArmPose : HumanoidModel.ArmPose.ITEM;
    }

    public static Stream<String> getAllPartNames(ModelPart root) {
        Set<String> names = new LinkedHashSet<>();
        root.visit(new PoseStack(), (pose, path, index, cube) -> {
            for (String segment : path.split("/")) {
                if (!segment.isEmpty()) {
                    names.add(segment);
                }
            }
        });
        return names.stream();
    }

    public static Map<String, ModelPartPose> saveModelSnapshot(List<String> allPartNames, Function<String, Optional<ModelPart>> getter) {
        Map<String, ModelPartPose> snapshot = new HashMap<>();
        for (String name : allPartNames) {
            getter.apply(name).ifPresent(part ->
                    snapshot.put(name, new ModelPartPose(
                            part.x, part.y, part.z,
                            part.xRot, part.yRot, part.zRot,
                            part.xScale, part.yScale, part.zScale,
                            part.visible
                    )));
        }
        return snapshot;
    }

    public static void loadPoseFromSnapshot(Map<String, ModelPartPose> snapshot, Function<String, Optional<ModelPart>> getter) {
        snapshot.forEach((name, pose) ->
                getter.apply(name).ifPresent(part -> {
                    part.x = pose.x();
                    part.y = pose.y();
                    part.z = pose.z();
                    part.xRot = pose.xRot();
                    part.yRot = pose.yRot();
                    part.zRot = pose.zRot();
                    part.xScale = pose.xScale();
                    part.yScale = pose.yScale();
                    part.zScale = pose.zScale();
                    part.visible = pose.visible();
                }));
    }
}
