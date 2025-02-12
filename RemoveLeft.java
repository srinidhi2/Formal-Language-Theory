import java.util.*;

public class RemoveLeft {
    private ArrayList<String[]> modifiedMatrix;

    // Constructor that takes grammar matrix and processes it to remove left recursion
    public RemoveLeft(ArrayList<String[]> grammarMatrix) {
        this.modifiedMatrix = new ArrayList<>();
        removeLeftRecursion(grammarMatrix);
    }

    // Method to remove left recursion from the grammar
    private void removeLeftRecursion(ArrayList<String[]> grammarMatrix) {
        // Step 1: Parse the grammar matrix into a map for easier processing
        Map<String, List<String>> productions = new LinkedHashMap<>();
        List<String> nonTerminalsOrder = new ArrayList<>();

        // Parse the input matrix
        for (String[] row : grammarMatrix) {
            String lhs = row[0];
            nonTerminalsOrder.add(lhs);
            List<String> rhsList = new ArrayList<>(Arrays.asList(row).subList(1, row.length));
            productions.put(lhs, rhsList);
        }

        System.out.println("Parsed Productions (in input order): " + productions);

        // Step 2: Process each non-terminal to remove left recursion
        Map<String, List<String>> newProductions = new LinkedHashMap<>();
        Set<String> processedNonTerminals = new HashSet<>();
        Map<String, String> newNonTerminalMap = new LinkedHashMap<>();

        // Process each non-terminal in the order it appears in the input
        for (String nonTerminal : nonTerminalsOrder) {
            // Avoid reprocessing any non-terminal that already has left recursion handled
            if (processedNonTerminals.contains(nonTerminal)) continue;

            List<String> rhsList = productions.get(nonTerminal);
            List<String> alpha = new ArrayList<>();
            List<String> beta = new ArrayList<>();
            String newNonTerminal = nonTerminal + "'";

            // Separate left-recursive (alpha) and non-recursive (beta) productions
            for (String rhs : rhsList) {
                if (rhs.startsWith(nonTerminal)) {
                    alpha.add(rhs.substring(nonTerminal.length()).trim()); // α part
                } else {
                    beta.add(rhs); // β part
                }
            }

            System.out.println("Processing non-terminal: " + nonTerminal);
            System.out.println("  Alpha (left-recursive): " + alpha);
            System.out.println("  Beta (non-recursive): " + beta);

            // If left recursion is detected
            if (!alpha.isEmpty()) {
                System.out.println("Left Recursion Detected for: " + nonTerminal);

                // Create new productions for non-terminal
                newProductions.put(nonTerminal, new ArrayList<>());
                newProductions.put(newNonTerminal, new ArrayList<>());

                // β A' for non-recursive part
                for (String b : beta) {
                    newProductions.get(nonTerminal).add(b + " " + newNonTerminal);
                }

                // α A' | ε for recursive part
                for (String a : alpha) {
                    newProductions.get(newNonTerminal).add(a + " " + newNonTerminal);
                }
                newProductions.get(newNonTerminal).add("ε");
            } else {
                // No left recursion, copy the original productions
                newProductions.put(nonTerminal, rhsList);
            }

            processedNonTerminals.add(nonTerminal);
            newNonTerminalMap.put(nonTerminal, newNonTerminal);
        }

        System.out.println("Modified Productions: " + newProductions);

        // Step 3: Convert modified productions back to 2D matrix format
        for (Map.Entry<String, List<String>> entry : newProductions.entrySet()) {
            String lhs = entry.getKey();
            List<String> rhsList = entry.getValue();

            // Create a row for each production rule
            String[] row = new String[rhsList.size() + 1];
            row[0] = lhs;
            for (int i = 0; i < rhsList.size(); i++) {
                row[i + 1] = rhsList.get(i);
            }
            modifiedMatrix.add(row);
        }
    }

    // Getter method to return the modified matrix
    public ArrayList<String[]> getModifiedMatrix() {
        return modifiedMatrix;
    }

    // Helper method to print the matrix in the input format
    public static void printMatrix(ArrayList<String[]> matrix) {
        for (String[] row : matrix) {
            System.out.println(String.join(" ", row));
        }
    }

    public static void main(String[] args) {
        // Example input grammar with left recursion
        ArrayList<String[]> grammarMatrix = new ArrayList<>();
        grammarMatrix.add(new String[] {"S", "S A", "B A"});
        grammarMatrix.add(new String[] {"A", "a"});
        grammarMatrix.add(new String[] {"B", "b"});

        // Create an instance of RemoveLeft to process the grammar
        RemoveLeft removeLeft = new RemoveLeft(grammarMatrix);

        // Get the modified grammar and print it
        ArrayList<String[]> modifiedGrammar = removeLeft.getModifiedMatrix();
        System.out.println("Resulting Grammar after Removing Left Productions:");
        printMatrix(modifiedGrammar);
    }
}
