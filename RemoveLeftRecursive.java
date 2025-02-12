import java.util.*;

public class RemoveLeftRecursive {

    // This method removes immediate left recursion from the grammar matrix
    public ArrayList<String[]> removeImmediateLeftRecursion(ArrayList<String[]> grammarMatrix) {
        ArrayList<String[]> newGrammar = new ArrayList<>(); // Initialize a new list for storing the updated grammar
        //System.out.println("Removing Immediate Left Recursion:");

        // Map to hold new rules (productions) after removing left recursion
        Map<String, List<String[]>> newRules = new HashMap<>();

        // Loop through all productions in the grammar
        for (String[] production : grammarMatrix) {
            String lhs = production[0]; // Left-hand side of the production (non-terminal)
            List<String> alpha = new ArrayList<>(); // This will store the recursive part (alpha)
            List<String> beta = new ArrayList<>(); // This will store the non-recursive part (beta)

            // Classify the right-hand side (RHS) of the production into alpha (left-recursive) or beta (non-recursive)
            for (int i = 1; i < production.length; i++) {
                String rhs = production[i]; // Right-hand side of the production
                if (rhs.equals(lhs)) {
                    alpha.add(rhs); // If RHS is equal to LHS, it's recursive (alpha)
                } else {
                    beta.add(rhs); // Otherwise, it's non-recursive (beta)
                }
            }

            // If alpha is not empty, we have left recursion, so we need to rewrite the production
            if (!alpha.isEmpty()) {
                String newNonTerminal = lhs + "'"; // Create a new non-terminal (e.g., A' for A -> A alpha)

                // Create new beta rules (non-recursive part)
                List<String[]> newBetaRules = new ArrayList<>();
                for (String b : beta) {
                    newBetaRules.add(new String[]{lhs, b, newNonTerminal}); // Beta -> A B' (new non-terminal)
                }

                // Create new alpha rules (recursive part)
                List<String[]> newAlphaRules = new ArrayList<>();
                for (String a : alpha) {
                    newAlphaRules.add(new String[]{newNonTerminal, a, newNonTerminal}); // A' -> A alpha A'
                }
                newAlphaRules.add(new String[]{newNonTerminal, "eps"}); // A' -> epsilon (empty)

                // Add the new rules to the map
                newRules.put(lhs, newBetaRules);
                newRules.put(newNonTerminal, newAlphaRules);
            } else {
                // If no left recursion, keep the original production
                newGrammar.add(production);
            }
        }

        // Add the newly created rules to the grammar matrix
        for (Map.Entry<String, List<String[]>> entry : newRules.entrySet()) {
            newGrammar.addAll(entry.getValue());
        }

        // Print the new grammar matrix after removing immediate left recursion
        //printGrammar(newGrammar);
        return newGrammar; // Return the updated grammar matrix
    }

    // This method eliminates left recursion for the grammar
    public ArrayList<String[]> eliminateLeftRecursion(ArrayList<String[]> grammarMatrix) {
        //System.out.println("Eliminating Left Recursion:");
        ArrayList<String[]> updatedGrammar = new ArrayList<>(grammarMatrix); // Create a copy of the grammar matrix

        // Loop through all productions in the grammar
        for (int i = 0; i < updatedGrammar.size(); i++) {
            String[] currentProduction = updatedGrammar.get(i);
            String currentLHS = currentProduction[0]; // Get the left-hand side (LHS) of the current production

            // Loop through the previous productions and substitute them if needed
            for (int j = 0; j < i; j++) {
                String[] lowerProduction = updatedGrammar.get(j);
                String lowerLHS = lowerProduction[0]; // Get the LHS of the lower (previous) production

                // Substitute the lower LHS with its corresponding rules
                updatedGrammar = substituteProductions(updatedGrammar, currentLHS, lowerLHS);
            }

            // We could potentially call removeImmediateLeftRecursion again, but it's commented out here
            // updatedGrammar = removeImmediateLeftRecursion(updatedGrammar); // Remove immediate left recursion again if necessary
        }

        // Print the grammar after eliminating left recursion
        //printGrammar(updatedGrammar);
        return updatedGrammar; // Return the updated grammar matrix
    }

    // This method substitutes lower-numbered non-terminals with their corresponding productions
    public ArrayList<String[]> substituteProductions(ArrayList<String[]> grammar, String lhs, String targetLHS) {
        ArrayList<String[]> newGrammar = new ArrayList<>(); // Create a new list for storing the updated grammar

        // Loop through the grammar and substitute the productions
        for (String[] production : grammar) {
            // If the production's LHS matches the current LHS and its RHS starts with the target LHS, we need to substitute
            if (production[0].equals(lhs) && production[1].equals(targetLHS)) {
                // Loop through all productions that have the target LHS on their LHS
                for (String[] targetProduction : grammar) {
                    if (targetProduction[0].equals(targetLHS)) {
                        // Create a new production by combining the current LHS with the target production
                        String[] substitutedProduction = new String[targetProduction.length + 1];
                        substitutedProduction[0] = lhs;
                        System.arraycopy(targetProduction, 1, substitutedProduction, 1, targetProduction.length - 1); // Copy RHS from target production
                        newGrammar.add(substitutedProduction); // Add the new substituted production to the grammar
                    }
                }
            } else {
                // If no substitution is needed, keep the original production
                newGrammar.add(production);
            }
        }

        // Print the grammar after the substitution step
        //System.out.println("Substitution Result:");
        //printGrammar(newGrammar);
        return newGrammar; // Return the updated grammar
    }

    // This is a helper method to print the grammar matrix to the console
    /*private void printGrammar(ArrayList<String[]> grammarMatrix) {
        //System.out.println("Current Grammar:");
        // Loop through each row in the grammar matrix and print it
        for (String[] row : grammarMatrix) {
            System.out.println(Arrays.toString(row)); // Print the current production as an array of strings
        }
    }*/
}
