# SpotifyAPI

This is an API written in Java to control the official Spotify-Client and/or read its state like *current playing song*, *album url*, etc..


## How to use it
Other than to mention it is licensed under the [GPL v3.0](https://www.gnu.org/licenses/gpl-3.0.de.html "License page") and 
therefore should mention anywhere that this (my) API was used, you are free to use it modify it or whatever you want. Just respect the 
license.


### Code-Examples
Recommended example:

    public void execute() {
        SpotifyAPI api;
        try {
            api = new SpotifyAPI();
        }catch(SpotifyException ex) {
            System.err.println("Error on initializing SpotifyAPI (" + ex.getClass().getName() + "): " + ex.getMessage());
            // No need for printing stacktrace since the SpotifyExceptions are stating everything
            // If this occurs, qdbus is missing or spotify is not installed or running
            return;
        }catch (IOException ex) {
            System.err.println("Unexpected IO-Error (" + ex.getClass().getName() + "): " + ex.getMessage());
            ex.printStackTrace();
            return;
        }
    
        SpotifyMetadata data = api.getMetadata(); // Contains data about Song, Album, Artist, AlbumUrl, etc.
        if(data == null){
            System.err.println("No song available.");
            return;
        }
        System.out.println("Current Song is: " + data.getSongTitle() + " from " + data.getArtist());
    
        if(api.next()) {
            System.out.println("Playing next title");
        }else{
            System.err.println("Cant play next title");
        }
    }

Quick and dirty (not recommended but surely wanted) example:

    public boolean playNextSong() {
        try {
            return new SpotifyAPI().next();
        } catch (IOException | SpotifyException ex) {}
        return false;
    }

    public String getCurrentSongByArtist() {
        try {
            SpotifyMetadata data = new SpotifyAPI().getMetadata();
            return data.getSongTitle() + " from " + data.getArtist();
        } catch (IOException | SpotifyException | NullPointerException ex) {}
        return null;
    }
    
    public void execute() {
        System.out.println("Current Song is: " + getCurrentSongByArtist());
        playNextSong();
    }


## Requirements
This API is intended to run on Unix-Like systems, especially Linux. However it should run under OS X or BSD if the 'qdbus'-Command is installed.
It would be nice to hear if it runs on OS X, (have no doubt that BSD can).

This API does **NOT** work with any **Windows**, because this API uses the 'qdbus'-Command which uses the DBUS-Interface which Windows doesn't have. And even if Windows could use it, the offical Spotify-Client wouln't use it unless its the only or most popular option on Windows.


## Building
Clone this repo with `$ git clone https://github.com/LinusCDE/SpotifyAPI.git` then *cd* into it and execute the command `$ mvn clean package` to build this Library with Maven.
Of course, if missing, you need to install *git* and *maven* which should be available in your default package manager or use [*brew*](https://brew.sh/) if you're on OS X.


## Further development
This API should be finished. But if there are any bugs or future problems with the official Spotify-Client don't struggle submitting the issue/request or contact me.
