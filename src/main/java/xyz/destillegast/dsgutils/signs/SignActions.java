package xyz.destillegast.dsgutils.signs;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

/**
 * Created by DeStilleGast 15-7-2021
 */
public interface SignActions {

    public boolean onSignPlace(Player player, Block block, String[] lines);
    public void onSignUpdate(Player player, Block block);
    public boolean onSignRemove(Player player, Block block);
    public void onSignInteract(Player player, Block block, Action action);
}
