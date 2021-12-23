package xyz.destillegast.dsgutils.api;

import org.bukkit.entity.Player;
import xyz.destillegast.dsgutils.signs.SignActions;

/**
 * Created by DeStilleGast 17-7-2021
 */
public interface SignManager {
    void registerHandler(String title, SignActions signActions);

    void unregisterHandler(String title);

    void forceUpdateSignsForPlayer(Player player);
}
