package xyz.destillegast.dsgutils.signs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.ServicePriority;
import xyz.destillegast.dsgutils.DSGUtils;
import xyz.destillegast.dsgutils.helpers.ColorHelper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by DeStilleGast 15-7-2021
 */
public class SignManager implements Listener, Runnable {

    private final Map<String, SignActions> actionHandlers = new HashMap<>();
    private final Map<Location, String> signLocations = new LinkedHashMap<>();
    private final DSGUtils main;
    private final File configFile;

    public SignManager(DSGUtils main) {
        this.main = main;
        Bukkit.getServicesManager().register(SignManager.class, this, main, ServicePriority.Normal);

        Bukkit.getPluginManager().registerEvents(this, main);
        Bukkit.getScheduler().runTaskTimer(main, this, 20 * 30, 20 * 30);

        configFile = new File(main.getDataFolder(), "signManager.yml");
        if(!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration configHelper = YamlConfiguration.loadConfiguration(configFile);

        for (String key : configHelper.getKeys(false)) {
            Location loc = stringToLocation(key);
            String header = configHelper.getString(key);

            signLocations.put(loc, header);
        }
    }

    public void registerHandler(String title, SignActions signActions){
        actionHandlers.put(getAsHeader(title), signActions);
    }

    public void unregisterHandler(String title){
        actionHandlers.remove(getAsHeader(title));
    }

    public String getAsHeader(String title){
        return "[" + title + "]";
    }

    @EventHandler
    public void onSignPlace(SignChangeEvent event){
        Block block = event.getBlock();
        if(block.getState() instanceof Sign) {
            String firstLine = event.getLine(0);
            if(actionHandlers.containsKey(firstLine)) {
                boolean allowSign = actionHandlers.get(firstLine).onSignPlace(event.getPlayer(), event.getBlock());

                if (allowSign) {
                    signLocations.put(block.getLocation(), firstLine);
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onSignRemove(BlockBreakEvent event){
        Block block = event.getBlock();
        if(block.getState() instanceof Sign){
            String firstLine = ((Sign) block.getState()).getLine(0);
            if(actionHandlers.containsKey(ChatColor.stripColor(firstLine))) {
                actionHandlers.get(firstLine).onSignRemove(event.getPlayer(), block);
                signLocations.remove(event.getBlock().getLocation());
            }
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event){
        if(event.hasBlock() && event.getClickedBlock() != null){
            Block block = event.getClickedBlock();
            if(block.getState() instanceof Sign){
                String firstLine = ((Sign) block.getState()).getLine(0);
                if(actionHandlers.containsKey(ChatColor.stripColor(firstLine))) {
                    actionHandlers.get(firstLine).onSignInteract(event.getPlayer(), block);
                }
            }
        }
    }

    private void updateSigns(){
        actionHandlers.keySet().forEach(this::updateSignByTitle);
    }

    private void updateSignByTitle(String title){
        for (Location signLocation : signLocations.keySet()) {
            if(signLocation.getChunk().isLoaded()){
                String key = signLocations.get(signLocation);
                if(key.equals(title)){
                    Bukkit.getOnlinePlayers().forEach(p -> actionHandlers.get(key).onSignUpdate(p, signLocation.getBlock()));
                }
            }
        }
    }

    public static boolean setLine(Block block, int index, String line){
        if(block.getState() instanceof Sign){
            Sign sign = (Sign) block.getState();
            sign.setLine(index, ColorHelper.translate(line));
            sign.update(true);
            return true;
        }
        return false;
    }

    public static String getLine(Block block, int index){
        if(block.getState() instanceof Sign){
            return ((Sign) block.getState()).getLine(index);
        }
        return null;
    }

    public static void sendSignUpdate(Block block, String[] lines){
        sendSignUpdate(block.getLocation(), lines);
    }
    public static void sendSignUpdate(Location location, String[] lines){
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendSignUpdate(player, location, lines);
        }
    }

    public static void sendSignUpdate(Player player, Block block, String[] lines){
        player.sendSignChange(block.getLocation(), lines);
    }
    public static void sendSignUpdate(Player player, Location location, String[] lines){
        player.sendSignChange(location, lines);
    }

    @Override
    public void run() {
        updateSigns();
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event){
        updateSignsNearLocationForPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        updateSignsNearLocationForPlayer(event.getPlayer());
    }

    public void updateSignsNearLocationForPlayer(Player player){
        for (Location signLocation : signLocations.keySet()) {
            if(signLocation.distance(player.getLocation()) < 64){
                String key = signLocations.get(signLocation);

                actionHandlers.get(key).onSignUpdate(player, signLocation.getBlock());
            }
        }
    }

    public void disable() {
        try {
            YamlConfiguration config = new YamlConfiguration();

            signLocations.forEach((location, s) -> config.set(locationToString(location), s));

            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private String locationToString(Location location) {
        if (location == null) return "";
        return location.getWorld().getName() + "," + (int)location.getX() + "," + (int)location.getY() + "," + (int)location.getZ();
    }

    private Location stringToLocation(String string){
        String[] locParts = string.split(",");
        return new Location(Bukkit.getWorld(locParts[0]), Integer.parseInt(locParts[1]), Integer.parseInt(locParts[2]), Integer.parseInt(locParts[3]));
    }
}
