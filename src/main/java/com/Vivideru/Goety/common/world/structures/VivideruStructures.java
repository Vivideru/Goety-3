package com.Vivideru.Goety.common.world.structures;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;

public class VivideruStructures {
    public static final ResourceKey<Structure> CRYPT_BOSS = ResourceKey.create(Registries.STRUCTURE, Goety.location("crypt_boss"));
    public static final ResourceKey<Structure> CRYPT_VAULT = ResourceKey.create(Registries.STRUCTURE, Goety.location("crypt_vault"));
    public static final List<ResourceKey<Structure>> CRYPT_WINGS = List.of(CRYPT_BOSS, CRYPT_VAULT,
            ResourceKey.create(Registries.STRUCTURE, Goety.location("crypt_wing_a")), ResourceKey.create(Registries.STRUCTURE, Goety.location("crypt_wing_b")),
            ResourceKey.create(Registries.STRUCTURE, Goety.location("crypt_wing_c")), ResourceKey.create(Registries.STRUCTURE, Goety.location("crypt_wing_d")),
            ResourceKey.create(Registries.STRUCTURE, Goety.location("crypt_wing_e")), ResourceKey.create(Registries.STRUCTURE, Goety.location("crypt_wing_f")));
}
