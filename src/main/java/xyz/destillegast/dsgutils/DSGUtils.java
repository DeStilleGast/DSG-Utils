package xyz.destillegast.dsgutils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.destillegast.dsgutils.commands.TestBungeeAPICommand;
import xyz.destillegast.dsgutils.commands.TestGuiCommand;
import xyz.destillegast.dsgutils.helpers.Tuple;
import xyz.destillegast.dsgutils.listener.InventoryMenuListener;
import xyz.destillegast.dsgutils.api.SignManager;
import xyz.destillegast.dsgutils.signs.SignManagerImpl;
import xyz.destillegast.dsgutils.signs.TestSign;

import java.util.logging.Level;

public final class DSGUtils extends JavaPlugin implements CommandExecutor {

    private SignManagerImpl signManager;

    @Override
    public void onEnable() {
        if(!getDataFolder().exists()) getDataFolder().mkdir();

        final PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new InventoryMenuListener(), this);

        signManager = new SignManagerImpl(this);

        getCommand("dsg-random-test-command").setExecutor(this);
        getCommand("dsg-test-gui").setExecutor(new TestGuiCommand());
        getCommand("dsg-test-bungee").setExecutor(new TestBungeeAPICommand(this));

//        BaseAnimation ba = new ScrollTextAnimation("Text", ChatColor.AQUA.asBungee().getColor());
//        getServer().getScheduler().runTaskTimer(this, () -> {
//            getServer().getConsoleSender().spigot().sendMessage(ba.getText());
//            for (Player onlinePlayer : getServer().getOnlinePlayers()) {
//                onlinePlayer.spigot().sendMessage(ba.getText());
//            }
//        }, 0, 20);

        getLogger().info("Loaded");


        ConfigurationSerialization.registerClass(Tuple.class);
        new TestSign(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        signManager.disable();
    }

//    @Override
//    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//        if(sender instanceof Player){
//
////            Map<String, Object> handMap = ((Player) sender).getInventory().getItemInMainHand().serialize();
//
//            YamlConfiguration config = new YamlConfiguration();
//            config.set("Item", ((Player) sender).getInventory().getItemInMainHand());
//
//
//            ItemMeta itemMeta = ((Player) sender).getInventory().getItemInMainHand().getItemMeta();
//
//
//
//            sender.sendMessage(config.saveToString());
//
////            Map<String, Object> back = //new Gson().fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
//            ItemStack backItenm = config.getItemStack("Item");
//
//            ((Player) sender).getWorld().dropItem(((Player) sender).getLocation(), backItenm);
//        }
//
//        return true;
//    }

    public SignManager getSignManager() {
        return signManager;
    }

    public void logError(String s) {
        getLogger().log(Level.FINE, s);
    }

    public void logInfo(String s) {
        getLogger().info(s);
    }
}
