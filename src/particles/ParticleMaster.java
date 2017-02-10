/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package particles;

import entities.Camera;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.naming.ldap.HasControls;
import org.lwjgl.util.vector.Matrix4f;
import renderengine.Loader;

/**
 *
 * @author crist
 */
public class ParticleMaster {
    private static Map<ParticlesTexture, List<Particle>> particles = new HashMap<ParticlesTexture,List<Particle>>();
    private static ParticleRenderer renderer;
    
    public static void init(Loader loader, Matrix4f projectionMatrix){
        renderer = new ParticleRenderer(loader, projectionMatrix);
    }
    
    public static void update(Camera camera){
        
        Iterator<Entry<ParticlesTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
        while(mapIterator.hasNext()){
            List<Particle> list = mapIterator.next().getValue();
            Iterator<Particle> iterator = list.iterator();
            while(iterator.hasNext()){
                Particle p = iterator.next();
                boolean stillAlive = p.update(camera);
                if(!stillAlive){
                    iterator.remove();
                    if(list.isEmpty())
                        mapIterator.remove();
                }
            }
            InsertionSort.sortHighToLow(list);
        }
        
    }
    
    public static void renderParticles(Camera camera) {
        if(!particles.isEmpty()){
            renderer.render(particles, camera);
        }
    }
    
    public static void cleanUp(){
        renderer.cleanUp();
    }
    
    public static void addParticles(Particle particle){
        List<Particle> list = particles.get(particle.getTexture());
        if(list==null){
            list = new ArrayList<Particle>();
            particles.put(particle.getTexture(), list);
        }
        list.add(particle);
    }
    
}
