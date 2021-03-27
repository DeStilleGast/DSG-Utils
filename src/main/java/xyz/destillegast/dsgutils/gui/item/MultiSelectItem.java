package xyz.destillegast.dsgutils.gui.item;

import org.bukkit.inventory.ItemStack;
import xyz.destillegast.dsgutils.helpers.ItemBuilder;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by DeStilleGast 21-2-2021
 */
public class MultiSelectItem implements GuiItem {

    private ItemStack item;

    private final List<String> values;
    private final Consumer<MultiSelectEvent> onValueUpdate;
    private int currentState;

    private boolean allowLoopAround = false;


    public MultiSelectItem(ItemStack item, List<String> values, int currentIndex, Consumer<MultiSelectEvent> onValueUpdate) {
        this.item = item;
        this.values = values;
        this.onValueUpdate = onValueUpdate;
        this.currentState = currentIndex;
    }

    public MultiSelectItem(ItemStack item, List<String> values, int currentState, Consumer<MultiSelectEvent> onValueUpdate, boolean allowLoopAround) {
        this(item, values, currentState, onValueUpdate);
        this.allowLoopAround = allowLoopAround;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public boolean isAllowLoopAround() {
        return allowLoopAround;
    }

    public void setAllowLoopAround(boolean allowLoopAround) {
        this.allowLoopAround = allowLoopAround;
    }

    @Override
    public void run(GuiItemEvent event) {
        if (event.getEvent().isLeftClick()) {
            currentState++;
            if (currentState >= values.size()) {
                currentState = allowLoopAround ? 0 : values.size() - 1;
            }
        } else if (event.getEvent().isRightClick()) {
            currentState--;
            if (currentState < 0) currentState = allowLoopAround ? values.size() - 1 : 0;
        }

        onValueUpdate.accept(new MultiSelectEvent(this, currentState));
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(item).withName(values.get(currentState)).build();
    }

    public static class MultiSelectEvent {
        private final MultiSelectItem owner;
        private final int newIndex;

        public MultiSelectEvent(MultiSelectItem owner, int newIndex) {
            this.owner = owner;
            this.newIndex = newIndex;
        }

        public MultiSelectItem getOwner() {
            return owner;
        }

        public int getNewIndex() {
            return newIndex;
        }
    }
}
