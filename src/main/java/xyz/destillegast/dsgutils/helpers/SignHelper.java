package xyz.destillegast.dsgutils.helpers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by DeStilleGast 17-7-2021
 */
public class SignHelper {
    /**
     * Set lines on a sign, this will affect the world
     * @param block Block of the sign
     * @param index Line index 0-3
     * @param text Text to replace, color codes supported
     * @return true if updated
     */
    public static boolean setLine(Block block, int index, String text){
        if(block.getState() instanceof Sign){
            Sign sign = (Sign) block.getState();
            sign.setLine(index, ColorHelper.translate(text));
            sign.update();
            return true;
        }
        return false;
    }

    /**
     * Get a line from sign
     * @param block Block of the sign
     * @param index line index 0-3
     * @return Line from on given sign, null if no sign was found
     */
    public static String getLine(Block block, int index){
        if(block.getState() instanceof Sign){
            return ((Sign) block.getState()).getLine(index);
        }
        return null;
    }


    /**
     * Update sign client side for all players
     * @param block Sign block
     * @param lines Text on the sign, color supported, must be 4 lines
     */
    public static void sendSignUpdate(Block block, String[] lines){
        sendSignUpdate(block.getLocation(), lines);
    }

    /**
     * Update sign client side for all players
     * @param location Sign location
     * @param lines Text on the sign, color supported, must be 4 lines
     */
    public static void sendSignUpdate(Location location, String[] lines){
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendSignUpdate(player, location, lines);
        }
    }

    /**
     * Update sign client side for specified player
     * @param block Sign block
     * @param lines Text on the sign, color supported, must be 4 lines
     */
    public static void sendSignUpdate(Player player, Block block, String[] lines){
        sendSignUpdate(player, block.getLocation(), lines);
    }

    /**
     * Update sign client side for specified player
     * @param location Sign location
     * @param lines Text on the sign, color supported, must be 4 lines
     */
    public static void sendSignUpdate(Player player, Location location, String[] lines){
        player.sendSignChange(location, Arrays.stream(lines).map(ColorHelper::translate).collect(Collectors.toList()).toArray(new String[]{}));
    }
}
