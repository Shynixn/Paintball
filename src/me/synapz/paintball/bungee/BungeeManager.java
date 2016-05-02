package me.synapz.paintball.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.enums.Databases;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Base64;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class BungeeManager implements PluginMessageListener {

    private Plugin pb;
    private HashMap<UUID, Arena> bungeePlayers = new HashMap<>();

    public BungeeManager(Plugin pb) {
        this.pb = pb;
        if (Databases.BUNGEE_ENABLED.getBoolean() && !Bukkit.getServer().getMessenger().getIncomingChannels().contains("BungeeCord")) {
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel(pb, "BungeeCord", this);
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(pb, "BungeeCord");
        }

        if (Databases.SERVER_ID.getString().equalsIgnoreCase("Generating")) {
            Random r = new Random(5);
            int randomId = r.nextInt(999);
            String serverIDString = Integer.toString(randomId);
            String serverID = Base64.getEncoder().encodeToString(serverIDString.getBytes());
            Settings.DATABASE_FILE.setValue("Bungee.serverID", serverID);
        }
    }

    public void updateBungeeSigns() {
        if (!pb.isEnabled() || !Databases.BUNGEE_ENABLED.getBoolean()) return;
        int numb = 0;
        String arenas = "";
        String sign = "";
        for (String an : ArenaManager.getArenaManager().getArenas().keySet()) {
            Arena a = ArenaManager.getArenaManager().getArenas().get(an);
            if (numb != 0) {
                arenas = arenas + ":" + a.getName();
                sign = sign + ":" + a.getSign();
            } else {
                arenas = a.getName();
                sign = a.getSign();
            }
            numb++;
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Paintball");
        out.writeUTF("Arenas");
        out.writeUTF(Databases.SERVER_ID.getString());
        out.writeUTF(arenas);
        out.writeUTF(sign);
        Bukkit.getServer().sendPluginMessage(pb, "BungeeCord", out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player sender, byte[] message) {
        if (!channel.equals("BungeeCord") || !Databases.BUNGEE_ENABLED.getBoolean()) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (!subchannel.equals("Paintball")) {
            return;
        }
        String cmd = in.readUTF();
        if (!cmd.equalsIgnoreCase("IncomingPlayer")) {
            return;
        }
        String serverID = in.readUTF();
        if (serverID.equalsIgnoreCase(Databases.SERVER_ID.getString())) {
            String player = in.readUTF();
            String arenaName = in.readUTF();
            Arena a = ArenaManager.getArenaManager().getArena(arenaName);
            // TODO: find a better way of checking if an arena is joinable
            if (a.getMax() < a.getAllPlayers().size()) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Paintball");
                out.writeUTF("Response");
                out.writeUTF(player);
                out.writeUTF("true");
                Bukkit.getServer().sendPluginMessage(pb, "BungeeCord", out.toByteArray());

                out = ByteStreams.newDataOutput();
                out.writeUTF("ConnectOther");
                out.writeUTF(player);
                out.writeUTF(Databases.BUNGEE_ID.getString());
                Bukkit.getServer().sendPluginMessage(pb, "BungeeCord", out.toByteArray());

                UUID uuid = UUID.fromString(player);
                bungeePlayers.put(uuid, a);
            } else {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Paintball");
                out.writeUTF("Response");
                out.writeUTF(player);
                out.writeUTF("false");
                Bukkit.getServer().sendPluginMessage(pb, "BungeeCord", out.toByteArray());
            }
            updateBungeeSigns();
        }
    }

    public HashMap<UUID, Arena> getBungeePlayers() {
        return bungeePlayers;
    }
}