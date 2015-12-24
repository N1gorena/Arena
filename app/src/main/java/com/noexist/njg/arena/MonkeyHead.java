package com.noexist.njg.arena;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by NJG on 12/8/2015.
 */
public class MonkeyHead extends Gobject {


    public MonkeyHead(OBJParser parsedData){
        super(parsedData);
        android.opengl.Matrix.setIdentityM(mWorldMatrix, 0);
    }

    @Override
    protected void init() {
        GLES20.glGenBuffers(2, mGPUDataBuffer, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mGPUDataBuffer[VERTEX_BUFFER]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mDataBuffer.capacity() * 4, mDataBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mGPUDataBuffer[ELEMENT_BUFFER]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, mElementBuffer.capacity() * 4, mElementBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER,0);
    }

    @Override
    protected void draw() {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(worldUniformLocation, 1, false, mWorldMatrix, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mGPUDataBuffer[VERTEX_BUFFER]);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mGPUDataBuffer[ELEMENT_BUFFER]);
        GLES20.glEnableVertexAttribArray(normalLocation);
        GLES20.glEnableVertexAttribArray(positionLocation);
        GLES20.glVertexAttribPointer(positionLocation, 3, GLES20.GL_FLOAT, false, 24, 0);
        GLES20.glVertexAttribPointer(normalLocation, 3, GLES20.GL_FLOAT, false, 24, 12);
        int offset = 0;
        for (int i = 0; i < mfaceCounts.length; i++){//Each face in the count has three elements, each face is 12 bytes(3 floats).
            offset += (i==0)?0:mfaceCounts[i-1]*12;
            GLES20.glDrawElements(GLES20.GL_TRIANGLES,mfaceCounts[i]*3,GLES20.GL_UNSIGNED_INT,offset);
        }
        //GLES20.glDrawElements(GLES20.GL_TRIANGLES, 1, GLES20.GL_UNSIGNED_INT,0);
    }
}
