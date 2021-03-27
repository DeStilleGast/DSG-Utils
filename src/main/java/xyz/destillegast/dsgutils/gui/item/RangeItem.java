package xyz.destillegast.dsgutils.gui.item;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import xyz.destillegast.dsgutils.helpers.ItemHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by DeStilleGast 7-2-2021
 */
public class RangeItem implements GuiItem {

    private final ItemStack itemHolder;
    private final int min, max;
    private int currentValue, stepSize;

    private final List<String> description = new ArrayList<>();

    private final Consumer<Integer> onValueChange;

    public RangeItem(ItemStack itemHolder, int min, int max, int currentValue, Consumer<Integer> onValueChange) {
        this(itemHolder, min, max, 1, currentValue, null, onValueChange);
    }

    public RangeItem(ItemStack itemHolder, int min, int max, int stepSize, int currentValue, Consumer<Integer> onValueChange) {
        this(itemHolder, min, max, stepSize, currentValue, null, onValueChange);
    }

    public RangeItem(ItemStack itemHolder, int min, int max, int currentValue, List<String> defaultLore, Consumer<Integer> onValueChange) {
        this(itemHolder, min, max, 1, currentValue, defaultLore, onValueChange);
    }

    public RangeItem(ItemStack itemHolder, int min, int max, int stepSize, int currentValue, List<String> defaultLore, Consumer<Integer> onValueChange) {
        this.itemHolder = itemHolder;
        this.min = min;
        this.max = max;
        this.stepSize = Math.abs(stepSize);
        this.onValueChange = onValueChange;
        this.currentValue = Math.max(Math.min(max, currentValue), min);

        if (defaultLore != null) description.addAll(defaultLore);

    }

    @Override
    public ItemStack getItem() {
        updateLore();
        return itemHolder;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public int getStepSize() {
        return stepSize;
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    @Override
    public void run(GuiItemEvent e) {
        int step = stepSize;
        if (e.getEvent().isShiftClick()) step *= 10;
        if (e.getEvent().isRightClick()) step *= -1;

        currentValue += step;

        if (currentValue > max) {
            currentValue = max;
        } else if (currentValue < min) {
            currentValue = min;
        }

        onValueChange.accept(currentValue);
    }

    private void updateLore() {
        ArrayList<String> newLore = new ArrayList<>(description);
        newLore.add("");
        newLore.add(ChatColor.RESET + "left click: " + ChatColor.LIGHT_PURPLE + "+" + stepSize + ChatColor.DARK_PURPLE + " (shift " + ChatColor.LIGHT_PURPLE + "+" + (stepSize * 10) + ChatColor.DARK_PURPLE + ")");
        newLore.add(ChatColor.RESET + "right click: " + ChatColor.LIGHT_PURPLE + "-" + stepSize + ChatColor.DARK_PURPLE + " (shift " + ChatColor.LIGHT_PURPLE + "-" + (stepSize * 10) + ChatColor.DARK_PURPLE + ")");

        newLore.add(ChatColor.RESET + "Current value: " + ChatColor.LIGHT_PURPLE + currentValue);

        ItemHelper.setLore(itemHolder, newLore);
    }
}
