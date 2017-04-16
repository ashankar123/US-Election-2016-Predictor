import java.text.SimpleDateFormat;
import java.util.Date;

public class ElectionNight {
	
	private static Date date = new Date();
	private static SimpleDateFormat ft = new SimpleDateFormat ("D");

	public static void main(String[] args) {
		String arguments[] = {ft.format(date), "data/statesElectionNight.dat", "20000"};
		Election.main(arguments);
	} // main method
} // ElectionNight class