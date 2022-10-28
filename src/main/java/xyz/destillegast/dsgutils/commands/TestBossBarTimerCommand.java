package xyz.destillegast.dsgutils.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destillegast.dsgutils.api.BossBarTimerManager;

import java.util.concurrent.TimeUnit;

public class TestBossBarTimerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player player) {
            BossBarTimerManager bbtm = Bukkit.getServicesManager().load(BossBarTimerManager.class);
            if(bbtm == null){
                sender.sendMessage("BossBarTimerManager was not found !!");
                return false;
            }

            bbtm.createBossBarTimer(player, "This is a test message [%time%]", 10, TimeUnit.SECONDS, () -> { player.sendMessage("Timer is finished"); });
        }

        return false;
    }
}
