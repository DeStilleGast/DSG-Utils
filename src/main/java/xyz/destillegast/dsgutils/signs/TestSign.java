package xyz.destillegast.dsgutils.signs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import xyz.destillegast.dsgutils.DSGUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by DeStilleGast 15-7-2021
 */
public class TestSign implements SignActions{

    private final DSGUtils utils;

    public TestSign(DSGUtils utils){
        this.utils = utils;
        utils.getSignManager().registerHandler("dsg-test", this);
    }


    @Override
    public boolean onSignPlace(Player player, Block block) {
        Bukkit.getScheduler().runTask(utils, () -> {
            SignManager.setLine(block, 3, "&4Hello #123456world");
        });

        return true;
    }

    @Override
    public void onSignUpdate(Player player, Block block) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("H:mm");
        SignManager.sendSignUpdate(block.getLocation(), new String[] { "[Time]", df.format(LocalDateTime.now().toLocalTime()), "", ""});
    }

    @Override
    public boolean onSignRemove(Player player, Block block) {
        return false;
    }

    @Override
    public void onSignInteract(Player player, Block block) {
        player.sendBlockChange(block.getLocation().subtract(0, 1, 0), Material.NETHERITE_BLOCK.createBlockData());
    }
}
