/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author crist
 */
public class Camera {
    private Vector3f position = new Vector3f(0,0,0);
    private float pitch = 20;
    private float yaw = 0;
    private float roll;
    
    private Player player;
    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;
    
    public Camera(Player player){
         this.player = player;
    }
    

    public void move() {
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();
        float horizontaDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontaDistance, verticalDistance);
        this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
    }
    
    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
    
    private float calculateHorizontalDistance(){
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }
    
    private float calculateVerticalDistance(){
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }
     
    private void calculateCameraPosition(float horizontalDistance, float verticalDistance){
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + verticalDistance;
    }
    
   
    
    private void calculateZoom(){
        float zoomLevel = Mouse.getDWheel() * 0.1f;
        if(distanceFromPlayer >= 15 && distanceFromPlayer <= 145)
            distanceFromPlayer -= zoomLevel;
        if(distanceFromPlayer > 145)
            distanceFromPlayer = 145;
        if(distanceFromPlayer < 15)
            distanceFromPlayer = 15;
        //de 15 a 145
    }
    
    private void calculatePitch(){
        if(Mouse.isButtonDown(1) && pitch >= -1 && pitch <= 90){
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
            if(pitch < -1)
                pitch = -1;
            if(pitch > 90)
                pitch = 90;
        }
    }
    
    private void calculateAngleAroundPlayer(){
        if(Mouse.isButtonDown(1)){
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;
        }
    }
    
    
    
}
