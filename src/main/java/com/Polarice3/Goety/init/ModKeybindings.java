package com.Polarice3.Goety.init;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class ModKeybindings {
    // NeoForge registers key mappings through the mod event bus, so the instances must exist before input events can read them.
    public static final KeyMapping[] keyBindings = new KeyMapping[]{
            new KeyMapping("key.goety.wand", GLFW.GLFW_KEY_Z, "key.goety.category"),
            new KeyMapping("key.goety.focusCircle", GLFW.GLFW_KEY_X, "key.goety.category"),
            new KeyMapping("key.goety.bag", GLFW.GLFW_KEY_C, "key.goety.category"),
            new KeyMapping("key.goety.witch.robe", GLFW.GLFW_KEY_V, "key.goety.witch.category"),
            new KeyMapping("key.goety.ceaseFire", GLFW.GLFW_KEY_B, "key.goety.category"),
            new KeyMapping("key.goety.lich.magnet", GLFW.GLFW_KEY_R, "key.goety.lich.category"),
            new KeyMapping("key.goety.lich.nightVision", GLFW.GLFW_KEY_M, "key.goety.lich.category"),
            new KeyMapping("key.goety.witch.extractPotions", GLFW.GLFW_KEY_G, "key.goety.witch.category"),
            new KeyMapping("key.goety.witch.brewBag", GLFW.GLFW_KEY_H, "key.goety.witch.category"),
            new KeyMapping("key.goety.witch.brewCircle", GLFW.GLFW_KEY_J, "key.goety.witch.category"),
            new KeyMapping("key.goety.mount.roar", GLFW.GLFW_KEY_R, "key.goety.mount.category"),
            new KeyMapping("key.goety.mount.freeRoam", GLFW.GLFW_KEY_H, "key.goety.mount.category"),
            new KeyMapping("key.goety.lich.lichForm", GLFW.GLFW_KEY_N, "key.goety.lich.category"),
            new KeyMapping("key.goety.lich.laugh", GLFW.GLFW_KEY_K, "key.goety.lich.category"),
            new KeyMapping("key.goety.activate_curio", GLFW.GLFW_KEY_G, "key.goety.category"),
            new KeyMapping("key.goety.dismiss", GLFW.GLFW_KEY_KP_DECIMAL, "key.goety.category")
    };

    public static void register(RegisterKeyMappingsEvent event){
        for (KeyMapping keyBinding : keyBindings) {
            if (keyBinding != null) {
                event.register(keyBinding);
            }
        }
    }

    public static KeyMapping get(int index) {
        if (keyBindings == null || index < 0 || index >= keyBindings.length) {
            return null;
        }
        return keyBindings[index];
    }

    public static KeyMapping wandSlot(){
        return get(0);
    }

    public static KeyMapping wandCircle(){
        return get(1);
    }

    public static KeyMapping brewCircle(){
        return get(9);
    }

    public static KeyMapping useCurios(){
        return get(14);
    }

}
