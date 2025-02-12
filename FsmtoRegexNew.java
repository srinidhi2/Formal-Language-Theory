import java.util.*;
import java.io.*;

public class FsmtoRegexNew {
    private List<String> alphabet;
    private List<List<Integer>> transitionTable;
    private Set<Integer> finalStates;
    private String[][] regexMatrix;
    private int stateCount;
    private int startState;

    public FsmtoRegexNew(String fileName) throws IOException {
        readDFSM(fileName);
        standardize();
        String regex = fsmtoregex();
        System.out.println("Regular Expression: " + regex);
    }

    // Reading the DFSM from the input file
    private void readDFSM(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        alphabet = Arrays.asList(reader.readLine().split(" "));
        System.out.println("alphabet : "+alphabet);
        reader.readLine(); // Skip empty line
        transitionTable = new ArrayList<>();
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            List<Integer> transitions = new ArrayList<>();
            for (String s : line.split(" ")) {
                if (s.equals("phi")) {
                transitions.add(0); // Using 0 to represent "phi" (no transition)
                } else {
                transitions.add(Integer.parseInt(s));
                }
            }
            transitionTable.add(transitions);
        }
        finalStates = new HashSet<>();
        for (String s : reader.readLine().split(" ")) {
            finalStates.add(Integer.parseInt(s));
        }
        System.out.println("final States : "+finalStates);
        stateCount = transitionTable.size();
        startState = 1;
        reader.close();
    }
    
    // Standardize the FSM: add new start and accepting states, and handle transitions
    private void standardize() {
        int newStartState = 0;
        int newAcceptingState = stateCount + 1;

        // Create a new transition table for states 0 to stateCount + 1 (new start and new accepting)
        transitionTable.add(new ArrayList<>(Collections.nCopies(alphabet.size(), 0)));
        
        // Initialize the regex matrix
        regexMatrix = new String[stateCount + 2][stateCount + 2];
        for (int i = 0; i <= stateCount + 1; i++) {
            for (int j = 0; j <= stateCount + 1; j++) {
                regexMatrix[i][j] = "phi"; // Initialize with phi
            }
        }

        // Set the transition for the new start state to the original start state
        regexMatrix[newStartState][startState] = "eps"; // eps transition from new start to original start

        // Set up transitions from the original DFSM
        for (int i = 0; i < stateCount; i++) {
            for (int j = 0; j < alphabet.size(); j++) {
                int nextState = transitionTable.get(i).get(j);
                if (nextState != 0) {
                    // Union logic: if there's already a transition, combine them
                    if (!regexMatrix[i + 1][nextState].equals("phi")) {
                        regexMatrix[i + 1][nextState] = regexMatrix[i + 1][nextState] + "+" + alphabet.get(j);
                    } else {
                        regexMatrix[i + 1][nextState] = alphabet.get(j); // Otherwise, just set the transition
                    }
                }
            }
        }

        // Set epsilon transitions from all original accepting states to the new accepting state
        for (Integer finalState : finalStates) {
            regexMatrix[finalState][newAcceptingState] = "eps"; // From final state to new accepting state
        }

        // Debug: Print the regex matrix after standardization
        System.out.println("Regex Matrix after start final:");
        for (int i = 0; i <= stateCount + 1; i++) {
            for (int j = 0; j <= stateCount + 1; j++) {
                System.out.print(regexMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    // FSM to regex conversion
    private String fsmtoregex() {
        for (int i = 1; i <= stateCount; i++) {
            ripState(i);
        }
        return regexMatrix[0][stateCount + 1];
    }

    // Ripping a state and updating transitions
    private void ripState(int rip) {
        //System.out.println("Ripping state " + rip);
        List<Integer> incoming = new ArrayList<>();
        List<Integer> outgoing = new ArrayList<>();

        // Find all incoming and outgoing transitions
        for (int i = 0; i <= stateCount + 1; i++) {
            if (!regexMatrix[i][rip].equals("phi")) {
                incoming.add(i);
            }
            if (!regexMatrix[rip][i].equals("phi")) {
                outgoing.add(i);
            }
        }

        // Print the incoming and outgoing transitions
        System.out.println("Incoming transitions: " + incoming);
        System.out.println("Outgoing transitions: " + outgoing);
        //System.out.println("Regex Matrix: "); 
        // Update the matrix based on the ripping formula
        for (int p : incoming) {
            for (int q : outgoing) {
                if (p != rip && q != rip) {
                    String r_pq = regexMatrix[p][q];
                    String r_pr = regexMatrix[p][rip];
                    String r_rr = regexMatrix[rip][rip];
                    String r_rq = regexMatrix[rip][q];

                    // Compute the new transition
                    String newRegex = combineRegex(r_pq, r_pr, r_rr, r_rq);
                    
                    regexMatrix[p][q] = newRegex;
                    System.out.println("p : "+p);
                    System.out.println("q : "+q);
                    System.out.println(regexMatrix[p][q]);
                }
            }
            //System.out.println();
        }

        // Set all transitions involving the ripped state to phi
        for (int i = 0; i <= stateCount + 1; i++) {
            regexMatrix[i][rip] = "phi";
            regexMatrix[rip][i] = "phi";
        }
        System.out.println("Regex Matrix after rip State "+rip);
        for (int i = 0; i <= stateCount + 1; i++) {
            for (int j = 0; j <= stateCount + 1; j++) {
                System.out.print(regexMatrix[i][j] + " ");
            }
            System.out.println();
        }
    } 

    // Combine regex expressions according to the ripping formula
    private String combineRegex(String r_pq, String r_pr, String r_rr, String r_rq) {
        String loopPart = r_rr.equals("phi") ? "" : "(" + r_rr + ")*";
        String pathThroughRip = r_pr.equals("phi") || r_rq.equals("phi") ? "" : r_pr + loopPart + r_rq;

        if (r_pq.equals("phi")) {
            return pathThroughRip.equals("") ? "phi" : pathThroughRip;
        } else {
            // If both r_pq and pathThroughRip exist, we need to add parentheses around the union
            String unionPart = pathThroughRip.equals("") ? r_pq : "(" + r_pq + "+" + pathThroughRip + ")";
            return unionPart;
        }
    }

    // Main function
    public static void main(String[] args) throws IOException {
        new FsmtoRegexNew(args[0]);
    }
}