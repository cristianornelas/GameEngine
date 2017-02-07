
package entities;

import java.util.Random;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import renderengine.DisplayManager;
import terrain.Terrain;
/**
 *
 * @author fernando
 */
public class Supplies extends Entity{
    private int ammo;
    private Random random;
    
    public Supplies(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale)
    {
        super(model, position, rotX, rotY, rotZ, scale);
        Random rand = new Random();
        ammo = (rand.nextInt(10) + 1) * 10;
        random = new Random();
    }
    
    public int getAmmo()
    {
        return ammo;
    }
    
    public Vector3f generatePosition(Terrain terrain)
    {
        float x = random.nextFloat() * Terrain.getSize() / 15 * -(random.nextInt(15) + 1);
        float z = random.nextFloat() * Terrain.getSize() / 15 * -(random.nextInt(15) + 1);
        float y = terrain.getHeightOfTerrain(x, z);
        return new Vector3f(x, y, z);
    }
    
    
}
