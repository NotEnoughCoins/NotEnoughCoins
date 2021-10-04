package me.mindlessly.notenoughcoins.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import com.google.common.base.CharMatcher;
import com.google.gson.JsonParser;

import me.mindlessly.notenoughcoins.commands.Flip;

public class APIPull {
	
    public static String getUuid(String name) {
        URL url = null;
        String uuid = null;
        try {
			//Shamelessly pulling data from Moulberry's website
			url = new URL("https://api.mojang.com/users/profiles/minecraft/"+name);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		URLConnection con = null;
		try {
			con = url.openConnection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		InputStream in = null;
		try {
			in = con.getInputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String encoding = con.getContentEncoding(); 
		encoding = encoding == null ? "UTF-8" : encoding;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		int len = 0;
		try {
			while ((len = in.read(buf)) != -1) {
			    baos.write(buf, 0, len);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String body = null;
		try {
			body = new String(baos.toByteArray(), encoding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		body = CharMatcher.anyOf("}{\\\"\\\"").removeFrom(body);
		
		
		String[] auctions = body.split(",");
		for(String auction: auctions) {
			if(auction.contains("id")) {
				String[] data = auction.split(":");
				uuid = data[1];
			}
		}
		return uuid;
	}
	

	public static void webrequest(String id, String name, double purse) {
	  String uuid = "";
	  
	  	name = getUuid(name);
	   
	  	
		URL url = null;
		try {
			//Shamelessly pulling data from Moulberry's website
			url = new URL("https://api.hypixel.net/skyblock/profiles?key="+id+"&uuid="+name);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		URLConnection con = null;
		try {
			con = url.openConnection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		InputStream in = null;
		try {
			in = con.getInputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String encoding = con.getContentEncoding(); 
		encoding = encoding == null ? "UTF-8" : encoding;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		int len = 0;
		try {
			while ((len = in.read(buf)) != -1) {
			    baos.write(buf, 0, len);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String body = null;
		try {
			body = new String(baos.toByteArray(), encoding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		body = CharMatcher.anyOf("[}{\\\"\\\"").removeFrom(body);
		
	
		
		
		String[] auctions = body.split(",");
		for(String auction: auctions) {
			if(auction.contains("coin_purse")) {
				String[] data = auction.split(":");
				purse = Double.valueOf(data[1]);
			}
		}
		Flip.purse = purse;
		
	}
}
