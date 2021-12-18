package xyz.destillegast.dsgutils.signs;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

/**
 * Created by DeStilleGast 15-7-2021
 */
public interface SignActions {

    /**
     * Event fired when a sign gets placed with registered header
     * @param player Player who placed it
     * @param block Block where it was placed
     * @param lines Lines on the sign
     * @return true if allowed to place, false will break the sign
     */
    public boolean onSignPlace(Player player, Block block, String[] lines);

    /**
     * Event fired when the sign mananger tell you can update
     * @param player Update for this specific player
     * @param block Block of sign
     */
    public void onSignUpdate(Player player, Block block);

    /**
     * Event fired when a sign gets removed with registered header
     * @param player who broke it
     * @param block where it was broken
     * @return true if allowed to break, false will keep the sign
     */
    public boolean onSignRemove(Player player, Block block);

    /**
     * Event fired when a player tries to speak with the sign
     * @param player Who tried to talk
     * @param block Where it tried to talk
     * @param action What action the player did
     */
    public void onSignInteract(Player player, Block block, Action action);
}
