package me.mindlessly.notenoughcoins.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.CharMatcher;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class BINScraper {
	
	
	
	public static void getBins(HashMap<String,Double> dataset, String[] data, ICommandSender sender) {
		URL url = null;
		try {
			//Shamelessly pulling data from Moulberry's website
			url = new URL("https://moulberry.codes/lowestbin.json");
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
			data = auction.split(":");
			dataset.put(data[0], Double.valueOf(data[1]));
		}
	}
}
