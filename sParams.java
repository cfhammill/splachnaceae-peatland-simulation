/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author chris
 */
public class sParams {
    private double k; // The species' carrying capacity
    private double r; // The species' daily rate of increase
    private double alphaFromCompetitor; // Influence from competitors
    private double arealYeild; // Spores delivered per unit mature cover
    private double relativeAttraction; // Maximum attractiveness relative to dung
    private String phenology; //functional representation of phenology
    private double minViableCoverage = 0.2; //Minimum coverage that allows mating
   
    public sParams(double k, double r, double afc, double arealYeild, double attraction, String phen){
        this.k = k;
        this.r = r;
        this.alphaFromCompetitor = afc;
        this.arealYeild = arealYeild;
        this.relativeAttraction = attraction;
        this.phenology = phen;    
    }
    
    public double getMVC(){
        return this.minViableCoverage;
    }
    
    public void setMVC(double newMVC){
        this.minViableCoverage = newMVC;
    }
    
    public double getK(){
        return this.k;
    }
    
    public void setK(double k){
        this.k = k;
    }
    
    public double getR(){
        return this.r;
    }
    
    public void setR(double r){
        this.r = r;
    }
    
    public double getAY(){
        return this.arealYeild;
    }
    
    public void setAY(double ay){
        this.arealYeild = ay;
    }
    
    public double getAlpha(){
        return this.alphaFromCompetitor;
    }
    
    public void setAlpha(double a){
        this.alphaFromCompetitor = a;
    }
    
    public double getAttraction(){
        return this.relativeAttraction;
    }
    
    public void setAttraction(double attraction){
        this.relativeAttraction = attraction;
    }
    
    public String getPhenology(){
        return this.phenology;
    }
    
    public void setPhenology(String phen){
        this.phenology = phen;
    }
}
