package c2g2.engine.graph;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import c2g2.engine.graph.Texture;

public class Mesh {

    private int vaoId;

    private List<Integer> vboIdList;

    private int vertexCount;

    private Material material;
    
    private float[] pos;
    private float[] textco;
    private float[] norms;
    private int[] inds;
    
    /*
     * I changed the structure of the Mesh object such that it holds an instance of texture. I did this
     * to streamline object design and allow the program to activate/deactivate the texture when applicable.
     * 
     * (I. Ukpo)
     */
    private Texture texture;
    private Texture secondTexture;
    
    /*
     * This boolean's value is passed down to material, which in turn is returned when isTextured() is called.
     * This now makes the value of isTextured() dynamic according to the shader chosen.
     * 
     * (I. Ukpo)
     */
    private boolean useTexture;
    private boolean useNormalMapTexture;

    public Mesh(){
    	this(new float[]{0.0f,0.0f,0.0f,0.0f,0.0f,1.0f,0.0f,1.0f,0.0f,0.0f,1.0f,1.0f,1.0f,0.0f,0.0f,1.0f,0.0f,1.0f,1.0f,1.0f,0.0f,1.0f,1.0f,1.0f}, 
    			new float[]{0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f}, 
    			new float[]{0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f}, 
    			new int[]{0,6,4,0,2,6,0,3,2,0,1,3,2,7,6,2,3,7,4,6,7,4,7,5,0,4,5,0,5,1,1,5,7,1,7,3});
    	
    	texture=null;
    	secondTexture=null;
    	
    	useTexture=false;
    	useNormalMapTexture=false;
    }
    
    public void setTexture(Texture texture, Texture secondTexture)
    {
    	this.texture=texture;
    	this.secondTexture=secondTexture;
    }
    
    public void activateTexture()
    {
    	useTexture=true;
    	material.setTexturedFlag(useTexture);
    }
    
    public void deactivateTexture()
    {
    	useTexture=false;
    	material.setTexturedFlag(useTexture);
    }
    
    public void activateNormalMapTexture()
    {
    	useNormalMapTexture=true;
    }
    
    public void deactivateNormalMapTexture()
    {
    	useNormalMapTexture=false;
    }
    
