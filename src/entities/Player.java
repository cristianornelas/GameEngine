/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.util.List;
import java.util.Random;
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
    
    private static final float RUN_SPEED = 40;
    private static final float TURN_SPEED = 160;
    private static final float HAUNTING_AREA = 400;
    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private int health = 100;
    private int ammo = 20;
    private int kill=0, death=0, assists=0;
    
    private Vector3f destination;
    private float distance =-1;
    private boolean traveling = false;
    private Player target = null;
    private boolean haunting = false;

    public int getKill() {
        return kill;
    }

    public void setKill(int kill) {
        this.kill = kill;
    }

    public int getDeath() {
        return death;
    }

    public void setDeath(int death) {
        this.death = death;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }
    
    
    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    public Vector3f getDestination() {
        return destination;
    }
    
    public void moveBot(Terrain terrain, List<Player> enemies){
        
        if(!traveling && !haunting){
            destination = randomWalk(terrain);
            traveling = true;
            
            distance = getDistance(destination);
            
            float distancePerSecond = RUN_SPEED * DisplayManager.getFrameTimeSeconds();
            float dx = (float) (distancePerSecond * Math.sin(Math.toRadians(super.getRotY())));
            float dz = (float) (distancePerSecond * Math.cos(Math.toRadians(super.getRotY())));

            float dy = 0.2f;
            
            if(destination.y < super.getPosition().y)
                dy = -dy;
            else if (destination.y == super.getPosition().y)
                dy = 0;
            
            if(super.getPosition().x < -400 && super.getPosition().x > -2000 && super.getPosition().z < -400 && super.getPosition().z > -2000)
                super.increasePosition(dx, 0, dz);
            else{
                //Limita o player ao terreno, se sair, da meia volta [ COLOCAR MSG AVISANDO QUE TA SAINDO ]
                if(super.getPosition().x >= -400){
                    super.increaseRotations(0, 180, 0);
                    super.getPosition().x -= 50;
                }
                if(super.getPosition().z >= -400){
                   super.increaseRotations(0, 180, 0);
                    super.getPosition().z -= 50;
                }
                if(super.getPosition().x <= -2000){
                    super.increaseRotations(0, 180, 0);
                    super.getPosition().x += 50;
                }
                if(super.getPosition().z <= -2000){
                    super.increaseRotations(0, 180, 0);
                    super.getPosition().z += 50;
                }
            }
            

            float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
            if(super.getPosition().y < terrainHeight)
                super.getPosition().y = terrainHeight;
            
            
        }
        else if(traveling && getDistance(destination) >= distance  && !haunting){
            Random random = new Random();
            
            int turnDir = random.nextInt(2) - 1;
            //System.err.println(turnDir);
            //System.err.println("DISTANCIA: "+ distance);
            
            distance = getDistance(destination);
            super.increaseRotations(0, turnDir * TURN_SPEED * DisplayManager.getFrameTimeSeconds(), 0);
            float distancePerSecond = RUN_SPEED * DisplayManager.getFrameTimeSeconds();
            
            float dx = (float) (distancePerSecond * Math.sin(Math.toRadians(super.getRotY())));
            float dz = (float) (distancePerSecond * Math.cos(Math.toRadians(super.getRotY())));

            float dy = 0.2f;
            
            if(destination.y < super.getPosition().y)
                dy = -dy;
            else if (destination.y == super.getPosition().y)
                dy = 0;

            if(super.getPosition().x < -400 && super.getPosition().x > -2000 && super.getPosition().z < -400 && super.getPosition().z > -2000)
                super.increasePosition(dx, 0, dz);
            else{
                //Limita o player ao terreno, se sair, da meia volta [ COLOCAR MSG AVISANDO QUE TA SAINDO ]
                if(super.getPosition().x >= -400){
                    super.increaseRotations(0, 180, 0);
                    super.getPosition().x -= 50;
                }
                if(super.getPosition().z >= -400){
                   super.increaseRotations(0, 180, 0);
                    super.getPosition().z -= 50;
                }
                if(super.getPosition().x <= -2000){
                    super.increaseRotations(0, 180, 0);
                    super.getPosition().x += 50;
                }
                if(super.getPosition().z <= -2000){
                    super.increaseRotations(0, 180, 0);
                    super.getPosition().z += 50;
                }
            }

            float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
            if(super.getPosition().y < terrainHeight)
                super.getPosition().y = terrainHeight;
            
            
        }
        else if(traveling && getDistance(destination) < distance  && !haunting){
            
            distance = getDistance(destination);
            float distancePerSecond = RUN_SPEED * DisplayManager.getFrameTimeSeconds();
            float dx = (float) (distancePerSecond * Math.sin(Math.toRadians(super.getRotY())));
            float dz = (float) (distancePerSecond * Math.cos(Math.toRadians(super.getRotY())));

            float dy = 0.2f;
            
            if(destination.y < super.getPosition().y)
                dy = -dy;
            else if (destination.y == super.getPosition().y)
                dy = 0;

            if(super.getPosition().x < -400 && super.getPosition().x > -2000 && super.getPosition().z < -400 && super.getPosition().z > -2000)
                super.increasePosition(dx, 0, dz);
            else{
                //Limita o player ao terreno, se sair, da meia volta [ COLOCAR MSG AVISANDO QUE TA SAINDO ]
                if(super.getPosition().x >= -400){
                    super.increaseRotations(0, 180, 0);
                    super.getPosition().x -= 50;
                }
                if(super.getPosition().z >= -400){
                   super.increaseRotations(0, 180, 0);
                    super.getPosition().z -= 50;
                }
                if(super.getPosition().x <= -2000){
                    super.increaseRotations(0, 180, 0);
                    super.getPosition().x += 50;
                }
                if(super.getPosition().z <= -2000){
                    super.increaseRotations(0, 180, 0);
                    super.getPosition().z += 50;
                }
            }

            float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
            if(super.getPosition().y < terrainHeight)
                super.getPosition().y = terrainHeight;
        }
        else if(!traveling && haunting){
            super.increaseRotations(target.getRotX(), target.getRotY(), target.getRotZ());
            
            float distancePerSecond = RUN_SPEED * DisplayManager.getFrameTimeSeconds();
            float dx = (float) (distancePerSecond * Math.sin(Math.toRadians(super.getRotY())));
            float dz = (float) (distancePerSecond * Math.cos(Math.toRadians(super.getRotY())));

            float dy = 0.2f;
            
            if(target.getPosition().y < super.getPosition().y)
                dy = -dy;
            else if (target.getPosition().y == super.getPosition().y)
                dy = 0;

            if(super.getPosition().x < -400 && super.getPosition().x > -2000 && super.getPosition().z < -400 && super.getPosition().z > -2000)
                super.increasePosition(dx, 0, dz);
            else{
                //Limita o player ao terreno, se sair, da meia volta [ COLOCAR MSG AVISANDO QUE TA SAINDO ]
                if(super.getPosition().x >= -400){
                    super.increaseRotations(0, 180, 0);
                    super.getPosition().x -= 50;
                }
                if(super.getPosition().z >= -400){
                   super.increaseRotations(0, 180, 0);
                    super.getPosition().z -= 50;
                }
                if(super.getPosition().x <= -2000){
                    super.increaseRotations(0, 180, 0);
                    super.getPosition().x += 50;
                }
                if(super.getPosition().z <= -2000){
                    super.increaseRotations(0, 180, 0);
                    super.getPosition().z += 50;
                }
            }

            float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
            if(super.getPosition().y < terrainHeight)
                super.getPosition().y = terrainHeight;
            
        }
        
        if(target!= null){
            super.increaseRotations(target.getRotX(), target.getRotY(), target.getRotZ());
            haunting = true;
            traveling = false;
        }

        
            
        //Player target = findClosestPlayer(enemies);
        
    }
    
    public Vector3f randomWalk(Terrain terrain){
        float minX = -2000;
        float maxX = -400;
        float minZ = -2000;
        float maxZ = -400;
        
        Random random = new Random();
        float x = random.nextFloat() * (maxX - minX) + minX;
        float z = random.nextFloat() * (maxZ - minZ) + minZ;
        
        float minY = terrain.getHeightOfTerrain(x, z);
        float maxY = 500;
        
        float y = random.nextFloat() * (maxY - minY) + minY;
        
        return new Vector3f(x, y, z);
    }
    
    public Player findClosestPlayer(List<Player> enemies){
        float closest = HAUNTING_AREA;
        Player target = null;
        
        for(Player enemy: enemies){
            float distance = getDistance(enemy.getPosition());
            if(distance < closest){
                closest = distance;
                target = enemy;
            }
        }
        return target;
    }
    
    public float getDistance(Vector3f enemy){
        float firstElem = enemy.x - super.getPosition().x;
        float secondElem = enemy.y - super.getPosition().y;
        float thirdElem = enemy.z - super.getPosition().z;
        
        firstElem *= firstElem;
        secondElem *= secondElem;
        thirdElem *= thirdElem;
        
        double sumElem = firstElem + secondElem + thirdElem;
        
        
        return (float) Math.sqrt(sumElem);
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
            super.increasePosition(0, 0.2f, 0);
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
            super.increasePosition(0, -0.2f, 0);
        }
        
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }
    
    
    
    public boolean launchMissile(){
        while (Mouse.next()){
            if (Mouse.getEventButtonState()) {
                if (Mouse.getEventButton() == 0) {
                //Left button pressed
                }
            }else {
                if (Mouse.getEventButton() == 0 && ammo > 0) {
                    ammo--;
                    return true;
                }
            }
        }
        
        return false;
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
