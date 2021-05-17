package xyz.destillegast.dsgutils.helpers;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Simple way to quickly make items with some specifications<br/>
 * <br>
 * Created by DeStilleGast 20-2-2021
 */
public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder(ItemStack item) {
        this.itemStack = item;
        this.itemMeta = item.getItemMeta();
    }

    public ItemBuilder withAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder withName(String name) {
        itemMeta.setDisplayName(ChatColor.RESET + name);
        return this;
    }

    public ItemBuilder withLore(String... lore) {
        itemMeta.setLore(Arrays.asList(lore));

        return this;
    }

    public ItemBuilder withLore(List<String> lore) {
        itemMeta.setLore(lore);

        return this;
    }

    public ItemBuilder withSkullOwner(OfflinePlayer player) {
        if (itemMeta instanceof SkullMeta) {
            ((SkullMeta) itemMeta).setOwningPlayer(player);
        }

        return this;
    }

    /**
     * If item is leather armor, potion or a map, you can change its color!
     *
     * @param color The color you wants to use
     * @return this
     */
    public ItemBuilder withColor(Color color) {
        if (itemMeta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) itemMeta).setColor(color);
        } else if (itemMeta instanceof PotionMeta) {
            ((PotionMeta) itemMeta).setColor(color);
        } else if (itemMeta instanceof MapMeta) {
            ((MapMeta) itemMeta).setColor(color);
        }

        return this;
    }

    /**
     * Add enchantment to your item, it will ignore restrictions !
     *
     * @param enchantment Enchantment
     * @param level       Enchantment level
     * @return this
     */
    public ItemBuilder withEnchantment(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);

        return this;
    }

    public ItemBuilder withEnchantments(Map<Enchantment, Integer> enchantmentMap) {
        itemStack.addUnsafeEnchantments(enchantmentMap);

        return this;
    }

    /**
     * Apply itemflags to item
     *
     * @param flags Flags you wanna use
     * @return this
     */
    public ItemBuilder withFlags(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);

        return this;
    }

    /**
     * This will add a enchantment glint to the item and add a itemflag to hide the enchantment
     *
     * @param glint true if you wanna have the glint, false if you regret the decision
     * @return this
     */
    public ItemBuilder withGlint(boolean glint) {
        if (glint) {
            withFlags(ItemFlag.HIDE_ENCHANTS);
            return withEnchantment(Enchantment.VANISHING_CURSE, 0);
        } else {
            itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.removeEnchant(Enchantment.VANISHING_CURSE);
        }

        return this;
    }

    /**
     * Get your hand crafted item
     *
     * @return ItemStack with all the specified stuff
     */
    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
