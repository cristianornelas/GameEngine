/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine;

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

    public static void main(String[] args) {
               
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        MasterRenderer renderer = new MasterRenderer(loader);
        TextMaster.init(loader);
        
        FontType font = new FontType(loader.loadTexture("ocr"), new File("res/ocr.fnt"));
        GUIText text = new GUIText("This is a test text!", 1, font, new Vector2f(0,0), 1f, true);
        
        RawModel playerModel = OBJLoader.loadObjModel("heli", loader);
        TexturedModel playerTexture = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("green")));
        Player player = new Player(playerTexture, new Vector3f(0,0,0), 0,0,0,1f);        
        
        Camera camera = new Camera(player);       
        Light light = new Light(new Vector3f(20000,40000,20000), new Vector3f(1,1,1)); 
        
        Terrain terrain = new Terrain(-1, -1, loader, new ModelTexture(loader.loadTexture("sand")), "heightmap");
        Terrain terrain2 = new Terrain(0, -1, loader, new ModelTexture(loader.loadTexture("sand")), "heightmap");
        Terrain terrain3 = new Terrain(-1, 0, loader, new ModelTexture(loader.loadTexture("sand")), "heightmap");
        Terrain terrain4 = new Terrain(0, 0, loader, new ModelTexture(loader.loadTexture("sand")), "heightmap");
        
        RawModel treeModel = OBJLoader.loadObjModel("tree", loader);
        TexturedModel treeTexture = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("treeTex")));
        List<Entity> trees = new ArrayList<Entity>();
        Random random = new Random();
        Entity entity;
        int tx, tz;
        for(int i=-5; i<5; i++){
            for(int j=-5; j<5; j++){
                float x = random.nextFloat() * Terrain.getSize() / 5 * i;
                float z = random.nextFloat() * Terrain.getSize() / 5 * j;
                float y = 0;
                
                if(x > 0)
                    tx = (int) ((x + (float) Terrain.getSize()) / (float) Terrain.getSize()) - 1;
                else
                    tx = (int) (x / (float) Terrain.getSize()) - 1;

                if(z > 0)
                    tz = (int) ((z + (float) Terrain.getSize()) / (float) Terrain.getSize()) - 1;
                else
                    tz = (int) (z / (float) Terrain.getSize()) - 1;
                if(tx == -1 && tz == -1)
                    y = terrain.getHeightOfTerrain(x, z);
                else if(tx == 0 && tz == -1)
                    y = terrain2.getHeightOfTerrain(x, z);
                else if(tx == -1 && tz == 0)
                    y = terrain3.getHeightOfTerrain(x, z);
                else if(tx == 0 && tz == 0)
                    y = terrain4.getHeightOfTerrain(x, z);
                
                entity = new Entity(treeTexture, new Vector3f(x, y, z), 0,0,0,1);
                trees.add(entity);
            }
        }
        
        GuiRenderer guiRenderer = new GuiRenderer(loader);
        List<GuiTexture> guis = new ArrayList<GuiTexture>();
        GuiTexture gui = new GuiTexture(loader.loadTexture("aim"), new Vector2f(0, 0.5f), new Vector2f(0.1f, 0.1f));
        guis.add(gui);
        
        //GameLoop
        while(!Display.isCloseRequested()) {
            int x,z;
            
            if(player.getPosition().x > 0)
                x = (int) ((player.getPosition().x + (float) Terrain.getSize()) / (float) Terrain.getSize()) - 1;
            else
                x = (int) (player.getPosition().x / (float) Terrain.getSize()) - 1;
            
            if(player.getPosition().z > 0)
                z = (int) ((player.getPosition().z + (float) Terrain.getSize()) / (float) Terrain.getSize()) - 1;
            else
                z = (int) (player.getPosition().z / (float) Terrain.getSize()) - 1;
            
            if(x == -1 && z == -1)
                player.move(terrain);
            else if(x == 0 && z == -1)
                player.move(terrain2);
            else if(x == -1 && z == 0)
                player.move(terrain3);
            else if(x == 0 && z == 0)
                player.move(terrain4);
            else
                player.setPosition(new Vector3f(0,0,0));
            
            camera.move();
            
            renderer.processTerrain(terrain);
            renderer.processTerrain(terrain2);
            renderer.processTerrain(terrain3);
            renderer.processTerrain(terrain4);
            
            for(Entity tree: trees){
                renderer.processEntity(tree);
            }
            
            renderer.processEntity(player);
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
