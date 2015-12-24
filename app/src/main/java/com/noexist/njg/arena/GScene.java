package com.noexist.njg.arena;

import android.opengl.GLES20;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by NJG on 12/14/2015.
 */
public class GScene {
    private String mName;
    private ArrayList<Gobject> sceneObjects;
    private int mProgram;
    private int normalLocation;
    private int positionLocation;
    private int worldMatrixLocation;
    public GScene(String sceneName){
        sceneObjects = new ArrayList<Gobject>();
        mName = sceneName;
        mProgram = -1;
    }
    public boolean addObject(Gobject object){
        if (!(mProgram < 0)){
            object.setProgram(mProgram);
            object.setNormalLocation(normalLocation);
            object.setPositionLocation(positionLocation);
        }
        return sceneObjects.add(object);
    }
    public void show(){
        for (Gobject var:sceneObjects) {
            var.draw();
        }
    }
    public void setProgram(int program){
        mProgram = program;
        normalLocation = GLES20.glGetAttribLocation(mProgram,"normal");

        positionLocation = GLES20.glGetAttribLocation(mProgram,"position");
        worldMatrixLocation = GLES20.glGetUniformLocation(mProgram, "world");
        for (Gobject var :
                sceneObjects) {
            var.init();
            var.setProgram(mProgram);
            var.setNormalLocation(normalLocation);

            var.setPositionLocation(positionLocation);
            var.setWorldMatrixLocation(worldMatrixLocation);
        }
    }
}
