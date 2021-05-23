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
public class BungeecordAPI implements PluginMessageListener {

    private final Plugin plugin;
    private final String BungeeChannel = "BungeeCord";

    public BungeecordAPI(Plugin plugin){
        this.plugin = plugin;

        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, BungeeChannel);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, BungeeChannel, this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(BungeeChannel)) return;
    }

    public boolean sendPlayerToServer(Player player, String server){
        return sendData(player, "Connect", server);
    }

    public boolean getRealIp(Player player, Consumer<Tuple<String, Integer>> onResult){
//        PluginMessageListener listener = new PluginMessageListener() {
//            @Override
//            public void onPluginMessageReceived(String channel, Player player, byte[] message) {
//                if (!channel.equals(BungeeChannel)) return;
//
//                ByteArrayInputStream stream = new ByteArrayInputStream(message);
//                DataInputStream in = new DataInputStream(stream);
//
//
//                try {
//                    if(!in.readUTF().equals("IP")) return; // check subchannel
//
//                    String ip = in.readUTF();
//                    int port = in.readInt();
//
//                    onResult.accept(new Tuple<>(ip, port));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                player.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, BungeeChannel, this);
//            }
//        };

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

    public boolean getPlayerCount(String server, Consumer<Integer> playerCount){
        Player player = getRandomPlayer();
        if(player != null) {

            player.getServer().getMessenger().registerIncomingPluginChannel(plugin, BungeeChannel, createListener("PlayerCount", in -> {
                try {
                    if(!in.readUTF().equals(server)) return false;

                    int playerCountOnServer = in.readInt();
                    playerCount.accept(playerCountOnServer);
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
