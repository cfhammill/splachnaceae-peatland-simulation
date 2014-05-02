/*
 * This class encapsulates the current state of the data matrix
 */
package peatland;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Random;


public class simData {
    
/* Data is a double matrix representing the peatland
     Row 0: Generation (1: gametophyte, 2: mature, 3: dung
     Row 1: Environmental suitability
     Row 2: Age
     Row 3: X coordinate
     Row 4: Y coordinate
     Row 5: Cover of Ampullaceum
     Row 6: Cover of Pensylvanicum
     Row 7: Spore Load of Ampullaceum
     Row 8: Spore Load of Pensylvanicum
     */
    private double[][] data;

    /*Contains a square matrix of distances between each row of data's coordinates
     the distance is calculated as euclidean distance
     */
    private double[][] distance;

    /*
    Instantiating a simData with a matrix of peatland values
    automatically calculates interindividual euclidean distance
    */
    public simData(double[][] data) {
        this.data = data;
        distanceMatrix();
    }

    /*
    Calculate the mean proportional coverage of Ampullaceum within mature populations
    from within data matrix
    */
    public double getMeanAmp() {
        int matures = 0;
        double runningTotal = 0;

        for (double[] r : data) {
            if (r[0] == 2) {
                matures++;
                runningTotal += r[5];
            }
        }

        return runningTotal / matures;
    }

    /*
    Calculate the mean proportional coverage of Pensylvanicum within mature populations
    from within data matrix    
    */
    public double getMeanPens() {
        int matures = 0;
        double runningTotal = 0;

        for (double[] r : data) {
            if (r[0] == 2) {
                matures++;
                runningTotal += r[6];
            }
        }

        return runningTotal / matures;
    }

