package net.dmulloy2.survivalgames.net;

import java.net.ServerSocket;
import java.net.Socket;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.util.Util;

public class Webserver extends Thread {
    private final SurvivalGames plugin;

    public Webserver(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        try {
            ServerSocket st = new ServerSocket(880);

            while (!plugin.isDisabling()) {
                Socket skt = st.accept();

                // Spin off request to a new thread to be handled
                Connection c = new Connection(plugin, skt);
                c.start();
            }

            st.close();
        } catch (Exception e) {
            plugin.getLogHandler().debug(Util.getUsefulStack(e, "running webserver"));
        }
    }
}
