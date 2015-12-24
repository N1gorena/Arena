package com.noexist.njg.arena;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by NJG on 12/6/2015.
 */
public class TestRenderer implements GLSurfaceView.Renderer {

    float[] mClipMatrix = new float[16];
    float[] mCameraMatrix = new float[16];
    static float mPitch = 0;
    static float mYaw = 0;
    private GScene mScene;
    private int mProgram;
    float mCameraXPos = 6.0f;
    float mCameraYPos = 0.0f;
    float mCameraZPos = 0.0f;
    int clipMatrixLocation;
    int cameraMatrixLocation;

    public TestRenderer(GScene scene) {
        mScene = scene;
    }
    public TestRenderer(){
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, .24f, .73f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        mProgram = new ShaderStore().getProgram();

        mScene.setProgram(mProgram);
        clipMatrixLocation = GLES20.glGetUniformLocation(mProgram, "clip");
        cameraMatrixLocation =  GLES20.glGetUniformLocation(mProgram, "camera");
        Matrix.setLookAtM(mCameraMatrix, 0, mCameraXPos, mCameraYPos, mCameraZPos, 0f, 0f, 0f, 0f, 1.0f, 0f);
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(cameraMatrixLocation, 1, false, mCameraMatrix, 0);
        GLES20.glUseProgram(0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        Matrix.perspectiveM(mClipMatrix, 0, 45.0f, ratio, .1f, 100);
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(clipMatrixLocation, 1, false, mClipMatrix, 0);
        GLES20.glUseProgram(0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //My helper function
        setLight();


        //My helper function
        setCamera();
        //
        float[] test = {0.0f,0.0f,-0.5f,0.0f,1.0f,0.0f,
        0.0f,0.5f,0.0f,0.0f,1.0f,0.0f,
        0.0f,0.0f,0.5f,0.0f,1.0f,0.0f};
        int[] testes = {2,1,0};
        FloatBuffer FB = ByteBuffer.allocateDirect(test.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        FB.put(test).position(0);
        IntBuffer IB = ByteBuffer.allocateDirect(testes.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        IB.put(testes).position(0);
        float[] mW = new float[16];
        Matrix.setIdentityM(mW, 0);
        int[] temp = new int[1];
        GLES20.glGenBuffers(1, temp, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, temp[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, FB.capacity() * 4, FB, GLES20.GL_STATIC_DRAW);
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgram, "world"), 1, false, mW, 0);

        GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(mProgram, "normal"));
        GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(mProgram, "position"));
        //Log.d("DEBUG", "" + GLES20.glGetAttribLocation(mProgram, "normal"));
        GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(mProgram, "position"), 3, GLES20.GL_FLOAT, false, 24, 0);
        GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(mProgram, "normal"), 3, GLES20.GL_FLOAT, false, 24, 12);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 3, GLES20.GL_UNSIGNED_INT, IB);

        mScene.show();
    }

    public void moveCamera(float dPitch, float dYaw){
        mPitch += dPitch;
        mYaw += dYaw;
        if(mPitch > 89.0f){
            mPitch = 89.0f;
        }
        else if( mPitch < -89.0f){
            mPitch = -89.0f;
        }
        Log.d("TESTING","pChange:"+mPitch);

    }
    private void setCamera(){
        mCameraXPos = (float)( Math.cos(Math.toRadians(mPitch)) * Math.cos(Math.toRadians(mYaw)) ) * 6;
        mCameraYPos = (float)( Math.sin(Math.toRadians(mPitch)) ) * 6;
        mCameraZPos = (float)( Math.cos(Math.toRadians(mPitch)) * Math.sin(Math.toRadians(mYaw)) ) * 6;

        Matrix.setLookAtM(mCameraMatrix, 0, mCameraXPos, mCameraYPos, mCameraZPos, 0f, 0f, 0f, 0f, 1.0f, 0f);
        GLES20.glUniformMatrix4fv(cameraMatrixLocation, 1, false, mCameraMatrix, 0);
    }
    private void setLight(){
        int lightColorPosition = GLES20.glGetUniformLocation(mProgram, "lightColor");
        int testObjectColor = GLES20.glGetUniformLocation(mProgram,"objectColor");
        int lightPositionPosition = GLES20.glGetUniformLocation(mProgram,"lightPosition");

        GLES20.glUniform3f(lightColorPosition,1.0f,1.0f,1.0f);
        GLES20.glUniform3f(testObjectColor,1.0f,0.0f,0.0f);
        GLES20.glUniform3f(lightPositionPosition,0.0f,10.8f,0.0f);

    }
}
