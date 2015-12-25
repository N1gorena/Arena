package com.noexist.njg.arena;

import android.content.res.Resources;
import android.opengl.Matrix;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * Created by NJG on 12/7/2015.
 */
public class OBJParser {
    //For external use
    private ArrayList<Float> vertexArray;
    private ArrayList<Integer> elementArray;
    //For internal logic
    private ArrayList<Float> vertexNormalData;
    private ArrayList<InfoArray> vertexExtrasArray;//0,1,2 normal coordinates,3
    //CUZ 3d....
    private final int THREE = 3;
    //Resource to parse. From RAW folder.
    private InputStream OBJResource;
    private ArrayList<InputStream> mMaterialLibraries;
    //Material holder
    private HashMap<String,Material> mMaterials;
    //Face Material Hack
    private int[] faceCounts;
    private Deque<String> mtlOrder;
    int currentMaterialFC = -1;

    private Resources mAndroidResources;

    OBJParser(InputStream OBJInputStream,Resources testResources){
        OBJResource = OBJInputStream;
        mAndroidResources=testResources;

        vertexArray = new ArrayList<Float>();
        elementArray = new ArrayList<Integer>();
        vertexExtrasArray = new ArrayList<InfoArray>();
        vertexNormalData = new ArrayList<Float>();
        mMaterials = new HashMap<String,Material>();
        mMaterialLibraries = new ArrayList<InputStream>();
        mtlOrder = new ArrayDeque<String>();


    }

    public void parseOBJ() throws FileNotFoundException{
            Scanner fileParser = new Scanner(OBJResource);
            while(fileParser.hasNext()){
                switch (fileParser.next()){
                    case "#":
                        fileParser.nextLine();
                        break;
                    case "f":
                        Log.d("DEBUG","TC"+1);
                        //PARSE: Get the 3 vertex indices of the given face. Get the face normal index for process step.
                        fileParser.useDelimiter(Pattern.compile("[\\/\\s]"));
                        int firstVertOfFace = fileParser.nextInt() - 1;
                        elementArray.add(firstVertOfFace);
                        fileParser.next();
                        int normalIndex = Integer.parseInt(fileParser.next());
                        int secondVertOfFace = fileParser.nextInt() - 1;
                        elementArray.add(secondVertOfFace);
                        fileParser.next();
                        fileParser.next();
                        int thirdVertOfFace = fileParser.nextInt() - 1;
                        elementArray.add(thirdVertOfFace);
                        fileParser.nextLine();
                        fileParser.useDelimiter(" ");
                        //PROCESS: Update vertex normals of indexed vertices using data from parsing.
                        vertexExtrasArray.get(firstVertOfFace).addNormalX(vertexNormalData.get(((normalIndex - 1)*THREE)));
                        vertexExtrasArray.get(firstVertOfFace).addNormalY(vertexNormalData.get(((normalIndex - 1) * THREE) + 1));
                        vertexExtrasArray.get(firstVertOfFace).addNormalZ(vertexNormalData.get(((normalIndex - 1) * THREE) + 2));
                        vertexExtrasArray.get(secondVertOfFace).addNormalX(vertexNormalData.get(((normalIndex - 1) * THREE)));
                        vertexExtrasArray.get(secondVertOfFace).addNormalY(vertexNormalData.get(((normalIndex - 1) * THREE) + 1));
                        vertexExtrasArray.get(secondVertOfFace).addNormalZ(vertexNormalData.get(((normalIndex - 1) * THREE) + 2));
                        vertexExtrasArray.get(thirdVertOfFace).addNormalX(vertexNormalData.get(((normalIndex - 1) * THREE)));
                        vertexExtrasArray.get(thirdVertOfFace).addNormalY(vertexNormalData.get(((normalIndex - 1) * THREE) + 1));
                        vertexExtrasArray.get(thirdVertOfFace).addNormalZ(vertexNormalData.get(((normalIndex - 1) * THREE) + 2));
                        faceCounts[currentMaterialFC]++;
                        break;
                    case "v":
                        //Parse: Get vertex co-ordinates.
                        vertexArray.add(fileParser.nextFloat());
                        vertexArray.add(fileParser.nextFloat());
                        vertexArray.add(fileParser.nextFloat());
                        //SETUP: Create a vertex normal for this vertex.
                        vertexExtrasArray.add(new InfoArray());
                        break;
                    case "vn":
                        //Parse: Get face normals.
                        vertexNormalData.add(fileParser.nextFloat());
                        vertexNormalData.add(fileParser.nextFloat());
                        vertexNormalData.add(fileParser.nextFloat());
                        break;
                    case "mtllib":
                        String fileName = fileParser.nextLine();
                        fileName = fileName.trim().substring(0,fileName.length()-5);//Remove .mtl
                        int matID = mAndroidResources.getIdentifier(fileName, "raw", "com.noexist.njg.arena");
                        mMaterialLibraries.add(mAndroidResources.openRawResource(matID));
                        break;
                    case "usemtl":
                        mtlOrder.addLast(fileParser.nextLine());
                        currentMaterialFC++;
                        break;
                    case "s":
                        fileParser.nextLine();
                        break;
                    case "o":
                        loadMaterials();
                        break;
                    default:
                        fileParser.nextLine();
                        break;
                }
            }
    }

