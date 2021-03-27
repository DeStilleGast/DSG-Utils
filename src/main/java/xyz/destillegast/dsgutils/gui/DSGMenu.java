package xyz.destillegast.dsgutils.gui;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import xyz.destillegast.dsgutils.gui.item.ActionItem;
import xyz.destillegast.dsgutils.gui.item.GuiItem;
import xyz.destillegast.dsgutils.gui.item.GuiItemEvent;
import xyz.destillegast.dsgutils.helpers.ColorHelper;
import xyz.destillegast.dsgutils.helpers.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DeStilleGast 7-2-2021
 */
public class DSGMenu implements InventoryHolder {

    private Inventory inventory;
    private final Map<Integer, GuiItem> actionItemList = new HashMap<>();
    private final Map<Integer, GuiItem> inUseItemList = new HashMap<>();
    private final Map<Integer, GuiItem> hotbarItems = new HashMap<>(); // TODO, find good way to implement this
    protected boolean canUserCloseIt = true;
    private boolean isOpen;
    private int currentPage = 0;

    private final int displayInventorySize;
    private final String displayInventoryTitle;

    private String[] helpText = null;

    private boolean forcePagination = false;
    private DSGMenu parent;

    public DSGMenu(String title, int rowsOrSlots) {
        this.displayInventoryTitle = title;

        int totalSlots = 0;
        if (rowsOrSlots <= 6) {
            totalSlots = rowsOrSlots * 9;
        } else {
            totalSlots = rowsOrSlots;
        }

        this.displayInventorySize = totalSlots;
    }

    public DSGMenu(String title, int rowsOrSlots, DSGMenu parent) {
        this(title, rowsOrSlots);
        this.parent = parent;

        if (this.parent != null) this.forcePagination = true;
    }

    public int getSize() {
        boolean addExtraRow = forcePagination || helpText != null;

        return Math.min(addExtraRow ? this.displayInventorySize + 9 : this.displayInventorySize, 6 * 9);
    }

    protected String getTitle() {
        return this.displayInventoryTitle;
    }

    public void setItem(int slot, GuiItem item) {
        if (inventory != null) {
            if (slot < inventory.getSize()) {
                inventory.setItem(slot, item.getItem());
            }
        }

        if (!isOpen) {
            actionItemList.put(slot, item);
        }
        inUseItemList.put(slot, item);
    }

    public void setItem(int x, int y, GuiItem item) {
        setItem(y * 9 + x, item);
    }

    public void updateItem(GuiItem item) {
        inUseItemList.forEach((index, guiItem) -> {
            if (guiItem == item) {
                setItem(index, item);
            }
        });
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() != this) return;

        if (inUseItemList.containsKey(e.getSlot())) {
            GuiItem item = inUseItemList.get(e.getSlot());
            item.run(new GuiItemEvent(item, e));

            inventory.setItem(e.getSlot(), item.getItem());
        }

        e.setCancelled(true);
    }

    public void open(Player player) {
        this.inventory = Bukkit.createInventory(this, this.getSize(), this.getTitle());

        this.isOpen = true;
        updateInventory();

        player.openInventory(this.inventory);
    }

    public void close() {
        this.canUserCloseIt = true;

        new ArrayList<>(this.inventory.getViewers()).forEach(HumanEntity::closeInventory);
    }

    public void close(Player player) {
        boolean oldCanClose = canUserCloseIt();
        this.canUserCloseIt = true;

        if (player.getOpenInventory().getTopInventory().getHolder() == this)
            player.closeInventory();

        this.canUserCloseIt = oldCanClose;
    }


    private void switchPreviousPage() {
        if (currentPage > 0)
            currentPage--;

        updateInventory();
    }

    private void switchNextPage() {
        if ((currentPage + 1) * (getSize() - 9) <= actionItemList.keySet().stream().mapToInt(v -> v).max().orElse(1))
            currentPage++;

        updateInventory();
    }

    private final GuiItem emptyHotbar = ActionItem.of(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).withName("").build(), null);
    private final ActionItem emptyAction = ActionItem.of(new ItemBuilder(Material.AIR).build(), null);

    private void updateInventory() {
        final int biggestSlotIndex = actionItemList.keySet().stream().mapToInt(v -> v).max().orElse(1);
        final boolean needPagination = forcePagination || (biggestSlotIndex > getSize()) || helpText != null;
        final int size = getSize() - (needPagination ? 9 : 0);

        for (int i = 0; i < size; i++) {
            GuiItem item = actionItemList.get(i + size * currentPage);
            setItem(i, item != null ? item : emptyAction);
        }

        if (needPagination) {

            GuiItem leftButton = ActionItem.of(new ItemBuilder(currentPage != 0 ? Material.LIME_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE).withName("Previous page").build(), e -> {
                this.switchPreviousPage();
                ((ActionItem) e.getGuiItem()).setItem(new ItemBuilder(currentPage != 0 ? Material.LIME_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE).withName("Previous page").build());
            });
            GuiItem rightButton = ActionItem.of(new ItemBuilder((currentPage + 1) * size <= biggestSlotIndex ? Material.LIME_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE).withName("Next page").build(), e -> {
                this.switchNextPage();
                ((ActionItem) e.getGuiItem()).setItem(new ItemBuilder((currentPage + 1) * size <= biggestSlotIndex ? Material.LIME_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE).withName("Next page").build());
            });

            for (int i = 0; i < 9; i++) {
                int slot = size + i;
                switch (i) {
                    case 0:
                        setItem(slot, leftButton);
                        break;
                    case 1:
                        int totalPages = (int) ((double) biggestSlotIndex / size + 1);
                        if (totalPages > 1) {
                            setItem(slot, ActionItem.of(new ItemBuilder(Material.PAPER).withName("Page " + (currentPage + 1) + "/" + totalPages).withAmount(currentPage + 1).build(), null));
                        } else {
                            setItem(slot, emptyHotbar);
                        }
                        break;
                    case 4:
                        setItem(slot, ActionItem.of(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).withName(ColorHelper.color(parent == null ? "Close" : "Back", ChatColor.DARK_RED.getColor(), ChatColor.RED.getColor())).build(), e -> {
                            if (parent == null) {
                                e.getEvent().getWhoClicked().closeInventory();
                            } else {
                                parent.open((Player) e.getEvent().getWhoClicked());
                            }
                        }));
                        break;
                    case 7:
                        if (helpText != null) {
                            setItem(slot, new ActionItem(new ItemBuilder(Material.KNOWLEDGE_BOOK).withName(ChatColor.GREEN + "Help").withLore(helpText).build()));
                        } else {
                            setItem(slot, emptyHotbar);
                        }
                        break;
                    case 8:
                        setItem(slot, rightButton);
                        break;
                    default:
                        setItem(slot, emptyHotbar);
                }
            }
        }
    }

    public boolean canUserCloseIt() {
        return canUserCloseIt;
    }

    public boolean isForcePagination() {
        return forcePagination;
    }

    public void setCanUserCloseIt(boolean canUserCloseIt) {
        this.canUserCloseIt = canUserCloseIt;
    }

    public void setForcePagination(boolean forcePagination) {
        this.forcePagination = forcePagination;
    }

    public void onInventoryClose(DSGMenuCloseEvent dsgMenuCloseEvent) {
        if (canUserCloseIt) dsgMenuCloseEvent.setCancelled(false);
    }

    public void setHelpText(String... text) {
        this.helpText = text;
    }
}
