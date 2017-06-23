package me.linuscde.spotifyapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Author: LinusCDE
 * GitHub: https://github.com/LinusCDE
 * License: GPL v2.0
 *
 * Copyright (C) 2017 LinusCDE
 */

public class ProcessUtils {

	public static String execute(String... command) throws IOException{
		Process proc = Runtime.getRuntime().exec(command);
		
		StringBuilder sb = new StringBuilder(); // Print result to
		boolean first = true; // Is first line? for \n
		
		BufferedReader br;
		
		for(InputStream stream : new InputStream[]{proc.getInputStream(), proc.getErrorStream()}){
			
			br = new BufferedReader(new InputStreamReader(stream, "UTF-8")); // UTF-8 is never a wrong choise in Linux
			
			String l;
			while((l = br.readLine()) != null){
				if(first) 
					first = false;
				else
					sb.append("\n");
				
				sb.append(l);
			}
			
		}
		
		if(proc.isAlive()) // Kill process if not done yet
			proc.destroy();
		return sb.toString(); // Return output of Process
	}
	
}
