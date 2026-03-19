/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import java.net.URL;
import javax.sound.sampled.*;


public class Sound {
    
    private Clip clip;
    
    public void load(String filename){
        
        try{
            
            URL url = getClass().getResource("/assets/game_music/" + filename);
            AudioInputStream rawAudio = AudioSystem.getAudioInputStream(url);
            
            AudioFormat originalFormat = rawAudio.getFormat();
            AudioFormat compatibleFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                originalFormat.getSampleRate(), 16,
                originalFormat.getChannels(),
                originalFormat.getChannels() * 2,
                originalFormat.getSampleRate(),
                false
            );
            AudioInputStream convertedAudio = AudioSystem.getAudioInputStream(
                compatibleFormat, rawAudio
            );
            
            clip = AudioSystem.getClip();
            clip.open(convertedAudio);
            
        }catch(Exception e){
            
            System.out.println("Sound error: " + filename);
            e.printStackTrace();
            
        }
    }
    
    public void play(){
        clip.setFramePosition(0);
        clip.start();
        
    }
    
    public void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        
    }
    
    public void stop(){
        clip.stop();
        
    }
    
    public boolean isRunning() {
        return clip != null && clip.isRunning();
    }
    
    public int getDurationTicks() {
    if (clip == null) return 0;
    return (int)(clip.getMicrosecondLength() / 1000000.0 * 60);
}
    
}