    /*
    Outputs the contents of the data matrix to a file for analysis
    */
    private void writeMatrixToFile(String file, double[][] data) {
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), "utf-8"));

            int rows = data.length;
            int cols = data[0].length;

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {

                    if (j == cols - 1) {
                        writer.write(data[i][j] + "\n");
                    } else {
                        writer.write(data[i][j] + ", ");
                    }
                }

            }

        } catch (IOException ex) {
            System.out.println("IOException in file writing");
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                System.out.println("IOException closing file writing");
            }
        }
    }

    /*
    Reads in a properly formatted file to a data matrix
    */
    public static simData readFromFile(String file) {
        BufferedReader reader = null;

        //Lots of kludgey arbitrary lengths for these variables, fix later
        char[] input = new char[10000];
        char[] currentNumber = new char[10];
        double[][] data2 = new double[100][100];
        int i = 0;
        int j = 0;
        int l = 0;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            //Arbitrary read length, sadface
            int r = reader.read(input, 0, 10000);

            for (int k = 0; k < r; k++) {
                switch (input[k]) {
                    case (char) ',':
                        data2[i][j] = charArrayToDouble(currentNumber);
                        i++;
                        l = 0;
                        currentNumber = new char[10];
                        break;
                    case (char) '\n':
                        data2[i][j] = charArrayToDouble(currentNumber);
                        j++;
                        l = 0;
                        currentNumber = new char[10];
                        break;
                    case (char) ' ':
                        break;
                    default:
                        currentNumber[l] = input[k];
                        l++;
                        break;
                }
            }

        } catch (IOException ex) {
            System.out.println("Failed to open file");
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                System.out.println("Error Closing file");
            }
        }

        double[][] data3 = new double[i][j];
        for (int ip = 0; ip < i; ip++) {
            for (int jp = 0; jp < j; jp++) {
                data3[ip][jp] = data2[ip][jp];
            }
        }

        return new simData(data3);
    }

    public double[][] getSimData() {
        return this.data;
    }

    public void setSimData(double[][] data) {
        this.data = data;
    }

    private static double charArrayToDouble(char[] data) {
        return Double.parseDouble(new String(data));
    }

    /*
     Calculate the distance matrix for all indivuals, in this case euclidean
     */
    private void distanceMatrix() {
        double[][] distM = new double[data.length][data.length];

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                double dX = data[i][3] - data[j][3];
                double dY = data[i][4] - data[j][4];
                distM[i][j] = Math.hypot(dX, dY);
            }
        }

        distance = distM;
    }

    /*
    Public access to calculate distance matrix
    */
    public void calcDistanceMatrix() {
        distanceMatrix();
    }

    public double[][] getDistanceMatrix() {
        return distance;
    }

    /*
     Write the distance matrix to a comma delimited file with name file
     */
    public void writeDMToFile(String file) {
        writeMatrixToFile(file, distance);
    }

    /*
     Output the data to a comma delimited file with name file
     */
    public void writeToFile(String file) {
        writeMatrixToFile(file, data);
    }

    /*
     Perform the operation to change gametophyte abundance as a function
     of daily competition
     */
    public void competeGametos(simParams params) {
        int i = 0;

        for (double[] d1 : data) {
            if (d1[0] == 1) {
                double[] updatedRow = lotkaVolterra(d1, params);
                data[i] = updatedRow;
                i++;
            } else {
                i++;
            }
        }
    }

    /*
     Calculate the spore deposition to each dung pat from all mature population
     scaled by inverse squared distance, relative attractiveness, and areal yeild 
     */
    public void transferSpores(simParams params, int day, int daysPerYear) {
        int[] matures = new int[data.length];
        int[] activeDung = new int[data.length];

        double ampYeild = params.getAmp().getAY();
        double pensYeild = params.getPens().getAY();
        double ampAttraction = params.getAmp().getAttraction();
        double pensAttraction = params.getPens().getAttraction();

        double ampPhenologyModifier = calcPhenologyModifier(params.getAmp().getPhenology(), day, daysPerYear);
        double pensPhenologyModifier = calcPhenologyModifier(params.getPens().getPhenology(), day, daysPerYear);
        
        double aMinViableCoverage = params.getAmp().getMVC();
        double pMinViableCoverage = params.getPens().getMVC();
        
        int m = 0;
        int ad = 0;

        for (int i = 0; i < data.length; i++) {
            if (data[i][0] == 2) {
                matures[m] = i;
                m++;
            }
            if (data[i][0] == 3 && data[i][2] >= 0) {
                activeDung[ad] = i;
                ad++;
            }
        }

        for (int dung = 0; dung < ad; dung++) {

            int myDung = activeDung[dung];
            int dungAge = (int) data[myDung][2];
            double attractionFromAmp = relativeAttraction(dungAge, ampAttraction, 0, 2);
            double attractionFromPens = relativeAttraction(dungAge, pensAttraction, 0, 2);

            for (int moss = 0; moss < m; moss++) {
                int myMoss = matures[moss];

                double dist = distance[myMoss][myDung];
                
                double aContribution = data[myMoss][5] * ampPhenologyModifier * attractionFromAmp * ampYeild / (dist * dist); //*(dungAge+1));
                double pContribution = data[myMoss][6] * pensPhenologyModifier * attractionFromPens * pensYeild / (dist * dist); //*(dungAge+1));

                /*
                Calculates an individual moss's spore contribution for both species
                minViable coverage precludes reproduction below a given coverage
                in essence saying too few to allow mating. The isNaN catch defends
                against underflow errors previously generated
                */
                if (!Double.isNaN(aContribution) && data[myMoss][5] >= aMinViableCoverage) {
                    data[myDung][7] += aContribution;
                }

                if (!Double.isNaN(pContribution) && data[myMoss][6] >= pMinViableCoverage) {
                    data[myDung][8] += pContribution;
                }
            }
        }

    }

    /*
     Age each individual one day
     */
    public void incrementAge() {
        for (int i = 0; i < data.length; i++) {
            data[i][2]++;
        }
    }

    /*
     Evaluate the pdf for a normal curve at x given mu and sigma
     */
    private static double calcNormal(double x, double mu, double sigma) {
        return Math.exp(-Math.pow(x - mu, 2) / (2 * Math.pow(sigma, 2))) / (sigma * Math.sqrt(Math.PI * 2));
    }

    /*
     Perform all the transitions involved in turning dung to gametophyte,
     gametophyte to matures and adding new dung for the next year
     */
    public void processTransitions(simParams sp) {
        int dpy = sp.getGP().getDPY();
        double pr = sp.getGP().getPR();

        int newIAD = Math.round(Math.round(dpy * pr));
        //int nNewDung = Math.round(Math.round(dpy * pr * stp));
        double[][] newData = new double[data.length + newIAD][data[1].length]; //formerly plus newDung
        int keep = 0;

        for (double[] r : data) {
            switch ((int) r[0]) {
                case 1:
                    r[0] = 2;
                    newData[keep] = r;
                    keep++;
                    break;
                case 2:
                    break;
                case 3:
                    r[0] = 1;
                    r[5] = r[7];
                    r[7] = 0;
                    r[6] = r[8];
                    r[8] = 0;
                    newData[keep] = r;
                    keep++;
                    break;
            }
        }

        /*for (int k = keep; k < (keep + nNewDung); k++) {
            newData[k] = newDung(true, sp);
        }

        keep += nNewDung;
        */
        
        for (int k = keep; k < (keep + newIAD); k++) {
            newData[k] = newDung(false, sp);
        }

        keep += newIAD;

        double[][] returnData = new double[keep][data[1].length];
        System.arraycopy(newData, 0, returnData, 0, keep);

        this.data = returnData;
        distanceMatrix();
    }

    /*
     Perform a data output for analysis with end of year results
     */
    public void dataOutput(String name, int year, String out) throws IOException {

        String fileName = out + "/" + name + "/" + name + "Year" + year + ".csv";
        writeToFile(fileName);
    }

    /*
     Generate a new dung pat with age 0 if it is active, or a random
     negative number indicating days until activation, simulating
     random deposition.
     */
    public static double[] newDung(boolean isActive, simParams sp) {
        double[] sData = new double[9];
        Random rand = new Random();

        if (isActive) {
            sData[0] = 3;
            sData[1] = 0;
            sData[2] = 0;
            sData[3] = rand.nextInt(sp.getGP().getSize()[0]);
            sData[4] = rand.nextInt(sp.getGP().getSize()[1]);
            sData[5] = 0;
            sData[6] = 0;
            sData[7] = 0;
            sData[8] = 0;
        } else {
            sData[0] = 3;
            sData[1] = 0;
            sData[2] = -rand.nextInt(sp.getGP().getDPY());
            sData[3] = rand.nextInt(sp.getGP().getSize()[0]);
            sData[4] = rand.nextInt(sp.getGP().getSize()[1]);
            sData[5] = 0;
            sData[6] = 0;
            sData[7] = 0;
            sData[8] = 0;
        }

        return sData;
    }

    /*
     Generate a new gametophyte population with complete amp or pens
     coverage
     */
    public static double[] newGameto(boolean isAmpullaceum, simParams sp) {
        double[] sData = new double[9];
        Random rand = new Random();

        if (isAmpullaceum) {
            sData[0] = 1;
            sData[1] = 0;
            sData[2] = 0;
            sData[3] = rand.nextInt(sp.getGP().getSize()[0]);
            sData[4] = rand.nextInt(sp.getGP().getSize()[1]);
            sData[5] = sp.getAmp().getK();
            sData[6] = 0;
            sData[7] = 0;
            sData[8] = 0;
        } else {
            sData[0] = 1;
            sData[1] = 0;
            sData[2] = 0;
            sData[3] = rand.nextInt(sp.getGP().getSize()[0]);
            sData[4] = rand.nextInt(sp.getGP().getSize()[1]);
            sData[5] = 0;
            sData[6] = sp.getPens().getK();
            sData[7] = 0;
            sData[8] = 0;
        }

        return sData;
    }

    /*
     Generate a new mature moss population with either complete amp or pens
     coverage
     */
    public static double[] newMature(boolean isAmpullaceum, simParams sp) {
        double[] sData = new double[9];
        Random rand = new Random();

        if (isAmpullaceum) {
            sData[0] = 2;
            sData[1] = 0;
            sData[2] = 0;
            sData[3] = rand.nextInt(sp.getGP().getSize()[0]);
            sData[4] = rand.nextInt(sp.getGP().getSize()[1]);
            sData[5] = sp.getAmp().getK();
            sData[6] = 0;
            sData[7] = 0;
            sData[8] = 0;
        } else {
            sData[0] = 2;
            sData[1] = 0;
            sData[2] = 0;
            sData[3] = rand.nextInt(sp.getGP().getSize()[0]);
            sData[4] = rand.nextInt(sp.getGP().getSize()[1]);
            sData[5] = 0;
            sData[6] = sp.getPens().getK();
            sData[7] = 0;
            sData[8] = 0;
        }

        return sData;
    }

    /*
     Perform a lotka-volterra style competition between gametophytes with given
     carrying capacity and daily growth rates
     */
    private static double[] lotkaVolterra(double[] row, simParams params) {
        double kA = params.getAmp().getK();
        double kP = params.getPens().getK();
        double rA = params.getAmp().getR();
        double rP = params.getPens().getR();
        double alphaAP = params.getAmp().getAlpha();
        double alphaPA = params.getPens().getAlpha();

        double A = row[5];
        double P = row[6];

        double nextA = A + rA * A * (1 - (A + alphaAP * P) / kA);
        double nextP = P + rP * P * (1 - (P + alphaPA * A) / kP);

        if (!Double.isNaN(nextA) && nextA >= 0) {
            row[5] = nextA;
        } else {
            row[5] = 0;
        }

        if (!Double.isNaN(nextP) && nextP >= 0) {
            row[6] = nextP;
        } else {
            row[6] = 0;
        }

        return row;
    }

    /*
     Calculate the scaled difference attraction scale to the maximum attraction
     evaluate decay curve(normal in this case) at age and divide by the maximum
     value, in this case at mu.
     */
    public static double relativeAttraction(int age, double attraction, double mu, double sigma) {
        double relativeAttraction = (calcNormal(age, mu, sigma) / calcNormal(mu, mu, sigma) - attraction) * attraction;

        if (Double.isNaN(relativeAttraction) || relativeAttraction < 0) {
            return 0;
        } else {
            return relativeAttraction;
        }
    }

    public static double calcPhenologyModifier(String phenology, int day, int daysPerYear) {
        if (phenology.equals("unif")) {
            return 0.5;
        }

        /* The scaling on the trig functions gives them a period of daysPerYear
         shifts them up by one to keep them positive or zero, divides by two to limit amplitude
         to 1
         */
        if (phenology.equals("cos")) {
            return (Math.cos(2 * Math.PI * day / daysPerYear) + 1) / 2;
        }
        if (phenology.equals("sin")) {
            return (Math.sin(2 * Math.PI * day / daysPerYear) + 1) / 2;
        }
        if (phenology.equals("-cos")) {
            return 1 - (Math.cos(2 * Math.PI * day / daysPerYear) + 1) / 2;
        }
        return 0;
    }
}
