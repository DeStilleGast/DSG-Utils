package xyz.destillegast.dsgutils.gui.item;

import org.bukkit.inventory.ItemStack;

/**
 * Created by DeStilleGast 7-2-2021
 */
public interface GuiItem {

    ItemStack getItem();

    void run(GuiItemEvent e);
}
