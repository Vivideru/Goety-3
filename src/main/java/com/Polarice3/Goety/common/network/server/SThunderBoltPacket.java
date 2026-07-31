package com.Polarice3.Goety.common.network.server;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import com.Polarice3.Goety.common.network.ModNetwork;

import java.util.function.Supplier;

public class SThunderBoltPacket {
    private final double x;
    private final double y;
    private final double z;
    private final double x2;
    private final double y2;
    private final double z2;
    private final float red;
    private final float green;
    private final float blue;
    private final int lifespan;

    public SThunderBoltPacket(double x, double y, double z, double x2, double y2, double z2, float red, float green, float blue, int lifespan){
        this.x = x;
        this.y = y;
        this.z = z;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.lifespan = lifespan;
    }

    public SThunderBoltPacket(Vec3 start, Vec3 end, int lifespan){
        this.x = start.x;
        this.y = start.y;
        this.z = start.z;
        this.x2 = end.x;
        this.y2 = end.y;
        this.z2 = end.z;
        ColorUtil colorUtil = new ColorUtil(0xb1ebdc);
        this.red = colorUtil.red();
        this.green = colorUtil.green();
        this.blue = colorUtil.blue();
        this.lifespan = lifespan;
    }

    public SThunderBoltPacket(Vec3 start, Vec3 end, ColorUtil colorUtil, int lifespan){
        this.x = start.x;
        this.y = start.y;
        this.z = start.z;
        this.x2 = end.x;
        this.y2 = end.y;
        this.z2 = end.z;
        this.red = colorUtil.red();
        this.green = colorUtil.green();
        this.blue = colorUtil.blue();
        this.lifespan = lifespan;
    }

    public static void encode(SThunderBoltPacket packet, FriendlyByteBuf buffer) {
        buffer.writeDouble(packet.x);
        buffer.writeDouble(packet.y);
        buffer.writeDouble(packet.z);
        buffer.writeDouble(packet.x2);
        buffer.writeDouble(packet.y2);
        buffer.writeDouble(packet.z2);
        buffer.writeFloat(packet.red);
        buffer.writeFloat(packet.green);
        buffer.writeFloat(packet.blue);
        buffer.writeInt(packet.lifespan);
    }

    public static SThunderBoltPacket decode(FriendlyByteBuf buffer) {
        return new SThunderBoltPacket(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt());
    }

    public static void consume(SThunderBoltPacket packet, Supplier<ModNetwork.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Vec3 start = new Vec3(packet.x, packet.y, packet.z);
            Vec3 end = new Vec3(packet.x2, packet.y2, packet.z2);
            ColorUtil colorUtil = new ColorUtil(packet.red, packet.green, packet.blue);
            Goety.PROXY.thunderBolt(start, end, colorUtil, packet.lifespan);
        });
        ctx.get().setPacketHandled(true);
    }
}
