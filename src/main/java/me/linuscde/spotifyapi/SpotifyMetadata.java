package me.linuscde.spotifyapi;

import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Author: LinusCDE
 * GitHub: https://github.com/LinusCDE
 * License: GPL v2.0
 *
 * Copyright (C) 2017 LinusCDE
 */
public class SpotifyMetadata {
	
	Properties metadata = new Properties();
	
	public SpotifyMetadata(String processResult) throws SpotifyException.CantAccessMetadata{
			/*
			 * Schema:
			 * 
			 * mpris:artUrl: https://open.spotify.com/image/...
			 * mpris:length: 214789000 (secific to each song)
			 * mpris:trackid: spotify:track:...
			 * xesam:album: Album Name
			 * xesam:albumArtist: Artist Name
			 * xesam:artist: Artist Name (seems to be the same as albumArtist)
			 * xesam:autoRating: 0.47 (secific to each song)
			 * xesam:discNumber: 1 (secific to each song)
			 * xesam:title: Song Name
			 * xesam:trackNumber: 1 (secific to each song)
			 * xesam:url: https://open.spotify.com/track/...
			 */
			
			if(!processResult.contains("\n"))
				throw new SpotifyException.CantAccessMetadata();
			
			for(String line : processResult.split(Pattern.quote("\n"))){
				if(line.equals("") || !line.contains(":")) continue;
				
				line = line.replace("://", "<--/-->//"); // Prevent Url to be cut
				int indexOfKVSplitter = line.lastIndexOf(':');
				line = line.replace("<--/-->//", "://"); // Prevent Url to be cut
				
				String key = line.substring(0, indexOfKVSplitter);
				String value = line.substring(indexOfKVSplitter + 1);
				if(value.startsWith(" ")) // Remove leading " " after the ":"
					value = value.substring(1);
				
				metadata.setProperty(key, value);
			}
	}
	
	public Properties getAllProperties(){
		return metadata;
	}
	
	public String getSongTitle(){
		return metadata.getProperty("xesam:title", null);
	}
	
	public String getAlbumName(){
		return metadata.getProperty("xesam:album", null);
	}
	
	public String getAlbumArtist(){
		return metadata.getProperty("xesam:albumArtist", null);
	}
	
	public String getArtist(){
		return metadata.getProperty("xesam:artist", null);
	}
	
	public String getSongUrl(){
		return metadata.getProperty("xesam:url", null);
	}
	
	public String getArtImageUrl(){
		return metadata.getProperty("mpris:artUrl", null);
	}
	
}
