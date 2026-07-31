package com.Polarice3.Goety.common.capabilities.soulenergy;

import com.Polarice3.Goety.common.capabilities.ModCapabilities;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class SEProvider {
    public static final Supplier<AttachmentType<SEImp>> CAPABILITY = ModCapabilities.SOUL_ENERGY;
}
