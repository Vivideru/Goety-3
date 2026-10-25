package com.Vivideru.Goety.common.entities.extra;

import com.Polarice3.Goety.Goety;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A hidden pack, always on, made at start-up for the extra servants. As a resource pack it holds the servant textures that are
 * PNG files of {@code config/goety}, and for each focus its item model, its name and description in English and, when it is
 * a PNG file too, its texture; as a data pack it puts the focuses in the tags of Goety's focuses and of the Potency and
 * Duration enchantments.
 */
public final class ExtraServantPack implements PackResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String ID = "goety_extra_servants";
    /** Tags every summoning focus is in: Goety's focus list and the items Potency and Duration go on. */
    private static final List<String> FOCUS_TAGS = List.of("focuses", "enchantable/focus/potency", "enchantable/focus/duration");
    private final PackLocationInfo location;
    private final PackType type;
    private final Map<ResourceLocation, byte[]> files;

    private ExtraServantPack(PackLocationInfo location, PackType type, Map<ResourceLocation, byte[]> files) {
        this.location = location;
        this.type = type;
        this.files = files;
    }

    public static void load(PackType type, Consumer<Pack> output) {
        Map<ResourceLocation, byte[]> files = type == PackType.CLIENT_RESOURCES ? assets() : data();
        if (files.isEmpty()) {
            return;
        }
        PackLocationInfo location = new PackLocationInfo(ID, Component.literal("Goety Extra Servants"), PackSource.BUILT_IN, Optional.empty());
        Pack pack = Pack.readMetaAndCreate(location, new Pack.ResourcesSupplier() {
            @Override
            public PackResources openPrimary(PackLocationInfo info) {
                return new ExtraServantPack(info, type, files);
            }

            @Override
            public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
                return new ExtraServantPack(info, type, files);
            }
        }, type, new PackSelectionConfig(true, Pack.Position.TOP, false));
        if (pack != null) {
            output.accept(pack);
        }
    }

    private static Map<ResourceLocation, byte[]> assets() {
        Map<ResourceLocation, byte[]> files = new LinkedHashMap<>();
        JsonObject lang = new JsonObject();
        Path folder = FMLPaths.CONFIGDIR.get().resolve("goety").normalize();
        for (ExtraServantDefinition definition : ExtraServants.definitions()) {
            if (definition.textureFile() != null && definition.texture() != null) {
                try {
                    files.put(definition.texture(), read(folder, definition.textureFile()));
                } catch (IOException e) {
                    LOGGER.error("{}: the texture {} of goety:{} could not be read, it keeps the mob's own: {}", ExtraServants.FILE, definition.textureFile(), definition.id(), e.getMessage());
                }
            }
            ExtraServantFocusDefinition focus = definition.focus();
            if (focus == null) {
                continue;
            }
            String texture = focus.texture();
            if (focus.textureFile()) {
                try {
                    files.put(ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "textures/item/extra_servants/" + focus.id() + ".png"), read(folder, texture));
                    texture = Goety.MOD_ID + ":item/extra_servants/" + focus.id();
                } catch (IOException e) {
                    LOGGER.error("{}: the focus texture {} of goety:{} could not be read: {}", ExtraServants.FILE, texture, definition.id(), e.getMessage());
                    texture = "goety:item/focus/spell_template";
                }
            }
            String model = "{\"parent\": \"minecraft:item/generated\", \"textures\": {\"layer0\": \"" + texture + "\"}}";
            files.put(ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "models/item/" + focus.id() + ".json"), model.getBytes(StandardCharsets.UTF_8));
            lang.addProperty("item." + Goety.MOD_ID + "." + focus.id(), focus.name());
            if (!focus.description().isEmpty()) {
                lang.addProperty("item." + Goety.MOD_ID + "." + focus.id() + ".info", focus.description());
            }
        }
        if (!lang.isEmpty()) {
            // English names for every language without its own, as other packs can still translate them.
            files.put(ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "lang/en_us.json"), lang.toString().getBytes(StandardCharsets.UTF_8));
        }
        return files;
    }

    private static byte[] read(Path folder, String path) throws IOException {
        Path file = folder.resolve(path).normalize();
        if (!file.startsWith(folder)) {
            throw new IOException("it is outside config/goety");
        }
        return Files.readAllBytes(file);
    }

    private static Map<ResourceLocation, byte[]> data() {
        JsonArray focuses = new JsonArray();
        for (ExtraServantDefinition definition : ExtraServants.definitions()) {
            if (definition.focus() != null) {
                // Optional, so a focus whose item was not added never breaks the whole tag.
                JsonObject entry = new JsonObject();
                entry.addProperty("id", Goety.MOD_ID + ":" + definition.focus().id());
                entry.addProperty("required", false);
                focuses.add(entry);
            }
        }
        Map<ResourceLocation, byte[]> files = new LinkedHashMap<>();
        if (!focuses.isEmpty()) {
            JsonObject tag = new JsonObject();
            tag.addProperty("replace", false);
            tag.add("values", focuses);
            byte[] bytes = tag.toString().getBytes(StandardCharsets.UTF_8);
            for (String name : FOCUS_TAGS) {
                files.put(ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "tags/item/" + name + ".json"), bytes);
            }
        }
        return files;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... path) {
        return null;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        byte[] bytes = type == this.type ? this.files.get(location) : null;
        return bytes == null ? null : () -> new ByteArrayInputStream(bytes);
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
        if (type != this.type) {
            return;
        }
        this.files.forEach((location, bytes) -> {
            if (location.getNamespace().equals(namespace) && location.getPath().startsWith(path + "/")) {
                output.accept(location, () -> new ByteArrayInputStream(bytes));
            }
        });
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return type == this.type ? Set.of(Goety.MOD_ID) : Set.of();
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) {
        if (serializer == PackMetadataSection.TYPE) {
            return (T) new PackMetadataSection(Component.literal("Focuses of the extra servants"), SharedConstants.getCurrentVersion().getPackVersion(this.type));
        }
        return null;
    }

    @Override
    public PackLocationInfo location() {
        return this.location;
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public void close() {
    }
}
