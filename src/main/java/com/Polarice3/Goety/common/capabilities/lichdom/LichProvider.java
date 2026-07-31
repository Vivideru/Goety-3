package com.Polarice3.Goety.common.capabilities.lichdom;

import com.Polarice3.Goety.common.capabilities.ModCapabilities;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class LichProvider {
    public static final Supplier<AttachmentType<LichImp>> CAPABILITY = ModCapabilities.LICHDOM;
}
