/* program : DFSM Simulate Interpreter */
/* Author : Srinidhi Ganta */
/* Assignment : Programming Assignment 1 */

/* This porgram has 6 functions along with the main function. The main function calls the DFSMparse function.
DFSMparse function reads the DFSM Sepcification file and converts into useful format by collecting alphabet,transisitons, accepting states 
and returns class object by assigning the required variables.
The function uses methods skipEmptyLines to skip the empty lines, alphabetparse to read alphabets into list, transistiontableparse to read table into 2D array and 
acceptstatesparse to read accepting states into set of integers and the start state.
The returned class object calls dfsmsimulate function to vverify if the string is accepted or rejected.
Possible Edge cases handled:
1.Incorrect numebr of arguments given in commandline.
2.DFSMSpecification file empty
3.Input STring File empty
4.Missing Alphabet Section, transition table section
5.Alphabet must be single characaters
6.transition table column size must be equal to number of alphabet.
7.All input characters must be in alphabet.
*/

import java.io.*;
import java.util.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DFSMSimulateInterpreter
{
     private List<Character> alphabet; //using list(because specific order of alphabet is required) to store alphabet characters from the file 
     private int[][] transitionTable;  // using 2D matrix to store transition table from the file.
     private Set<Integer> acceptStates; //using set(for O(1) lookup to check if the state is accepting) to store accept states
     private int startState;
     
     public DFSMSimulateInterpreter(List<Character> alphabet, int[][] transitionTable, Set<Integer> acceptStates, int startState)
     {
         this.alphabet = alphabet;
         this.transitionTable = transitionTable;
         this.acceptStates = acceptStates;
         this.startState = startState;
     }
     public static void main(String[] args)
     {
         if(args.length!=2) //If the two input files given as arguments are missing, exit from the program.
         {
          System.out.println("Missing arguments files");
          return;
         }
         String dfsmSpecificationFile = args[0]; // Storing them into string variables
         String inputStringFile = args[1];
         File dfsmfile = new File(dfsmSpecificationFile);
         try
         {
           if(dfsmfile.length()==0) //checks for empty(no content) dfsm specification file
           {
             System.out.println("DFSM Specification file is empty");
             return;
           }
           // reads the content of the input string file
           String content = new String(Files.readAllBytes(Paths.get(inputStringFile)));
           // to check if the content of input string file is empty(contains any whitespaces)
           if(content.trim().isEmpty())
           {
             System.out.println("Input String File is empty");
             return;
           }
           // calling method DFSMparse to read dfsm specification file
           DFSMSimulateInterpreter dfsm = DFSMSimulateInterpreter.DFSMparse(dfsmSpecificationFile);
           if(dfsm.dfsmsimulate(content))
           {
            System.out.println("Yes,String Accepted");
	   }
	   else
	   {
	    System.out.println("No,String not accepted");
  	   }
         }
         catch(Exception e)
         { 
           System.out.println("Exception occured in reading files: "+e.getMessage());
         }
     }
     private static String skipEmptyLines(BufferedReader reader) throws IOException
     {
       String line;
       line =  reader.readLine();
       // we try to get the line that is not empty.
       while( line != null && line.trim().isEmpty() )
       {
        // continues to loop until we reach end of the file or find a non empty line 
        line = reader.readLine();
        // reads the empty lines repeatedly but doesnt return them.
       }
       return line; // returns line that is not empty. 
     }

     public static DFSMSimulateInterpreter DFSMparse(String file) throws IOException
     {
       BufferedReader reader = new BufferedReader(new FileReader(file));
       //Parsing the alphabet section
       String line = reader.readLine();
       if(line ==null || line.trim().isEmpty()) // checking if string is empty
       {
         throw new IllegalArgumentException("Missing Alphabet section in DFSM file");
       }
       List<Character> alphabet = Alphabetparse(line.trim()); // calling method to store alphabet characters into list
       // skips blank lines between sections and points to nonempty line.
       line = skipEmptyLines(reader);       
       //Parsing Transition Table section
       if(line==null)
       {
        throw new IllegalArgumentException("Missing transition table in DFSM specification file"); 
       }
       int[][] transitionTable = TransitionTableparse(reader, alphabet.size(), line.trim());
       line = skipEmptyLines(reader);
       //Parsing Accepting states section
       Set<Integer> acceptStates = new HashSet<>();
       acceptStates = AcceptStatesparse(line.trim());

       reader.close();
       //creating class object to assign parsed variables, start state is always 1
       DFSMSimulateInterpreter dfsmObj = new DFSMSimulateInterpreter(alphabet,transitionTable,acceptStates,1);
       return dfsmObj;      
     }
       
       private static List<Character> Alphabetparse(String line)
       {
         List<Character> alphabet = new ArrayList<>();
         for(String singleChar : line.trim().split("\\s+")) // splits line into array of strings based on 1or more white space characters
         {
           if(singleChar.length()!=1)
           {
            throw new IllegalArgumentException("alphabet symbols must be single chars");
           }
           alphabet.add(singleChar.charAt(0));
         }
         //System.out.println("Alphabet : "+alphabet);
         return alphabet;
       }
       private static int[][] TransitionTableparse(BufferedReader reader,int alphabetSize, String firstline)throws IOException
       {
         // list has array of integers. Each row of transition table will be an array. All arrays are stored in list
         List<int[]> rows = new ArrayList<>();
         String line = firstline;
         // continues the loop till eof is reached or empty line is seen
         while(line!=null && !line.trim().isEmpty())
         {
          // seperates each line by 1 or more spaces and stores into string as string elements
          String[] singleStrings = line.trim().split("\\s+");
          // number of elements in row must be equal to number of alphabets
          if(singleStrings.length!=alphabetSize)
          {
           throw new IllegalArgumentException("Transition table row doesn't match alphabet size");
          }
          int []eachrow = new int[alphabetSize];
          // all strings are converted to integers to represent state transitions
          for(int i=0;i<alphabetSize;i++)
          {
            eachrow[i]=Integer.parseInt(singleStrings[i]);
          }
          rows.add(eachrow);
          // reads next line from the transition table section
          line = reader.readLine();
         }
         // converts the list into a 2D array dynamically based on the rows size and returns it.
         return rows.toArray(new int[0][]);
       }
       private static Set<Integer> AcceptStatesparse(String line)
       {
         Set<Integer> acceptingStates = new HashSet<>();
         if(!line.isEmpty())// if the line is not empty
         {
           for(String state : line.split("\\s+"))// splits into array of strings of single chars
           {
             // convert string to int and add to set
             acceptingStates.add(Integer.parseInt(state));
           }
         }
         return acceptingStates;
       }
       public boolean dfsmsimulate(String input)
       {
         int currentState = startState;
         //System.out.println("Input to Charact Array : "+input.toCharArray());
         String inputString = input.trim();
         for(char singleChar : inputString.toCharArray())
         {
           //gets columnindex of char stored in alphabet list
           //System.out.println("Single character : "+singleChar);
           int charIndex = alphabet.indexOf(singleChar);
           if(charIndex == -1)
           {
             System.out.println("input character not in alphabet");
             return false;
           }
           // indexing of matrix starts from 0 but startstate starts from 1 by default.
           currentState = transitionTable[currentState-1][charIndex];

         }        
         return acceptStates.contains(currentState); 
      }
}
