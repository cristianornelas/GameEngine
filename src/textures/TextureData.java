/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textures;

import java.nio.ByteBuffer;

/**
 *
 * @author crist
 */
public class TextureData {
    private int width;
    private int height;
    private ByteBuffer buffer;
     
    public TextureData(ByteBuffer buffer, int width, int height){
        this.buffer = buffer;
        this.width = width;
        this.height = height;
    }
     
    public int getWidth(){
        return width;
    }
     
    public int getHeight(){
        return height;
    }
     
    public ByteBuffer getBuffer(){
        return buffer;
    }
 
}