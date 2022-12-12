import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class ATC {
     String name;
     Queue<flight> ATCRunningQ = new LinkedList<flight>();
     PriorityQueue<flight> ATCWaitingQ = new PriorityQueue<flight>(10000, new WaitingQ());


}
