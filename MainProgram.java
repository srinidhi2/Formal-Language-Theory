import java.io.*;
import java.util.*;

public class MainProgram {

    public static void main(String[] args) {
        // Check if the program has been provided with the correct number of arguments (input and output files)
        if (args.length < 2) {
            System.out.println("Please provide two file names as arguments: inputFile and outputFile.");
            return;
        }

        String inputFile = args[0];  // Input file containing the grammar
        String outputFile = args[1];  // Output file to write the grammar in BNF format
        ArrayList<String[]> grammarMatrix = readGrammar(inputFile);  // Read the grammar from the input file

        // Print the initial grammar matrix to the console
        System.out.println("Initial Grammar Matrix:");
        printMatrix(grammarMatrix);

        // Calling function to remove unproductive grammar and storing the result
        ArrayList<String[]> productiveGrammar = RemoveUnproductive.removeunproductive(grammarMatrix);
        System.out.println("Resulting Grammar after Removing Unproductive Symbols:");
        printMatrix(productiveGrammar);

        // Calling function to remove unreachable symbols and storing the result
        ArrayList<String[]> reachableGrammar = RemoveUnreachable.removeunreachable(productiveGrammar);
        System.out.println("Resulting Grammar after Removing Unreachable Symbols: ");
        printMatrix(reachableGrammar);

        // Calling function to remove epsilon (empty) productions and storing the result
        ArrayList<String[]> EpsFreeGrammar = RemoveEpsProductions.removeEps(reachableGrammar);
        System.out.println("Resulting Grammar after Removing Epsilon Productions: ");
        printMatrix(EpsFreeGrammar);

        // Calling function to remove unit productions and storing the result
        ArrayList<String[]> UnitFreeGrammar = RemoveUnitProductions.removeUnits(EpsFreeGrammar);
        System.out.println("Resulting Grammar after Removing Unit Productions: ");
        printMatrix(UnitFreeGrammar);

        // Create an instance of RemoveLeftRecursive
        /*RemoveLeftRecursive removeLR = new RemoveLeftRecursive();
        // Call the methods
        ArrayList<String[]> updatedGrammar = removeLR.removeImmediateLeftRecursion(UnitFreeGrammar);
        updatedGrammar = removeLR.eliminateLeftRecursion(updatedGrammar);
        System.out.println("Resulting Grammar after Removing Immediate Left: ");
        // Print the final grammar matrix
        printMatrix(updatedGrammar);

        GreibachNormalForm greibach = new GreibachNormalForm();
        // Call the method to convert the grammar to Greibach Normal Form
        ArrayList<String[]> convertedGrammar = greibach.convertToGNF(updatedGrammar);
        // Optionally print the final result (already printed inside GreibachNormalForm)
        System.out.println("Resulting Grammar after converting to Greibach Grammar: ");
        printMatrix(convertedGrammar);*/
        // Write the resulting grammar in BNF format to the output file
        writeGrammarToFile(UnitFreeGrammar, outputFile);
    }

    // Reads the grammar from the input file and returns it as a matrix
    private static ArrayList<String[]> readGrammar(String inputFile) {
        ArrayList<String[]> grammarMatrix = new ArrayList<>();  // Initialize the grammar matrix
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {  // Open the input file for reading
            String line;
            while ((line = br.readLine()) != null) {  // Read the file line by line
                // Split the line by "::=" to separate the left-hand side and right-hand side of the production
                String[] parts = line.split("::=");
                if (parts.length != 2) {  // Check for invalid production format
                    System.out.println("Invalid production format: " + line);
                    continue;
                }

                // Remove angle brackets from the left-hand side non-terminal and trim spaces
                String lhs = parts[0].trim().replaceAll("[<>]", "");
                
                // Split the right-hand side by spaces and handle empty productions (epsilon)
                String rhs = parts[1].trim();
                String[] rhsParts = rhs.isEmpty() ? new String[] {""} : rhs.split("\\s+");

                // Create a row for the matrix starting with lhs and followed by rhs parts
                String[] row = new String[rhsParts.length + 1];
                row[0] = lhs;
                for (int i = 0; i < rhsParts.length; i++) {
                    row[i + 1] = rhsParts[i].replaceAll("[<>]", "");  // Remove angle brackets from rhs
                }
                
                // Add the row to the grammar matrix
                grammarMatrix.add(row);
            }
        } catch (IOException e) {  // Handle any input/output exceptions
            System.out.println("Error reading file: " + e.getMessage());
        }
        return grammarMatrix;  // Return the populated grammar matrix
    }

    // Method to print the matrix to the console
    private static void printMatrix(ArrayList<String[]> matrix) {
        for (String[] row : matrix) {  // Iterate through each row of the matrix
            for (String cell : row) {  // Iterate through each cell of the row
                System.out.print((cell != null ? cell : "") + " ");  // Print each cell, checking for null values
            }
            System.out.println();  // Move to the next line after printing a row
        }
    }

    // Writes the grammar matrix to a file in BNF format
    private static void writeGrammarToFile(ArrayList<String[]> grammarMatrix, String outputFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (String[] row : grammarMatrix) {
                String lhs = row[0];  // Left-hand side of the production
                StringBuilder rhs = new StringBuilder();  // Right-hand side of the production

                // Iterate through the rest of the row (the right-hand side parts)
                for (int i = 1; i < row.length; i++) {
                    if (i > 1) {
                        rhs.append(" | ");  // Add pipe symbol for alternative productions
                    }
                    rhs.append(row[i]);  // Append the production part to the right-hand side
                }

                // Write the production in BNF format: lhs ::= rhs1 | rhs2 | ...
                writer.write(lhs + " ::= " + rhs.toString());
                writer.newLine();  // Write a new line after each production
            }
            System.out.println("Grammar written to file: " + outputFile);  // Inform the user that the grammar was written
        } catch (IOException e) {  // Handle any input/output exceptions while writing to the file
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}
