/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package volume;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author michel
 * @Anna 
 * Volume object: This class contains the object and assumes that the distance between the voxels in x,y and z are 1 
 */
public class Volume {
    
    public Volume(int xd, int yd, int zd) {
        data = new short[xd*yd*zd];
        dimX = xd;
        dimY = yd;
        dimZ = zd;
    }
    
    public Volume(File file) {
        
        try {
            VolumeIO reader = new VolumeIO(file);
            dimX = reader.getXDim();
            dimY = reader.getYDim();
            dimZ = reader.getZDim();
            data = reader.getData().clone();
            computeHistogram();
        } catch (IOException ex) {
            System.out.println("IO exception");
        }
        
    }
    
    
    public short getVoxel(int x, int y, int z) {
        return data[x + dimX*(y + dimY * z)];
    }
    

    
    public void setVoxel(int x, int y, int z, short value) {
        data[x + dimX*(y + dimY*z)] = value;
    }

    public void setVoxel(int i, short value) {
        data[i] = value;
    }
    
    public short getVoxelInterpolate(double[] coord) {
        if (coord[0] < 0 || coord[0] > (dimX-1) || coord[1] < 0 || coord[1] > (dimY-1)
                || coord[2] < 0 || coord[2] > (dimZ-1)) {
            return 0;
        }

        double x = coord[0];
        double y = coord[1];
        double z = coord[2];
        int x1 = (int) Math.floor(coord[0]);
        int y1 = (int) Math.floor(coord[1]);
        int z1 = (int) Math.floor(coord[2]);
        int x2 = (int) Math.ceil(coord[0]);
        int y2 = (int) Math.ceil(coord[1]);
        int z2 = (int) Math.ceil(coord[2]);

        short q000 = getVoxel(x1,y1,z1);
        short q100 = getVoxel(x2,y1,z1);
        short q010 = getVoxel(x1,y2,z1);
        short q110 = getVoxel(x2,y2,z1);
        short q001 = getVoxel(x1,y1,z2);
        short q101 = getVoxel(x2,y1,z2);
        short q011 = getVoxel(x1,y2,z2);
        short q111 = getVoxel(x2,y2,z2);

        double x00 = getLinearInterpolate(x, x1, x2, q000, q100);
        double x10 = getLinearInterpolate(x, x1, x2, q010, q110);
        double x01 = getLinearInterpolate(x, x1, x2, q001, q101);
        double x11 = getLinearInterpolate(x, x1, x2, q011, q111);
        double r0 = getLinearInterpolate(y, y1, y2, x00, x01);
        double r1 = getLinearInterpolate(y, y1, y2, x10, x11);

        return (short) getLinearInterpolate(z, z1, z2, r0, r1);
    }

    public static double getLinearInterpolate(double x, int x1, int x2, double q00, double q01) {
        return ((x2 - x) / (x2 - x1)) * q00 + ((x - x1) / (x2 - x1)) * q01;
    }
    
    public short getVoxel(int i) {
        return data[i];
    }
    
    public int getDimX() {
        return dimX;
    }
    
    public int getDimY() {
        return dimY;
    }
    
    public int getDimZ() {
        return dimZ;
    }

    public short getMinimum() {
        short minimum = data[0];
        for (int i=0; i<data.length; i++) {
            minimum = data[i] < minimum ? data[i] : minimum;
        }
        return minimum;
    }

    public short getMaximum() {
        short maximum = data[0];
        for (int i=0; i<data.length; i++) {
            maximum = data[i] > maximum ? data[i] : maximum;
        }
        return maximum;
    }
 
    public int[] getHistogram() {
        return histogram;
    }
    
    private void computeHistogram() {
        histogram = new int[getMaximum() + 1];
        for (int i=0; i<data.length; i++) {
            histogram[data[i]]++;
        }
    }
    
    private int dimX, dimY, dimZ;
    private short[] data;
    private int[] histogram;
}
