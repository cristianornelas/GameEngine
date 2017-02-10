/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import renderengine.DisplayManager;
import terrain.Terrain;

/**
 *
 * @author crist
 */
public class Ammo extends Entity{
    
    int dmg;
    float speed;
    float distanceCapacity = 1000;

    public Ammo(TexturedModel model, Vector3f position, int damage, float speed, float distanceCapacity, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
        this.dmg = damage;
        this.speed = speed;
        this.distanceCapacity = distanceCapacity;
    }

    public void move(Terrain terrain){
        //super.increasePosition(0, 0, speed);
        float distance = speed * DisplayManager.getFrameTimeSeconds();
        distanceCapacity -= distance;
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
        if(super.getPosition().y < terrainHeight)
            distanceCapacity = 0;
        super.increasePosition(dx, 0.0f, dz);
    }
    
    public boolean explodeMissile(){
        if(distanceCapacity <= 0)
            return true;
        return false;
    }
    
    public int getDmg() {
        return dmg;
    }

    public void setDmg(int dmg) {
        this.dmg = dmg;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    
}
