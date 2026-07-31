package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.Polarice3.Goety.common.network.ModNetwork;

import java.util.function.Supplier;

public class SRepositionPacket {
    public int mob;
    public double x;
    public double y;
    public double z;

    public SRepositionPacket(int id, double x, double y, double z) {
        this.mob = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public SRepositionPacket(int id, Vec3 vec3) {
        this.mob = id;
        this.x = vec3.x;
        this.y = vec3.y;
        this.z = vec3.z;
    }

    public static void encode(SRepositionPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.mob);
        buffer.writeDouble(packet.x);
        buffer.writeDouble(packet.y);
        buffer.writeDouble(packet.z);
    }

    public static SRepositionPacket decode(FriendlyByteBuf buffer) {
        return new SRepositionPacket(buffer.readInt(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public static void consume(SRepositionPacket packet, Supplier<ModNetwork.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level level = Goety.PROXY.getLevel();
            if (level instanceof ClientLevel clientWorld) {
                Entity entity = clientWorld.getEntity(packet.mob);
                if (entity != null) {
                    entity.setPos(packet.x, packet.y, packet.z);
                    entity.xo = packet.x;
                    entity.yo = packet.y;
                    entity.zo = packet.z;
                    entity.xOld = packet.x;
                    entity.yOld = packet.y;
                    entity.zOld = packet.z;
                    entity.setDeltaMovement(Vec3.ZERO);
                    if (entity instanceof LocalPlayer) {
                        return;
                    }
                    if (entity instanceof LivingEntity living) {
                        living.lerpTo(packet.x, packet.y, packet.z, entity.getYRot(), entity.getXRot(), 0);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
