package c2g2.engine.graph;

import org.joml.Vector3f;

public class Material {

    private static final Vector3f DEFAULT_COLOUR = new Vector3f(1.0f, 1.0f, 1.0f);

    private Vector3f colour;
    
    private float reflectance;
    
    /*
     * Added this boolean to enable toggling of mesh texture activation. (I. Ukpo)
     */
    private boolean isTextured;

    
    public Material() {
        colour = DEFAULT_COLOUR;
        reflectance = 0;
        isTextured = false;
    }
    
    public Material(Vector3f colour, float reflectance) {
        this();
        this.colour = colour;
        this.reflectance = reflectance;
    }

 

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public float getReflectance() {
        return reflectance;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public boolean isTextured() {
        return isTextured;
    }
    
    public void setTexturedFlag(boolean val)
    {
    	isTextured=val;
    }

}