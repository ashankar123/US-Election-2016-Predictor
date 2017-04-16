import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Election {

	public static void main(String[] args) {
		int electionDay = 313;
		int n = -1;
		String fileName = "data/states.dat";
		try {
			electionDay = Integer.parseInt(args[0]);
			fileName = args[1];
			n = Integer.parseInt(args[2]);
		} catch (Exception ex) {
		} // try catch
		double clintonAvg = 0;
		double trumpAvg = 0;
		double otherAvg = 0;
		double clintonProb = 0;
		double trumpProb = 0;
		double otherProb = 0;
		double tieProb = 0;
		double deadlockProb = 0;
		int clintonEVs[] = new int[539];
		int trumpEVs[] = new int[539];
		int otherEVs[] = new int[539];
		double stateAvgClinton[] = new double[54];
		double stateAvgTrump[] = new double[54];
		double stateAvgOther[] = new double[54];
		double stateProbClinton[] = new double[54];
		double stateProbTrump[] = new double[54];
		double stateProbOther[] = new double[54];
		String regionNames[] = {"Northeast", "Southeast", "Midwest", "Southwest", "Far West", "West Coast", "Arctic", "Pacific"};
		
		if (n <= 0) {
			System.out.print("Enter number of simulations.\n> ");
		} // if else
		Scanner keyboard = new Scanner(System.in);
		while (n <= 0) {
			try { // making sure that an integer is entered
				n = Integer.parseInt(keyboard.nextLine());
				if (n <= 0) {
					System.out.print("Please enter a number greater than 0.\n> ");
				} // if else
			} catch (Exception ex) {
				System.out.print("Please enter a number greater than 0.\n> ");
			} // catch
		} // while
		State states[] = null;
		for (int i = 1; i <= n; i++) {
			Simulator simulator = new Simulator(fileName, electionDay);
			simulator.run();
			states = simulator.getStates();
			for (int j = 0; j < states.length; j++) {
				if (states[j].getWinner().equals("Clinton")) {
					stateProbClinton[j] = (stateProbClinton[j] * (i - 1) + 100) / i;
					stateProbTrump[j] = (stateProbTrump[j] * (i - 1)) / i;
					stateProbOther[j] = (stateProbOther[j] * (i - 1)) / i;
				} else if (states[j].getWinner().equals("Trump")){
					stateProbTrump[j] = (stateProbTrump[j] * (i - 1) + 100) / i;
					stateProbClinton[j] = (stateProbClinton[j] * (i - 1)) / i;
					stateProbOther[j] = (stateProbOther[j] * (i - 1)) / i;
				} else {
					stateProbOther[j] = (stateProbOther[j] * (i - 1) + 100) / i;
					stateProbClinton[j] = (stateProbClinton[j] * (i - 1)) / i;
					stateProbTrump[j] = (stateProbTrump[j] * (i - 1)) / i;
				} // if else
				
				stateAvgClinton[j] = (stateAvgClinton[j] * (i - 1) + states[j].getClinton()) / i;
				stateAvgTrump[j] = (stateAvgTrump[j] * (i - 1) + states[j].getTrump()) / i;
				stateAvgOther[j] = (stateAvgOther[j] * (i - 1) + states[j].getOther()) / i;
			} // for j
			
			int clintonEV = simulator.getClintonEV();
			int trumpEV = simulator.getTrumpEV();
			int otherEV = simulator.getOtherEV();
			
			clintonEVs[clintonEV] ++;
			trumpEVs[trumpEV] ++;
			otherEVs[otherEV] ++;
			
			if (otherEV > clintonEV && otherEV > trumpEV && otherEV > 270) {
				otherProb = (otherProb * (i - 1) + 100) / i;
				clintonProb = (clintonProb * (i - 1)) / i;
				trumpProb = (trumpProb * (i - 1)) / i;
				tieProb = (tieProb * (i - 1)) / i;
			} else if (clintonEV > trumpEV && clintonEV >= 270) {
				clintonProb = (clintonProb * (i - 1) + 100) / i;
				trumpProb = (trumpProb * (i - 1)) / i;
				tieProb = (tieProb * (i - 1)) / i;
				otherProb = (otherProb * (i - 1)) / i;
			} else if (clintonEV < trumpEV && trumpEV >= 270) {
				trumpProb = (trumpProb * (i - 1) + 100) / i;
				clintonProb = (clintonProb * (i - 1)) / i;
				tieProb = (tieProb * (i - 1)) / i;
				otherProb = (otherProb * (i - 1)) / i;
			} else if (clintonEV == trumpEV){ // I'm going to ignore other ties
				tieProb = (tieProb * (i - 1) + 100) / i;
				clintonProb = (clintonProb * (i - 1)) / i;
				trumpProb = (trumpProb * (i - 1)) / i;
				otherProb = (otherProb * (i - 1)) / i;
			} else {
				clintonProb = (clintonProb * (i - 1)) / i;
				trumpProb = (trumpProb * (i - 1)) / i;
				tieProb = (tieProb * (i - 1)) / i;
				otherProb = (otherProb * (i - 1)) / i;
			} // if else
			
			if (clintonEV < 270 && trumpEV < 270 && otherEV < 270) {
				deadlockProb = (deadlockProb * (i - 1) + 100) / i;
			} else {
				deadlockProb = (deadlockProb * (i - 1)) / i;
			} // if else
			
			clintonAvg = (clintonAvg * (i - 1) + clintonEV) / i;
			trumpAvg = (trumpAvg * (i - 1) + trumpEV) / i;
			otherAvg = (otherAvg * (i - 1) + otherEV) / i;
		} // for i
		
		try {
			FileWriter data = new FileWriter(new File("data/results.dat"));
			data.write("Clinton EV: " + Math.round(clintonAvg * 10) / 10. + "\tClinton Wins: " + Math.round(clintonProb * 10) / 10. + "%\n"
			+ "Trump EV: " + Math.round(trumpAvg * 10) / 10. + "\t\tTrump Wins: " + Math.round(trumpProb * 10) / 10. + "%\n"
			+ "Other EV: " + Math.round(otherAvg * 10) / 10. + "\t\tOther Wins: " + Math.round(otherProb * 10) / 10. + "%\n"
			+ "Tie: " + Math.round(tieProb * 10) / 10. + "%\nDeadlock: " + Math.round(deadlockProb * 10) / 10. + "%\n\n");
			data.write("State               |Region    |Abb|EV|% Rep |C Avg |T Avg |O Avg |C Prob|T Prob|O Prob\n");
			data.write("--------------------+----------+---+--+------+------+------+------+------+------+------\n");
			for (int i = 0; i <states.length; i++) {
				data.write(String.format("%-20s", states[i].getName()) + "|" + String.format("%-10s", regionNames[states[i].getRegion() - 1]) + "|" + String.format("%-3s", states[i].getAbbreviation())
					+ "|" + String.format("%-2s", states[i].getElectoralVotes()) + "|" + String.format("%-5.1f", states[i].getReporting()) + "%|" + String.format("%-6.1f", stateAvgClinton[i]) + "|"
					+ String.format("%-6.1f", stateAvgTrump[i]) + "|" + String.format("%-6.1f", stateAvgOther[i]) + "|" + String.format("%-5.1f", stateProbClinton[i]) 
					+ "%|" + String.format("%-5.1f", stateProbTrump[i]) + "%|" + String.format("%-5.1f", stateProbOther[i]) + "%\n");
			} // for i
			data.close();
		} catch (Exception ex) {
			System.out.println("Error exporting data!");
		} // try catch
		
		System.out.println("Clinton EV: " + Math.round(clintonAvg * 10) / 10. + "\tClinton Wins: " + Math.round(clintonProb * 10) / 10. + "%");
		System.out.println("Trump EV: " + Math.round(trumpAvg * 10) / 10. + "\t\tTrump Wins: " + Math.round(trumpProb * 10) / 10. + "%");
		System.out.println("Other EV: " + Math.round(otherAvg * 10) / 10. + "\t\tOther Wins: " + Math.round(otherProb * 10) / 10. + "%");
		System.out.println("Tie: " + Math.round(tieProb * 10) / 10. + "%\nDeadlock: " + Math.round(deadlockProb * 10) / 10. + "%");
		
		while (true) {
			System.out.print("\nEnter State for detailed information.\n> ");
			String state = keyboard.nextLine().replace(".", "").replace(" ", "");
			if (state.equalsIgnoreCase("exit") || state.equalsIgnoreCase("quit")) {
				return;
			} else if (state.equalsIgnoreCase("national") || state.equalsIgnoreCase("US")) {
				System.out.println("Clinton EV: " + Math.round(clintonAvg * 10) / 10. + "\tClinton Wins: " + Math.round(clintonProb * 10) / 10. + "%");
				System.out.println("Trump EV: " + Math.round(trumpAvg * 10) / 10. + "\t\tTrump Wins: " + Math.round(trumpProb * 10) / 10. + "%");
				System.out.println("Other EV: " + Math.round(otherAvg * 10) / 10. + "\t\tOther Wins: " + Math.round(otherProb * 10) / 10. + "%");
				System.out.println("Tie: " + Math.round(tieProb * 10) / 10. + "%\nDeadlock: " + Math.round(deadlockProb * 10) / 10. + "%");
			} else {
				for (int i = 0; i < states.length; i++) {
					if (state.equalsIgnoreCase(states[i].getName().replace(".", "").replace(" ", "")) || state.equalsIgnoreCase(states[i].getAbbreviation().replace(".", "").replace(" ", ""))) {
						System.out.println(states[i].getName() + " (" + states[i].getAbbreviation() + "), " + regionNames[states[i].getRegion() - 1] + ": " + states[i].getElectoralVotes() + " Electoral Votes");
						System.out.println(states[i].getStatus() + ", " + (states[i].getReporting()) + "% Reporting");
						System.out.println("Clinton Avg: " + Math.round(stateAvgClinton[i] * 10) / 10. + "\tClinton Prob: " + Math.round(stateProbClinton[i] * 10) / 10. + "%");
						System.out.println("Trump Avg: " + Math.round(stateAvgTrump[i] * 10) / 10. + "\t\tTrump Prob: " + Math.round(stateProbTrump[i] * 10) / 10. + "%");
						System.out.println("Other Avg: " + Math.round(stateAvgOther[i] * 10) / 10. + "\t\tOther Prob: " + Math.round(stateProbOther[i] * 10) / 10. + "%");
						break;
					} else if (i == states.length - 1) {
						System.out.println("State not found!");
					} // if else;
				} // for i
			} // if else
		} // while
	} // main method
} // Election class
