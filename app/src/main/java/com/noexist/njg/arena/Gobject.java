package com.noexist.njg.arena;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Deque;
import java.util.HashMap;

/**
 * Created by NJG on 12/8/2015.
 */
public abstract class Gobject {
    static int worldUniformLocation = -1;
    FloatBuffer mDataBuffer;
    IntBuffer mElementBuffer;
    float[] mWorldMatrix = new float[16];
    int mProgram = -1;
    static int positionLocation = -1;
    static int normalLocation = -1;
    int[] mGPUDataBuffer = new int[2];
    int[] mGPUElementBuffer = new int[1];
    int[] mfaceCounts;
    HashMap<String,Material> mMaterials;
    Deque<String> mMaterialOrder;
    protected static final int VERTEX_BUFFER = 0;
    protected static final int ELEMENT_BUFFER = 1;

    public Gobject(OBJParser parsedData){
        mDataBuffer = parsedData.getVertices();
        mElementBuffer = parsedData.getElements();
        mfaceCounts = parsedData.getCounts();
        mMaterialOrder = parsedData.getMatOrder();
        mMaterials = parsedData.getMaterials();
    }
    abstract protected void init();
    abstract protected void draw();
    public void setProgram(int program){
        mProgram = program;
    }
    public void setWorldMatrixLocation(int location){
        worldUniformLocation = location;
    }
    public void setPositionLocation(int location){
        positionLocation = location;
    }
    public void setNormalLocation(int location){
        normalLocation = location;
    }
    public boolean setPositionMatrix(float[] worldMatrix){
        if(worldMatrix.length == 16){
            mWorldMatrix = worldMatrix;
            return true;
        }
        else{
            return false;
        }
    }
}
