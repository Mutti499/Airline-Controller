
public class flight   {
    ACC ACC;
    String pName;
    int addTime;
    String accName;
    String depart;
    String arrival;
    int step = 0;
    int[] operations = new int[22];

    public flight(int addTime, String pName, String accName, String depart, String arrival) {
        this.addTime = addTime;
        this.pName = pName;
        this.accName = accName;
        this.depart = depart;
        this.arrival = arrival;
    }



}

