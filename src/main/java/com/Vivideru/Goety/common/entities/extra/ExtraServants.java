package com.Vivideru.Goety.common.entities.extra;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.init.ModCreativeTab;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Servants made from other mobs by configuration alone. {@code config/goety/goety-extra-servants.json} lists them; Goety
 * ships no such file, so nothing is added unless a pack maker writes one. Each entry becomes the entity type
 * {@code goety:<id>}: the mob with Goety's servant state and goals, drawn and made like the mob itself, with a focus that
 * summons it when the entry describes one. The file is read at
 * start-up, before entity types are registered, so changes need a restart and must be the same on server and clients.
 */
public final class ExtraServants {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String FILE = "goety/goety-extra-servants.json";
    private static final Map<String, ExtraServantDefinition> DEFINITIONS = load();
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Goety.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Goety.MOD_ID);
    private static final Map<String, DeferredHolder<EntityType<?>, EntityType<Mob>>> TYPES = new LinkedHashMap<>();
    public static final DeferredHolder<Item, Item> SPAWN_EGG = ITEMS.register("extra_servant_spawn_egg", () -> new ExtraServantSpawnEggItem(new Item.Properties()));
    /** Servants whose mob had no attributes yet when they were registered: they get them once every mod has added its own. */
    private static final List<ExtraServantDefinition> LATE_ATTRIBUTES = new ArrayList<>();
    /** Focus items by servant id, registered once every other Goety item is, so a clashing id is skipped instead of crashing. */
    private static final Map<String, Item> FOCUSES = new LinkedHashMap<>();

    static {
        for (ExtraServantDefinition definition : DEFINITIONS.values()) {
            TYPES.put(definition.id(), ENTITY_TYPES.register(definition.id(), () -> buildType(definition)));
        }
    }

    private ExtraServants() {
    }

    public static void init(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
        ITEMS.register(modBus);
        modBus.addListener(ExtraServants::createAttributes);
        modBus.addListener(ExtraServants::lateAttributes);
        modBus.addListener(EventPriority.LOWEST, ExtraServants::registerFocuses);
        modBus.addListener(ExtraServants::creativeTab);
        modBus.addListener(ExtraServants::addPacks);
        NeoForge.EVENT_BUS.addListener(ExtraServants::noDrops);
        NeoForge.EVENT_BUS.addListener(ExtraServants::noExperience);
        if (!DEFINITIONS.isEmpty()) {
            LOGGER.info("Extra servants from {}: {}", FILE, DEFINITIONS.keySet());
        }
    }

    public static Collection<ExtraServantDefinition> definitions() {
        return DEFINITIONS.values();
    }

    @Nullable
    public static ExtraServantDefinition definition(@Nullable EntityType<?> type) {
        if (type == null) {
            return null;
        }
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        return id.getNamespace().equals(Goety.MOD_ID) ? DEFINITIONS.get(id.getPath()) : null;
    }

    @Nullable
    public static ExtraServantDefinition definition(String id) {
        return DEFINITIONS.get(id);
    }

    /** The mob a servant is made from, once it is registered. */
    @Nullable
    public static EntityType<?> parentType(@Nullable ExtraServantDefinition definition) {
        return definition == null ? null : BuiltInRegistries.ENTITY_TYPE.getOptional(definition.parent()).orElse(null);
    }

    @Nullable
    public static EntityType<?> type(ExtraServantDefinition definition) {
        DeferredHolder<EntityType<?>, EntityType<Mob>> holder = TYPES.get(definition.id());
        return holder != null && holder.isBound() ? holder.get() : null;
    }

    /** A spawn egg for the servant: the shared egg item, told which servant it holds through its entity data. */
    public static ItemStack spawnEgg(ExtraServantDefinition definition) {
        ItemStack stack = new ItemStack(SPAWN_EGG.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("id", ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, definition.id()).toString());
        stack.set(DataComponents.ENTITY_DATA, CustomData.of(tag));
        return stack;
    }

    private static Map<String, ExtraServantDefinition> load() {
        Map<String, ExtraServantDefinition> definitions = new LinkedHashMap<>();
        Path file = FMLPaths.CONFIGDIR.get().resolve(FILE);
        if (!Files.isRegularFile(file)) {
            return definitions;
        }
        try (Reader reader = Files.newBufferedReader(file)) {
            JsonElement root = JsonParser.parseReader(reader);
            JsonArray servants = root.isJsonObject() && root.getAsJsonObject().has("servants") ? root.getAsJsonObject().getAsJsonArray("servants") : new JsonArray();
            for (int i = 0; i < servants.size(); i++) {
                if (!servants.get(i).isJsonObject()) {
                    LOGGER.error("{}: servant #{} is not an object", FILE, i + 1);
                    continue;
                }
                JsonObject json = servants.get(i).getAsJsonObject();
                StringBuilder problem = new StringBuilder();
                ExtraServantDefinition definition = ExtraServantDefinition.parse(json, problem);
                if (definition == null) {
                    LOGGER.error("{}: servant #{} skipped: {}", FILE, i + 1, problem);
                } else if (definitions.containsKey(definition.id())) {
                    LOGGER.error("{}: servant #{} skipped: id {} is used twice", FILE, i + 1, definition.id());
                } else {
                    definitions.put(definition.id(), definition);
                }
            }
        } catch (Exception e) {
            LOGGER.error("{} could not be read, no extra servants are added", FILE, e);
            definitions.clear();
        }
        return definitions;
    }

    /** Sized and tracked like the mob it is made from, when that mob is already registered (vanilla mobs always are). */
    private static EntityType<Mob> buildType(ExtraServantDefinition definition) {
        EntityType<?> parent = parentType(definition);
        EntityType.Builder<Mob> builder = EntityType.Builder.of((type, level) -> ExtraServantClasses.create(type, level, definition),
                parent != null ? parent.getCategory() : MobCategory.MONSTER);
        if (parent != null) {
            EntityDimensions dimensions = parent.getDimensions();
            builder.sized(dimensions.width(), dimensions.height()).eyeHeight(dimensions.eyeHeight())
                    .clientTrackingRange(parent.clientTrackingRange()).updateInterval(parent.updateInterval());
            if (parent.fireImmune()) {
                builder.fireImmune();
            }
        } else {
            LOGGER.warn("{}: {} is not registered yet when goety:{} is, so it gets a default size", FILE, definition.parent(), definition.id());
            builder.sized(0.6F, 1.95F).clientTrackingRange(8);
        }
        return builder.build(ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, definition.id()).toString());
    }

    @SuppressWarnings("unchecked")
    private static void createAttributes(EntityAttributeCreationEvent event) {
        for (ExtraServantDefinition definition : DEFINITIONS.values()) {
            EntityType<?> type = type(definition);
            if (type == null) {
                continue;
            }
            EntityType<?> parent = parentType(definition);
            AttributeSupplier.Builder builder = Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE, 2.0D);
            if (parent != null && DefaultAttributes.hasSupplier(parent)) {
                AttributeSupplier supplier = DefaultAttributes.getSupplier((EntityType<? extends LivingEntity>) parent);
                for (Holder<Attribute> attribute : BuiltInRegistries.ATTRIBUTE.holders().toList()) {
                    if (supplier.hasAttribute(attribute)) {
                        builder.add(attribute, supplier.getBaseValue(attribute));
                    }
                }
            } else {
                LATE_ATTRIBUTES.add(definition);
            }
            event.put((EntityType<? extends LivingEntity>) type, builder.build());
        }
    }

    @SuppressWarnings("unchecked")
    private static void lateAttributes(EntityAttributeModificationEvent event) {
        for (ExtraServantDefinition definition : LATE_ATTRIBUTES) {
            EntityType<?> type = type(definition);
            EntityType<?> parent = parentType(definition);
            if (type == null || parent == null || !DefaultAttributes.hasSupplier(parent)) {
                LOGGER.warn("{}: goety:{} keeps basic attributes, {} has none", FILE, definition.id(), definition.parent());
                continue;
            }
            AttributeSupplier supplier = DefaultAttributes.getSupplier((EntityType<? extends LivingEntity>) parent);
            for (Holder<Attribute> attribute : BuiltInRegistries.ATTRIBUTE.holders().toList()) {
                if (supplier.hasAttribute(attribute)) {
                    event.add((EntityType<? extends LivingEntity>) type, attribute, supplier.getBaseValue(attribute));
                }
            }
        }
    }

    /** The focus item of a servant, when its entry has one and it was registered. */
    @Nullable
    public static Item focus(ExtraServantDefinition definition) {
        return FOCUSES.get(definition.id());
    }

    private static void registerFocuses(RegisterEvent event) {
        event.register(Registries.ITEM, helper -> {
            for (ExtraServantDefinition definition : DEFINITIONS.values()) {
                ExtraServantFocusDefinition focus = definition.focus();
                if (focus == null) {
                    continue;
                }
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, focus.id());
                if (BuiltInRegistries.ITEM.containsKey(id)) {
                    LOGGER.error("{}: the focus of goety:{} is not added, the item {} already exists; give it another \"id\"", FILE, definition.id(), id);
                    continue;
                }
                Item item = new ExtraServantFocus(definition, focus);
                helper.register(id, item);
                FOCUSES.put(definition.id(), item);
            }
        });
    }

    /** The eggs go with the servant eggs, the focuses with the focuses. */
    private static void creativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(ModCreativeTab.SERVANT_TAB.getKey())) {
            for (ExtraServantDefinition definition : DEFINITIONS.values()) {
                event.accept(spawnEgg(definition));
            }
        } else if (event.getTabKey().equals(ModCreativeTab.FOCUS_TAB.getKey())) {
            FOCUSES.values().forEach(event::accept);
        }
    }

    /** The body texture of an extra servant, when its entry gives one; null for every other entity. */
    @Nullable
    public static ResourceLocation texture(Entity entity) {
        ExtraServantDefinition definition = entity instanceof ExtraServant servant ? servant.goety$definition() : null;
        return definition != null ? definition.texture() : null;
    }

    private static void addPacks(AddPackFindersEvent event) {
        if (DEFINITIONS.values().stream().anyMatch(definition -> definition.focus() != null || definition.textureFile() != null)) {
            event.addRepositorySource(output -> ExtraServantPack.load(event.getPackType(), output));
        }
    }

    private static boolean isOwnedServant(LivingEntity entity) {
        return entity instanceof ExtraServant && entity instanceof com.Polarice3.Goety.api.entities.IOwned owned && owned.getOwnerId() != null;
    }

    private static void noDrops(LivingDropsEvent event) {
        if (isOwnedServant(event.getEntity())) {
            event.getDrops().clear();
        }
    }

    private static void noExperience(LivingExperienceDropEvent event) {
        if (isOwnedServant(event.getEntity())) {
            event.setDroppedExperience(0);
        }
    }
}
