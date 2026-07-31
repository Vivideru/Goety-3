package com.Polarice3.Goety.common.network;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.capabilities.lichdom.LichUpdatePacket;
import com.Polarice3.Goety.common.capabilities.misc.MiscCapUpdatePacket;
import com.Polarice3.Goety.common.capabilities.soulenergy.SEUpdatePacket;
import com.Polarice3.Goety.common.capabilities.witchbarter.WBUpdatePacket;
import com.Polarice3.Goety.common.network.client.*;
import com.Polarice3.Goety.common.network.client.brew.CBrewBagKeyPacket;
import com.Polarice3.Goety.common.network.client.brew.CThrowBrewKeyPacket;
import com.Polarice3.Goety.common.network.client.focus.CAddFocusToBagPacket;
import com.Polarice3.Goety.common.network.client.focus.CAddFocusToInventoryPacket;
import com.Polarice3.Goety.common.network.client.focus.CSwapFocusPacket;
import com.Polarice3.Goety.common.network.client.focus.CSwapFocusTwoPacket;
import com.Polarice3.Goety.common.network.server.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModNetwork {
    public static NetworkChannel INSTANCE = new NetworkChannel();
    private static final CustomPacketPayload.Type<GoetyPayload> CLIENTBOUND_TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "clientbound"));
    private static final CustomPacketPayload.Type<GoetyPayload> SERVERBOUND_TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "serverbound"));
    private static final StreamCodec<RegistryFriendlyByteBuf, GoetyPayload> CLIENTBOUND_CODEC = codec(CLIENTBOUND_TYPE, NetworkDirection.PLAY_TO_CLIENT);
    private static final StreamCodec<RegistryFriendlyByteBuf, GoetyPayload> SERVERBOUND_CODEC = codec(SERVERBOUND_TYPE, NetworkDirection.PLAY_TO_SERVER);
    private static int id = 0;
    private static boolean initialized;

    public static int nextID() {
        return id++;
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        init();
        var registrar = event.registrar("1.0");
        registrar.playToClient(CLIENTBOUND_TYPE, CLIENTBOUND_CODEC, (payload, context) -> handle(payload, context, NetworkDirection.PLAY_TO_CLIENT));
        registrar.playToServer(SERVERBOUND_TYPE, SERVERBOUND_CODEC, (payload, context) -> handle(payload, context, NetworkDirection.PLAY_TO_SERVER));
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        INSTANCE.registerMessage(nextID(), EntityUpdatePacket.class, EntityUpdatePacket::encode, EntityUpdatePacket::decode, EntityUpdatePacket::consume);
        INSTANCE.registerMessage(nextID(), SEUpdatePacket.class, SEUpdatePacket::encode, SEUpdatePacket::decode, SEUpdatePacket::consume);
        INSTANCE.registerMessage(nextID(), LichUpdatePacket.class, LichUpdatePacket::encode, LichUpdatePacket::decode, LichUpdatePacket::consume);
        INSTANCE.registerMessage(nextID(), MiscCapUpdatePacket.class, MiscCapUpdatePacket::encode, MiscCapUpdatePacket::decode, MiscCapUpdatePacket::consume);
        INSTANCE.registerMessage(nextID(), CWandKeyPacket.class, CWandKeyPacket::encode, CWandKeyPacket::decode, CWandKeyPacket::consume);
        INSTANCE.registerMessage(nextID(), CBagKeyPacket.class, CBagKeyPacket::encode, CBagKeyPacket::decode, CBagKeyPacket::consume);
        INSTANCE.registerMessage(nextID(), CStopAttackPacket.class, CStopAttackPacket::encode, CStopAttackPacket::decode, CStopAttackPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CWitchRobePacket.class, CWitchRobePacket::encode, CWitchRobePacket::decode, CWitchRobePacket::consume);
        INSTANCE.registerMessage(nextID(), CAddWitchFuelKeyPacket.class, CAddWitchFuelKeyPacket::encode, CAddWitchFuelKeyPacket::decode, CAddWitchFuelKeyPacket::consume);
        INSTANCE.registerMessage(nextID(), CAddCatalystKeyPacket.class, CAddCatalystKeyPacket::encode, CAddCatalystKeyPacket::decode, CAddCatalystKeyPacket::consume);
        INSTANCE.registerMessage(nextID(), CExtractPotionKeyPacket.class, CExtractPotionKeyPacket::encode, CExtractPotionKeyPacket::decode, CExtractPotionKeyPacket::consume);
        INSTANCE.registerMessage(nextID(), CRavagerRoarPacket.class, CRavagerRoarPacket::encode, CRavagerRoarPacket::decode, CRavagerRoarPacket::consume);
        INSTANCE.registerMessage(nextID(), CAutoRideablePacket.class, CAutoRideablePacket::encode, CAutoRideablePacket::decode, CAutoRideablePacket::consume);
        INSTANCE.registerMessage(nextID(), CScytheStrikePacket.class, CScytheStrikePacket::encode, CScytheStrikePacket::decode, CScytheStrikePacket::consume);
        INSTANCE.registerMessage(nextID(), CBoEStrikePacket.class, CBoEStrikePacket::encode, CBoEStrikePacket::decode, CBoEStrikePacket::consume);
        INSTANCE.registerMessage(nextID(), CLichKissPacket.class, CLichKissPacket::encode, CLichKissPacket::decode, CLichKissPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CMagnetPacket.class, CMagnetPacket::encode, CMagnetPacket::decode, CMagnetPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CSetLichMode.class, CSetLichMode::encode, CSetLichMode::decode, CSetLichMode::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CSetLichNightVisionMode.class, CSetLichNightVisionMode::encode, CSetLichNightVisionMode::decode, CSetLichNightVisionMode::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CBeamPacket.class, CBeamPacket::encode, CBeamPacket::decode, CBeamPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CTargetPlayerPacket.class, CTargetPlayerPacket::encode, CTargetPlayerPacket::decode, CTargetPlayerPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CSwapFocusPacket.class, CSwapFocusPacket::encode, CSwapFocusPacket::decode, CSwapFocusPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CSwapFocusTwoPacket.class, CSwapFocusTwoPacket::encode, CSwapFocusTwoPacket::decode, CSwapFocusTwoPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CAddFocusToBagPacket.class, CAddFocusToBagPacket::encode, CAddFocusToBagPacket::decode, CAddFocusToBagPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CAddFocusToInventoryPacket.class, CAddFocusToInventoryPacket::encode, CAddFocusToInventoryPacket::decode, CAddFocusToInventoryPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CBrewBagKeyPacket.class, CBrewBagKeyPacket::encode, CBrewBagKeyPacket::decode, CBrewBagKeyPacket::consume);
        INSTANCE.registerMessage(nextID(), CThrowBrewKeyPacket.class, CThrowBrewKeyPacket::encode, CThrowBrewKeyPacket::decode, CThrowBrewKeyPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CMultiJumpPacket.class, CMultiJumpPacket::encode, CMultiJumpPacket::decode, CMultiJumpPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CSetDeltaMovement.class, CSetDeltaMovement::encode, CSetDeltaMovement::decode, CSetDeltaMovement::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CTramplerPacket.class, CTramplerPacket::encode, CTramplerPacket::decode, CTramplerPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CBroomCollisionPacket.class, CBroomCollisionPacket::encode, CBroomCollisionPacket::decode, CBroomCollisionPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CPrisonerMinePacket.class, CPrisonerMinePacket::encode, CPrisonerMinePacket::decode, CPrisonerMinePacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CActivateCurioKeyPacket.class, CActivateCurioKeyPacket::encode, CActivateCurioKeyPacket::decode, CActivateCurioKeyPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CDismissServantsPacket.class, CDismissServantsPacket::encode, CDismissServantsPacket::decode, CDismissServantsPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), TotemDeathPacket.class, TotemDeathPacket::encode, TotemDeathPacket::decode, TotemDeathPacket::consume);
        INSTANCE.registerMessage(nextID(), SPlayPlayerSoundPacket.class, SPlayPlayerSoundPacket::encode, SPlayPlayerSoundPacket::decode, SPlayPlayerSoundPacket::consume);
        INSTANCE.registerMessage(nextID(), SPlayWorldSoundPacket.class, SPlayWorldSoundPacket::encode, SPlayWorldSoundPacket::decode, SPlayWorldSoundPacket::consume);
        INSTANCE.registerMessage(nextID(), SPlayEntitySoundPacket.class, SPlayEntitySoundPacket::encode, SPlayEntitySoundPacket::decode, SPlayEntitySoundPacket::consume);
        INSTANCE.registerMessage(nextID(), SPlayFollowSoundPacket.class, SPlayFollowSoundPacket::encode, SPlayFollowSoundPacket::decode, SPlayFollowSoundPacket::consume);
        INSTANCE.registerMessage(nextID(), SPlayLoopSoundPacket.class, SPlayLoopSoundPacket::encode, SPlayLoopSoundPacket::decode, SPlayLoopSoundPacket::consume);
        INSTANCE.registerMessage(nextID(), SPlayEffectLoopSoundPacket.class, SPlayEffectLoopSoundPacket::encode, SPlayEffectLoopSoundPacket::decode, SPlayEffectLoopSoundPacket::consume);
        INSTANCE.registerMessage(nextID(), SFungusExplosionPacket.class, SFungusExplosionPacket::encode, SFungusExplosionPacket::decode, SFungusExplosionPacket::consume);
        INSTANCE.registerMessage(nextID(), SLootingExplosionPacket.class, SLootingExplosionPacket::encode, SLootingExplosionPacket::decode, SLootingExplosionPacket::consume);
        INSTANCE.registerMessage(nextID(), SApostleSmitePacket.class, SApostleSmitePacket::encode, SApostleSmitePacket::decode, SApostleSmitePacket::consume);
        INSTANCE.registerMessage(nextID(), SRCGlowPacket.class, SRCGlowPacket::encode, SRCGlowPacket::decode, SRCGlowPacket::consume);
        INSTANCE.registerMessage(nextID(), STentacleRangePacket.class, STentacleRangePacket::encode, STentacleRangePacket::decode, STentacleRangePacket::consume);
        INSTANCE.registerMessage(nextID(), SSoulExplodePacket.class, SSoulExplodePacket::encode, SSoulExplodePacket::decode, SSoulExplodePacket::consume);
        INSTANCE.registerMessage(nextID(), SAddBrewParticlesPacket.class, SAddBrewParticlesPacket::encode, SAddBrewParticlesPacket::decode, SAddBrewParticlesPacket::consume);
        INSTANCE.registerMessage(nextID(), SLightningPacket.class, SLightningPacket::encode, SLightningPacket::decode, SLightningPacket::consume);
        INSTANCE.registerMessage(nextID(), SThunderBoltPacket.class, SThunderBoltPacket::encode, SThunderBoltPacket::decode, SThunderBoltPacket::consume);
        INSTANCE.registerMessage(nextID(), SLightningBoltPacket.class, SLightningBoltPacket::encode, SLightningBoltPacket::decode, SLightningBoltPacket::consume);
        INSTANCE.registerMessage(nextID(), SSetPlayerOwnerPacket.class, SSetPlayerOwnerPacket::encode, SSetPlayerOwnerPacket::decode, SSetPlayerOwnerPacket::consume);
        INSTANCE.registerMessage(nextID(), SUpdateBossBar.class, SUpdateBossBar::encode, SUpdateBossBar::decode, SUpdateBossBar::consume);
        INSTANCE.registerMessage(nextID(), SFocusCooldownPacket.class, SFocusCooldownPacket::encode, SFocusCooldownPacket::decode, SFocusCooldownPacket::consume);
        INSTANCE.registerMessage(nextID(), SFocusSpecificCooldownPacket.class, SFocusSpecificCooldownPacket::encode, SFocusSpecificCooldownPacket::decode, SFocusSpecificCooldownPacket::consume);
        INSTANCE.registerMessage(nextID(), SRemoveEffectPacket.class, SRemoveEffectPacket::encode, SRemoveEffectPacket::decode, SRemoveEffectPacket::consume);
        INSTANCE.registerMessage(nextID(), SPurifyEffectPacket.class, SPurifyEffectPacket::encode, SPurifyEffectPacket::decode, SPurifyEffectPacket::consume);
        INSTANCE.registerMessage(nextID(), SPlayerRotationPacket.class, SPlayerRotationPacket::encode, SPlayerRotationPacket::decode, SPlayerRotationPacket::consume);
        INSTANCE.registerMessage(nextID(), SRepositionPacket.class, SRepositionPacket::encode, SRepositionPacket::decode, SRepositionPacket::consume);
        INSTANCE.registerMessage(nextID(), SInstaLookPacket.class, SInstaLookPacket::encode, SInstaLookPacket::decode, SInstaLookPacket::consume);
        INSTANCE.registerMessage(nextID(), WBUpdatePacket.class, WBUpdatePacket::encode, WBUpdatePacket::decode, WBUpdatePacket::consume);
        INSTANCE.registerMessage(nextID(), SStaffParticlePacket.class, SStaffParticlePacket::encode, SStaffParticlePacket::decode, SStaffParticlePacket::consume);
    }

    public static <MSG> void sendTo(Player player, MSG msg) {
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, INSTANCE.payloadFor(NetworkDirection.PLAY_TO_CLIENT, msg));
        }
    }

    public static <MSG> void sendToServer(MSG msg) {
        PacketDistributor.sendToServer(INSTANCE.payloadFor(NetworkDirection.PLAY_TO_SERVER, msg));
    }

    public static <MSG> void sentToTrackingChunk(LevelChunk chunk, MSG msg) {
        if (chunk.getLevel() instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersTrackingChunk(serverLevel, chunk.getPos(), INSTANCE.payloadFor(NetworkDirection.PLAY_TO_CLIENT, msg));
        }
    }

    public static <MSG> void sentToTrackingEntity(Entity entity, MSG msg) {
        PacketDistributor.sendToPlayersTrackingEntity(entity, INSTANCE.payloadFor(NetworkDirection.PLAY_TO_CLIENT, msg));
    }

    public static <MSG> void sentToTrackingEntityAndPlayer(Entity entity, MSG msg) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, INSTANCE.payloadFor(NetworkDirection.PLAY_TO_CLIENT, msg));
    }

    public static <MSG> void sendToALL(MSG msg) {
        PacketDistributor.sendToAllPlayers(INSTANCE.payloadFor(NetworkDirection.PLAY_TO_CLIENT, msg));
    }

    public static <MSG> void sendToClient(ServerPlayer player, MSG msg) {
        PacketDistributor.sendToPlayer(player, INSTANCE.payloadFor(NetworkDirection.PLAY_TO_CLIENT, msg));
    }

    private static StreamCodec<RegistryFriendlyByteBuf, GoetyPayload> codec(CustomPacketPayload.Type<GoetyPayload> type, NetworkDirection direction) {
        return StreamCodec.of(
                (buffer, payload) -> {
                    buffer.writeVarInt(payload.packetId);
                    INSTANCE.encode(payload.packetId, payload.message, buffer);
                },
                buffer -> {
                    int packetId = buffer.readVarInt();
                    return new GoetyPayload(type, packetId, INSTANCE.decode(direction, packetId, buffer));
                });
    }

    private static void handle(GoetyPayload payload, IPayloadContext payloadContext, NetworkDirection direction) {
        INSTANCE.consume(direction, payload.packetId, payload.message, new Context(payloadContext, direction));
    }

    public enum NetworkDirection {
        PLAY_TO_CLIENT(LogicalSide.CLIENT),
        PLAY_TO_SERVER(LogicalSide.SERVER);

        private final LogicalSide receptionSide;

        NetworkDirection(LogicalSide receptionSide) {
            this.receptionSide = receptionSide;
        }

        public LogicalSide getReceptionSide() {
            return this.receptionSide;
        }
    }

    public static class Context {
        private final IPayloadContext context;
        private final NetworkDirection direction;

        private Context(IPayloadContext context, NetworkDirection direction) {
            this.context = context;
            this.direction = direction;
        }

        public CompletableFuture<Void> enqueueWork(Runnable task) {
            return this.context.enqueueWork(task);
        }

        @Nullable
        public ServerPlayer getSender() {
            Player player = this.context.player();
            return player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
        }

        public NetworkDirection getDirection() {
            return this.direction;
        }

        public void setPacketHandled(boolean handled) {
        }
    }

    public static class NetworkChannel {
        private final Map<Integer, MessageSpec<?>> clientboundMessages = new HashMap<>();
        private final Map<Integer, MessageSpec<?>> serverboundMessages = new HashMap<>();
        private final Map<Class<?>, Integer> clientboundIds = new HashMap<>();
        private final Map<Class<?>, Integer> serverboundIds = new HashMap<>();

        public <T> void registerMessage(int id, Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<Context>> consumer) {
            this.registerMessage(id, type, encoder, decoder, consumer, Optional.empty());
        }

        public <T> void registerMessage(int id, Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<Context>> consumer, Optional<NetworkDirection> direction) {
            MessageSpec<T> spec = new MessageSpec<>(type, encoder, decoder, consumer);
            if (direction.isEmpty() || direction.get() == NetworkDirection.PLAY_TO_CLIENT) {
                this.clientboundMessages.put(id, spec);
                this.clientboundIds.put(type, id);
            }
            if (direction.isEmpty() || direction.get() == NetworkDirection.PLAY_TO_SERVER) {
                this.serverboundMessages.put(id, spec);
                this.serverboundIds.put(type, id);
            }
        }

        public CustomPacketPayload payloadFor(NetworkDirection direction, Object message) {
            Map<Class<?>, Integer> ids = direction == NetworkDirection.PLAY_TO_CLIENT ? this.clientboundIds : this.serverboundIds;
            Integer packetId = ids.get(message.getClass());
            if (packetId == null) {
                throw new IllegalArgumentException("Unregistered Goety packet: " + message.getClass().getName());
            }
            CustomPacketPayload.Type<GoetyPayload> type = direction == NetworkDirection.PLAY_TO_CLIENT ? CLIENTBOUND_TYPE : SERVERBOUND_TYPE;
            return new GoetyPayload(type, packetId, message);
        }

        private Object decode(NetworkDirection direction, int packetId, FriendlyByteBuf buffer) {
            MessageSpec<?> spec = this.message(direction, packetId);
            return spec.decode(buffer);
        }

        private void encode(int packetId, Object message, FriendlyByteBuf buffer) {
            MessageSpec<?> spec = this.message(message, packetId);
            spec.encode(message, buffer);
        }

        private void consume(NetworkDirection direction, int packetId, Object message, Context context) {
            MessageSpec<?> spec = this.message(direction, packetId);
            spec.consume(message, () -> context);
        }

        private MessageSpec<?> message(NetworkDirection direction, int packetId) {
            MessageSpec<?> spec = (direction == NetworkDirection.PLAY_TO_CLIENT ? this.clientboundMessages : this.serverboundMessages).get(packetId);
            if (spec == null) {
                throw new IllegalArgumentException("Unknown Goety packet id " + packetId + " for " + direction);
            }
            return spec;
        }

        private MessageSpec<?> message(Object message, int packetId) {
            MessageSpec<?> spec = this.clientboundMessages.get(packetId);
            if (spec == null || !spec.type().isInstance(message)) {
                spec = this.serverboundMessages.get(packetId);
            }
            if (spec == null || !spec.type().isInstance(message)) {
                throw new IllegalArgumentException("Goety packet id " + packetId + " does not match " + message.getClass().getName());
            }
            return spec;
        }
    }

    private record GoetyPayload(CustomPacketPayload.Type<GoetyPayload> type, int packetId, Object message) implements CustomPacketPayload {
    }

    private record MessageSpec<T>(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<Context>> consumer) {
        private Object decode(FriendlyByteBuf buffer) {
            return this.decoder.apply(buffer);
        }

        private void encode(Object message, FriendlyByteBuf buffer) {
            this.encoder.accept(this.type.cast(message), buffer);
        }

        private void consume(Object message, Supplier<Context> context) {
            this.consumer.accept(this.type.cast(message), context);
        }
    }
}
