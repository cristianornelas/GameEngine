/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guis;

import org.lwjgl.util.vector.Matrix4f;
import shaders.ShadersProgram;

/**
 *
 * @author crist
 */


public class GuiShader extends ShadersProgram{
     
    private static final String VERTEX_FILE = "src/guis/guiVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/guis/guiFragmentShader.txt";
     
    private int location_transformationMatrix;
 
    public GuiShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
     
    public void loadTransformation(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }
 
    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
     
     
     
 
}
