import java.io.*;
import java.util.*;

public class NDFSMtoDFSM 
{
    public static void convert(String ndfsmFileName, String dfsmFileName) 
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(ndfsmFileName));
             FileWriter writer = new FileWriter(dfsmFileName)) 
        {
            // Read the alphabet
            String line = reader.readLine().trim();
            String[] alphabetArray = line.split(" ");
            char[] alphabet = new char[alphabetArray.length];
            for (int i = 0; i < alphabetArray.length; i++) 
            {
                alphabet[i] = alphabetArray[i].charAt(0);
            }

            // skips the empty line after the alphabet section
            reader.readLine();

            // read the transition table
            List<Map<Character, Set<Integer>>> transitionTable = new ArrayList<>();
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) 
            {
                String[] transitions = line.trim().split(" ");
                Map<Character, Set<Integer>> stateTransitions = new HashMap<>();

                for (int i = 0; i < transitions.length; i++) 
                {
                    String[] targetStates = transitions[i].replaceAll("[\\[\\]]", "").split(",");
                    Set<Integer> targets = new HashSet<>();
                    for (String state : targetStates) 
                    {
                        if (!state.isEmpty()) {
                            targets.add(Integer.parseInt(state));  // Parse and add the valid state nu
                        }
                    }
                    stateTransitions.put(alphabet[i], targets);
                //System.out.println("Processing state transitions: " + Arrays.toString(transitions));
                }
                transitionTable.add(stateTransitions);
            }

            // Read final states
            Set<Integer> finalStates = new HashSet<>();
            line = reader.readLine().trim();
            String[] finalStatesArray = line.replaceAll("[\\[\\]]", "").split(" ");
            for (String state : finalStatesArray) 
            {
                if (!state.isEmpty()) 
                {
                    finalStates.add(Integer.parseInt(state.trim()));
                }
            }

            // Convert NDFSM to DFSM
            Map<Set<Integer>, Integer> dfaStates = new HashMap<>();  // Map from NDFSM state sets to DFA state numbers
            List<Map<Character, Set<Integer>>> dfaTransitionTable = new ArrayList<>();
            List<Set<Integer>> dfaStatesList = new ArrayList<>();  // Track DFA state sets in order
            Queue<Set<Integer>> queue = new LinkedList<>();

            // Start state is [1]
            Set<Integer> startState = new HashSet<>(Collections.singletonList(1));
            queue.add(startState);
            dfaStates.put(startState, 1);  // Assign DFA state 1 to the NDFSM start state [1]
            dfaStatesList.add(startState);

            while (!queue.isEmpty()) 
            {
                Set<Integer> currentState = queue.poll(); // returns element at the front of queue
                Map<Character, Set<Integer>> dfaTransitions = new HashMap<>();

                for (char symbol : alphabet) 
                {
                    Set<Integer> nextStates = new HashSet<>();
                    for (Integer state : currentState) 
                    {
                        Set<Integer> targets = transitionTable.get(state - 1).get(symbol);
                        if (targets != null) 
                        {
                            nextStates.addAll(targets);
                        }
                    }
                    if (!nextStates.isEmpty()) 
                    {
                        // Assign a new DFA state number to this set if it's not seen before
                        if (!dfaStates.containsKey(nextStates)) 
                        {
                            dfaStates.put(nextStates, dfaStates.size() + 1);
                            dfaStatesList.add(nextStates);
                            queue.add(nextStates);
                        }
                        dfaTransitions.put(symbol, nextStates);
                    }
                }
                dfaTransitionTable.add(dfaTransitions);
            }

            // Write DFA output file
            // First, write the alphabet
            for (char symbol : alphabet) 
            {
                writer.write(symbol + " ");
            }
            writer.write("\n\n");

            // Now write the transition table
            for (int i = 0; i < dfaStatesList.size(); i++) 
            {
                Map<Character, Set<Integer>> transitions = dfaTransitionTable.get(i);
                for (char symbol : alphabet) 
                {
                    Set<Integer> targets = transitions.get(symbol);
                    if (targets == null) 
                    {
                        writer.write("[] ");
                    } 
                    else 
                    {
                        int targetState = dfaStates.get(targets);  // Get the DFA state number for this set of NDFSM states
                        writer.write(targetState + " ");
                    }
                }
                writer.write("\n");
            }

            // Write final DFA states (those that contain NDFSM final states)
            Set<Integer> dfaFinalStates = new HashSet<>();
            for (Map.Entry<Set<Integer>, Integer> entry : dfaStates.entrySet()) 
            {
                if (!Collections.disjoint(entry.getKey(), finalStates)) 
                {
                    dfaFinalStates.add(entry.getValue());
                }
            }
            writer.write("\n" + dfaFinalStates.toString().replaceAll("[\\[\\],]", "") + "\n");

        } catch (IOException e) 
        {
            System.err.println("Error reading or writing file: " + e.getMessage());
        }
    }
    public static void main(String[] args) 
    {
        convert("ndfsmOutputFile.txt","dfsmoutput.txt");
    }
}
