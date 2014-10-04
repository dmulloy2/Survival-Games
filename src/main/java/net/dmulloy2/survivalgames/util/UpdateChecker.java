package net.dmulloy2.survivalgames.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class UpdateChecker {
    // Includes simple metrics!!
    public void check(Player player, Plugin p) {
        return;

        /*		@SuppressWarnings("unused")
        		String response = "";
        		String data = "";

        		String v = p.getDescription().getVersion();
        		String ip = Bukkit.getIp();
        		int port = Bukkit.getPort();
        		try
        		{
        			// IP and PORT used so data is unique so amount of servers using the
        			// plugin can be calculated correctly
        			data = URLEncoder.encode("version", "UTF-8") + "=" + URLEncoder.encode(v, "UTF-8");
        			data += "&" + URLEncoder.encode("ip", "UTF-8") + "=" + URLEncoder.encode(ip, "UTF-8");
        			data += "&" + URLEncoder.encode("port", "UTF-8") + "=" + URLEncoder.encode("" + port, "UTF-8");
        			// data += "&"+ URLEncoder.encode("a", "UTF-8") + "=" +
        			// URLEncoder.encode(""+arenas, "UTF-8");

        			URL url = new URL("http://mc-sg.org/plugins/SurvivalGames/assets/updater/updatecheck.php");
        			URLConnection conn = url.openConnection();
        			conn.setDoOutput(true);
        			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        			wr.write(data);
        			wr.flush();

        			// Get The Response
        			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        			String line;
        			while ((line = rd.readLine()) != null)
        			{
        				response = line;
        			}
        			// System.out.println(response);

        			String[] in = response.split("~");

        			Boolean b = Boolean.parseBoolean(in[0]);

        			if (b)
        			{
        				player.sendMessage(ChatColor.DARK_BLUE + "--------------------------------------");
        				player.sendMessage(ChatColor.DARK_RED + "[SurvivalGames] Update Available!");
        				player.sendMessage(ChatColor.DARK_AQUA + "Your version: " + ChatColor.GOLD + v + ChatColor.DARK_AQUA + " Latest: "
        						+ ChatColor.GOLD + in[1]);
        				player.sendMessage(ChatColor.DARK_AQUA + in[2]);
        				player.sendMessage(ChatColor.AQUA + "" + ChatColor.UNDERLINE + in[3]);
        				player.sendMessage(ChatColor.DARK_BLUE + "--------------------------------------");
        			}
        		}
        		catch (Exception e)
        		{
        			//
        		}
        */}
}
