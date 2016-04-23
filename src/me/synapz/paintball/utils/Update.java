package me.synapz.paintball.utils;

import me.synapz.paintball.enums.UpdateResult;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class Update {

    private String newVersion;
    private Plugin pb;
    private UpdateResult result = UpdateResult.DISABLED;
    private static Update updater;

    public Update(final Plugin pb) {
        if (updater == null) {
            this.pb = pb;
            check();
            updater = this;
        }
    }

    public static Update getUpdater() {
        return updater;
    }

    public UpdateResult getResult() {
        return result;
    }

    public String getNewVersion() {
        return newVersion;
    }

    private void check() {
        if (!Settings.UPDATE_CHECK)
            return;

        String url = "http://synapz1.github.io";

        try {
            String source = getUrlSource(url);

            newVersion = source;
            int version = strToVersion(source); // current version from database
            int currentVersion = strToVersion(pb.getDescription().getVersion()); // current plugin version from plugin.yml

            if (version == 0) // Version should never be 0 except during a NumberFormatException, so there was an error
                result = UpdateResult.ERROR;
            else if (version > currentVersion) // If database version is 321 and current version is 320, 321 > 320 so we need to update
                result = UpdateResult.UPDATE;
            else
                result = UpdateResult.NO_UPDATE;


        } catch (IOException exc) {
            Messenger.error(Bukkit.getConsoleSender(), "Error loading update checking website");
            exc.printStackTrace();
        }
    }

    private String getUrlSource(String url) throws IOException {
        URL website = new URL(url);
        URLConnection yc = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            a.append(inputLine);
        in.close();

        return a.toString();
    }

    private int strToVersion(String strVersion) {
        int version = 0;
        strVersion = strVersion.replace("BETA-", ""); // Turns BETA-1.2.3 to 1.2.3
        strVersion = strVersion.replace(".", ""); // Separates 1.2.3 into 123

        try {
            version = Integer.parseInt(strVersion);
        } catch (NumberFormatException exc) {
            Messenger.error(Bukkit.getConsoleSender(), "Error parsing version");
            exc.printStackTrace();
        }

        return version;
    }
}
