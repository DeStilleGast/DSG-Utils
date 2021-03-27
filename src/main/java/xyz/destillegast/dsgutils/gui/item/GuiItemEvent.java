package xyz.destillegast.dsgutils.gui.item;

import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Created by DeStilleGast 20-2-2021
 */
public class GuiItemEvent {
    private final GuiItem guiItem;
    private final InventoryClickEvent event;

    public GuiItemEvent(GuiItem guiItem, InventoryClickEvent event) {
        this.guiItem = guiItem;
        this.event = event;
    }

    public GuiItem getGuiItem() {
        return guiItem;
    }

    public InventoryClickEvent getEvent() {
        return event;
    }
}
