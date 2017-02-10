/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package audio;

import org.lwjgl.openal.AL10;

/**
 *
 * @author crist
 */
public class Source {
    private int sourceId;
    private int rolloff, reference, maxDistance;
    
    public Source(int rolloff, int reference, int maxDistance){
        sourceId = AL10.alGenSources();
        AL10.alSourcef(sourceId, AL10.AL_ROLLOFF_FACTOR, rolloff);
        AL10.alSourcef(sourceId, AL10.AL_REFERENCE_DISTANCE, reference);
        AL10.alSourcef(sourceId, AL10.AL_MAX_DISTANCE, maxDistance);
        
    }
    
    
    
    public int getRolloff() {
		return rolloff;
	}



	public void setRolloff(int rolloff) {
		this.rolloff = rolloff;
	}



	public int getReference() {
		return reference;
	}



	public void setReference(int reference) {
		this.reference = reference;
	}



	public int getMaxDistance() {
		return maxDistance;
	}



	public void setMaxDistance(int maxDistance) {
		this.maxDistance = maxDistance;
	}



	public void play(int buffer){
    	stop();
        AL10.alSourcei(sourceId, AL10.AL_BUFFER, buffer);
        countinuePlaying();
    }
    
    public void pause(){
    	AL10.alSourcePause(sourceId);
    }
    
    public void countinuePlaying(){
    	AL10.alSourcePlay(sourceId);
    }
    
    public void stop(){
    	AL10.alSourceStop(sourceId);
    }
    
    public void setVelocity(float x, float y, float z){
    	AL10.alSource3f(sourceId, AL10.AL_VELOCITY, x, y, z);
    }
    
    public void setLoop(boolean loop){
    	AL10.alSourcef(sourceId, AL10.AL_LOOPING, loop?AL10.AL_TRUE: AL10.AL_FALSE);
    }
    
    public boolean isPlaying(){
    	return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }
    
    public void setVolume(float volume){
    	AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);
    }
    
    public void setPitch(float pitch){
    	AL10.alSourcef(sourceId, AL10.AL_PITCH, pitch);
    }
    
    public void setPosition(float x, float y, float z){
    	AL10.alSource3f(sourceId, AL10.AL_POSITION, x, y, z);
    }
    
    public void delete(){
    	stop();
        AL10.alDeleteSources(sourceId);
    }
}
