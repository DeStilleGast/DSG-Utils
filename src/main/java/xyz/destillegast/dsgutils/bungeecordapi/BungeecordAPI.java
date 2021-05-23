package xyz.destillegast.dsgutils.bungeecordapi;

import com.google.common.io.ByteArrayDataOutput;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import xyz.destillegast.dsgutils.helpers.Tuple;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by DeStilleGast 23-5-2021
 */
public class BungeecordAPI {

    private final Plugin plugin;
    private final String BungeeChannel = "BungeeCord";

    public BungeecordAPI(Plugin plugin){
        this.plugin = plugin;

        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, BungeeChannel);
    }

    /**
     * Send player to a named server
     * @param player Player to send
     * @param server Server to send too
     * @return true if request was successful
     */
    public boolean sendPlayerToServer(Player player, String server){
        return sendData(player, "Connect", server);
    }

    /**
     * Get real IP from player
     * @param player
     * @param onResult Callback with IP from player
     * @return true if request was successful
     */
    public boolean getRealIp(Player player, Consumer<Tuple<String, Integer>> onResult){
        player.getServer().getMessenger().registerIncomingPluginChannel(plugin, BungeeChannel, createListener("IP", in -> {
            try {
                String ip = in.readUTF();
                int port = in.readInt();

                onResult.accept(new Tuple<>(ip, port));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }));

        return sendData(player, "IP");
    }

    /**
     * Get player count from given server name
     * @param server Servername
     * @param playerCountConsumer player count on given server
     * @return true if request was successful
     */
    public boolean getPlayerCount(String server, Consumer<Integer> playerCountConsumer){
        Player player = getRandomPlayer();
        if(player != null) {

            player.getServer().getMessenger().registerIncomingPluginChannel(plugin, BungeeChannel, createListener("PlayerCount", in -> {
                try {
                    if(!in.readUTF().equals(server)) return false;

                    int playerCountOnServer = in.readInt();
                    playerCountConsumer.accept(playerCountOnServer);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }));

            sendData(player, "PlayerCount", server);
            return true;
        }

        return false;
    }

    /**
     * Get list of players from given server
     * @param server Servername
     * @param playerListConsumer Playernames from given server
     * @return true if request was successful
     */
    public boolean getPlayerList(String server, Consumer<List<String>> playerListConsumer){
        Player player = getRandomPlayer();
        if(player != null) {

            player.getServer().getMessenger().registerIncomingPluginChannel(plugin, BungeeChannel, createListener("PlayerList", in -> {
                try {
                    if(!in.readUTF().equals(server)) return false;

                    String[] playerNames = in.readUTF().split(", ");
                    playerListConsumer.accept(Arrays.asList(playerNames));
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }));

            sendData(player, "PlayerList", server);
            return true;
        }

        return false;
    }

    /**
     * Get List of servernames
     * @param serverListConsumer List of servernames
     * @return true if request was successful
     */
    public boolean getServerList(Consumer<List<String>> serverListConsumer){
        Player player = getRandomPlayer();
        if(player != null) {

            player.getServer().getMessenger().registerIncomingPluginChannel(plugin, BungeeChannel, createListener("GetServers", in -> {
                try {
                    String[] serverList = in.readUTF().split(", ");
                    serverListConsumer.accept(Arrays.asList(serverList));
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }));

            sendData(player, "GetServers");
            return true;
        }

        return false;
    }

    /**
     * Get the current server name given from Bungeecord
     * @param serverNameConsumer current servername
     * @return true if request was successful
     */
    public boolean getCurrentServerName(Consumer<String> serverNameConsumer){
        Player player = getRandomPlayer();
        if(player != null) {

            player.getServer().getMessenger().registerIncomingPluginChannel(plugin, BungeeChannel, createListener("GetServer", in -> {
                try {
                    serverNameConsumer.accept(in.readUTF());
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }));

            sendData(player, "GetServer");
            return true;
        }

        return false;
    }

    /**
     * Get UUID from player
     * @param player
     * @param uuidConsumer
     * @return true if request was successful
     */
    public boolean getPlayerUUID(Player player, Consumer<String> uuidConsumer){
        if(player != null) {
            player.getServer().getMessenger().registerIncomingPluginChannel(plugin, BungeeChannel, createListener("UUID", in -> {
                try {
                    uuidConsumer.accept(in.readUTF());
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }));

            sendData(player, "UUID");
            return true;
        }

        return false;
    }

    /**
     * Kick player from the entire network
     * @param player player to kick
     * @param message kick message
     * @return true if request was successful
     */
    public boolean kickPlayer(Player player, String message){
        if(player != null) {
            sendData(player, "KickPlayer", player.getName(), message);
            return true;
        }

        return false;
    }

    private Player getRandomPlayer(){
        return plugin.getServer().getOnlinePlayers().stream().findFirst().orElseGet(null);
    }

    private boolean sendData(Player player, String subChannel, String...extraData){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        try {
            out.writeUTF(subChannel);
            if(extraData != null){
                for (String data : extraData) {
                    out.writeUTF(data);
                }
            }

            player.sendPluginMessage(plugin, BungeeChannel, stream.toByteArray());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private PluginMessageListener createListener(String subChannel, Function<DataInputStream, Boolean> inputStreamConsumer){
        return new PluginMessageListener() {
            @Override
            public void onPluginMessageReceived(String channel, Player player, byte[] message) {
                if (!channel.equals(BungeeChannel)) return;

                ByteArrayInputStream stream = new ByteArrayInputStream(message);
                DataInputStream in = new DataInputStream(stream);

                boolean shouldUnregister = false;
                try {
                    if(!in.readUTF().equals(subChannel)) return; // check subchannel
                    shouldUnregister = inputStreamConsumer.apply(in);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(shouldUnregister) {
                    player.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, BungeeChannel, this);
                }
            }
        };
    }
}
