package xyz.destillegast.dsgutils.gui;

import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Created by DeStilleGast 25-2-2021
 */
public class DSGMenuCloseEvent {

    private final InventoryCloseEvent event;
    private boolean cancel = false;

    public DSGMenuCloseEvent(InventoryCloseEvent event) {
        this.event = event;
    }

    public InventoryCloseEvent getEvent() {
        return event;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
