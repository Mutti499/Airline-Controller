import java.util.Comparator;

class WaitingQ implements Comparator<flight>{
             

    public int compare(flight s1, flight s2) {
        

        int add1 = s1.operations[s1.step];
        int add2 = s2.operations[s2.step];

        if(add1 - add2 != 0){return add1 - add2;}
        
        
        String name1 = s1.pName.toUpperCase();
        String name2 = s2.pName.toUpperCase();

        return name1.compareTo(name2);
        }
}