    public void setMesh(float[] positions, float[] textCoords, float[] normals, int[] indices){
    	pos = positions;
    	textco = textCoords;
    	norms = normals;
    	inds = indices;
    	FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        IntBuffer indicesBuffer = null;
        System.out.println("create mesh:");
        System.out.println("v: "+positions.length+" t: "+textCoords.length+" n: "+normals.length+" idx: "+indices.length);
        try {
            vertexCount = indices.length;
            vboIdList = new ArrayList<Integer>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Position VBO
            int vboId = glGenBuffers();
            vboIdList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Texture coordinates VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // Vertex normals VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            // Index VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

        } finally {
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (textCoordsBuffer != null) {
                MemoryUtil.memFree(textCoordsBuffer);
            }
            if (vecNormalsBuffer != null) {
                MemoryUtil.memFree(vecNormalsBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
    	setMesh(positions, textCoords, normals, indices);

    	texture=null;
    	secondTexture=null;
    	
    	useTexture=false;
    	useNormalMapTexture=false;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void render() {
    	
    	//Activate texture banks if applicable. Got idea from code from Chp.8 of lwjglbook.
    	
    	//If using the regular texture, go ahead and activate/bind
    	if (useTexture)
    	{
    		
       	    // Activate first texture bank
           glActiveTexture(GL_TEXTURE0);
           
           // Bind the texture
           glBindTexture(GL_TEXTURE_2D, texture.getId());
    	}
    	
    	//If using normal map, go ahead and bind.
    	if (useNormalMapTexture)
        {
     	   //Activate second texture bank where normal map is loaded
            glActiveTexture(GL_TEXTURE1);
            
            glBindTexture(GL_TEXTURE_2D, secondTexture.getId());
        }
    	
        
        // Draw the mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
        
        //Unnecessary. Just use above calls for loading texture.
        //glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void cleanUp() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }
        
        /*
         * Call cleanup method on texture objects when done. This was adapted from chapter 8 in lwjglbook.
         */
        // Delete the texture(s)
        texture.cleanup();
        secondTexture.cleanup();
        
        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
    
    public void scaleMesh(float sx, float sy, float sz){
    	cleanUp();
    	for (int i = 0; i < pos.length/3; i++) {
			pos[3*i] = pos[3*i]*sx;
			pos[3*i+1] = pos[3*i+1]*sy;
			pos[3*i+2] = pos[3*i+2]*sz;
		}
    	
    	setMesh(pos, textco, norms, inds);
    }
    
    public void translateMesh(Vector3f trans){
    	cleanUp();
    	//reset position of each point
    	//student code
    	for(int i=0; i< pos.length/3; i++){
    		
    		Vector3f result=new Vector3f();
    		Vector3f x=new Vector3f(pos[i*3],pos[i*3+1],pos[i*3+2]);
    		
    		result=x.add(trans);
    		
    		pos[i*3]=result.x;
    		pos[(i*3)+1]=result.y;
    		pos[(i*3)+2]=result.z;
    		
    	}
    	setMesh(pos, textco, norms, inds);
    }
    
    public void rotateMesh(Vector3f axis, float angle){
    	cleanUp();
    	//reset position of each point
    	//student code
    	for(int i=0; i< pos.length/3; i++){
Matrix3f rotMatrix=new Matrix3f();
    		
    		//Need to convert angle to radians.
    		float cos=(float) Math.cos(Math.toRadians(angle));
    		float sin=(float) Math.sin(Math.toRadians(angle));
    		float tan=(float) (1.0-cos);
    		float x=axis.x;
    		float y=axis.y;
    		float z=axis.z;
    		
    		//Populate the rotation matrix with the appropriate entities.
    		rotMatrix.m00 = (tan * x * x + cos);
    		rotMatrix.m01 = (tan * x * y - sin * z);
    		rotMatrix.m02 = (tan * x * z + sin * y);
    		rotMatrix.m10 = (tan * x * y + sin * z);
    		rotMatrix.m22 = (tan * y * y + cos);
    		rotMatrix.m12 = (tan * y * z - sin * x);
    		rotMatrix.m20 = (tan * x * z - sin * y);
    		rotMatrix.m21 = (tan * y * z + sin * x);
    		rotMatrix.m22 = (tan * z * z + cos);
    		
    		Vector3f posAsVector=new Vector3f();
    		
    		posAsVector.x=pos[i*3];
    		posAsVector.y=pos[(i*3)+1];
    		posAsVector.z=pos[(i*3)+2];
    		
    		//Multiply the points as vector by rotation matrix.
    		Vector3f result=posAsVector.mul(rotMatrix);
    		
    		pos[i*3]=result.x;
    		pos[(i*3)+1]=result.y;
    		pos[(i*3)+2]=result.z;
    	}
    	setMesh(pos, textco, norms, inds);
    }
    
    public void reflectMesh(Vector3f p, Vector3f n){
    	cleanUp();
    	//reset position of each point
    	//student code
    	for(int i=0; i< pos.length/3; i++){
    		Vector3f result=new Vector3f();
    		Vector3f x=new Vector3f(pos[i*3],pos[i*3+1],pos[i*3+2]);
    		
    		//Subtract p from x, given formula for this assignment
    		Vector3f difference=x.sub(p);
    		float dotProduct=difference.dot(n);
    		
    		//From https://en.wikipedia.org/wiki/Reflection_(mathematics)
			result.x = difference.x - 2.0f * dotProduct * n.x;
			result.y = difference.y - 2.0f * dotProduct * n.y;
			result.z = difference.z - 2.0f * dotProduct * n.z;
			
			pos[i*3]=result.x;
			pos[i*3+1]=result.y;
			pos[i*3+2]=result.z;
    	}
    	setMesh(pos, textco, norms, inds);
    }
}
