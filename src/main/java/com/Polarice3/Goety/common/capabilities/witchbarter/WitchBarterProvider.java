package com.Polarice3.Goety.common.capabilities.witchbarter;

import com.Polarice3.Goety.common.capabilities.ModCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class WitchBarterProvider {
    public static final Supplier<AttachmentType<WitchBarterImp>> CAPABILITY = ModCapabilities.WITCH_BARTER;

    public static CompoundTag save(CompoundTag tag, IWitchBarter witchBarter) {
        tag.putInt("barterTimer", witchBarter.getTimer());
        tag.putInt("barterTraderID", witchBarter.getTraderID());
        return tag;
    }

    public static IWitchBarter load(CompoundTag tag, IWitchBarter witchBarter) {
        if (tag.contains("barterTimer")) {
            witchBarter.setTimer(tag.getInt("barterTimer"));
        }
        if (tag.contains("barterTraderID")) {
            witchBarter.setTraderID(tag.getInt("barterTraderID"));
        }
        return witchBarter;
    }
}
