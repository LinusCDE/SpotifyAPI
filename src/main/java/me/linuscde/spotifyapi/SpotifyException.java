package me.linuscde.spotifyapi;

/**
 * Author: LinusCDE
 * GitHub: https://github.com/LinusCDE
 * License: GPL v2.0
 *
 * Copyright (C) 2017 LinusCDE
 */

public class SpotifyException extends Exception{

	public SpotifyException(String message){
		super(message);
	}
	
	public static class QDBusNotFoundException extends SpotifyException {
		
		public QDBusNotFoundException() {
			super("Program QDBus was not found. User should install it with his default package manager.");
		}
		
	}
	
	public static class SpotifyServiceNotFoundException extends SpotifyException {
		
		public SpotifyServiceNotFoundException() {
			super("Spotify Service not found. User maybe have an old version of Spotify installed, Spotify is not started or not installed.");
		}
		
	}
	
	public static class WrongOperatingSystemException extends SpotifyException {
		
		public WrongOperatingSystemException() {
			super("This API only uses Unix-Utilities");
		}
		
	}
	
	public static class CantAccessMetadata extends SpotifyException {
		
		public CantAccessMetadata() {
			super("Could not get Metadata of Player. Maybe the Spotify-Instance is no more running.");
		}
		
	}
	
}
