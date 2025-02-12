import java.util.*;

public class RemoveUnreachable {

    public static ArrayList<String[]> removeunreachable(ArrayList<String[]> productiveGrammar) {
        //System.out.println("Starting RemoveUnreachable algorithm...");
        
        ArrayList<String[]> reachableGrammar = new ArrayList<>(productiveGrammar);
        
        // Initialize reachable set with the start symbol (assumed to be the first row's LHS)
        Set<String> reachableSymbols = new HashSet<>();
        String startSymbol = reachableGrammar.get(0)[0]; // Assuming the first rule has the start symbol
        reachableSymbols.add(startSymbol);
        //System.out.println("Marking start symbol '" + startSymbol + "' as reachable.");

        boolean newSymbolMarked;
        
        // Mark reachable symbols
        do {
            newSymbolMarked = false;
            for (String[] rule : reachableGrammar) {
                String lhs = rule[0];
                if (reachableSymbols.contains(lhs)) { // Only check if the LHS is already marked as reachable
                    for (int i = 1; i < rule.length; i++) {
                        String symbol = rule[i];
                        if (isNonTerminal(symbol, productiveGrammar) && !reachableSymbols.contains(symbol)) {
                            reachableSymbols.add(symbol);
                            newSymbolMarked = true;
                            //System.out.println("Marking non-terminal '" + symbol + "' as reachable.");
                        }
                    }
                }
            }
        } while (newSymbolMarked);

        // Remove unreachable symbols
        ArrayList<String[]> filteredGrammar = new ArrayList<>();
        for (String[] rule : reachableGrammar) {
            if (reachableSymbols.contains(rule[0])) {
                filteredGrammar.add(rule);
            } else {
                //System.out.println("Removing unreachable rule with LHS: " + rule[0]);
            }
        }

        // Remove rules containing unreachable symbols on RHS
        for (String[] rule : filteredGrammar) {
            for (int i = 1; i < rule.length; i++) {
                if (!reachableSymbols.contains(rule[i]) && isNonTerminal(rule[i], productiveGrammar)) {
                    //System.out.println("Removing unreachable symbol '" + rule[i] + "' from rule: " + rule[0]);
                    rule[i] = ""; // Replace unreachable symbols with empty string
                }
            }
        }

        return filteredGrammar;
    }

    // Utility method to check if a symbol is a non-terminal (exists in LHS of any rule)
    private static boolean isNonTerminal(String symbol, ArrayList<String[]> grammar) {
        for (String[] rule : grammar) {
            if (rule[0].equals(symbol)) {
                return true;
            }
        }
        return false;
    }
}
