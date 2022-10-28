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
import xyz.destillegast.dsgutils.api.SignManager;
import xyz.destillegast.dsgutils.internalconfig.SignManagerSettings;
import xyz.destillegast.dsgutils.internalutils.Reloadable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by DeStilleGast 15-7-2021
 */
public class SignManagerImpl implements Listener, Runnable, SignManager, Reloadable {

    private final Map<String, SignActions> actionHandlers = new HashMap<>();
    private final Map<Location, String> signLocations = new LinkedHashMap<>();
    private final DSGUtils main;
    private final File configFile;

    public SignManagerImpl(DSGUtils main) {
        this.main = main;
        Bukkit.getServicesManager().register(SignManager.class, this, main, ServicePriority.Normal);

        Bukkit.getPluginManager().registerEvents(this, main);
        onReload();

        configFile = new File(main.getDataFolder(), "SignManager.yml");
        if (!configFile.exists()) {
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

    private boolean isActionBlacklisted(String rawTitle){
        return getConfig().blacklistedHandlers.contains(rawTitle) || getConfig().blacklistedHandlers.contains(getAsHeader(rawTitle));
    }

    @Override
    public void registerHandler(String title, SignActions signActions) {
        if(isActionBlacklisted(title)){
            main.logError(String.format("%s was NOT registered because it is blacklisted !", getAsHeader(title)));
            return;
        }

        actionHandlers.put(getAsHeader(title), signActions);
        main.logInfo(String.format("%s has been registered by %s", title, signActions.getClass()));
    }

    @Override
    public void unregisterHandler(String title) {
        actionHandlers.remove(getAsHeader(title));
    }

    public String getAsHeader(String title) {
        return "[" + title + "]";
    }

    @EventHandler
    public void onSignPlace(SignChangeEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof Sign) {
            String firstLine = event.getLine(0);
            if (actionHandlers.containsKey(firstLine)) {
                SignActions signAction = actionHandlers.get(firstLine);
                boolean allowSign = signAction.onSignPlace(event.getPlayer(), event.getBlock(), event.getLines());

                if (allowSign) {
                    signLocations.put(block.getLocation(), firstLine);
                    Bukkit.getScheduler().runTaskLater(main, () -> signAction.onSignUpdate(event.getPlayer(), event.getBlock()), 5);
                } else {
                    event.getBlock().breakNaturally();
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onSignRemove(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof Sign) {
            String firstLine = ((Sign) block.getState()).getLine(0);
            if (actionHandlers.containsKey(ChatColor.stripColor(firstLine))) {
                boolean allowBreak = actionHandlers.get(firstLine).onSignRemove(event.getPlayer(), block);
                if (allowBreak) {
                    signLocations.remove(event.getBlock().getLocation());
                }else{
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event) {
        if (event.hasBlock() && event.getClickedBlock() != null) {
            Block block = event.getClickedBlock();
            if (block.getState() instanceof Sign) {
                String firstLine = ((Sign) block.getState()).getLine(0);
                if (actionHandlers.containsKey(ChatColor.stripColor(firstLine))) {
                    actionHandlers.get(firstLine).onSignInteract(event.getPlayer(), block, event.getAction());
                }
            }
        }
    }

    private void updateSigns() {
//        actionHandlers.keySet().forEach(this::updateSignByTitle);
        updateSignByTitle(null);
    }

    @Override
    public void forceUpdateSignsForPlayer(Player player) {
        for (Location signLocation : signLocations.keySet()) {
            if (signLocation.getWorld() == null) continue;

            String key = signLocations.get(signLocation);

            updateSignForPlayer(player, signLocation, key);
        }
    }

    private void updateSignByTitle(String title) {
        if(isActionBlacklisted(title)) return;

        for (Location signLocation : signLocations.keySet()) {
            if(signLocation.getWorld() == null) continue;

            String key = signLocations.get(signLocation);
            if (title != null && key.equals(title) || title == null) {
                for (Player player : signLocation.getWorld().getPlayers()) {
                    updateSignForPlayer(player, signLocation, key);
                }
            }
        }
    }

    /**
     * Updates all signs that are in range of the player.
     */
    private void updateSignForPlayer(Player player, Location signLocation, String key) {
        if(signLocation.getWorld() != player.getWorld()) return;

        if (signLocation.distance(player.getLocation()) <= signLocation.getWorld().getViewDistance() * 16 - 16) {
            Bukkit.getScheduler().runTask(main, () -> {
                actionHandlers.get(key).onSignUpdate(player, signLocation.getBlock());
            });
        }
    }

    @Override
    public void run() {
        Bukkit.getScheduler().runTaskAsynchronously(main, this::updateSigns);
//        updateSigns();
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        updateSignsNearLocationForPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateSignsNearLocationForPlayer(event.getPlayer());
    }

    public void updateSignsNearLocationForPlayer(final Player player) {
        Bukkit.getScheduler().runTask(main, () -> {
            for (Location signLocation : signLocations.keySet()) {
                if (signLocation.getWorld() == player.getWorld()) {
                    if (signLocation.distance(player.getLocation()) <= getConfig().nearbyDistance) {
                        String key = signLocations.get(signLocation);

                        actionHandlers.get(key).onSignUpdate(player, signLocation.getBlock());
                    }
                }
            }
        });
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
        return location.getWorld().getName() + "," + (int) location.getX() + "," + (int) location.getY() + "," + (int) location.getZ();
    }

    private Location stringToLocation(String string) {
        String[] locParts = string.split(",");
        return new Location(Bukkit.getWorld(locParts[0]), Integer.parseInt(locParts[1]), Integer.parseInt(locParts[2]), Integer.parseInt(locParts[3]));
    }

    @Override
    public void onReload() {
        long refreshTimer = getConfig().refreshTimer * 20;
        Bukkit.getScheduler().runTaskTimer(main, this, refreshTimer, refreshTimer);
    }

    private SignManagerSettings getConfig(){
        return main.getInternalConfig().signManagerSettings;
    }
}
