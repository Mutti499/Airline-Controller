import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;


public class ACC {
    String name;
    int time = 0;
    int atcSize;
    ATC[] ATCMap = new ATC[1000];
    boolean isFinished = false;
    ArrayList<ATC> ATCList = new ArrayList<ATC>();
    ArrayList<flight> flights = new ArrayList<flight>();
    Queue<flight> ACCRunningQ = new LinkedList<flight>();
    PriorityQueue<flight> ACCWaitingQ = new PriorityQueue<flight>(10000, new WaitingQ());
    int restriction = 30;

    public void flightsPrinter() {
        flights.forEach((n) -> System.out.println(n));
        System.out.println();
    }

    @Override
    public String toString() {
        return "name=" + name
                + ",\n time=" + time
                + ",\n atcSize=" + atcSize + "\n";

    }

}
