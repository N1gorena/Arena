package com.noexist.njg.arena;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.FileNotFoundException;
import java.nio.FloatBuffer;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);
        DisplaySurface mainDisplay = new DisplaySurface(this);

        OBJParser testParser = new OBJParser(getResources().openRawResource(R.raw.target), getResources());
        GScene mainScene = new GScene("MainScene");
        
        try {
            testParser.parseOBJ();
            MonkeyHead testHead = new MonkeyHead(testParser);
            mainScene.addObject(testHead);
            TestRenderer mRenderer = new TestRenderer(mainScene);
            mainDisplay.setRenderer(mRenderer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        setContentView(mainDisplay);
    }
}
