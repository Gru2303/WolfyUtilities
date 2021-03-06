package me.wolfyscript.utilities.api.utils.inventory;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.wolfyscript.utilities.api.utils.inventory.item_builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

import static me.wolfyscript.utilities.api.utils.EncryptionUtils.getBase64EncodedString;

public class PlayerHeadUtils {

    /**
     * Gets the Player Head ItemStack via a URL or Base64 encoded string.
     * <p>This method uses the {@link ItemBuilder}!
     *
     * @see ItemBuilder#setPlayerHeadValue(String)
     * @param value Skin URL or Base64 encoded value of textures object
     * @return the Player Head ItemStack with the corresponding Texture
     */
    public static ItemStack getViaValue(String value) {
        return new ItemBuilder(Material.PLAYER_HEAD).setPlayerHeadValue(value).create();
    }

    /**
     * Get the Player Head via URL value
     * <p>This method uses the {@link ItemBuilder}!
     *
     * @see ItemBuilder#setPlayerHeadURL(String)
     * @param value the Base64 value at the end of the textures url.
     *              <p>e.g. http://textures.minecraft.net/texture/{value}
     * @return the Player Head ItemStack with the corresponding Texture
     */
    public static ItemStack getViaURL(String value) {
        return new ItemBuilder(Material.PLAYER_HEAD).setPlayerHeadURL(value).create();
    }

    /**
     * Gets the SkullMeta for this Texture value.
     *
     *
     * @param value the texture value. Base64 or texture link
     * @param skullMeta the {@link SkullMeta} the texture value should be added to
     * @return the original skullMeta with the new texture value
     */
    public static SkullMeta getSkullMeta(String value, SkullMeta skullMeta) {
        if (value != null && !value.isEmpty()) {
            String texture = value;
            if (value.startsWith("https://") || value.startsWith("http://")) {
                texture = getBase64EncodedString(String.format("{textures:{SKIN:{url:\"%s\"}}}", value));
            }
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", texture));
            Field profileField = null;
            try {
                profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
            } catch (NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
            try {
                profileField.set(skullMeta, profile);
                return skullMeta;
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return skullMeta;
    }

    public static String getTextureValue(SkullMeta skullMeta) {
        GameProfile profile = null;
        Field profileField;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            try {
                profile = (GameProfile) profileField.get(skullMeta);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException | SecurityException ex) {
            ex.printStackTrace();
        }
        if (profile != null) {
            if (!profile.getProperties().get("textures").isEmpty()) {
                for (Property property : profile.getProperties().get("textures")) {
                    if (!property.getValue().isEmpty())
                        return property.getValue();
                }
            }
        }
        return null;
    }


    /**
     * Both items Material must be equal to PLAYER_HEAD!
     * <p>
     * Returns result with the texture from the input
     * <p>
     * Returns result when one of the items is not Player Head
     */
    public static ItemStack migrateTexture(ItemStack input, ItemStack result) {
        if (input.getType().equals(Material.PLAYER_HEAD) && result.getType().equals(Material.PLAYER_HEAD)) {
            SkullMeta inputMeta = (SkullMeta) input.getItemMeta();
            String value = getTextureValue(inputMeta);
            if (value != null && !value.isEmpty()) {
                result.setItemMeta(getSkullMeta(value, (SkullMeta) result.getItemMeta()));
            }
        }
        return result;
    }

    /**
     * Result's Material must be equal to PLAYER_HEAD!
     * <p>
     * Returns ItemMeta with the texture from the input
     */
    public static ItemMeta migrateTexture(SkullMeta input, ItemStack result) {
        if (result.getType().equals(Material.PLAYER_HEAD)) {
            String value = getTextureValue(input);
            if (value != null && !value.isEmpty()) {
                return getSkullMeta(value, (SkullMeta) result.getItemMeta());
            }
        }
        return result.getItemMeta();
    }
}
