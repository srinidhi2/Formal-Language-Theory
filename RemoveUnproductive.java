import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class RemoveUnproductive {

    // This function removes unproductive symbols from the grammar.
    public static ArrayList<String[]> removeunproductive(ArrayList<String[]> grammarMatrix) {
        // Initialize G' as a copy of G (grammarMatrix)
        ArrayList<String[]> productiveGrammar = new ArrayList<>(grammarMatrix);

        // Initialize non-terminals as unproductive
        // Extract all non-terminal symbols from the grammar
        HashSet<String> productiveSymbols = new HashSet<>();
        HashSet<String> nonTerminals = extractNonTerminals(grammarMatrix);
        //System.out.println("Initial Non-Terminals (Unproductive): " + nonTerminals);

        //  Mark all terminals as productive
        // Extract all terminals and treat them as productive
        HashSet<String> terminals = extractTerminals(grammarMatrix);
        productiveSymbols.addAll(terminals);
        //System.out.println("Initial Terminals (Productive): " + terminals);

        // Iteratively mark non-terminals as productive if they derive only productive symbols
        // Keep updating the set of productive symbols until no new symbols are added
        boolean markedNewSymbol;
        do {
            markedNewSymbol = false;
            for (String[] rule : grammarMatrix) {
                String lhs = rule[0];  // The left-hand side is a non-terminal symbol
                // Check if the rule's non-terminal can be derived from productive symbols
                if (!productiveSymbols.contains(lhs) && allSymbolsProductive(rule, productiveSymbols)) {
                    productiveSymbols.add(lhs);  // Mark this non-terminal as productive
                    markedNewSymbol = true;
                    //System.out.println("Marked as Productive: " + lhs);
                }
            }
        } while (markedNewSymbol);  // Continue until no new productive symbols are found

        // Remove unproductive symbols from the non-terminal list
        // Identify which non-terminals are unproductive and need to be removed
        HashSet<String> unproductiveSymbols = new HashSet<>(nonTerminals);
        unproductiveSymbols.removeAll(productiveSymbols);
        //System.out.println("Unproductive Symbols to Remove: " + unproductiveSymbols);

        // Remove all rules containing unproductive symbols
        // Create a new grammar by removing the rules that involve unproductive symbols
        ArrayList<String[]> finalGrammar = new ArrayList<>();
        for (String[] rule : productiveGrammar) {
            // Only keep rules that don't contain unproductive non-terminals and their RHS is productive
            if (!unproductiveSymbols.contains(rule[0]) && allSymbolsInRuleProductive(rule, productiveSymbols)) {
                finalGrammar.add(rule);
            }
        }

        //  Return the updated grammar, now free of unproductive symbols
        return finalGrammar;
    }

    // This function extracts the set of non-terminal symbols from the grammar.
    // It considers only the left-hand side (LHS) of each production rule as non-terminals.
    private static HashSet<String> extractNonTerminals(ArrayList<String[]> grammarMatrix) {
        HashSet<String> nonTerminals = new HashSet<>();
        for (String[] rule : grammarMatrix) {
            nonTerminals.add(rule[0]);  // The LHS of a rule is always a non-terminal
        }
        return nonTerminals;
    }

    // This function extracts the set of terminal symbols from the grammar.
    // It assumes that terminals are lowercase letters or other characters that are not non-terminals.
    private static HashSet<String> extractTerminals(ArrayList<String[]> grammarMatrix) {
        HashSet<String> terminals = new HashSet<>();
        for (String[] rule : grammarMatrix) {
            // Loop through the RHS of each rule to identify terminals
            for (int i = 1; i < rule.length; i++) {
                String symbol = rule[i];
                // Terminals are typically lowercase letters or symbols
                if (!symbol.isEmpty() && Character.isLowerCase(symbol.charAt(0))) {
                    terminals.add(symbol);
                }
            }
        }
        return terminals;
    }

    // This function checks if all symbols in the RHS of a rule are productive.
    // A rule is productive if every symbol in its RHS is either a terminal or a productive non-terminal.
    private static boolean allSymbolsProductive(String[] rule, HashSet<String> productiveSymbols) {
        for (int i = 1; i < rule.length; i++) {
            String symbol = rule[i];
            // If any symbol is not productive, return false
            if (!symbol.isEmpty() && !productiveSymbols.contains(symbol)) {
                return false;
            }
        }
        return true;  // All symbols in the RHS are productive
    }

    // This function checks if all symbols (both LHS and RHS) of a rule are productive.
    // It ensures the entire rule is valid before including it in the final grammar.
    private static boolean allSymbolsInRuleProductive(String[] rule, HashSet<String> productiveSymbols) {
        for (int i = 0; i < rule.length; i++) {
            String symbol = rule[i];
            // If any symbol is not productive, return false
            if (!symbol.isEmpty() && !productiveSymbols.contains(symbol)) {
                return false;
            }
        }
        return true;  // All symbols in the rule (LHS and RHS) are productive
    }
}
