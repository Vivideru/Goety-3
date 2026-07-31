package com.Polarice3.Goety.common.capabilities;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.capabilities.lichdom.LichImp;
import com.Polarice3.Goety.common.capabilities.misc.MiscImp;
import com.Polarice3.Goety.common.capabilities.soulenergy.SEImp;
import com.Polarice3.Goety.common.capabilities.witchbarter.WitchBarterImp;
import com.Polarice3.Goety.common.capabilities.witchbarter.WitchBarterProvider;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class ModCapabilities {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Goety.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<MiscImp>> MISC = ATTACHMENT_TYPES.register("misc",
            () -> AttachmentType.builder(MiscImp::new).serialize(new IAttachmentSerializer<CompoundTag, MiscImp>() {
                @Override
                public MiscImp read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
                    return (MiscImp) MiscCapHelper.load(tag, new MiscImp());
                }

                @Override
                @Nullable
                public CompoundTag write(MiscImp attachment, HolderLookup.Provider provider) {
                    return MiscCapHelper.save(new CompoundTag(), attachment);
                }
            }).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SEImp>> SOUL_ENERGY = ATTACHMENT_TYPES.register("soulenergy",
            () -> AttachmentType.builder(SEImp::new).serialize(new IAttachmentSerializer<CompoundTag, SEImp>() {
                @Override
                public SEImp read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
                    return (SEImp) SEHelper.load(tag, new SEImp());
                }

                @Override
                @Nullable
                public CompoundTag write(SEImp attachment, HolderLookup.Provider provider) {
                    return SEHelper.save(new CompoundTag(), attachment);
                }
            }).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LichImp>> LICHDOM = ATTACHMENT_TYPES.register("lichdom",
            () -> AttachmentType.builder(LichImp::new).serialize(new IAttachmentSerializer<CompoundTag, LichImp>() {
                @Override
                public LichImp read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
                    return (LichImp) LichdomHelper.load(tag, new LichImp());
                }

                @Override
                @Nullable
                public CompoundTag write(LichImp attachment, HolderLookup.Provider provider) {
                    return LichdomHelper.save(new CompoundTag(), attachment);
                }
            }).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<WitchBarterImp>> WITCH_BARTER = ATTACHMENT_TYPES.register("witchbarter",
            () -> AttachmentType.builder(WitchBarterImp::new).serialize(new IAttachmentSerializer<CompoundTag, WitchBarterImp>() {
                @Override
                public WitchBarterImp read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
                    return (WitchBarterImp) WitchBarterProvider.load(tag, new WitchBarterImp());
                }

                @Override
                @Nullable
                public CompoundTag write(WitchBarterImp attachment, HolderLookup.Provider provider) {
                    return WitchBarterProvider.save(new CompoundTag(), attachment);
                }
            }).build());
}
