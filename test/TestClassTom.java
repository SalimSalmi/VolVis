/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tom
 */
//import volvis.RaycastRenderer;
import util.VectorMath;

public class TestClassTom {
    public static void main(String []args){
        double[] a = new double[3];
        double[] b = new double[3];
        VectorMath.setVector(a,1,20,3);
        double maxa = VectorMath.max(a);
        System.out.println(maxa);
           
        
//         double[] a = new double[3];
//         double[] b = new double[3];
//         VectorMath.setVector(a,1,2,3);
//         System.out.println(a[2]);
//         
//         double[] entryPoint = new double[3];
//         double[] exitPoint = new double[3];
//         double[] viewVec = new double[3];
//         double sampleStep = 0.02;
//         
//         VectorMath.setVector(entryPoint,1,2,3);
//         VectorMath.setVector(exitPoint, 5, 5, 5);
//         VectorMath.setVector(exitPoint, 3, 4, 5);
//         
//         
//        int val = RaycastRenderer.traceRayMIP(entryPoint, exitPoint, viewVec, sampleStep);
     }
}