    private void loadMaterials(){
        for (InputStream materialLib :
                mMaterialLibraries) {
            Scanner librayParser = new Scanner(materialLib);
            while(librayParser.hasNext()){
                switch (librayParser.next()) {
                    case "newmtl":
                        Material newMaterial = new Material(librayParser.nextLine());
                        librayParser.nextLine();
                        librayParser.next();
                        newMaterial.setAmbient(librayParser.nextFloat(), librayParser.nextFloat(), librayParser.nextFloat());
                        librayParser.next();
                        newMaterial.setDiffuse(librayParser.nextFloat(), librayParser.nextFloat(), librayParser.nextFloat());
                        librayParser.next();
                        newMaterial.setSpecular(librayParser.nextFloat(), librayParser.nextFloat(), librayParser.nextFloat());
                        mMaterials.put(newMaterial.getName(), newMaterial);
                        break;
                    default:
                        librayParser.nextLine();
                        break;
                }
            }
        }
        faceCounts = new int[mMaterials.size()];
        //Arrays.fill(faceCounts,0);
    }
    public FloatBuffer getVertices(){
        float[] toReturn = new float[vertexArray.size()+(vertexExtrasArray.size()*3) + (mMaterials.size()*6)];
        float x,y,z;
        //Do loop for evert vertex.
        for (int i = 1 ; i <= vertexExtrasArray.size(); i++){//VertexNormalsArray size is equal to amount of opengl vertices parsed.
            int vertexStartIndex = (i-1)*6;
            toReturn[vertexStartIndex] = vertexArray.get((i - 1) * 3);
            toReturn[vertexStartIndex+1] = vertexArray.get(((i - 1) * 3) + 1);
            toReturn[vertexStartIndex+2] = vertexArray.get(((i - 1) * 3) + 2);
            x=vertexExtrasArray.get(i-1).getNormalX();
            y=vertexExtrasArray.get(i-1).getNormalY();
            z=vertexExtrasArray.get(i-1).getNormalZ();
            float magnitude = Matrix.length(x,y,z);
            x = x/magnitude;
            y = y/magnitude;
            z = z/magnitude;
            toReturn[vertexStartIndex+3] = x;
            toReturn[vertexStartIndex+4] = y;
            toReturn[vertexStartIndex+5] = z;
        }
        FloatBuffer bufferToReturn = ByteBuffer.allocateDirect(4*toReturn.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        bufferToReturn.put(toReturn).position(0);
        return  bufferToReturn;
    }
    public IntBuffer getElements(){
        int[] toReturn = new int[elementArray.size()];
        for (int i = 0 ; i < elementArray.size() ; i++){
            toReturn[i] = elementArray.get(i);
            Log.d("DEBUG:Meth:getElements",""+toReturn[i]);
        }
        IntBuffer bufferToReturn = ByteBuffer.allocateDirect(4*toReturn.length).order(ByteOrder.nativeOrder()).asIntBuffer();
        bufferToReturn.put(toReturn).position(0);
        return  bufferToReturn;
    }

    private class InfoArray{
        float[] normalVector;
        String materialName;
        InfoArray(){
            normalVector = new float[THREE];
        }
        public void putNormalX(float x){
            normalVector[0] = x;
        }
        public void addNormalX(float x){
            normalVector[0] += x;
        }
        public void putNormalY(float y){
            normalVector[1]= y;
        }
        public void addNormalY(float y){
            normalVector[1] += y;
        }
        public void putNormalZ(float z){
            normalVector[2] = z;
        }
        public void addNormalZ(float z){
            normalVector[2] += z;
        }
        public float getNormalX(){
           return normalVector[0];
        }
        public float getNormalY(){
            return normalVector[1];
        }
        public float getNormalZ(){
            return normalVector[2];
        }
        public void putMaterialName(String materialString) {
            this.materialName = materialString.trim();
        }
        public String getMaterialName() {
            return materialName;
        }
    }
    public int[] getCounts(){
        return faceCounts;
    }
    public Deque<String> getMatOrder(){
        return mtlOrder;
    }
    public HashMap<String,Material> getMaterials(){
        return mMaterials;
    }
}
