public class State {
    private String name;
    private String abbreviation;
    private int region;
    private int electoralVotes;
    private double clinton;
    private double trump;
    private double other;
    private double stdDev;
    private double elasticity;
    private String status;
    private double reporting;
    private String winner = "";
    
    public State(String name, String abbreviation, int region, int electoralVotes, double clinton, double trump, double stdDev, double elasticity, String status, double reporting) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.region = region;
        this.electoralVotes = electoralVotes;
        this.clinton = clinton;
        this.trump = trump;
        this.other = 100. - clinton - trump;
        this.stdDev = stdDev;
        this.elasticity = elasticity;
        this.status = status;
        this.reporting = reporting;
    } // State constructor

    public String getName() {
        return name;
    } // getName method
    
    public String getAbbreviation() {
        return abbreviation;
    } // getAbbreviation method
    
    public int getRegion() {
        return region;
    } // getRegion method

    public int getElectoralVotes() {
        return electoralVotes;
    } // getElectoralVotes method

    public double getClinton() {
        return clinton;
    } // getClinton method

    public void setClinton(double clinton) {
        this.clinton = clinton;
    } // setClinton method

    public double getTrump() {
        return trump;
    } // getTrump method
    
    public void setTrump(double trump) {
        this.trump = trump;
    } // setTrump method

    public double getOther() {
        return other;
    } // getOther method
    
    public void setOther(double other) {
        this.other = other;
    } // setOther method
    
    public double getStdDev() {
        return stdDev;
    } // getStdDev method
    
    public double getElasticity() {
        return elasticity;
    } // getElasticity method
    
    public String getStatus() {
        return status;
    } // getStatus method
    
    public int getStatusAsInt() {
        if (status.equals("POLL")) {
            return 1;
        } else {
            return 0;
        } // if else
    } // getStatusAsInt method
    
    public double getReporting() {
        return reporting;
    } // getElasticity method
    
    public String getWinner() {
        return winner;
    } // getWinner method
    
    public void setWinner(String winner) {
        this.winner = winner;
    } // setWinner method
} // State class
