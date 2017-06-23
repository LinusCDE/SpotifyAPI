package me.linuscde.spotifyapi;

import java.io.File;
import java.io.IOException;

/**
 * Author: LinusCDE
 * GitHub: https://github.com/LinusCDE
 * License: GPL v2.0
 *
 * Copyright (C) 2017 LinusCDE
 */

public class SpotifyAPI {
	
	private static String DEF_DBUS_ADDRESS = "org.mpris.MediaPlayer2.spotify";
	private static String DEF_DBUS_PATH = "/org/mpris/MediaPlayer2";
	private static String DEF_DBUS_METHODPREFIX = "org.mpris.MediaPlayer2.";
	
	private String QDBUS_BINARY_PATH;
	
	public SpotifyAPI() throws IOException, SpotifyException{
			if(File.listRoots().length != 1 || File.listRoots()[0].equals("/")) // On Windows listRoots() can be like ["C:", "D:"] on Unix just ["/"]
				throw new SpotifyException.WrongOperatingSystemException();
			
			QDBUS_BINARY_PATH = ProcessUtils.execute("/bin/which", "qdbus");
			if(QDBUS_BINARY_PATH.contains("no qdbus in ("))
				throw new SpotifyException.QDBusNotFoundException();
			
			String methodList = ProcessUtils.execute(QDBUS_BINARY_PATH, DEF_DBUS_ADDRESS, DEF_DBUS_PATH).replace("\n", "");
			
			if(methodList.equals("") || methodList.equals("Service '" + DEF_DBUS_ADDRESS + "' does not exist."))
				throw new SpotifyException.SpotifyServiceNotFoundException();
	}
	
	/**
	 * Spotify-PlayButton
	 * @return returns true if successful
	 */
	public boolean play(){
		return playerAction("Play");
	}
	
	/**
	 * Spotify-PauseButton
	 * @return returns true if successful
	 */
	public boolean pause(){
		return playerAction("Pause");
	}
	
	/**
	 * Spotify-PlayPauseButton (Toggle)
	 * @return true if successful
	 */
	public boolean toggle(){
		return playerAction("PlayPause");
	}
	
	/**
	 * Spotify-NextButton
	 * @return true if successful
	 */
	public boolean next(){
		return playerAction("Next");
	}
	
	/**
	 * Spotify-PreviousButton
	 * @return true if successful
	 */
	public boolean previous(){
		return playerAction("Previous");
	}
	
	/**
	 * Spotify-StopButton
	 * @return true if successful
	 */
	public boolean stop(){
		return playerAction("Stop");
	}
	
	/**
	 * @return true if playing otherwise false
	 */
	public boolean isPlaying(){
		try{
			return invokeMethod("Player.PlaybackStatus").equalsIgnoreCase("Playing");
		}catch(IOException ex){}
		return false;
	}
	
	/**
	 * @return true if Paused otherwise false
	 */
	public boolean isPaused(){
		try{
			return invokeMethod("Player.PlaybackStatus").equalsIgnoreCase("Paused");
		}catch(IOException ex){}
		return false;
	}
	
	
	/**
	 * @return true if can go Next otherwise false
	 */
	public boolean canGoNext(){
		try{
			return invokeMethod("Player.CanGoNext").equalsIgnoreCase("true");
		}catch(IOException ex){}
		return false;
	}
	
	/**
	 * @return true if can go Previous otherwise false
	 */
	public boolean canGoPrevious(){
		try{
			return invokeMethod("Player.CanGoPrevious").equalsIgnoreCase("true");
		}catch(IOException ex){}
		return false;
	}
	
	/**
	 * Gets information about current song
	 * @return SpotifyMetadata (null if not Song found)
	 */
	public SpotifyMetadata getMetadata(){
		try{
			return new SpotifyMetadata(invokeMethod("Player.Metadata"));
		}catch(SpotifyException.CantAccessMetadata ex){
		}catch(IOException ex){}
		return null;
	}
	
	/**
	 * Invoke Player-Method
	 * @return returns true if successful
	 */
	public boolean playerAction(String action){
		try{
		invokeMethod("Player." + action);
		}catch(Exception ex){
			return false;
		}
		return true;
	}
	
	private String invokeMethod(String methodName, String... args) throws IOException{
		String[] cmd = new String[4 + args.length];
		cmd[0] = QDBUS_BINARY_PATH;
		cmd[1] = DEF_DBUS_ADDRESS;
		cmd[2] = DEF_DBUS_PATH;
		cmd[3] = DEF_DBUS_METHODPREFIX + methodName;
		for(int i = 0; i < args.length; i++)
			cmd[4 + i] = args[i];
		
		return ProcessUtils.execute(cmd);
		// Command in a linux term looks like: $ /usr/bin/qdbus org.mpris.MediaPlayer2.spotify /org/mpris/MediaPlayer2 org.mpris.MediaPlayer2....
	}
	
}
