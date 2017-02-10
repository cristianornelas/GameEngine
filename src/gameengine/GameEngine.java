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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.lwjgl.opengl.Display;
import renderengine.DisplayManager;
import renderengine.Loader;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.openal.Audio;

import audio.AudioMaster;
import audio.Source;
import particles.Particle;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticlesTexture;
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
        ParticleMaster.init(loader, renderer.getProjectionMatrix());
        TextMaster.init(loader);
          
        //TERRAIN STUFF
        int seed = new Random().nextInt(1000000000);
        Terrain terrain = new Terrain(-1, -1, loader, new ModelTexture(loader.loadTexture("sand")), seed);
        
        //TREE STUFF
        RawModel treeModel = OBJLoader.loadObjModel("tree", loader);
        TexturedModel treeTexture = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("treeTex")));
        List<Entity> trees = new ArrayList<Entity>();
        Random random = new Random();
        Entity entity;
        int tx, tz;
        for(int i=-20; i<0; i++){
            for(int j=-20; j<0; j++){
                float x = random.nextFloat() * Terrain.getSize() / 20 * i;
                float z = random.nextFloat() * Terrain.getSize() / 20 * j;
                float y = terrain.getHeightOfTerrain(x, z);
                
                entity = new Entity(treeTexture, new Vector3f(x, y, z), 0,0,0,1);
                trees.add(entity);
            }
        }
        
        //SUPLIES STUFF
        RawModel suppliesModel = OBJLoader.loadObjModel("supplies", loader);
        TexturedModel suppliesTexture = new TexturedModel(suppliesModel, new ModelTexture(loader.loadTexture("supply")));
        List<Supplies> SuppliesList = new ArrayList<Supplies>();
        for(int i = -40 ; i < 0 ; i++)
        {
            float x = random.nextFloat() * Terrain.getSize() / 40 * i;
            float z = random.nextFloat() * Terrain.getSize() / 40 * -(random.nextInt(15) + 1);
            float y = terrain.getHeightOfTerrain(x, z);
        
            Supplies s = new Supplies(suppliesTexture, new Vector3f(0,0,0), 0, 0, 0, 0.2f);
            s.setPosition(s.generatePosition(terrain));
            //SuppliesList.add(new Supplies(suppliesTexture, new Vector3f(x, y, z), 0, 0, 0, 1f));
            SuppliesList.add(s);
        }
        
        //PLAYER STUFF
        RawModel playerModel = OBJLoader.loadObjModel("chopper", loader);
        TexturedModel playerTexture1 = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("grey")));
        Player player = new Player(playerTexture1, new Vector3f(-Terrain.getSize() + 800f, terrain.getHeightOfTerrain(-Terrain.getSize() + 800f, -500)+5f, -500), 0,180,0,1f);
        player.setIsBot(false);
        
        //MAIN ROTOR STUFF
        RawModel heliceModel = OBJLoader.loadObjModel("helice", loader);
        TexturedModel heliceTexture = new TexturedModel(heliceModel, new ModelTexture(loader.loadTexture("grey")));
        Entity helice = new Entity(heliceTexture, new Vector3f(player.getPosition().x, player.getPosition().y, player.getPosition().z), 0,180,0,1f);
        
        //AMBIENT MUSIC
        AudioMaster.init();
        AudioMaster.setListenerPosition(player.getPosition().x, player.getPosition().y, player.getPosition().z);
        AL10.alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED);
        int musicBuffer = AudioMaster.loadSound("audio/music.wav");
        int missileBuffer = AudioMaster.loadSound("audio/missile.wav");
        int explosionBuffer = AudioMaster.loadSound("audio/explosion.wav");
        int playerBuffer = AudioMaster.loadSound("audio/chopper.wav");
        
        Source musicSource = new Source(1, 2, 2);
        musicSource.setLoop(true);
        musicSource.setVolume(0.6f);
        musicSource.setPosition(player.getPosition().x, player.getPosition().y, player.getPosition().z);
        musicSource.play(musicBuffer);
        
        //TEAM1 STUFF
        List<Player> team1 = new ArrayList<Player>();
        List<Entity> rotorsTeam1 = new ArrayList<Entity>();
        Map<Player, Source> playersSources = new HashMap<Player, Source>();
        team1.add(player);
        Source player1Source = new Source(1, 50, 400);
        player1Source.setVolume(0.04f);
        player1Source.setLoop(true);
        player1Source.setPosition(player.getPosition().x, player.getPosition().y, player.getPosition().z);
        playersSources.put(player, player1Source);
        rotorsTeam1.add(helice);
        for(int i=1; i<10; i++){
            float x = -Terrain.getSize() + 800f + ((800/10) * i);
            Player player1 = new Player(playerTexture1, new Vector3f(x , terrain.getHeightOfTerrain(x, -500) , -500), 0,180,0,1f);
            team1.add(player1);
            rotorsTeam1.add(new Entity(heliceTexture, new Vector3f(x , terrain.getHeightOfTerrain(x, -500) , -500), 0,180,0,1f));
            Source playerSource = new Source(1, 50, 400);
            playerSource.setVolume(0.04f);
            playerSource.setLoop(true);
            playerSource.setPosition(player.getPosition().x, player.getPosition().y, player.getPosition().z);
            playersSources.put(player1, playerSource);
        }
        
        //TEAM2 STUFF
        TexturedModel enemyTexture = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("enemy")));
        List<Player> team2 = new ArrayList<Player>();
        List<Entity> rotorsTeam2 = new ArrayList<Entity>();      
        for(int i=0; i<10; i++){
            float x = -Terrain.getSize() + 800f + ((800/10) * i);
            Player player2 = new Player(enemyTexture, new Vector3f(x , terrain.getHeightOfTerrain(x, -1900), -1900), 0, 0,0,1f);
            team2.add(player2);
            rotorsTeam2.add(new Entity(heliceTexture, new Vector3f(x , terrain.getHeightOfTerrain(x, -1900), -1900), 0,0,0,1f));
            Source playerSource = new Source(1, 50, 400);
            playerSource.setVolume(0.04f);
            playerSource.setLoop(true);
            playerSource.setPosition(player.getPosition().x, player.getPosition().y, player.getPosition().z);
            playersSources.put(player2, playerSource);
        }
        
        //MISSILE STUFF
        RawModel missileModel = OBJLoader.loadObjModel("missile", loader);
        TexturedModel missileTexture = new TexturedModel(missileModel, new ModelTexture(loader.loadTexture("missileTex")));
        List<Ammo> missiles = new ArrayList<Ammo>();
        List<Ammo> explodedMissiles = new ArrayList<Ammo>();
        List<Player> explodedPlayers = new ArrayList<Player>();
        List<Vector3f> bigExplosionPos = new ArrayList<Vector3f>();
        List<Vector3f> explosionPos = new ArrayList<Vector3f>();
        Map<Ammo, Source> missileSources = new HashMap<Ammo, Source>();
        
        //PARTICLE STUFF
        ParticlesTexture fireTexture = new ParticlesTexture(loader.loadTexture("fire"), 8);
        ParticlesTexture explosionTexture = new ParticlesTexture(loader.loadTexture("explosion"), 5);
        ParticleSystem explosion = new ParticleSystem(explosionTexture, 10, 0, -0.15f, 0.3f, 15);
        ParticleSystem bigExplosion = new ParticleSystem(explosionTexture, 15, 0, -0.15f, 1f, 50);
        ParticleSystem fire = new ParticleSystem(fireTexture, 50, 15, 0.3f, 0.15f, 5);
        fire.setDirection(new Vector3f(0,0,1), 0.1f);
        
        //TEXT STUFF
        FontType font = new FontType(loader.loadTexture("ocr"), new File("res/ocr.fnt"));
        GUIText text = null;
        
        //CAMERA AND LIGHT
        Camera camera = new Camera(player);       
        Light light = new Light(new Vector3f(20000,40000,20000), new Vector3f(1,1,1)); 
        
       
        /*GUI STUFF
        GuiRenderer guiRenderer = new GuiRenderer(loader);
        List<GuiTexture> guis = new ArrayList<GuiTexture>();
        GuiTexture gui = new GuiTexture(loader.loadTexture("aim"), new Vector2f(0, 0.5f), new Vector2f(0.1f, 0.1f));
        guis.add(gui);*/        
        
        int count1 = 0;        
        int count2 = 0;
        //GameLoop
        while(!Display.isCloseRequested()) {

            AudioMaster.setListenerPosition(player.getPosition().x, player.getPosition().y, player.getPosition().z);
        	
            //TEXT STUFF
            if(text != null)
                TextMaster.removeText(text);
            
            text = new GUIText("Health: "+ player.getHealth()+" Ammo: "+player.getAmmo() + "/" + AMMO_MAX + " - Kill: " + player.getKill() + " Death: " + player.getDeath(), 1, font, new Vector2f(0,0), 1f, false);
            
            //CAMERA AND LIGHT STUFF
            camera.move();
            renderer.render(light, camera);
                    
            //ENTITIES RENDERING
            renderer.processTerrain(terrain);
            for(Entity tree: trees){
                renderer.processEntity(tree);
            }
            for(Player ent: team1){
                renderer.processEntity(ent);
            }
            for(Entity ent: rotorsTeam1){
                ent.increaseRotations(0, 30, 0);
                renderer.processEntity(ent);
            }
            for(Player ent: team2){
                renderer.processEntity(ent);
            }
            for(Entity ent: rotorsTeam2){
                ent.increaseRotations(0, 30, 0);
                renderer.processEntity(ent);
            }
            for(Supplies ent: SuppliesList){
                renderer.processEntity(ent);
            }
            
            
            //TEAM1 MOVEMENTS
            for(int i=1; i<team1.size(); i++){
                team1.get(i).moveBot(terrain, team2);
                rotorsTeam1.get(i).getPosition().x = team1.get(i).getPosition().x;
                rotorsTeam1.get(i).getPosition().y = team1.get(i).getPosition().y;
                rotorsTeam1.get(i).getPosition().z = team1.get(i).getPosition().z;
                playersSources.get(team1.get(i)).setPosition(team1.get(i).getPosition().x, team1.get(i).getPosition().y, team1.get(i).getPosition().z);
                if(!playersSources.get(team1.get(i)).isPlaying())
                	playersSources.get(team1.get(i)).play(playerBuffer);
            }
            
            //TEAM2 MOVEMENTS  
            for(int i=0; i<team2.size(); i++){
                team2.get(i).moveBot(terrain, team1);
                rotorsTeam2.get(i).getPosition().x = team2.get(i).getPosition().x;
                rotorsTeam2.get(i).getPosition().y = team2.get(i).getPosition().y;
                rotorsTeam2.get(i).getPosition().z = team2.get(i).getPosition().z;
                playersSources.get(team2.get(i)).setPosition(team2.get(i).getPosition().x, team2.get(i).getPosition().y, team2.get(i).getPosition().z);
                if(!playersSources.get(team2.get(i)).isPlaying())
                	playersSources.get(team2.get(i)).play(playerBuffer);
            }
            
            //TERRAIN LIMIT
            if(player.getPosition().x < -400 && player.getPosition().x > -2000 && player.getPosition().z < -400 && player.getPosition().z > -2000){
                player.move(terrain);
                helice.getPosition().x = player.getPosition().x;
                helice.getPosition().y = player.getPosition().y;
                helice.getPosition().z = player.getPosition().z;
                if(!playersSources.get(player).isPlaying())
                	playersSources.get(player).play(playerBuffer);
            }
            else{
                //Limita o player ao terreno, se sair, da meia volta [ COLOCAR MSG AVISANDO QUE TA SAINDO ]
                if(player.getPosition().x >= -400){
                    player.increaseRotations(0, 180, 0);
                    player.getPosition().x -= 50;
                    helice.getPosition().x = player.getPosition().x;
                }
                if(player.getPosition().z >= -400){
                   player.increaseRotations(0, 180, 0);
                    player.getPosition().z -= 50;
                    helice.getPosition().z = player.getPosition().z;
                }
                if(player.getPosition().x <= -2000){
                    player.increaseRotations(0, 180, 0);
                    player.getPosition().x += 50;
                    helice.getPosition().x = player.getPosition().x;
                }
                if(player.getPosition().z <= -2000){
                    player.increaseRotations(0, 180, 0);
                    player.getPosition().z += 50;
                    helice.getPosition().z = player.getPosition().z;
                }
            }
            
            
            //RELOAD AND RESPAWN OF AMMO
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

            //LAUNCH MISSILE
            if(player.launchMissile()){
                int posX = Mouse.getX();
                int posY = Mouse.getY();
                Ammo missile = new Ammo(missileTexture, new Vector3f(player.getPosition().x, player.getPosition().y, player.getPosition().z), MISSILE_DMG, MISSILE_SPEED, MISSILE_DISTANCECAP , player.getRotX(), player.getRotY(), player.getRotZ(), 0.125f);
                missiles.add(missile);
                missileSources.put(missile, new Source(1, 100, 500));
            }
            
            //MISSILE MOVEMENT AND STRIKE
            if(missiles.size() > 0){
            	int i = 0;
                for(Ammo missile: missiles){
                    missile.move(terrain);
                    renderer.processEntity(missile);
                    
                    missileSources.get(missile).setVolume(1f);
                    missileSources.get(missile).setPosition(missile.getPosition().x,missile.getPosition().y,missile.getPosition().z);
                    if(!missileSources.get(missile).isPlaying())
                    	missileSources.get(missile).play(missileBuffer);
                    i++;
                     
                    if(missile.explodeMissile()){
                        explodedMissiles.add(missile);
                        explosionPos.add(new Vector3f(missile.getPosition()));
                        missileSources.get(missile).stop();
                    }
                    for(Player p: team2)
                    {
                        if(p.getDistance(missile.getPosition()) <= 5)
                        {
                            explosionPos.add(p.getExplosionPosition());
                            p.takeDamage(missile.getDmg());
                            if(p.getHealth() <= 0)
                            {
                                explodedPlayers.add(p);
                                player.setKill( player.getKill() + 1);
                            }
                            //System.out.println(p.getHealth());
                            explodedMissiles.add(missile);
                            missileSources.get(missile).stop();
                        }
                    }
                }
            }
            
            //MISSILE EXPLOSION
            if(explodedMissiles.size() > 0){
                for(Ammo exploded: explodedMissiles){
                    missiles.remove(exploded);
                    Source explosionSource = new Source(1, 100, 500);
                    explosionSource.setVolume(1f);
                    explosionSource.setPosition(exploded.getPosition().x, exploded.getPosition().y, exploded.getPosition().z);
                    explosionSource.play(explosionBuffer);
                }
                explodedMissiles.clear();
            }
            
            //PLAYER EXPLOSION
            if(explodedPlayers.size() > 0)
            {
                for(Player p: explodedPlayers)
                {
                    team1.remove(p);
                    team2.remove(p);
                    bigExplosionPos.add(new Vector3f(p.getPosition().x,p.getPosition().y+4, p.getPosition().z));
                }
                explodedPlayers.clear();
            }
            
            //PARTICLES STUFF
            //MISSILE FIRE PARTICLES
            ParticleMaster.update(camera);
            for (Ammo missile : missiles) {
                fire.generateParticles(missile.getPosition());
            }
            //MISSILE EXPLOSION PARTICLES
            if(count1 < 10){
                for (Vector3f position: explosionPos){
                    explosion.generateParticles(position);
                }
                if(explosionPos.size() > 0)
                    count1++;
            }
            else{
                explosionPos.clear();
                count1 = 0;
            }
            //PLAYER EXPLOSION PARTICLES
            if(count2 < 10){
                for (Vector3f position: bigExplosionPos){
                    bigExplosion.generateParticles(position);
                }
                if(bigExplosionPos.size() > 0)
                    count2++;
            }
            else{
                bigExplosionPos.clear();
                count2 = 0;
            }
            ParticleMaster.renderParticles(camera);
            
            //guiRenderer.render(guis);
            TextMaster.render();
            DisplayManager.updateDisplay();  
        }
        AudioMaster.cleanUp();
        ParticleMaster.cleanUp();
        TextMaster.cleanUp();
        //guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
    
}
