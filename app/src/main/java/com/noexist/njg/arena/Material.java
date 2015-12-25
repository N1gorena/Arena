package com.noexist.njg.arena;

import android.content.res.Resources;
import android.util.Log;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by NJG on 12/20/2015.
 */
public class Material {
    private String mName;
    float[] mAmbient = new float[3];
    float[] mDiffuse = new float[3];
    public  Material(String matName){
        mName = matName;
    }

    public boolean load(){
        return true;
    }
    public String getName(){
        return mName;
    }
    public void setAmbient(float v, float v1, float v2) {
        mAmbient[0] = v;
        mAmbient[1] = v1;
        mAmbient[2] = v2;
    }
    public void setDiffuse(float v, float v1, float v2) {
        mDiffuse[0] = v;
        mDiffuse[1] = v1;
        mDiffuse[2] = v2;
    }
    public void setSpecular(float v, float v1, float v2){}

    public float[] getAmbient(){
        return mAmbient;
    }
    public float[] getDiffuse(){
        return mDiffuse;
    }
    public float getAmbientX(){
        return mAmbient[0];
    }
    public float getAmbientY(){
        return mAmbient[1];
    }
    public float getAmbientZ(){
        return mAmbient[2];
    }
    public float getDiffuseX(){
        return mDiffuse[0];
    }
    public float getDiffuseY(){
        return mDiffuse[1];
    }
    public float getDiffuseZ(){
        return mDiffuse[2];
    }
}
