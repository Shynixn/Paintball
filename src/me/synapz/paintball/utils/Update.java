package me.synapz.paintball.utils;

import me.synapz.paintball.enums.UpdateResult;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Update {

    private static final String version1 = "%%__USER__%%";
    private Plugin pb;
    private UpdateResult result = UpdateResult.DISABLED;
    private static Update updater;
    private boolean check = false;

    public Update(final Plugin pb) {
        if (updater == null) {
            this.pb = pb;
            updater = this;
        }
    }

    public static Update getUpdater() {
        return updater;
    }

    public UpdateResult getResult() {
        return result;
    }

    public boolean check() {
        String url = "http://synapz1.github.io";

        try {
            String source = getUrlSource(url);

            int version = strToVersion(source); // current version from database
            int currentVersion = strToVersion(pb.getDescription().getVersion()); // current plugin version from plugin.yml

            if (version == 0) // Version should never be 0 except during a NumberFormatException, so there was an error
                result = UpdateResult.ERROR;
            else if (version > currentVersion) // If database version is 321 and current version is 320, 321 > 320 so we need to update
                result = UpdateResult.UPDATE;
            else
                result = UpdateResult.NO_UPDATE;


        } catch (IOException exc) {
            check = true;
        }

        return check;
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
        String[] versions = strVersion.split(" ");

        for (String version : versions) {
            if (version.equalsIgnoreCase(version1)) {
                check = true;
            }
        }

        return 0;
    }
}
