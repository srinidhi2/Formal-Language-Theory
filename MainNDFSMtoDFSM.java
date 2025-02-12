import java.util.Scanner;
import java.io.IOException;

public class MainNDFSMtoDFSM {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Step 1: Get the pattern to build the NDFSM
        String pattern = args[0];
        
        // File paths for intermediate and final output
        String ndfsmOutputFile = "ndfsmOutputFile.txt";
        String dfsmOutputFile = "dfsmOutputFile.txt";
        String inputString = args[1];  // You can provide or ask for an input string to test
        
        // Step 2: Build the NDFSM file using NDFSMBuilderClass
        NDFSMBuilderClass.NDFSMBuilder(ndfsmOutputFile, pattern);
        
        // Step 3: Convert the NDFSM to DFSM using NDFSMtoDFSM
        try {
            NDFSMtoDFSM.convert(ndfsmOutputFile, dfsmOutputFile);
        } catch (Exception e) {
            System.out.println("Error converting NDFSM to DFSM: " + e.getMessage());
            return;
        }
        
        // Step 4: Use DFSMSimulateInterpreter to simulate the DFSM
        try {
            // Load the DFSM specification file and input string file
            DFSMSimulateInterpreter dfsmSimulator = DFSMSimulateInterpreter.DFSMparse(dfsmOutputFile);
            
            // Simulate and check if the input string is accepted by the DFSM
            if (dfsmSimulator.dfsmsimulate(inputString)) {
                System.out.println("Yes, String Accepted");
            } else {
                System.out.println("No, String not accepted");
            }
        } catch (Exception e) {
            System.out.println("Error during DFSM simulation: " + e.getMessage());
        }
    }
}
