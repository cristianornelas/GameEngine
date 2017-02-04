/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skybox;

import entities.Camera;
import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import renderengine.Loader;

/**
 *
 * @author crist
 */
public class SkyBoxRenderer {
    
    private static final float SIZE =500f;
	
	private static final float[] VERTICES = {        
	    -SIZE,  SIZE, -SIZE,
	    -SIZE, -SIZE, -SIZE,
	    SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	    -SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE
	};
        
        private static final String[] TEXTURE_FILES = {"right", "left", "top", "bottom", "back", "front"};
        private RawModel cube;
        private int texture;
        private SkyboxShader shader;
        
        public SkyBoxRenderer(Loader loader, Matrix4f projectionMatrix){
            cube = loader.loadToVAO(VERTICES,3);
            texture = loader.loadCubeMap(TEXTURE_FILES);
            shader = new SkyboxShader();
            shader.start();
            shader.loadProjectionMatrix(projectionMatrix);
            shader.stop();
        }
        
        public void render(Camera camera){
            shader.start();
            shader.loadViewMatrix(camera);
            GL30.glBindVertexArray(cube.getVaoID());
            GL20.glEnableVertexAttribArray(0);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
            GL20.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);
            shader.stop();
        }
}