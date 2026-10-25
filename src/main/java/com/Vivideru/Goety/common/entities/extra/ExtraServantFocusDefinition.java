package com.Vivideru.Goety.common.entities.extra;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.magic.SpellType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The focus that summons an extra servant, from the {@code focus} object of its entry. It becomes the item
 * {@code goety:<id>}, a Goety focus whose spell summons the servant.
 *
 * @param id           path of the focus item: by default the servant id without "_servant", plus "_focus"
 * @param name         the focus name, shown unless a language file names {@code item.goety.<id>}
 * @param texture      a texture already in the game ({@code namespace:path}, such as {@code minecraft:item/iron_ingot}) or
 *                     a PNG file inside {@code config/goety} (such as {@code extra_servants/iron_golem_focus.png})
 * @param description  the text shown while holding Shift; empty for the default one
 * @param spellType    the school of the spell: which staffs and robes favour it, and its particles
 * @param soulCost     Soul Energy spent on each cast
 * @param castDuration ticks to cast it
 * @param cooldown     ticks before it can be cast again
 * @param summonDown   ticks of Summon Down given to the caster after each cast, 0 for none
 * @param summonCount  servants summoned by each cast
 * @param staffCounts  servants summoned by each cast with a given staff, instead of {@code summonCount}
 * @param summonLimit  servants of this kind a caster can have at once
 * @param limitedLife  whether summoned servants die after a while, as most Goety summons do
 */
public record ExtraServantFocusDefinition(String id, String name, String texture, String description, SpellType spellType,
                                          int soulCost, int castDuration, int cooldown, int summonDown, int summonCount,
                                          Map<ResourceLocation, Integer> staffCounts, int summonLimit, boolean limitedLife) {
    private static final Pattern ID = Pattern.compile("[a-z0-9_]+");

    /** The texture is a PNG file of the config folder rather than a texture of the game. */
    public boolean textureFile() {
        return !this.texture.contains(":");
    }

    @Nullable
    static ExtraServantFocusDefinition parse(String servantId, JsonObject json, StringBuilder problem) {
        String base = servantId.endsWith("_servant") ? servantId.substring(0, servantId.length() - "_servant".length()) : servantId;
        String id = GsonHelper.getAsString(json, "id", base + "_focus");
        if (!ID.matcher(id).matches()) {
            problem.append("focus \"id\" must be lower case letters, digits and underscores, got \"").append(id).append('"');
            return null;
        }
        String name = GsonHelper.getAsString(json, "name", "");
        if (name.isEmpty()) {
            problem.append("the focus needs a \"name\"");
            return null;
        }
        String texture = GsonHelper.getAsString(json, "texture", "goety:item/focus/spell_template");
        if (!texture.contains(":") && (!texture.endsWith(".png") || texture.contains(".."))) {
            problem.append("focus \"texture\" must be a texture id such as minecraft:item/iron_ingot or a .png file inside config/goety");
            return null;
        }
        SpellType spellType;
        try {
            spellType = SpellType.valueOf(GsonHelper.getAsString(json, "spell_type", "none").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            problem.append("focus \"spell_type\" must be one of none, necromancy, nether, ill, frost, geomancy, wind, storm, abyss, wild, void");
            return null;
        }
        Map<ResourceLocation, Integer> staffCounts = new LinkedHashMap<>();
        if (json.has("staff_summon_count")) {
            for (Map.Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(json, "staff_summon_count").entrySet()) {
                // A staff named without a namespace is one of Goety's, such as geo_staff.
                ResourceLocation staff = ResourceLocation.tryParse(entry.getKey().contains(":") ? entry.getKey() : Goety.MOD_ID + ":" + entry.getKey());
                if (staff == null) {
                    problem.append("focus \"staff_summon_count\" has an invalid item id \"").append(entry.getKey()).append('"');
                    return null;
                }
                staffCounts.put(staff, Math.max(1, GsonHelper.convertToInt(entry.getValue(), entry.getKey())));
            }
        }
        return new ExtraServantFocusDefinition(id, name, texture,
                GsonHelper.getAsString(json, "description", ""),
                spellType,
                Math.max(0, GsonHelper.getAsInt(json, "soul_cost", 20)),
                Math.max(0, GsonHelper.getAsInt(json, "cast_duration", 40)),
                Math.max(0, GsonHelper.getAsInt(json, "cooldown", 200)),
                Math.max(0, GsonHelper.getAsInt(json, "summon_down", 0)),
                Math.max(1, GsonHelper.getAsInt(json, "summon_count", 1)),
                Map.copyOf(staffCounts),
                Math.max(1, GsonHelper.getAsInt(json, "summon_limit", 4)),
                GsonHelper.getAsBoolean(json, "limited_life", true));
    }
}
