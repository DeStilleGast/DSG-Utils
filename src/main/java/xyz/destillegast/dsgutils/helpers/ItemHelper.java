package xyz.destillegast.dsgutils.helpers;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

/**
 * Created by DeStilleGast 20-2-2021
 */
public class ItemHelper {

    public static ItemStack addLore(ItemStack item, String loreLine) {
        return addLore(item, Collections.singletonList(loreLine));
    }

    public static ItemStack addLore(ItemStack item, List<String> loreLines) {
        ItemMeta itemMeta = item.getItemMeta();
        List<String> oldLore = itemMeta.getLore();
        oldLore.addAll(loreLines);

        itemMeta.setLore(oldLore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack setLore(ItemStack item, List<String> loreLines) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(loreLines);
        item.setItemMeta(itemMeta);

        return item;
    }

}
