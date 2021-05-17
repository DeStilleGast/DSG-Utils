package xyz.destillegast.dsgutils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.destillegast.dsgutils.commands.TestGuiCommand;
import xyz.destillegast.dsgutils.listener.InventoryMenuListener;

public final class DSGUtils extends JavaPlugin {


    @Override
    public void onEnable() {
        final PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new InventoryMenuListener(), this);

        getCommand("dsg-test-gui").setExecutor(new TestGuiCommand());

//        BaseAnimation ba = new ScrollTextAnimation("Text", ChatColor.AQUA.asBungee().getColor());
//        getServer().getScheduler().runTaskTimer(this, () -> {
//            getServer().getConsoleSender().spigot().sendMessage(ba.getText());
//            for (Player onlinePlayer : getServer().getOnlinePlayers()) {
//                onlinePlayer.spigot().sendMessage(ba.getText());
//            }
//        }, 0, 20);

        getLogger().info("Loaded");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
