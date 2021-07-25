package xyz.destillegast.dsgutils.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.destillegast.dsgutils.gui.item.ActionItem;
import xyz.destillegast.dsgutils.helpers.ColorHelper;
import xyz.destillegast.dsgutils.helpers.ItemBuilder;

import java.util.function.Consumer;

/**
 * Created by DeStilleGast 23-7-2021
 */
public class ConfirmDialog {

    private final DSGMenu internalMenu;

    public ConfirmDialog(String question, Consumer<Boolean> resultConsumer) {
        internalMenu = new DSGMenu(ColorHelper.translate(question), 1);

        ActionItem yesItem = new ActionItem(
                new ItemBuilder(Material.GREEN_WOOL)
                        .withName(ChatColor.GREEN + "Yes").build(), (d) -> {
            resultConsumer.accept(true);
            internalMenu.close();
        });

        ActionItem noItem = new ActionItem(
                new ItemBuilder(Material.RED_WOOL)
                        .withName(ChatColor.RED + "No").build(), (d) -> {
            resultConsumer.accept(false);
            internalMenu.close();
        });

        for (int i = 0; i < 3; i++) {
            internalMenu.setItem(i, yesItem);
        }

        for (int i = 0; i < 3; i++) {
            internalMenu.setItem(8 - i, noItem);
        }
    }

    public void open(Player player){
        internalMenu.open(player);
    }

    public void close(Player player){
        internalMenu.close(player);
    }
}
