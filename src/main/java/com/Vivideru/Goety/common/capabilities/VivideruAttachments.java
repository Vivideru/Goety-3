package com.Vivideru.Goety.common.capabilities;

import com.Polarice3.Goety.Goety;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class VivideruAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Goety.MOD_ID);

    /** Plinth assignment of a golem or Haunted Armor: the Plinth position and dimension, and whether the mob is resting on it. */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CompoundTag>> PLINTH = ATTACHMENT_TYPES.register("plinth",
            () -> AttachmentType.builder(() -> new CompoundTag()).serialize(CompoundTag.CODEC).build());

    @SuppressWarnings("removal")
    public static void init() {
        ATTACHMENT_TYPES.register(Goety.getModEventBus());
    }
}
