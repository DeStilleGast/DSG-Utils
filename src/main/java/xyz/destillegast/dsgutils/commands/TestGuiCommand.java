package xyz.destillegast.dsgutils.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.destillegast.dsgutils.gui.ConfirmDialog;
import xyz.destillegast.dsgutils.gui.DSGMenu;
import xyz.destillegast.dsgutils.gui.item.ActionItem;
import xyz.destillegast.dsgutils.gui.item.MultiSelectItem;
import xyz.destillegast.dsgutils.gui.item.RangeItem;
import xyz.destillegast.dsgutils.gui.item.ToggleItem;
import xyz.destillegast.dsgutils.helpers.ColorHelper;
import xyz.destillegast.dsgutils.helpers.ItemBuilder;

import java.awt.*;
import java.util.Arrays;

/**
 * Created by DeStilleGast 20-2-2021
 */
public class TestGuiCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if(args.length != 0 && args[0].equalsIgnoreCase("confirm")){
                ConfirmDialog confirmDialog = new ConfirmDialog("Do you wanna delete this item", aBoolean -> {
                    sender.sendMessage("Sender said: " + aBoolean);
                });
                confirmDialog.open((Player) sender);
            }else {

                DSGMenu menu = new DSGMenu("Test GUI", 3);

                menu.setItem(0, ActionItem.of(Material.BARRIER, event -> {
                    sender.sendMessage("Hi");
                    menu.close();
                }));
                menu.setItem(1, new ToggleItem(false, b -> sender.sendMessage("new state: " + b)));
                menu.setItem(2, ActionItem.of(new ItemBuilder(Material.BOOK).withLore("Dit is gewoon een hex kleur in naam test").withName("Kleur" + ChatColor.of(Color.GREEN) + "TEST").build(), event -> {

                }));
                java.util.List<String> testValues = Arrays.asList("Test", "Hello world", "Meow =^.^=");
                menu.setItem(3, new MultiSelectItem(new ItemBuilder(Material.PAPER).build(), testValues, 0, e -> {
                    sender.sendMessage("new value: " + testValues.get(e.getNewIndex()));
                    e.getOwner().setItem(new ItemStack(Material.HOPPER));
                }));

                menu.setItem(6, new RangeItem(new ItemStack(Material.END_CRYSTAL), 0, 100, 50, integer -> sender.sendMessage("new value: " + integer)));
//            menu.setItem(8, ActionItem.of(Material.BARRIER, null));

                for (int i = 1; i < 101; i++) {
                    menu.setItem(26, ActionItem.of(new ItemBuilder(Material.values()[i]).withName(ColorHelper.color("Item: " + i, Color.BLUE, Color.YELLOW)).build(), null));
                }

//            menu.setItem(200, ActionItem.of(new ItemBuilder(Material.ENDER_EYE).withName("You found me :D").build(), null));

                menu.setHelpText("Hello world", ChatColor.GOLD + "This is gold", ChatColor.of(new Color(100, 0, 100)) + "This text is purple");

                menu.open((Player) sender);
            }
        } else {
            sender.sendMessage("ingame only!");
        }


        return false;
    }
}
