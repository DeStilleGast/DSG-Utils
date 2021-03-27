package xyz.destillegast.dsgutils.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import xyz.destillegast.dsgutils.DSGUtils;
import xyz.destillegast.dsgutils.gui.DSGMenu;
import xyz.destillegast.dsgutils.gui.DSGMenuCloseEvent;

/**
 * Created by DeStilleGast 20-2-2021
 */
public class InventoryMenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof DSGMenu) {
            if (event.getCurrentItem() == null) return;
            if (event.getClickedInventory() != holder.getInventory()) {
                event.setCancelled(true);
                return;
            }

            ((DSGMenu) holder).onInventoryClick(event);
        }
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof DSGMenu) {
            DSGMenu menu = (DSGMenu) holder;

            DSGMenuCloseEvent closeEvent = new DSGMenuCloseEvent(event);
            menu.onInventoryClose(closeEvent);

            if (closeEvent.isCancelled()) {
                Bukkit.getScheduler().runTask(DSGUtils.getPlugin(DSGUtils.class), () -> event.getPlayer().openInventory(event.getInventory()));
            }
        }
    }
}
