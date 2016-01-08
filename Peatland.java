/*
 * This package is designed to simulate a peatland on the Avalon peninsula on the 
 * island of newfoundland. It is an individual based model used to examine
 * population dynamics
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chris
 */
public class Peatland {
    private static List<peatlandSimulation> experimentList;
    private static String outDir = System.getProperty("user.dir") + "/out/";
    private static String[] globalParams;

    public static void main(String[] args) throws FileNotFoundException, IOException {
       

        if (args.length == 0) {
            
            makeDir(outDir);
            makeDir(outDir + "collatedResults/");
            manualSim();
            
        } else if (args.length == 1) {
            
            buildExperiments(args[0]);
            makeDir(outDir);
            makeDir(outDir + "collatedResults/");
            
            for (peatlandSimulation sim : experimentList) {
                sim.runSimulation();
            }
        }
       
}
    
    public static void makeDir(String dir){
       Path outPath = Paths.get(dir);
       
       if(!Files.isDirectory(outPath)){
           
               try {
                   Files.createDirectory(outPath);
               } catch (IOException ex) {
                   Logger.getLogger(peatlandSimulation.class.getName()).log(Level.SEVERE, null, ex);
               }
        }
    }

 public static void manualSim() throws FileNotFoundException, UnsupportedEncodingException, IOException{
     /* Parameter Choices:
        Size 500 * 500
        Days per year 90
        Each day adds .3 dung pats at random
        Each year starts with .3 * days per year dung pats
        */   
     generalParams gp = new generalParams(80, 90, .3);
     
     /*
     Generic moss
     Mature population can have a maximum of 100 individuals (carrying cap)
     Grows by 5% per day as a gametophyte
     Competed with by competitor at 1% of intraspecific competition
     Delivers 100 units of spores per unit of sporophyte
     Attracts flies at 50% of fresh dung
     */
     sParams gener = new sParams(100,.05, .01, 100, .5, "unif");
     
     /*
     Compete two identical species at generic parameter choices
     */
     simParams sp = new simParams(gp, gener, gener);
     
     simData sd = new simData();
     
     peatlandSimulation run1 = new peatlandSimulation("Generic", sd, sp, outDir, 5, "none", 1, 0);
     
     run1.runSimulation();
     
     
     
 }
 
   private static void buildExperiments(String file) throws UnsupportedEncodingException, IOException{
        experimentList  = new ArrayList<peatlandSimulation>();
        
       
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            
            reader.readLine(); //discard first header
            
            globalParams = reader.readLine().split("[,;\t]");
            outDir = globalParams[2].replace("\"","");
            
            reader.readLine();
            
            reader.readLine(); // This is done to discard the second header line
            
            
            //Reads in up to 100 experiments with 18 parameter values
            while(reader.ready()){
                String line = reader.readLine();
                String[] paramVals = line.split("[,;\t]");
                experimentList.add(new peatlandSimulation(paramVals, globalParams, outDir));
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Peatland.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
   private static void runExperiments() throws IOException{
       for(peatlandSimulation experiment : experimentList){
           experiment.runSimulation();
       }          
   }
    
}
