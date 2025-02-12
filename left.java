
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;


class CFG {
   
     // Map storing the grammar's non-terminals and their production rules
Map<String, List<List<String>>> productions = new LinkedHashMap<>();
// Starting symbol of the grammar
String startState = null;
// List of non-terminal symbols in the grammar
List<String> nonterminalList = new ArrayList<>();
// List of terminal symbols in the grammar
List<String> terminalList = new ArrayList<>();
// List to maintain the order of non-terminal symbols as they appear in the grammar definition
List<String> orderList = new ArrayList<>();




/**
 * Removes immediate left recursion within the grammar for all non-terminals.
 */

    public void removeImmediateLeftRecursion() {
        Map<String, List<List<String>>> newProductions = new LinkedHashMap<>(productions);

        for (String nonTerminal : new ArrayList<>(productions.keySet())) {
            List<List<String>> rules = productions.get(nonTerminal);
            List<List<String>> alphaRules = new ArrayList<>();
            List<List<String>> betaRules = new ArrayList<>();

            for (List<String> rule : rules) {
                if (!rule.isEmpty() && rule.get(0).equals(nonTerminal)) { // If rule is of form A -> Aα
                    alphaRules.add(new ArrayList<>(rule.subList(1, rule.size())));
                } else { // If rule is of form A -> β
                    betaRules.add(new ArrayList<>(rule));
                }
            }

            if (!alphaRules.isEmpty()) {
                String newNonTerminal = createNewNonTerminal(nonTerminal, newProductions.keySet());
                List<List<String>> newAlphaRules = new ArrayList<>();
                List<List<String>> newBetaRules = new ArrayList<>();

                for (List<String> alphaRule : alphaRules) {
                    ArrayList<String> newAlphaRule = new ArrayList<>(alphaRule);
                    newAlphaRule.add(newNonTerminal); // A' -> αA'
                    newAlphaRules.add(newAlphaRule);
                }
                newAlphaRules.add(Collections.singletonList("eps")); // Add A' -> ε

                for (List<String> betaRule : betaRules) {
                    ArrayList<String> newBetaRule = new ArrayList<>(betaRule);
                    newBetaRule.add(newNonTerminal); // A -> βA'
                    newBetaRules.add(newBetaRule);
                }

                newProductions.put(nonTerminal, newBetaRules); // Replace A rules with new beta rules
                newProductions.put(newNonTerminal, newAlphaRules); // Add new A' rules
            }
        }

        productions = newProductions;
    }

    /**
 * Creates a new unique non-terminal symbol based on an existing one.
 * @param nonTerminal The existing non-terminal to base the new one on.
 * @param existingNonTerminals The set of existing non-terminal symbols to avoid duplication.
 * @return A new unique non-terminal symbol.
 */

    private String createNewNonTerminal(String nonTerminal, Set<String> existingNonTerminals) {
        String baseNewNonTerminal = nonTerminal + "'";
        String newNonTerminal = baseNewNonTerminal;
        int counter = 1;

        while (existingNonTerminals.contains(newNonTerminal)) {
            newNonTerminal = baseNewNonTerminal + counter++;
        }

        return newNonTerminal;
    }
    /**
 * Eliminates left recursion from the grammar. It first substitutes productions for each non-terminal
 * to avoid indirect left recursion, and then removes any immediate left recursion.
 * @param nonTerminalOrder A list defining the order of non-terminals to process for left recursion elimination.
 */
   
    public void eliminateLeftRecursion(List<String> nonTerminalOrder) {
        for (String nonTerminal : nonTerminalOrder) {
            substituteProductions(nonTerminal,nonTerminalOrder);
            removeImmediateLeftRecursion(nonTerminal);
        }
    }

    /**
 * Substitutes productions in a non-terminal with those from lower-order non-terminals to eliminate indirect left recursion.
 * @param nonTerminal The non-terminal to process.
 * @param nonTerminalOrder The ordered list of non-terminals.
 */

