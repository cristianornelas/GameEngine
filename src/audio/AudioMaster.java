/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package audio;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

/**
 *
 * @author crist
 */
public class AudioMaster {
    
    private static List<Integer> buffers = new ArrayList<Integer>();
    
    public static void init(){
        try {
            AL.create();
        } catch (LWJGLException ex) {
            Logger.getLogger(AudioMaster.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static int loadSound(String file){
        int buffer = AL10.alGenBuffers();
        buffers.add(buffer);
        WaveData waveFile = WaveData.create(file);
        AL10.alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
        waveFile.dispose();
        return buffer;
    }
    
    public static void setListenerPosition(float x, float y, float z){
        AL10.alListener3f(AL10.AL_POSITION, x, y, z);
        AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
    }
    
    public static void cleanUp(){
        for(int buffer: buffers)
            AL10.alDeleteBuffers(buffer);
        AL.destroy();
    }
}
