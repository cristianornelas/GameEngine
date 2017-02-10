/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package audio;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

/**
 *
 * @author crist
 */
public class Test {
    public static void main(String[] args) {
        System.setProperty("user.name","crist");
        AudioMaster.init();
        AudioMaster.setListenerPosition(0,0,0);
        AL10.alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED);
        
        int buffer = AudioMaster.loadSound("audio/missile.wav");
        Source source = new Source(1, 10, 50);
        source.setVolume(0.02f);
        source.setLoop(true);
        source.play(buffer);
        
        float xPos = 8;
        source.setPosition(0, 0, 0);
        
        char c = ' ';
        while(c != 'q'){
        	xPos -= 0.05f;
        	source.setPosition(xPos, 0, 0);
        	System.out.println(xPos);
        	try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        source.delete();
        AudioMaster.cleanUp();
    }
}
