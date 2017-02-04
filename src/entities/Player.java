/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import renderengine.DisplayManager;
import terrain.Terrain;

/**
 *
 * @author crist
 */
public class Player extends Entity {
    
    private static final float RUN_SPEED = 200;
    private static final float TURN_SPEED = 160;
    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private int health = 100;
    private int ammo = 20;
    
    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }
    
    
    public void move(Terrain terrain){
        checkInputs();
        super.increaseRotations(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
        float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx, 0.0f, dz);
        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
        if(super.getPosition().y < terrainHeight)
            super.getPosition().y = terrainHeight;
        
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && super.getPosition().y < 500){
            super.increasePosition(0, 0.5f, 0);
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
            super.increasePosition(0, -0.5f, 0);
        }
        
    }
    
    private void checkInputs(){
        if(Keyboard.isKeyDown(Keyboard.KEY_W)){
            this.currentSpeed = RUN_SPEED;
        }
        else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
            this.currentSpeed = -RUN_SPEED;
        }
        else{
            this.currentSpeed = 0;
        }
        
        if(Keyboard.isKeyDown(Keyboard.KEY_D)){
            this.currentTurnSpeed = -TURN_SPEED;
        }
        else if(Keyboard.isKeyDown(Keyboard.KEY_A)){
            this.currentTurnSpeed = TURN_SPEED;
        }
        else{
            this.currentTurnSpeed = 0;
        }
    }
    
    
    
}
