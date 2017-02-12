/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package volume;

import util.VectorMath;

/**
 *
 * @author michel
 * @ Anna
 * This class contains the pre-computes gradients of the volume. This means calculates the gradient
 * at all voxel positions, and provides functions
 * to get the gradient at any position in the volume also continuous..
*/
public class GradientVolume {

    public GradientVolume(Volume vol) {
        volume = vol;
        dimX = vol.getDimX();
        dimY = vol.getDimY();
        dimZ = vol.getDimZ();
        data = new VoxelGradient[dimX * dimY * dimZ];
        compute();
        maxmag = -1.0;
    }

    public VoxelGradient getGradient(int x, int y, int z) {
        return data[x + dimX * (y + dimY * z)];
    }


    private VoxelGradient interpolate(VoxelGradient g0, VoxelGradient g1, float factor) {
        double[] sg0 = VectorMath.scalarproduct(g0.getArray(), factor);
        double[] sg1 = VectorMath.scalarproduct(g1.getArray(), 1-factor);

        double[] result = VectorMath.sum(sg0, sg1);
        return new VoxelGradient((float) result[0], (float) result[1], (float) result[2]);
    }

    
    public VoxelGradient getGradientNN(double[] coord) {
        /* Nearest neighbour interpolation applied to provide the gradient */
        if (coord[0] < 0 || coord[0] > (dimX-2) || coord[1] < 0 || coord[1] > (dimY-2)
                || coord[2] < 0 || coord[2] > (dimZ-2)) {
            return zero;
        }

        int x = (int) Math.round(coord[0]);
        int y = (int) Math.round(coord[1]);
        int z = (int) Math.round(coord[2]);
        
        return getGradient(x, y, z);
    }

    
    public VoxelGradient getGradient(double[] coord) {
        if (coord[0] < 0 || coord[0] > (dimX-2) || coord[1] < 0 || coord[1] > (dimY-2)
                || coord[2] < 0 || coord[2] > (dimZ-2)) {
            return zero;
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

        VoxelGradient q000 = getGradient(x1,y1,z1);
        VoxelGradient q100 = getGradient(x2,y1,z1);
        VoxelGradient q010 = getGradient(x1,y2,z1);
        VoxelGradient q110 = getGradient(x2,y2,z1);
        VoxelGradient q001 = getGradient(x1,y1,z2);
        VoxelGradient q101 = getGradient(x2,y1,z2);
        VoxelGradient q011 = getGradient(x1,y2,z2);
        VoxelGradient q111 = getGradient(x2,y2,z2);

        VoxelGradient x00 = interpolate(q000, q100, getFactor(x, x1, x2));
        VoxelGradient x10 = interpolate(q010, q110, getFactor(x, x1, x2));
        VoxelGradient x01 = interpolate(q001, q101, getFactor(x, x1, x2));
        VoxelGradient x11 = interpolate(q011, q111, getFactor(x, x1, x2));
        VoxelGradient r0 = interpolate(x00, x01, getFactor(y, y1, y2));
        VoxelGradient r1 = interpolate(x10, x11, getFactor(y, y1, y2));

        return interpolate(r0, r1, getFactor(z, z1, z2));
    }

    private float getFactor(double x, double x0, double x1) {
        return (float) ((x-x0)/(x1-x0));
    }

    public void setGradient(int x, int y, int z, VoxelGradient value) {
        data[x + dimX * (y + dimY * z)] = value;
    }

    public void setVoxel(int i, VoxelGradient value) {
        data[i] = value;
    }

    public VoxelGradient getVoxel(int i) {
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

    private void compute() {
        for(int x=0; x<dimX; x++) {
            for(int y=0; y<dimY; y++) {
                for(int z=0; z<dimZ; z++) {
                    volume.getVoxel(x,y,z);
                    float gx, gy, gz;
                    if(x + 1 == dimX || x==0) {gx = 0f;}
                    else {gx = (volume.getVoxel(x+1,y,z)-volume.getVoxel(x-1,y,z))/2;}
                    if(y + 1 == dimY || y==0) {gy = 0f;}
                    else {gy = (volume.getVoxel(x,y+1,z)-volume.getVoxel(x,y-1,z))/2;}
                    if(z + 1 == dimZ || z==0) {gz = 0f;}
                    else {gz = (volume.getVoxel(x,y,z+1)-volume.getVoxel(x,y,z-1))/2;}

                    VoxelGradient gradient = new VoxelGradient(gx, gy, gz);

                    setGradient(x, y, z, gradient);
                }
            }
        }

    }
    
    public double getMaxGradientMagnitude() {
        double max = 0;
        for (int i = 0; i < data.length; i++) {
            if(max < data[i].mag){max = data[i].mag;}
        }
        return max;
    }
    
    private int dimX, dimY, dimZ;
    private VoxelGradient zero = new VoxelGradient();
    VoxelGradient[] data;
    Volume volume;
    double maxmag;
}
