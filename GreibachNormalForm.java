import java.util.*;

public class GreibachNormalForm {

    // This method converts the given grammar into Greibach Normal Form (GNF)
    public ArrayList<String[]> convertToGNF(ArrayList<String[]> grammarMatrix) {
        ArrayList<String[]> newGrammar = new ArrayList<>();
        //System.out.println("Converting to Greibach Normal Form:");

        // Iterate through the productions in the grammar matrix
        for (String[] production : grammarMatrix) {
            String lhs = production[0]; // Left-hand side of the production (non-terminal)
            String rhs = production[1]; // Right-hand side of the production (terminal or non-terminal)

            // Check if the production is already in GNF format (it starts with a terminal symbol)
            if (Character.isLowerCase(rhs.charAt(0))) {
                // If it already starts with a terminal, keep the production as is
                newGrammar.add(production);
            } else {
                // If it doesn't start with a terminal, we need to apply transformations to convert it
                String[] transformedProduction = transformToGNF(lhs, rhs, grammarMatrix);
                newGrammar.add(transformedProduction);
            }
        }

        // Print the grammar in GNF format after transformation
        //printGrammar(newGrammar);
        return newGrammar; // Return the new grammar in Greibach Normal Form
    }

    // This helper method transforms a non-terminal-starting production into GNF
    private String[] transformToGNF(String lhs, String rhs, ArrayList<String[]> grammarMatrix) {
        // Find the first non-terminal in the right-hand side
        String firstNonTerminal = String.valueOf(rhs.charAt(0));

        // Create a new production to replace the non-terminal with its corresponding RHS
        for (String[] production : grammarMatrix) {
            if (production[0].equals(firstNonTerminal)) {
                String newRhs = production[1]; // Get the RHS of the production for the first non-terminal
                // Append the rest of the RHS after the first non-terminal to form the transformed production
                String newProduction = newRhs + rhs.substring(1);

                // Return a new production with the original LHS and the transformed RHS
                return new String[]{lhs, newProduction};
            }
        }

        // If no transformation is found, return the original production (error case)
        //System.out.println("Error: No valid transformation found for " + lhs + " -> " + rhs);
        return new String[]{lhs, rhs}; // Fallback (should not happen if the grammar is well-formed)
    }

    // This method prints the grammar matrix for debugging and verification purposes
    /*private void printGrammar(ArrayList<String[]> grammarMatrix) {
        System.out.println("Grammar in Greibach Normal Form:");
        // Iterate through each production in the grammar and print it
        for (String[] production : grammarMatrix) {
            System.out.println(Arrays.toString(production)); // Print the current production as an array of strings
        }
    }*/
}
