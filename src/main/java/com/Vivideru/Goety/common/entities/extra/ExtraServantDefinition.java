package com.Vivideru.Goety.common.entities.extra;

import com.Polarice3.Goety.Goety;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * One servant of {@code config/goety/goety-extra-servants.json}: an existing mob that becomes a Goety servant under its own
 * entity type, {@code goety:<id>}.
 *
 * @param id                  the path of the new entity type
 * @param parent              the mob it is made from
 * @param replaceBrain        for mobs driven by a Brain (piglins and other newer mobs): the Brain is switched off and the
 *                            servant is driven by goals alone, losing the mob's own behaviours
 * @param followOwner         whether the servant follows its owner and patrols; off for mobs rooted where they are summoned
 * @param followPriority      priority of the goal that follows the owner
 * @param goalShift           how far the mob's own goals are moved down, so the servant goals come first (mobs with goals)
 * @param removePlayerTargets whether the mob's own goals that hunt players are taken away (mobs with goals)
 * @param removeGoals         class names, simple or full, of the mob's own goals to take away (mobs with goals)
 * @param eggPrimary          spawn egg base colour
 * @param eggSecondary        spawn egg spots colour
 * @param data                NBT every new servant starts with, as given to /summon (a piglin's IsImmuneToZombification)
 * @param texture             the texture its body is drawn with instead of the mob's, if it has one
 * @param textureFile         the PNG file of {@code config/goety} that texture comes from, when it is not one of the game
 * @param focus               the focus that summons the servant, if it has one
 */
public record ExtraServantDefinition(String id, ResourceLocation parent, boolean replaceBrain, boolean followOwner, int followPriority, int goalShift,
                                     boolean removePlayerTargets, List<String> removeGoals, int eggPrimary, int eggSecondary,
                                     @Nullable CompoundTag data, @Nullable ResourceLocation texture, @Nullable String textureFile,
                                     @Nullable ExtraServantFocusDefinition focus) {
    private static final Pattern ID = Pattern.compile("[a-z0-9_]+");

    /** The definition in a JSON object, or null (with the reason in {@code problem}) when it cannot be used. */
    @Nullable
    static ExtraServantDefinition parse(JsonObject json, StringBuilder problem) {
        String id = GsonHelper.getAsString(json, "id", "");
        if (!ID.matcher(id).matches()) {
            problem.append("\"id\" must be lower case letters, digits and underscores, got \"").append(id).append('"');
            return null;
        }
        ResourceLocation parent = ResourceLocation.tryParse(GsonHelper.getAsString(json, "parent", ""));
        if (parent == null) {
            problem.append("\"parent\" must be an entity type id such as minecraft:piglin");
            return null;
        }
        int primary = 0x7A7A7A;
        int secondary = 0x3F3F3F;
        if (json.has("spawn_egg")) {
            JsonObject egg = GsonHelper.getAsJsonObject(json, "spawn_egg");
            primary = colour(GsonHelper.getAsString(egg, "primary", "#7A7A7A"), primary);
            secondary = colour(GsonHelper.getAsString(egg, "secondary", "#3F3F3F"), secondary);
        }
        CompoundTag data = null;
        if (json.has("data")) {
            try {
                data = TagParser.parseTag(GsonHelper.getAsString(json, "data"));
            } catch (CommandSyntaxException e) {
                problem.append("\"data\" is not valid NBT: ").append(e.getMessage());
                return null;
            }
        }
        List<String> removeGoals = new ArrayList<>();
        if (json.has("remove_goals")) {
            for (JsonElement goal : GsonHelper.getAsJsonArray(json, "remove_goals")) {
                removeGoals.add(GsonHelper.convertToString(goal, "remove_goals entry"));
            }
        }
        ResourceLocation texture = null;
        String textureFile = null;
        if (json.has("texture")) {
            String path = GsonHelper.getAsString(json, "texture");
            if (path.contains(":")) {
                texture = ResourceLocation.tryParse(path);
            } else if (path.endsWith(".png") && !path.contains("..")) {
                texture = ResourceLocation.fromNamespaceAndPath(Goety.MOD_ID, "textures/entity/extra_servants/" + id + ".png");
                textureFile = path;
            }
            if (texture == null) {
                problem.append("\"texture\" must be a texture of the game such as minecraft:textures/entity/iron_golem/iron_golem.png or a .png file inside config/goety");
                return null;
            }
        }
        ExtraServantFocusDefinition focus = null;
        if (json.has("focus")) {
            focus = ExtraServantFocusDefinition.parse(id, GsonHelper.getAsJsonObject(json, "focus"), problem);
            if (focus == null) {
                return null;
            }
        }
        return new ExtraServantDefinition(id, parent,
                GsonHelper.getAsBoolean(json, "replace_brain", false),
                GsonHelper.getAsBoolean(json, "follow_owner", true),
                GsonHelper.getAsInt(json, "follow_priority", 4),
                GsonHelper.getAsInt(json, "goal_shift", 0),
                GsonHelper.getAsBoolean(json, "remove_player_targets", true),
                List.copyOf(removeGoals), primary, secondary, data, texture, textureFile, focus);
    }

    private static int colour(String text, int fallback) {
        String hex = text.startsWith("#") ? text.substring(1) : text;
        try {
            return Integer.parseInt(hex, 16) & 0xFFFFFF;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
