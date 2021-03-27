package xyz.destillegast.dsgutils.gui.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.destillegast.dsgutils.helpers.ItemBuilder;

import java.util.function.Consumer;

/**
 * Created by DeStilleGast 7-2-2021
 */
public class ToggleItem implements GuiItem {

    private final ItemStack active, notActive;
    private final Consumer<Boolean> onValueUpdate;
    private boolean currentState;


    public ToggleItem(boolean currentState, Consumer<Boolean> onValueUpdate) {
        this(currentState,
                onValueUpdate,
                new ItemBuilder(Material.LIME_CONCRETE).withName(ChatColor.GREEN + "Enabled").build(),
                new ItemBuilder(Material.RED_CONCRETE).withName(ChatColor.RED + "Disabled").build());
    }

    public ToggleItem(String name, boolean currentState, Consumer<Boolean> onValueUpdate) {
        this(currentState,
                onValueUpdate,
                new ItemBuilder(Material.LIME_CONCRETE).withName(name + ": " + ChatColor.GREEN + "Enabled").build(),
                new ItemBuilder(Material.RED_CONCRETE).withName(name + ": " + ChatColor.RED + "Disabled").build());
    }


    public ToggleItem(boolean currentState, Consumer<Boolean> onValueUpdate, ItemStack activeItem, ItemStack notActiveItem) {
        this.currentState = currentState;
        this.onValueUpdate = onValueUpdate;

        this.active = activeItem;
        this.notActive = notActiveItem;
    }


    @Override
    public void run(GuiItemEvent event) {
        currentState = !currentState;
        onValueUpdate.accept(currentState);
    }

    @Override
    public ItemStack getItem() {
        return currentState ? active : notActive;
    }
}
