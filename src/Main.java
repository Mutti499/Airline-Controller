import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.PriorityQueue;


public class Main {
    public static void main(String[] args) throws IOException {
        FileOutputStream foutput = new FileOutputStream(args[1]);


        System.setOut(new PrintStream(foutput));
        int ACCNum;
        int flightNum;
        PriorityQueue<flight> compareQ = new PriorityQueue<flight>(10000, new WaitingQ());

        ArrayList<String> inputLines = new ArrayList<String>();
        ArrayList<String> outputLines = new ArrayList<String>();

        ArrayList<Integer> TIMES = new ArrayList<Integer>();

        FileReader in = new FileReader(args[0]);


        BufferedReader br = new BufferedReader(in);

        String line;
        while ((line = br.readLine()) != null) {
            inputLines.add(line);
        }
        in.close();


        // Part to initiate ACC and flight number
        String[] arrOfStr = inputLines.get(0).split(" ", 3);
        ACCNum = Integer.parseInt(arrOfStr[0]);
        flightNum = Integer.parseInt(arrOfStr[1]);

        ACC[] allMap = new ACC[ACCNum];
        for (int i = 0; i < ACCNum; i++) {
            String[] accLine = inputLines.get(i + 1).split(" ", 10000);
            int ATCNum = accLine.length - 1;

            String ACCName = accLine[0];

            ACC myACC = new ACC();
            myACC.name = ACCName;

            for (int j = 0; j < ATCNum; j++) {
                ATC myATC = new ATC();
                myATC.name = accLine[1 + j];
                int has = hashFinder(myATC.name);
                int x = 0;
                while (myACC.ATCMap[has + x] != null) {
                    x++;
                }
                myACC.ATCMap[has + x] = myATC;
                myACC.ATCList.add(myATC);
            }
            myACC.atcSize = ATCNum;

            allMap[i] = myACC;
        }

        for (int i = 0; i < flightNum; i++) {
            String[] flightLine = inputLines.get(i + 1 + ACCNum).split(" ", 10000);
            int addition = Integer.parseInt(flightLine[0]);
            String plainname = flightLine[1];
            String acc = flightLine[2];
            String depart = flightLine[3];
            String arrival = flightLine[4];

            flight plain = new flight(addition, plainname, acc, depart, arrival);
            plain.operations[0] = addition;
            for (int j = 0; j < 21; j++) {
                plain.operations[j + 1] = Integer.parseInt(flightLine[5 + j]);
            }
            for (int j = 0; j < ACCNum; j++) {
                if (acc.compareTo(allMap[j].name) == 0) {
                    allMap[j].flights.add(plain);

                }
            }

        }


        for (ACC ACC1 : allMap) {

            while (!ACC1.isFinished) {


                for (flight f : ACC1.flights) {
                    compareQ.add(f);

                }
                for (flight f : ACC1.ACCWaitingQ) {
                    compareQ.add(f);
                }

                if (ACC1.ACCRunningQ.peek() != null) {
                    flight f = ACC1.ACCRunningQ.peek();
                    compareQ.add(f);
                }

                ACC1.ATCList.forEach((n) -> {
                    if (n.ATCRunningQ.peek() != null) {
                        compareQ.add(n.ATCRunningQ.peek());
                    }

                    for (flight flt : n.ATCWaitingQ) {
                        compareQ.add(flt);
                    }

                });


                if (compareQ.size() == 0) {

                    ACC1.isFinished = true;
                    break;
                }

                flight operationFlight = compareQ.peek();
                compareQ.clear();
                int operationTime = operationFlight.operations[operationFlight.step];

                int res = ACC1.restriction;
                flight minFlight = null;
                if (ACC1.ACCRunningQ.peek() != null) {
                    minFlight = ACC1.ACCRunningQ.peek();
                }

                if (minFlight == null) {
                    minFlight = operationFlight;
                }

                int min = Math.min(res, operationTime);
                ACC1.time += min;

                // TIME UPDATE PART
                // **************************************************************************
                // 1) ADDITION QUEUE
                for (int j = 0; j < ACC1.flights.size(); j++) {
                    ACC1.flights.get(j).addTime -= min;
                    ACC1.flights.get(j).operations[0] -= min;

                }

                // 2) Operation 1 and 3 and 11 and 13 and 21
                try {
                    flight first = ACC1.ACCRunningQ.peek();
                    first.operations[first.step] -= min;
                    ACC1.restriction -= min;

                } catch (Exception e) {
                }

                // 3) Operation 2 and 12

                for (flight n : ACC1.ACCWaitingQ) {

                    n.operations[n.step] -= min;

                }

                // 4) Rest operations
                for (ATC n : ACC1.ATCList) {

                    // 4.1) ATCRUNNING
                    try {
                        flight first = n.ATCRunningQ.peek();
                        first.operations[first.step] -= min;

                    } catch (Exception e) {
                    }

                    // 4.2) ATCWAITING

                    for (flight k : n.ATCWaitingQ) {

                        k.operations[k.step] -= min;

                    }

                }

                // SECOND TIME ADDING

                for (flight f : ACC1.flights) {
                    if (f.operations[f.step] == 0) {
                        compareQ.add(f);

                    }
                }
                for (flight f : ACC1.ACCWaitingQ) {
                    if (f.operations[f.step] == 0) {
                        compareQ.add(f);

                    }
                }

                if (ACC1.ACCRunningQ.peek() != null) {
                    flight f = ACC1.ACCRunningQ.peek();
                    if (ACC1.restriction == 0) {
                        compareQ.add(f);
                    } else if (f.operations[f.step] == 0) {
                        compareQ.add(f);
                    }
                }

                ACC1.ATCList.forEach((n) -> {
                    if (n.ATCRunningQ.peek() != null) {
                        flight f = n.ATCRunningQ.peek();
                        if (f.operations[f.step] == 0) {
                            compareQ.add(f);
                        }

                    }

                    for (flight f : n.ATCWaitingQ) {
                        if (f.operations[f.step] == 0) {
                            compareQ.add(f);

                        }
                    }

                });


                for (flight operationF : compareQ) {

                    int stage = operationF.step;

                    switch (stage) {
                        case 0:
                            if (operationF.addTime == 0) {
                                ACC1.flights.remove(operationF);

                                ACC1.ACCRunningQ.add(operationF);
                                operationF.step++;
                            }
                            break;

                        case 1:
                        case 11:
                            ACC1.restriction = 30;

                            if (operationF.operations[stage] == 0) {
                                ACC1.ACCRunningQ.remove(operationF);
                                ACC1.ACCWaitingQ.add(operationF);
                                operationF.step++;

                            } else {
                                ACC1.ACCRunningQ.remove(operationF);

                                ACC1.ACCRunningQ.add(operationF);

                            }

                            break;
                        case 2:
                        case 12:
                            if (operationF.operations[stage] == 0) {
                                ACC1.ACCWaitingQ.remove(operationF);

                                ACC1.ACCRunningQ.add(operationF);

                                operationF.step++;

                            } else {
                            }

                            break;
                        case 3:
                            ACC1.restriction = 30;

                            if (operationF.operations[stage] == 0) {
                                ACC1.ACCRunningQ.remove(operationF);

                                String depart = operationF.depart;
                                int index = hashFinder(depart);
                                ACC1.ATCMap[index].ATCRunningQ.add(operationF);
                                operationF.step++;

                            } else {
                                ACC1.ACCRunningQ.remove(operationF);

                                ACC1.ACCRunningQ.add(operationF);

                            }
                            break;
                        case 4:
                        case 6:
                        case 8:
                            ATC operationATC = ACC1.ATCMap[hashFinder(operationF.depart)];

                            if (operationF.operations[stage] == 0) {
                                operationATC.ATCRunningQ.remove(operationF);

                                operationATC.ATCWaitingQ.add(operationF);
                                operationF.step++;
                            } else {
                                operationATC.ATCRunningQ.remove(operationF);

                                operationATC.ATCRunningQ.add(operationF);
                            }

                            break;

                        case 5:
                        case 7:
                        case 9:
                            ATC operationATC2 = ACC1.ATCMap[hashFinder(operationF.depart)];

                            if (operationF.operations[stage] == 0) {
                                operationATC2.ATCWaitingQ.remove(operationF);

                                operationATC2.ATCRunningQ.add(operationF);

                                operationF.step++;

                            } else {
                            }
                            break;
                        case 10:
                            ATC operationATC3 = ACC1.ATCMap[hashFinder(operationF.depart)];

                            if (operationF.operations[stage] == 0) {
                                operationATC3.ATCRunningQ.remove(operationF);

                                ACC1.ACCRunningQ.add(operationF);
                                operationF.step++;
                            } else {
                                operationATC3.ATCRunningQ.remove(operationF);

                                operationATC3.ATCRunningQ.add(operationF);
                            }
                            break;
                        case 13:
                            ACC1.restriction = 30;

                            if (operationF.operations[stage] == 0) {
                                ACC1.ACCRunningQ.remove(operationF);

                                String arrival = operationF.arrival;
                                int index = hashFinder(arrival);
                                ACC1.ATCMap[index].ATCRunningQ.add(operationF);
                                operationF.step++;

                            } else {
                                ACC1.ACCRunningQ.remove(operationF);

                                ACC1.ACCRunningQ.add(operationF);

                            }
                            break;

                        case 14:
                        case 16:
                        case 18:
                            ATC operationATC4 = ACC1.ATCMap[hashFinder(operationF.arrival)];

                            if (operationF.operations[stage] == 0) {
                                operationATC4.ATCRunningQ.remove(operationF);

                                operationATC4.ATCWaitingQ.add(operationF);
                                operationF.step++;
                            } else {
                                operationATC4.ATCRunningQ.remove(operationF);

                                operationATC4.ATCRunningQ.add(operationF);
                            }
                            break;
                        case 15:
                        case 17:
                        case 19:
                            ATC operationATC5 = ACC1.ATCMap[hashFinder(operationF.arrival)];
                            if (operationF.operations[stage] == 0) {
                                operationATC5.ATCWaitingQ.remove(operationF);

                                operationATC5.ATCRunningQ.add(operationF);

                                operationF.step++;

                            } else {
                                // IDK
                            }
                            break;
                        case 20:
                            ATC operationATC6 = ACC1.ATCMap[hashFinder(operationF.arrival)];

                            if (operationF.operations[stage] == 0) {
                                operationATC6.ATCRunningQ.remove(operationF);

                                ACC1.ACCRunningQ.add(operationF);
                                operationF.step++;
                            } else {
                                operationATC6.ATCRunningQ.remove(operationF);

                                operationATC6.ATCRunningQ.add(operationF);
                            }
                            break;
                        case 21:
                            ACC1.restriction = 30;

                            if (operationF.operations[stage] == 0) {
                                ACC1.ACCRunningQ.remove(operationF);

                            } else {
                                ACC1.ACCRunningQ.remove(operationF);

                                ACC1.ACCRunningQ.add(operationF);

                            }

                    }

                }
                compareQ.clear();


            }
            TIMES.add(ACC1.time);

            String output = ACC1.name + " ";
            output += ACC1.time + " ";

            for (ATC n : ACC1.ATCMap) {
                if (n != null) {
                    int hash = hashFinder(n.name);
                    if (hash < 100) {
                        if (hash < 10) {
                            output += n.name + "00" + hash;
                        } else {
                            output += n.name + "0" + hash;
                        }
                    } else {
                        output += n.name + hash;

                    }
                    output += " ";
                }
            }
            ;
            outputLines.add(output);

        }

        outputLines.forEach((n) -> {
            System.out.println(n);
        });

    }

    public static int hashFinder(String str) {

        int hashVal = 0;
        for (int j = 0; j < str.length(); j++) {
            int letter = str.charAt(j);
            hashVal += letter * Math.pow(31, j);
            hashVal = hashVal % 1000;
        }

        return hashVal;
    }
}
