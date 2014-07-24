/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package peatland;

import java.util.Random;
/**
 *
 * @author chris
 */
public class enhancedRandom extends Random{
    private double[][] biweightTable;
    
    public enhancedRandom(){
       fillBiweightTable(1, 2000);
    }
    
    private void fillBiweightTable(double uMax, int nVals){
        biweightTable = new double[nVals][2];
        uMax = Math.abs(uMax);
        double step = 2 * uMax / (nVals - 1);
        
        for(int i = 0; i < nVals;  i++){
           
           double u = -uMax + i * step;
           double pr = biweightIntegral(u);
           
           biweightTable[i][0] = u;
           biweightTable[i][1] = pr;
        }
    }
    
    private double biweightIntegral(double u){
       return u * (3 * Math.pow(u,4) - 10 * Math.pow(u,2) + 15)/16 + .5;
    }
    
    public double nextBiweight(){
        double pr = nextDouble();
        
        int i = 0;
        
        while(i < biweightTable.length && pr > biweightTable[i][1]){
            i++;
        }
        
        return Math.abs(biweightTable[i][0]);
    }
    
    public double nextRadialAngle(){
        return nextDouble() * 2 * Math.PI;
    }
}
