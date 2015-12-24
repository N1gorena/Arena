package com.noexist.njg.arena;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by NJG on 12/6/2015.
 */
public class DisplaySurface extends GLSurfaceView {
    private float priorXPos;
    private float priorYPos;
    private final float ROTATION_MODIFIER = 0.1f;
    private TestRenderer mRenderer;

    public DisplaySurface(Context context) {
        super(context);
        setEGLContextClientVersion(2);

    }
    @Override
    public boolean onTouchEvent(MotionEvent e){
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()){
            case MotionEvent.ACTION_MOVE:


                float dx = x - priorXPos;
                float dy = y - priorYPos;

                float pitchChange = ROTATION_MODIFIER*dy;
                float yawChange = ROTATION_MODIFIER*dx;

                mRenderer.moveCamera(pitchChange,yawChange);


                break;
            default:break;
        }

        priorXPos = x;
        priorYPos = y;
        return true;
    }
    @Override
    public void setRenderer(Renderer renderer){
        mRenderer = (TestRenderer)renderer;
        super.setRenderer(renderer);
    }



}
