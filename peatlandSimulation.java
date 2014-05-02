/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package peatland;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chris
 */
class peatlandSimulation {
    private simData data;
    private simParams params;
    private int years;
    private int curYear;
    private String simName;
    private String outputFolder;
    private int ncols = 9;
   
    
    public peatlandSimulation(String myName, simData myData, simParams myParams, String myOut, int y){
           data = myData;
           params = myParams;
           simName = myName;
           outputFolder = myOut;
           years = y;
           this.generateStartingData(10, 10, 10, 10);
     }
    
    public peatlandSimulation(String[] paramList, String[] options,  String myOut){
       String name = paramList[0];
       int xmax = Integer.parseInt(paramList[1]);
       int ymax = Integer.parseInt(paramList[2]);
       int dpy = Integer.parseInt(paramList[3]);
       double poopRate = Double.parseDouble(paramList[4]);
       double kA = Double.parseDouble(paramList[5]);
       double rA = Double.parseDouble(paramList[6]);
       double alphaAP = Double.parseDouble(paramList[7]);
       double yA = Double.parseDouble(paramList[8]);
       double aA = Double.parseDouble(paramList[9]);
       String phenA = paramList[10];
       double kP = Double.parseDouble(paramList[11]);
       double rP = Double.parseDouble(paramList[12]);
       double alphaPA = Double.parseDouble(paramList[13]);
       double yP = Double.parseDouble(paramList[14]);
       double aP = Double.parseDouble(paramList[15]);
       String phenP = paramList[16];
       int y = Integer.parseInt(paramList[17]);
       
       
       generalParams gp = new generalParams(xmax, ymax, dpy, poopRate);
       sParams amp = new sParams(kA,rA, alphaAP, yA, aA, phenA);
       sParams pens = new sParams(kP, rP, alphaPA, yP, aP, phenP);
       this.params = new simParams(gp, amp, pens);
       this.years  = y;
       this.outputFolder = myOut;
       this.simName = name;
       
       this.generateStartingData(10, 10, 10, 10);
       
    }
    
    public void runSimulation() throws IOException{
        createFolder(outputFolder, simName);
        System.out.println(simName);
        
        this.curYear = 0;
        this.getData().dataOutput(simName, curYear, outputFolder);
        this.summarize(new PrintStream(outputFolder + "/" + simName + "/" + simName + "SimulationSummary.txt"));
        
        int dpy = this.getParameters().getGP().getDPY();
        
        for(int y = 0; y < years; y++){
            for(int d = 0; d < dpy; d++){
                dayTriggers(d, dpy);
            }
            yearTriggers();
        }
        
        //this.getData().dataOutput(simName, curYear, outputFolder);
    }
    
    public void runUntilExtinction(boolean forAmp, int maxYears) throws IOException {
        this.curYear = 0;
        int dpy = this.getParameters().getGP().getDPY();
        String name = simName;

        if (forAmp) {
            name += "AmpExtinct";
            createFolder(outputFolder, name);
            this.getData().dataOutput(name, curYear, outputFolder);
            this.summarize(new PrintStream(outputFolder + "/" + name + "/" + name + "SimulationSummary.txt"));

            while (this.getData().getMeanAmp() > 0 && curYear < 5000) {
                for (int d = 0; d < dpy; d++) {
                    dayTriggers(d,dpy);
                }
                yearTriggers();
            }
        } else {
            name += "PensExtinct";
            createFolder(outputFolder, name);
            this.getData().dataOutput(name, curYear, outputFolder);
            this.summarize(new PrintStream(outputFolder + "/" + name + "/" + name + "SimulationSummary.txt"));

            while (this.getData().getMeanAmp() > 0 && curYear < 5000) {
                for (int d = 0; d < dpy; d++) {
                    dayTriggers(d, dpy);
                }
                yearTriggers(name);
            }

        }

    }
   
    
    private void dayTriggers(int day, int daysPerYear){
        this.getData().competeGametos(this.getParameters());
        this.getData().transferSpores(this.getParameters(), day, daysPerYear);
        this.getData().incrementAge();
    }
    
    public void yearTriggers() throws IOException{
        this.getData().processTransitions(this.getParameters());
        this.curYear++;
        this.getData().dataOutput(simName, curYear, outputFolder);
    }
    
    public void yearTriggers(String name) throws IOException{
        this.getData().processTransitions(this.getParameters());
        this.curYear++;
        this.getData().dataOutput(name, curYear, outputFolder);
    }
    
    public simData getData(){
        return this.data;
    }
    
    public void setData(simData myData){
        this.data = myData;
    }
    
    public simParams getParameters(){
        return this.params;
    }
    
    public void setParameters(simParams myParams){
        this.params = myParams;
    }
    
    //Use starting numbers of individuals and dung to produce the empty matrix
    private void generateStartingData(int nGA, int nGP, int nMA, int nMP){
        int nIAD = Math.round(Math.round(params.getGP().getDPY() * params.getGP().getPR()));
        int nRows = nGA + nGP + nMA + nMP + nIAD;
        double[][] sData = new double[nRows][ncols];
        Random rand = new Random();
        simParams sp = this.getParameters();
        
        for(int i = 0; i < nRows; i++){
            boolean isGA = i < nGA;
            boolean isGP = i >= nGA && i < (nGA + nGP);
            boolean isMA = i >= (nGP + nGA) && i < (nGA + nGP + nMA);
            boolean isMP = i >= (nGA + nGP + nMA) && i < (nGA + nGP + nMA + nMP);
            boolean isIAD = i >= (nGA + nGP + nMA + nMP);
                    
            if(isGA) {
                sData[i] = simData.newGameto(true, sp);
            }
            
            if(isGP) {
                sData[i] = simData.newGameto(false, sp);
            }
            
            if(isMA) {
                sData[i] = simData.newMature(true, sp);
            }
            
            if(isMP) {
                sData[i] = simData.newMature(false, sp);
            }
             
            if(isIAD) {
                sData[i] = simData.newDung(false, sp);
            }
        }
        
        this.setData(new simData(sData));
        this.getData().calcDistanceMatrix();
    }
    
    public void summarize(PrintStream out){
        
        out.println("kA = " + this.getParameters().getAmp().getK() + 
                "\t kP = " + this.getParameters().getPens().getK());
        
        out.println("rA = " + this.getParameters().getAmp().getR() + 
                "\t rP = " + this.getParameters().getPens().getR());
        
        out.println("a12 = " + this.getParameters().getAmp().getAlpha() + 
                "\t a21 = " + this.getParameters().getPens().getAlpha());
        
        out.println("A Yeild = " + this.getParameters().getAmp().getAY() + 
                "\t P Yeild = " + this.getParameters().getPens().getAY());
        
        out.println("A Relative Attraction = " + this.getParameters().getAmp().getAttraction() + 
                "\t P Relative Attraction = " + this.getParameters().getPens().getAttraction());
        
        out.println("Size = " + this.getParameters().getGP().getSize()[0] + 
                " x " + this.getParameters().getGP().getSize()[1]);
        
        out.println("Daily Dung Deposition Proportion = " + this.getParameters().getGP().getPR());
        out.println("Year length = " + this.getParameters().getGP().getDPY());
        
        if(!out.equals(System.out)){ out.close(); }
    }
    
    public static void createFolder(String outFolder, String name){
        Path outPath = Paths.get(outFolder, name);
       
       if(!Files.isDirectory(outPath)){
               try {
                   Files.createDirectory(outPath);
               } catch (IOException ex) {
                   Logger.getLogger(peatlandSimulation.class.getName()).log(Level.SEVERE, null, ex);
               }
        }
    }
}
