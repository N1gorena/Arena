package com.noexist.njg.arena;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Created by NJG on 12/7/2015.
 */
public class ShaderStore {
    private String vertexSourceCode = "attribute vec3 position;"+
            "attribute vec3 color;"+
            "attribute vec3 normal;"+
            "uniform mat4 world;"+
            "uniform mat4 camera;"+
            "uniform mat4 clip;"+
            "varying vec3 fragPosition;"+
            "varying vec3 normalVector;"+
            "void main(){" +
            "normalVector = normal;"+
            "fragPosition = vec3(world*vec4(position,1.0));"+
            "gl_Position = clip*camera*world*vec4(position,1.0);"+
            "}";
    private String fragmentSourceCode = "precision mediump float;" +
            "uniform vec3 ambient;"+
            //"uniform vec3 specular;"+
            "uniform vec3 diffuse;"+
            "uniform vec3 lightColor;" +
            //"uniform vec3 objectColor;"+
            "uniform vec3 lightPosition;"+
            "varying vec3 fragPosition;"+
            "varying vec3 normalVector;"+

            "void main() {"+
            //Diffuse
            "vec3 pointer = normalize(lightPosition-fragPosition);"+
            "float affect = max(dot(normalVector,pointer),0.0);"+
            "vec3 diffuseLight = (affect*diffuse)*lightColor;"+
            //Ambient
            "vec3 ambientLight = ambient*lightColor;"+

            "vec3 result = (ambientLight+diffuseLight);"+
            "  gl_FragColor = vec4(result,0.0f);" +
            "}";

    private int mProgram;

    ShaderStore(){
        int program;
        program = GLES20.glCreateProgram();


        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);


        GLES20.glShaderSource(vertexShader, vertexSourceCode);
        GLES20.glShaderSource(fragmentShader, fragmentSourceCode);

        IntBuffer success = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
        GLES20.glCompileShader(vertexShader);
        GLES20.glGetShaderiv(vertexShader, GLES20.GL_COMPILE_STATUS, success);
        int status = success.get(0);
        if(status==0){
            Log.d("SHADER","VERTEX");
            Log.d("SHADER",GLES20.glGetShaderInfoLog(vertexShader));
        }
        GLES20.glCompileShader(fragmentShader);
        GLES20.glGetShaderiv(fragmentShader, GLES20.GL_COMPILE_STATUS, success);
        status = success.get(0);
        if(status==0){
            Log.d("SHADER","FRAGMENT");
            Log.d("SHADER",GLES20.glGetShaderInfoLog(fragmentShader));
        }

        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

        mProgram = program;

    }

    public int getProgram() {
        return mProgram;
    }
}
