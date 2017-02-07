/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine;

import entities.Ammo;
import entities.Supplies;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.lwjgl.opengl.Display;
import renderengine.DisplayManager;
import renderengine.Loader;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderengine.MasterRenderer;
import renderengine.OBJLoader;
import renderengine.EntityRenderer;
import shaders.StaticShader;
import terrain.Terrain;
import textures.ModelTexture;

/**
 *
 * @author crist
 */
public class GameEngine {

    private final static int MISSILE_DMG = 10;
    private final static float MISSILE_SPEED = 200f;
    private final static float MISSILE_DISTANCECAP = 200f;
    private final static int AMMO_MAX = 500;
    
    public static void main(String[] args) {
               
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        MasterRenderer renderer = new MasterRenderer(loader);
        TextMaster.init(loader);
        
        FontType font = new FontType(loader.loadTexture("ocr"), new File("res/ocr.fnt"));
        
        RawModel playerModel = OBJLoader.loadObjModel("heli", loader);
        TexturedModel playerTexture1 = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("grey")));
        
        RawModel suppliesModel = OBJLoader.loadObjModel("supplies", loader);
        TexturedModel suppliesTexture = new TexturedModel(suppliesModel, new ModelTexture(loader.loadTexture("supply")));
        List<Supplies> SuppliesList = new ArrayList<Supplies>();
        
        int seed = new Random().nextInt(1000000000);
        
        Terrain terrain = new Terrain(-1, -1, loader, new ModelTexture(loader.loadTexture("sand")), seed);
        
        List<Player> team1 = new ArrayList<Player>();
        Player player = new Player(playerTexture1, new Vector3f(-Terrain.getSize() + 800f, terrain.getHeightOfTerrain(-Terrain.getSize() + 800f, -500)+5f, -500), 0,180,0,1f);
        player.setIsBoot(false);
        team1.add(player);
        for(int i=1; i<=1; i++){
            float x = -Terrain.getSize() + 800f + ((800/10) * i);
            team1.add(new Player(playerTexture1, new Vector3f(x , terrain.getHeightOfTerrain(x, -500) , -500), 0,180,0,1f));
        }
        
        TexturedModel playerTexture2 = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("enemy")));
        List<Player> team2 = new ArrayList<Player>();
        for(int i=0; i<10; i++){
            float x = -Terrain.getSize() + 800f + ((800/10) * i);
            team2.add(new Player(playerTexture2, new Vector3f(x , terrain.getHeightOfTerrain(x, -1900), -1900), 0, 0,0,1f));
        }
        
        RawModel missileModel = OBJLoader.loadObjModel("missile", loader);
        TexturedModel missileTexture = new TexturedModel(missileModel, new ModelTexture(loader.loadTexture("missileTex")));
        List<Ammo> missiles = new ArrayList<Ammo>();
        List<Ammo> explodedMissiles = new ArrayList<Ammo>();
        List<Player> explodedPlayers = new ArrayList<Player>();
        
        Camera camera = new Camera(player);       
        Light light = new Light(new Vector3f(20000,40000,20000), new Vector3f(1,1,1)); 
        
        
        RawModel treeModel = OBJLoader.loadObjModel("tree", loader);
        TexturedModel treeTexture = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("treeTex")));
        List<Entity> trees = new ArrayList<Entity>();
        Random random = new Random();
        Entity entity;
        int tx, tz;
        for(int i=-15; i<0; i++){
            for(int j=-15; j<0; j++){
                float x = random.nextFloat() * Terrain.getSize() / 15 * i;
                float z = random.nextFloat() * Terrain.getSize() / 15 * j;
                float y = terrain.getHeightOfTerrain(x, z);
                
                entity = new Entity(treeTexture, new Vector3f(x, y, z), 0,0,0,1);
                trees.add(entity);
            }
        }
        
        for(int i = -15 ; i < 0 ; i++)
        {
           
            float x = random.nextFloat() * Terrain.getSize() / 15 * i;
            float z = random.nextFloat() * Terrain.getSize() / 15 * -(random.nextInt(15) + 1);
            float y = terrain.getHeightOfTerrain(x, z);
        
            Supplies s = new Supplies(suppliesTexture, new Vector3f(0,0,0), 0, 0, 0, 0.1f);
            s.setPosition(s.generatePosition(terrain));
            //SuppliesList.add(new Supplies(suppliesTexture, new Vector3f(x, y, z), 0, 0, 0, 1f));
            SuppliesList.add(s);
        }
        
        
        GUIText text = null;
        
        GuiRenderer guiRenderer = new GuiRenderer(loader);
        List<GuiTexture> guis = new ArrayList<GuiTexture>();
        //GuiTexture gui = new GuiTexture(loader.loadTexture("aim"), new Vector2f(0, 0.5f), new Vector2f(0.1f, 0.1f));
        //guis.add(gui);
        
        //GameLoop
        while(!Display.isCloseRequested()) {
            if(text != null)
                TextMaster.removeText(text);
            
            text = new GUIText("Health: "+ player.getHealth()+" Ammo: "+player.getAmmo() + "/" + AMMO_MAX + " - Kill: " + player.getKill() + " Death: " + player.getDeath() /* + "/" + player.getAssists()*/, 1, font, new Vector2f(0,0), 1f, false);
            
            if(player.getPosition().x < -400 && player.getPosition().x > -2000 && player.getPosition().z < -400 && player.getPosition().z > -2000)
                player.move(terrain);
            else{
                //Limita o player ao terreno, se sair, da meia volta [ COLOCAR MSG AVISANDO QUE TA SAINDO ]
                if(player.getPosition().x >= -400){
                    player.increaseRotations(0, 180, 0);
                    player.getPosition().x -= 50;
                }
                if(player.getPosition().z >= -400){
                   player.increaseRotations(0, 180, 0);
                    player.getPosition().z -= 50;
                }
                if(player.getPosition().x <= -2000){
                    player.increaseRotations(0, 180, 0);
                    player.getPosition().x += 50;
                }
                if(player.getPosition().z <= -2000){
                    player.increaseRotations(0, 180, 0);
                    player.getPosition().z += 50;
                }
            }
            
            camera.move();
            
            renderer.processTerrain(terrain);
            
            for(Entity tree: trees){
                renderer.processEntity(tree);
            }
            
            renderer.processEntity(player);
            
            for(int i=1; i<team1.size(); i++){
                team1.get(i).moveBot(terrain, team2);
            }
            
            /*System.out.println("Pos: " + team1.get(1).getPosition());
            System.out.println("Dest: " + team1.get(1).getDestination());
            System.out.println("Dis: " + team1.get(1).getDistance(team1.get(1).getDestination()));
            System.out.println();*/
            for(int i=0; i<team2.size(); i++){
                team2.get(i).moveBot(terrain, team1);
            }
            
            for(Player ent: team1){
                renderer.processEntity(ent);
            }
            
            for(Player ent: team2){
                renderer.processEntity(ent);
            }
            
            for(Supplies ent: SuppliesList){
                renderer.processEntity(ent);
            }
           
            
            
            if(player.launchMissile()){
                int posX = Mouse.getX();
                int posY = Mouse.getY();
                missiles.add(new Ammo(missileTexture, new Vector3f(player.getPosition().x, player.getPosition().y, player.getPosition().z), MISSILE_DMG, MISSILE_SPEED, MISSILE_DISTANCECAP , player.getRotX(), player.getRotY(), player.getRotZ(), 0.125f));
            }
            
            if(SuppliesList.size() > 0)
            {
                for (Supplies s: SuppliesList)
                {
                    if(player.getDistance(s.getPosition()) <= 5 && player.getAmmo() < AMMO_MAX){
                        player.setAmmo(Math.min(player.getAmmo() + s.getAmmo(), AMMO_MAX));
                        s.setPosition(s.generatePosition(terrain));
                        break;
                    }
                    
                }
            }
            
            if(missiles.size() > 0){
                for(Ammo missile: missiles){
                    missile.move(terrain);
                    renderer.processEntity(missile);
                    if(missile.explodeMissile())
                        explodedMissiles.add(missile);
                    for(Player p: team2)
                    {
                        if(p.getDistance(missile.getPosition()) <= 5)
                        {
                            p.takeDamage(missile.getDmg());
                            if(p.getHealth() <= 0)
                            {
                                explodedPlayers.add(p);
                                player.setKill( player.getKill() + 1);
                            }
                            //System.out.println(p.getHealth());
                            explodedMissiles.add(missile);
                        }
                    }
                }

            }
            
            if(explodedMissiles.size() > 0){
                for(Ammo exploded: explodedMissiles){
                    missiles.remove(exploded);
                }
                explodedMissiles.clear();
            }
            
            if(explodedPlayers.size() > 0)
            {
               
                for(Player p: explodedPlayers)
                {
                    team1.remove(p);
                    team2.remove(p);
                }
                explodedPlayers.clear();
            }
            
            renderer.render(light, camera);
            guiRenderer.render(guis);
            TextMaster.render();
            DisplayManager.updateDisplay();  
        }
        TextMaster.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
    
}
