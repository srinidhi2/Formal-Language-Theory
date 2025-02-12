import java.util.*;

public class RemoveUnitProductions {

    // Main method that removes unit productions from the given grammar.
    public static ArrayList<String[]> removeUnits(ArrayList<String[]> grammar) {
        // Lists to store unit and non-unit productions
        ArrayList<String[]> unitProductions = new ArrayList<>();
        ArrayList<String[]> nonUnitProductions = new ArrayList<>();

        // Classify the grammar into unit and non-unit productions
        for (String[] production : grammar) {
            if (isUnitProduction(production)) {
                // If it's a unit production, add it to the unitProductions list
                unitProductions.add(production);
            } else {
                // Otherwise, add it to nonUnitProductions list
                nonUnitProductions.add(production);
            }
        }

        // Set to track processed unit productions to avoid duplication
        Set<String> processedUnits = new HashSet<>();
        ArrayList<String[]> newProductions = new ArrayList<>();

        // Process each unit production until none remain
        while (!unitProductions.isEmpty()) {
            Iterator<String[]> iterator = unitProductions.iterator();

            while (iterator.hasNext()) {
                String[] production = iterator.next();
                String lhs = production[0];

                // Concatenate the right-hand side of the production
                String rhs = String.join("", Arrays.copyOfRange(production, 1, production.length)).trim();

                // Skip the unit production if it has already been processed
                if (processedUnits.contains(lhs + "→" + rhs)) {
                    iterator.remove();
                    continue;
                }

                // Mark the unit production as processed
                processedUnits.add(lhs + "→" + rhs);
                iterator.remove();

                // Expand the unit production by adding all the rules from rhs
                for (String[] prod : grammar) {
                    if (prod[0].equals(rhs)) {
                        // Ensure we add non-unit productions derived from rhs
                        if (prod.length > 1) {
                            String[] expandedProduction = new String[prod.length];
                            expandedProduction[0] = lhs;
                            System.arraycopy(prod, 1, expandedProduction, 1, prod.length - 1);

                            // Add expanded productions if not already present
                            if (!containsRule(newProductions, expandedProduction)) {
                                newProductions.add(expandedProduction);
                            }
                        }
                    }
                }
            }

            // Add newly derived non-unit productions to the existing ones
            nonUnitProductions.addAll(newProductions);
            newProductions.clear();
        }

        // Return the updated list of non-unit productions
        return nonUnitProductions;
    }

    // Checks if a given production is a unit production (i.e., A → B where B is a single non-terminal)
    private static boolean isUnitProduction(String[] production) {
        // A unit production must have exactly two parts: [LHS, RHS]
        if (production.length == 2) {
            String rhs = production[1].trim(); // Ensure RHS is trimmed to avoid whitespace issues
            // Check if RHS is either only uppercase letters or starts with an uppercase letter followed by digits
            return rhs.matches("[A-Z]+") || rhs.matches("[A-Z][A-Za-z0-9]*");
        }
        return false;
    }

    // Checks if a rule already exists in the grammar to avoid duplicates
    private static boolean containsRule(List<String[]> grammar, String[] rule) {
        for (String[] production : grammar) {
            if (Arrays.equals(production, rule)) {
                return true;
            }
        }
        return false;
    }

    // Utility function to print the grammar matrix (for debugging purposes)
    /*private static void printMatrix(ArrayList<String[]> matrix) {
        // Print each rule in the matrix for inspection
        for (String[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }*/
}
