import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Simulator {
    private Random rand = new Random();
    private State states[] = new State[54];
    private int clintonEV = 0;
    private int trumpEV = 0;
    private int otherEV = 0;
    private Date date = new Date();
    private SimpleDateFormat ft = new SimpleDateFormat("D");
    private int daysToElection = 0;
    private double nationalAdj = 0;
    private double actualNationalAdj[] = {0, 0, 0};
    private double regionalAdj[] = new double[8];
    private double actualRegionalAdj[][] = new double[8][3];
    
    public Simulator(String fileName, int electionDay) {
        if (Integer.parseInt(ft.format(date)) < electionDay) {
            daysToElection = electionDay - Integer.parseInt(ft.format(date));
        } // if else
        nationalAdj = rand.nextGaussian() * (1.5 + daysToElection/50.);
        for (int i = 0; i < regionalAdj.length; i++) {
            regionalAdj[i] = rand.nextGaussian() * 2.0;
        } // for i
        try {
            Scanner file = new Scanner(new FileInputStream(new File(fileName)));
            for (int i = 0; i < states.length; i++) {
                String line = file.nextLine();
                if (line.split("\t")[8].equals("POLL")) {
                    states[i] = new State(line.split("\t")[0], line.split("\t")[1], Integer.parseInt(line.split("\t")[2]), Integer.parseInt(line.split("\t")[3]),
                            Double.parseDouble(line.split("\t")[4]), Double.parseDouble(line.split("\t")[5]), ((Double.parseDouble(line.split("\t")[6])) - 3.25)/1.28,
                            Double.parseDouble(line.split("\t")[7]), line.split("\t")[8], 0);
                } else {
                    states[i] = new State(line.split("\t")[0], line.split("\t")[1], Integer.parseInt(line.split("\t")[2]), Integer.parseInt(line.split("\t")[3]),
                            Double.parseDouble(line.split("\t")[9]) * (Double.parseDouble(line.split("\t")[11]) / 100.0) + Double.parseDouble(line.split("\t")[4]) * (1.0 - Double.parseDouble(line.split("\t")[11]) / 100.0), 
                            Double.parseDouble(line.split("\t")[10]) * (Double.parseDouble(line.split("\t")[11]) / 100.0) + Double.parseDouble(line.split("\t")[5]) * (1.0 - Double.parseDouble(line.split("\t")[11]) / 100.0), 
                            (1.0 - (Double.parseDouble(line.split("\t")[11]) / 100.0)) * 5.0, Double.parseDouble(line.split("\t")[7]), line.split("\t")[8], Double.parseDouble(line.split("\t")[11]));
                    actualNationalAdj[0] = ((actualNationalAdj[0] * actualNationalAdj[2] + ((Double.parseDouble(line.split("\t")[9]) - Double.parseDouble(line.split("\t")[4])) 
                            * (1.25/(Double.parseDouble(line.split("\t")[6])/1.28)) * (states[i].getReporting() / 100.0))) / (actualNationalAdj[2] + 1));
                    actualNationalAdj[1] = ((actualNationalAdj[1] * actualNationalAdj[2] + ((Double.parseDouble(line.split("\t")[10]) - Double.parseDouble(line.split("\t")[5])) 
                            * (1.25/(Double.parseDouble(line.split("\t")[6])/1.28)) * (states[i].getReporting() / 100.0))) / (actualNationalAdj[2] + 1));
                    actualRegionalAdj[states[i].getRegion() - 1][0] = ((actualRegionalAdj[states[i].getRegion() - 1][0] * actualRegionalAdj[states[i].getRegion() - 1][2]
                            + ((Double.parseDouble(line.split("\t")[9]) - Double.parseDouble(line.split("\t")[4])) * (2.0/(Double.parseDouble(line.split("\t")[6])/1.28)) 
                                    * (states[i].getReporting() / 100.0)))    / (actualRegionalAdj[states[i].getRegion() - 1][2] + 1));
                    actualRegionalAdj[states[i].getRegion() - 1][1] = ((actualRegionalAdj[states[i].getRegion() - 1][1] * actualRegionalAdj[states[i].getRegion() - 1][2]
                            + ((Double.parseDouble(line.split("\t")[10]) - Double.parseDouble(line.split("\t")[5])) * (2.0/(Double.parseDouble(line.split("\t")[6])/1.28)) 
                                    * (states[i].getReporting() / 100.0)))    / (actualRegionalAdj[states[i].getRegion() - 1][2] + 1));
                    actualNationalAdj[2]++;
                    actualRegionalAdj[states[i].getRegion() - 1][2]++;
                } // if else
            } // for i
            file.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error importing data!");
        } // try catch
    } // Simulator constructor
    
    public void run() {
        
        for (int i = 0; i < states.length; i++) {
            double adj = rand.nextGaussian() * states[i].getStdDev();
            double actualAdj = rand.nextGaussian() * states[i].getStdDev() * (1 - states[i].getStatusAsInt());
            if (states[i].getName().equals("Utah")) {
                double otherAdj = rand.nextGaussian() * states[i].getStdDev();
                if (otherAdj > 0) {
                    otherAdj *= 2.0;
                } // if else
                states[i].setOther(otherAdj + states[i].getOther());
            } // if else;
            double clinton = states[i].getClinton() + (adj + ((nationalAdj + actualNationalAdj[0]) * states[i].getElasticity())
                    + regionalAdj[states[i].getRegion() - 1] + actualRegionalAdj[states[i].getRegion() - 1][0]) * states[i].getStatusAsInt()
                    + actualAdj + (1.0 - states[i].getReporting()/100.0) * (actualNationalAdj[0] + actualRegionalAdj[states[i].getRegion() - 1][0]) * (1 - states[i].getStatusAsInt());
            states[i].setClinton(clinton);
            double trump = states[i].getTrump() + (-adj + ((-nationalAdj + actualNationalAdj[1]) * states[i].getElasticity())
                    - regionalAdj[states[i].getRegion() - 1] + actualRegionalAdj[states[i].getRegion() - 1][1]) * states[i].getStatusAsInt()
                    - actualAdj - (1.0 - states[i].getReporting()/100.0) * (actualNationalAdj[1] + actualRegionalAdj[states[i].getRegion() - 1][1]) * (1 - states[i].getStatusAsInt());
            states[i].setTrump(trump);
            if (states[i].getOther() >= states[i].getClinton() && states[i].getOther() >= states[i].getTrump()) {
                states[i].setWinner("Other");
                otherEV += states[i].getElectoralVotes();
            } else if (states[i].getClinton() >= states[i].getTrump()) {
                states[i].setWinner("Clinton");
                clintonEV += states[i].getElectoralVotes();
            } else {
                states[i].setWinner("Trump");
                trumpEV += states[i].getElectoralVotes();
            } // if else
        } // for i
    } // run method

    public State[] getStates() {
        return states;
    } // getStates method

    public int getClintonEV() {
        return clintonEV;
    } // getClintonEV method

    public int getTrumpEV() {
        return trumpEV;
    } // getTrumpEV method
    
    public int getOtherEV() {
        return otherEV;
    } // getOtherEV method
} // Simulator class
