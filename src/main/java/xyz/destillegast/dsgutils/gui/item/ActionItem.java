package xyz.destillegast.dsgutils.gui.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * Created by DeStilleGast 7-2-2021
 */
public class ActionItem implements GuiItem {

    private ItemStack item;
    private Consumer<GuiItemEvent> action;

    public ActionItem(ItemStack item) {
        this.item = item;
    }

    public ActionItem(ItemStack item, Consumer<GuiItemEvent> action) {
        this.item = item;
        this.action = action;
    }

    public static ActionItem of(ItemStack item, Consumer<GuiItemEvent> action) {
        return new ActionItem(item, action);
    }

    public static ActionItem of(Material material, Consumer<GuiItemEvent> action) {
        return new ActionItem(new ItemStack(material), action);
    }

    public void run(GuiItemEvent e) {
        if (this.action != null) this.action.accept(e);
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return this.item;
    }
}
