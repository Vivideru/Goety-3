package com.Vivideru.Goety.client.events;

import com.Polarice3.Goety.Goety;
import com.Vivideru.Goety.common.entities.ally.Warg;
import net.minecraft.client.model.PlayerModel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

@EventBusSubscriber(modid = Goety.MOD_ID, value = Dist.CLIENT)
public final class WargClientEvents {
    private WargClientEvents() {
    }

    @SubscribeEvent
    public static void poseWargRider(RenderPlayerEvent.Pre event) {
        if (!(event.getEntity().getVehicle() instanceof Warg)) {
            return;
        }
        PlayerModel<?> model = event.getRenderer().getModel();
        model.rightLeg.xRot = 0.0F;
        model.rightLeg.yRot = 0.0F;
        model.rightLeg.zRot = 0.12F;
        model.leftLeg.xRot = 0.0F;
        model.leftLeg.yRot = 0.0F;
        model.leftLeg.zRot = -0.12F;
    }
}