    private void substituteProductions(String nonTerminal,List<String> nonTerminalOrder) {
        for (int i = 0; i < nonTerminalOrder.indexOf(nonTerminal); i++) {
            String lowerNonTerminal = nonTerminalOrder.get(i);
            List<List<String>> newProductions = new ArrayList<>();
            List<List<String>> currentProductions = productions.getOrDefault(nonTerminal, new ArrayList<>());

            for (List<String> production : currentProductions) {
                if (!production.isEmpty() && production.get(0).equals(lowerNonTerminal)) {
                    List<List<String>> lowerProductions = productions.getOrDefault(lowerNonTerminal, new ArrayList<>());
                    for (List<String> lowerProduction : lowerProductions) {
                        List<String> newProduction = new ArrayList<>(lowerProduction);
                        newProduction.addAll(production.subList(1, production.size()));
                        newProductions.add(newProduction);
                    }
                } else {
                    newProductions.add(new ArrayList<>(production));
                }
            }
            productions.put(nonTerminal, newProductions);
        }
    }

    /**
 * Removes immediate left recursion for a specific non-terminal.
 * @param nonTerminal The non-terminal to process for immediate left recursion removal.
 */

    private void removeImmediateLeftRecursion(String nonTerminal) {
        List<List<String>> rules = productions.getOrDefault(nonTerminal, new ArrayList<>());
        List<List<String>> alpha = new ArrayList<>();
        List<List<String>> beta = new ArrayList<>();

        for (List<String> rule : rules) {
            if (!rule.isEmpty() && rule.get(0).equals(nonTerminal)) {
                alpha.add(new ArrayList<>(rule.subList(1, rule.size())));
            } else {
                beta.add(new ArrayList<>(rule));
            }
        }

        if (!alpha.isEmpty()) {
            String newNonTerminal = generateNewNonTerminal(nonTerminal);
            List<List<String>> newAlphaRules = new ArrayList<>();
            List<List<String>> newBetaRules = new ArrayList<>();

            for (List<String> a : alpha) {
                List<String> newRule = new ArrayList<>(a);
                newRule.add(newNonTerminal);
                newAlphaRules.add(newRule);
            }
            newAlphaRules.add(Collections.singletonList("eps"));

            for (List<String> b : beta) {
                List<String> newRule = new ArrayList<>(b);
                newRule.add(newNonTerminal);
                newBetaRules.add(newRule);
            }

            productions.put(nonTerminal, newBetaRules);
            productions.put(newNonTerminal, newAlphaRules);
        }
    }
    /**
 * Generates a new non-terminal symbol that doesn't already exist in the grammar.
 * @param base The base non-terminal symbol to start from.
 * @return A new unique non-terminal symbol.
 */

    private String generateNewNonTerminal(String base) {
        String newNonTerminal = base + "'";
        int count = 2;
        while (productions.containsKey(newNonTerminal)) {
            newNonTerminal = base + "'" + count;
            count++;
        }
        return newNonTerminal;
    }


/**
 * Prog4
 */
public class Prog4 {

    public static void main(String[] args) {
        // Check if the correct number of command line arguments is provided
        if (args.length != 2) {
            System.out.println("Usage: java Prog4 <input_filename> <output_filename>");
            return;
        }


      // Process the grammar to remove immediate left recursion
      grammar.removeImmediateLeftRecursion();
      System.out.println("Productions after removing immediate left recursion: " + grammar.productions);

      // Eliminate left recursion for the entire grammar based on non-terminal order
      grammar.eliminateLeftRecursion(grammar.nonterminalList);
      System.out.println("Productions after eliminating left recursion: " + grammar.productions);

        // Write the final modified grammar to the output file
        grammar.writeOutput(outputFileName);

        System.out.println("Simplified Grammar is written into "+outputFileName);
    }
}
