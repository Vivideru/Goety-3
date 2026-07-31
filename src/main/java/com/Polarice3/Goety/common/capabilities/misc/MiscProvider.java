package com.Polarice3.Goety.common.capabilities.misc;

import com.Polarice3.Goety.common.capabilities.ModCapabilities;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class MiscProvider {
    public static final Supplier<AttachmentType<MiscImp>> CAPABILITY = ModCapabilities.MISC;
}
