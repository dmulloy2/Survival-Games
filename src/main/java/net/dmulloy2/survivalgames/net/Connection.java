package net.dmulloy2.survivalgames.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.util.Util;

// TODO: Does this serve any sort of purpose?
public class Connection extends Thread
{
	protected BufferedReader in;
	protected DataOutputStream out;
	protected Socket skt;
	protected HashMap<String, String> html = new HashMap<String, String>();

	private final SurvivalGames plugin;
	public Connection(SurvivalGames plugin, Socket skt)
	{
		this.plugin = plugin;
		
		try
		{
			this.in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
			this.out = new DataOutputStream(skt.getOutputStream());
			this.skt = skt;
		}
		catch (Exception e)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(e, "enabling connection"));
		}
	}

	@Override
	public void run()
	{
		try
		{
			write(out, in.readLine());
			skt.close();
		}
		catch (Exception e)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(e, "running connection"));
		}
	}

	public void write(OutputStream out, String header)
	{
		String s = "HTTP/1.0 ";
		s = s + "200 OK";
		s = s + "\r\n";
		s = s + "Connection: close\r\n";
		s = s + "Server: SurvivalGames v0\r\n";
		s = s + "Content-Type: text/html\r\n";
		s = s + "\r\n";
		s = s + FileCache.getHTML(plugin, "template", true);

		try
		{
			out.write(s.getBytes());
		}
		catch (IOException e)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(e, "writing connection"));
		}
	}
}