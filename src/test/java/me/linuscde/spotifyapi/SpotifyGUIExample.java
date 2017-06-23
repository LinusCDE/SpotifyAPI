package me.linuscde.spotifyapi;

import me.linuscde.spotifyapi.ProcessUtils;
import me.linuscde.spotifyapi.SpotifyAPI;
import me.linuscde.spotifyapi.SpotifyException;
import me.linuscde.spotifyapi.SpotifyMetadata;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.bind.DatatypeConverter;

/**
 * Yes, this is pretty dirty (should not be static, ...)
 * But ITS JUST A TEST. This is not intended to be used meaningful, just to proof
 * the working state of the API.
 *
 * If you use this API you should remove this file, because it is useless for the API.
 */
public class SpotifyGUIExample {
	
	private static File tmpDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "spotifyController-" + System.getProperty("user.name"));
	
	private static JFrame win = new JFrame("Spotify-Controller");
	private static JLabel artImg = new JLabel();
	private static JLabel songLbl = new JLabel("??");
	private static JLabel artistLbl = new JLabel("??");
	private static JLabel albumLbl = new JLabel("??");
	private static JButton playPauseBtn = new JButton("...");
	private static JButton stopBtn = new JButton("Stop");
	private static JButton nextBtn = new JButton(">");
	private static JButton prevBtn = new JButton("<");
	
	private static SpotifyAPI API = null;
	
	public static void main(String[] args) {

		// Setup API:
		try{
		API = new SpotifyAPI();
		}catch(SpotifyException ex){
			
			if(ex instanceof SpotifyException.WrongOperatingSystemException){
				JOptionPane.showMessageDialog(win, "This is not Unix!", "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}else if(ex instanceof SpotifyException.QDBusNotFoundException){
				JOptionPane.showMessageDialog(win, "Please install the command 'qdbus'! (via default package manager)", "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}else if(ex instanceof SpotifyException.SpotifyServiceNotFoundException){
				JOptionPane.showMessageDialog(win, "Spotify not installed / started / up-to-date\nStart it if not running.", "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
			
		}catch(IOException ex){
			JOptionPane.showMessageDialog(win, "Unexpected IO-Error (more in console)", "Error", JOptionPane.ERROR_MESSAGE);
			System.out.println("Unexpected IO-Error:");
			ex.printStackTrace();
			System.exit(0);
		}
		
		// Setup Window:
		win.setLayout(null);
		
		int y = 5;
		
		int imgSize = 100;
		artImg.setBounds(5, y, imgSize, imgSize);
		y += 5;
		songLbl.setBounds(15 + imgSize, y, 500 - 15 - imgSize, 40);
		y += 30;
		artistLbl.setBounds(15 + imgSize, y, 500 - 15 - imgSize, 20);
		y += 20;
		albumLbl.setBounds(15 + imgSize, y, 500 - 15 - imgSize, 20);
		y+= 50;
		
		prevBtn.setBounds(25, y, 50, 25);
		playPauseBtn.setBounds(25 + 50 + 5, y, 200, 25);
		stopBtn.setBounds(25 + 50 + 5 + 200 + 5, y, 100, 25);
		nextBtn.setBounds(25 + 50 + 5 + 200 + 5 + 100 + 5, y, 50, 25);
		y += 30;
		
		win.add(artImg);
		win.add(songLbl);
		win.add(artistLbl);
		win.add(albumLbl);
		win.add(prevBtn);
		win.add(playPauseBtn);
		win.add(stopBtn);
		win.add(nextBtn);
		
		songLbl.setFont(new Font("Arial", Font.BOLD, 22));
		artistLbl.setFont(new Font("Arial", Font.BOLD, 14));
		albumLbl.setFont(new Font("Arial", Font.BOLD, 14));
		
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		win.setVisible(true);
		final Dimension size = new Dimension(win.getInsets().left + win.getInsets().right + 450,
				win.getInsets().top + win.getInsets().bottom + y);
		win.setSize(size); // Sometimes the windows doesn't size properly, just run it 2-3 times. ;)
		win.setResizable(false);
		
		refreshGUI();
		
		new Thread(() -> {

			try{
				Thread.sleep(200);
				win.setLocationRelativeTo(null);
			}catch(Exception ex){}
			while(win.isVisible()){
				try{
					Thread.sleep(750);

					SwingUtilities.invokeAndWait(new Runnable() {

						@Override
						public void run() {
							refreshGUI();
						}
					});
				}catch(Exception ex){}
			}

		}).start();
		
		nextBtn.addActionListener((ActionEvent e) -> {
			API.next();
			refreshGUI();
		});
		
		prevBtn.addActionListener((ActionEvent e) -> {
			API.previous();
			refreshGUI();
		});
		
		playPauseBtn.addActionListener((ActionEvent e) -> {
			if(playPauseBtn.getText().equals("Play")) API.play();
			else API.pause();
			refreshGUI();
		});
		
		stopBtn.addActionListener((ActionEvent e) -> {
			API.stop();
			refreshGUI();
		});
	}
	
	public static void refreshGUI(){
		SpotifyMetadata song = API.getMetadata();
		
		if(song == null)
			artImg.setIcon(null);
		else
			setImage(song.getArtImageUrl());
		
		songLbl.setText(song == null ? "No Song" : song.getSongTitle());
		artistLbl.setText(song == null ? "---" : "by " + song.getArtist());
		albumLbl.setText(song == null ? "---" : "Album: " + song.getAlbumName());
				
		prevBtn.setEnabled(API.canGoPrevious());
		playPauseBtn.setText(API.isPlaying() ? "Pause" : "Play");
		nextBtn.setEnabled(API.canGoNext());
	}
	
	private static String WGET_PATH = null;
	
	public static void setImage(final String url){
		
		new Thread(new Runnable() {

			/**
			 * There was a problem downloading the image with UrlConnection
			 * which seems to be a fault by the Spotify-Servers
			 * However the `wget`-Command works just fine
			 */
			public Image getImg(String url) throws Exception{
				String md5 = DatatypeConverter.printHexBinary(MessageDigest.getInstance("MD5").digest(url.getBytes("UTF-8"))).toLowerCase();
				tmpDir.mkdirs();
				File target = new File(tmpDir.getAbsolutePath() + File.separator + md5 + ".jpg");
				if(!target.exists()){
				if(WGET_PATH == null)
					WGET_PATH = ProcessUtils.execute("/bin/which", "wget").replace("\n", "");
				
				Process proc = Runtime.getRuntime().exec(new String[]{WGET_PATH, "-qO-", url});
				InputStream in = proc.getInputStream();
				FileOutputStream fout = new FileOutputStream(target);
				
				byte[] buffer = new byte[1024];
				int count;
				target.createNewFile();
				
				while((count = in.read(buffer)) > 0)
					fout.write(buffer, 0, count);
				
				fout.flush();
				fout.close();
				in.close();
				if(proc.isAlive()) proc.destroy();
				}
				
				return ImageIO.read(target);
			}
			
			@Override
			public void run() {
				try{
					artImg.setIcon(null);
					Image img = getImg(url);
					Image scaled = img.getScaledInstance(artImg.getWidth(), artImg.getHeight(), Image.SCALE_SMOOTH);
					ImageIcon icon = new ImageIcon(scaled);
					artImg.setIcon(icon);
					win.setIconImage(scaled);
				}catch(Exception ex){
					System.out.println("Img-Load-Fail:");
					ex.printStackTrace();
				}
			}
		}).start();
	}
	
}
