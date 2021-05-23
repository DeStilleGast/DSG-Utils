package xyz.destillegast.dsgutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.destillegast.dsgutils.DSGUtils;
import xyz.destillegast.dsgutils.bungeecordapi.BungeecordAPI;

/**
 * Created by DeStilleGast 23-5-2021
 */
public class TestBungeeAPICommand implements CommandExecutor {

    private final BungeecordAPI bungeecordAPI;

    public TestBungeeAPICommand(DSGUtils main) {
        this.bungeecordAPI = new BungeecordAPI(main);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            bungeecordAPI.getRealIp((Player) sender, stringIntegerTuple -> {
                sender.sendMessage("IP: " + stringIntegerTuple.getPartA() + ":" + stringIntegerTuple.getPartB());
            });

            bungeecordAPI.getCurrentServerName(s -> sender.sendMessage("Current server: " + s));

            bungeecordAPI.getServerList(strings -> sender.sendMessage("Known servers: " + String.join(", ", strings)));

        }

        return false;
    }
}
