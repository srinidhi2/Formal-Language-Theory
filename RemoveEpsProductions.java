import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RemoveEpsProductions {

    // Main function to remove epsilon productions from the grammar
    public static ArrayList<String[]> removeEps(ArrayList<String[]> reachableGrammar) {
        //System.out.println("Starting RemoveEps algorithm...");

        ArrayList<String[]> epsFreeGrammar = new ArrayList<>(reachableGrammar);

        // Identify all nullable variables in the grammar
        Set<String> nullableVariables = findNullableVariables(epsFreeGrammar);
        //System.out.println("Nullable Variables: " + nullableVariables);

        // Remove epsilon rules from the grammar
        eliminateEpsilonRules(epsFreeGrammar, nullableVariables);

        // Clean up nullable variables that don't have meaningful rules anymore
        cleanupNullableNonTerminals(epsFreeGrammar, nullableVariables);

        // Explicitly remove rules that are of the form X -> ε
        removeEpsilonRules(epsFreeGrammar);

        // Remove duplicate rules to ensure the grammar remains consistent
        deduplicateRules(epsFreeGrammar);

        return epsFreeGrammar;
    }

    // Finds nullable variables (variables that can derive epsilon)
    private static Set<String> findNullableVariables(ArrayList<String[]> grammar) {
        Set<String> nullableVariables = new HashSet<>();
        // Check for epsilon productions (X -> ε)
        for (String[] rule : grammar) {
            if (rule.length > 1 && rule[1].isEmpty()) {
                nullableVariables.add(rule[0]);
                //System.out.println("Adding nullable variable (epsilon production): " + rule[0]);
            }
        }

        // Iterate and add variables that can derive epsilon indirectly
        boolean addedNewNullable;
        do {
            addedNewNullable = false;
            for (String[] rule : grammar) {
                String lhs = rule[0];
                if (!nullableVariables.contains(lhs)) {
                    boolean isNullable = true;
                    for (int i = 1; i < rule.length; i++) {
                        if (!nullableVariables.contains(rule[i])) {
                            isNullable = false;
                            break;
                        }
                    }
                    if (isNullable) {
                        nullableVariables.add(lhs);
                        addedNewNullable = true;
                        //System.out.println("Adding nullable variable based on RHS: " + lhs);
                    }
                }
            }
        } while (addedNewNullable);

        return nullableVariables;
    }

    // Removes epsilon rules from the grammar
    private static void eliminateEpsilonRules(ArrayList<String[]> grammar, Set<String> nullableVariables) {
        ArrayList<String[]> newRules = new ArrayList<>();
        Set<String[]> existingRules = new HashSet<>();

        // For each rule, generate new rules by removing nullable symbols
        for (String[] rule : grammar) {
            String lhs = rule[0];
            ArrayList<String[]> modifiedRules = generateModifiedRules(lhs, rule, nullableVariables);
            for (String[] modifiedRule : modifiedRules) {
                // Avoid adding duplicates
                if (!existingRules.contains(modifiedRule) && modifiedRule.length > 1) {
                    newRules.add(modifiedRule);
                    existingRules.add(modifiedRule);
                }
            }
        }

        // Replace the old grammar with the modified rules
        grammar.clear();
        grammar.addAll(newRules);
    }

    // Generates all possible rules by removing nullable variables from the RHS of the rule
    private static ArrayList<String[]> generateModifiedRules(String lhs, String[] rule, Set<String> nullableVariables) {
        ArrayList<String[]> modifiedRules = new ArrayList<>();
        ArrayList<String> currentRule = new ArrayList<>();

        // Add the LHS of the rule
        currentRule.add(lhs);

        // Recursive function to handle nullable combinations
        generateCombinations(rule, nullableVariables, currentRule, 1, modifiedRules);

        return modifiedRules;
    }

    // Recursive helper function to generate combinations of nullable variables
    private static void generateCombinations(String[] rule, Set<String> nullableVariables, 
                                            ArrayList<String> currentRule, int index, 
                                            ArrayList<String[]> modifiedRules) {
        if (index == rule.length) {
            // Add the generated rule to the modified rules list
            if (currentRule.size() > 1) {
                modifiedRules.add(currentRule.toArray(new String[0]));
            }
            return;
        }

        // Include the current symbol
        currentRule.add(rule[index]);
        generateCombinations(rule, nullableVariables, currentRule, index + 1, modifiedRules);

        // Backtrack: Remove the current symbol
        currentRule.remove(currentRule.size() - 1);

        // Exclude the current symbol if it's nullable
        if (nullableVariables.contains(rule[index])) {
            generateCombinations(rule, nullableVariables, currentRule, index + 1, modifiedRules);
        }
    }



    // Cleans up nullable non-terminals that no longer have meaningful productions
    private static void cleanupNullableNonTerminals(ArrayList<String[]> grammar, Set<String> nullableVariables) {
        Set<String> lhsVariables = new HashSet<>();
        // Identify all LHS variables
        for (String[] rule : grammar) {
            lhsVariables.add(rule[0]);
        }

        // For each nullable variable, remove it from the rules where it doesn't appear on the LHS
        for (String nullable : nullableVariables) {
            if (!lhsVariables.contains(nullable)) {
                for (String[] rule : grammar) {
                    for (int i = 1; i < rule.length; i++) {
                        if (rule[i].equals(nullable)) {
                            rule[i] = "";  // Mark the nullable variable as removed
                            //System.out.println("Removing nullable non-terminal: " + nullable + " from rule: " + rule[0]);
                        }
                    }
                }
            }
        }
    }

    // Explicitly removes rules of the form X -> ε
    private static void removeEpsilonRules(ArrayList<String[]> grammar) {
        grammar.removeIf(rule -> rule.length == 2 && rule[1].isEmpty());
        //System.out.println("Removed all epsilon rules.");
    }

    // Removes duplicate rules from the grammar to ensure it's in a canonical form
    private static void deduplicateRules(ArrayList<String[]> grammar) {
        //System.out.println("Starting deduplication process...");
        Set<String> uniqueRules = new HashSet<>();
        ArrayList<String[]> deduplicatedGrammar = new ArrayList<>();

        // Loop through each rule and check if it's unique
        for (String[] rule : grammar) {
            // Create a string representation of the rule for comparison
            StringBuilder ruleString = new StringBuilder(rule[0].trim());
            for (int i = 1; i < rule.length; i++) {
                ruleString.append(" ").append(rule[i].trim());
            }
            String ruleKey = ruleString.toString().trim();

            // Check if this rule is already present
            //System.out.println("Processing rule: '" + ruleKey + "', Length: " + ruleKey.length());
            if (!uniqueRules.contains(ruleKey)) {
                uniqueRules.add(ruleKey);
                deduplicatedGrammar.add(rule);
                //System.out.println("Added unique rule: '" + ruleKey + "'");
            } else {
                //System.out.println("Duplicate rule found and ignored: '" + ruleKey + "'");
            }
        }

        // Update grammar with deduplicated rules
        grammar.clear();
        grammar.addAll(deduplicatedGrammar);
        //System.out.println("Deduplication process completed.");
    }
}
