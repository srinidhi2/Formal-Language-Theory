import java.io.*;
import java.util.*;

public class NDFSMtoDFSMClass {

    public static void convert(String ndfsmFileName, String dfsmFileName) {
        List<Map<Character, Set<Integer>>> ndfsmTransitions = new ArrayList<>();
        Set<Integer> acceptingStates = new HashSet<>();
        Set<Character> alphabet = new LinkedHashSet<>();

        // Parse the NDFSM file
        try (BufferedReader reader = new BufferedReader(new FileReader(ndfsmFileName))) {
            // Read the alphabet (first line)
            String line = reader.readLine().trim();
            if (line.isEmpty()) {
                System.err.println("Error: Alphabet line is empty.");
                return;
            }
            String[] alphabetChars = line.split("\\s+");
            for (String symbol : alphabetChars) {
                alphabet.add(symbol.charAt(0));
            }
            reader.readLine();
            // Read transitions
            int stateIndex = 0;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] transitionStrings = line.split("\\s+");
                Map<Character, Set<Integer>> stateTransitions = new HashMap<>();
                
                int i = 0;
                for (char symbol : alphabet) {
                    if (i >= transitionStrings.length) break;
                    String transitionString = transitionStrings[i].replaceAll("[\\[\\],]", "").trim();
                    Set<Integer> transitionSet = new HashSet<>();
                    
                    if (!transitionString.isEmpty()) {
                        String[] states = transitionString.split("\\s+");
                        for (String state : states) {
                            try {
                                int stateIndexValue = Integer.parseInt(state);
                                if (stateIndexValue > 0) {
                                    transitionSet.add(stateIndexValue - 1); // Convert 1-based to 0-based index
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid state number: " + state);
                            }
                        }
                    }
                    stateTransitions.put(symbol, transitionSet);
                    i++;
                }
                ndfsmTransitions.add(stateTransitions);
                stateIndex++;
            }
            System.out.println("ndfsmTransitions : "+ ndfsmTransitions);
            reader.readLine();
            // Read the final accepting state(s) from the last line
            if ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] acceptingStateStrings = line.replaceAll("[\\[\\],]", "").split("\\s+");
                for (String state : acceptingStateStrings) {
                    try {
                        int stateIndexValue = Integer.parseInt(state);
                        if (stateIndexValue > 0) {
                            acceptingStates.add(stateIndexValue - 1); // Convert 1-based to 0-based index
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid final state number: " + state);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading NDFSM file: " + e.getMessage());
            return;
        }

        // Step 2: Convert NDFSM to DFSM
        Map<Set<Integer>, Integer> dfaStates = new HashMap<>();
        List<int[]> dfaTransitions = new ArrayList<>();
        int dfaStateCounter = 1;
        dfaStates.put(new HashSet<>(Collections.singletonList(0)), dfaStateCounter++);

        Queue<Set<Integer>> stateQueue = new LinkedList<>();
        stateQueue.add(new HashSet<>(Collections.singletonList(0)));

        Map<Integer, Map<Character, Integer>> dfsmTransitionMap = new HashMap<>();

        while (!stateQueue.isEmpty()) {
            Set<Integer> currentNDFSMStates = stateQueue.poll();
            int currentDFSMState = dfaStates.get(currentNDFSMStates);

            Map<Character, Set<Integer>> newTransitions = new HashMap<>();
            
            for (char symbol : alphabet) {
                Set<Integer> newNDFSMStates = new HashSet<>();

                for (int ndState : currentNDFSMStates) {
                    if (ndState >= ndfsmTransitions.size()) {
                        System.err.println("Error: NDFSM state index out of bounds: " + ndState);
                        continue;
                    }
                    
                    Set<Integer> transition = ndfsmTransitions.get(ndState).get(symbol);
                    if (transition != null) {
                        newNDFSMStates.addAll(transition);
                    }
                }

                if (!newNDFSMStates.isEmpty()) {
                    if (!dfaStates.containsKey(newNDFSMStates)) {
                        dfaStates.put(newNDFSMStates, dfaStateCounter++);
                        stateQueue.add(newNDFSMStates);
                    }
                    newTransitions.put(symbol, newNDFSMStates);
                }
            }

            Map<Character, Integer> currentDFSMTransitions = new HashMap<>();
            for (Map.Entry<Character, Set<Integer>> entry : newTransitions.entrySet()) {
                Set<Integer> newStates = entry.getValue();
                currentDFSMTransitions.put(entry.getKey(), dfaStates.get(newStates));
            }
            dfsmTransitionMap.put(currentDFSMState, currentDFSMTransitions);
        }

        // Print results for debugging
        System.out.println("DFA States: " + dfaStates);
        System.out.println("DFSM Transitions: " + dfsmTransitionMap);

        // Optionally, write to DFSM file or further processing
    }

    public static void main(String[] args) {
        String ndfsmFileName = "ndfsmoutput.txt";  // Replace with actual file path
        String dfsmFileName = "dfsmoutput.txt";    // Replace with actual file path
        convert(ndfsmFileName, dfsmFileName);
    }
}
