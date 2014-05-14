/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package peatland;

/**
 *
 * @author chris
 */
public class spatialAggregator {
    private enhancedRandom enRand = new enhancedRandom();
    private double[][] aggregationCentres; 
    private String aggregationMethod;
    private int nAggregationCentres;
    private double aggregationStrength;
    private simParams params;
    
    public spatialAggregator(String aggregationMethod, int nAggregationCentres, 
                double aggregationStrength, simParams sp){
        
        this.aggregationMethod = aggregationMethod;
        this.nAggregationCentres = nAggregationCentres;
        this.aggregationStrength = aggregationStrength;
        this.params = sp;
        
        aggregationCentres = new double[nAggregationCentres][2];
        double radiusMax = sp.getGP().getRadius();
        
        
        for(int i = 0; i < nAggregationCentres; i++){
            aggregationCentres[i][0] = enRand.nextDouble() * sp.getGP().getRadius();
            aggregationCentres[i][1] = enRand.nextRadialAngle();
        }
    }
    
    public double[] generateAggregatedPoint(){
        double[] newCoords = new double[2];
        double[] centre = new double[2];
        
        int i = enRand.nextInt(aggregationCentres.length);
        
        centre[0] = aggregationCentres[i][0];
        centre[1] = aggregationCentres[i][1];
        
        switch(aggregationMethod) {
            case "biweight":
                double lc = centre[0];
                double lfc = enRand.nextBiweight()*aggregationStrength;
                double thetac = centre[1];
                double thetafc = enRand.nextRadialAngle();
                
                if(thetafc == Math.PI){
                 newCoords[0] = lfc + lc;
                 newCoords[1] = thetac;
                 break;
                } 
                
                double l = Math.sqrt(lc * lc + lfc * lfc - 2 * lc * lfc * Math.cos(thetafc));
                double theta = Math.acos((lc * lc + lfc * lfc - l * l)/(2 * lc * lfc));
                
                newCoords[0] = l;
                
                if(thetafc < Math.PI){
                 newCoords[1] = thetac + theta;
                } else {
                 newCoords[1] = thetac - theta;
                }
                
                break;
                
            default: 
                
        }
        
        return newCoords;
    }
    
    public String getAggregationMethod(){
        return aggregationMethod;
    }
    
    public void setAggregationMethod(String agMethod){
        aggregationMethod = agMethod;
    }
    
    public int getNAggregationCentres(){
        return nAggregationCentres;
    }
    
    public void setNAggregationCentres(int n){
        nAggregationCentres = n;
        
        aggregationCentres = new double[nAggregationCentres][2];
        
        for(int i = 0; i < nAggregationCentres; i++){
            aggregationCentres[i][0] = enRand.nextDouble() * params.getGP().getRadius();
            aggregationCentres[i][1] = enRand.nextRadialAngle();
        }
    }
    
    public double[][] getAggregationCentres(){
        return aggregationCentres;
    }
    
}