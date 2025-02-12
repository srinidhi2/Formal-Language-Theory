import java.io.IOException;
import java.io.FileWriter;
import java.util.*;

public class NDFSMBuilderClass
{
    // method that builds NDFSM given a string pattern and prints to file
    public static void NDFSMBuilder(String outputFileName, String pattern)
    {
        // check if the given string pattern is empty
        if(pattern == null || pattern.isEmpty())
        {
            System.out.println("Error!! Pattern is empty");
            return;
        }
        // check if the pattern contains only valid characters(pattern characters should be one of alphabets)
        if(!pattern.matches("[a-zA-Z]+"))
        {
            System.out.println("Error!! Pattern contains invalid characters");
            return;
        }
        // extract unique characters from the pattern for the alphabet section
        Set<Character> uniqueCharsinPattern = new HashSet<>();
        for(char c : pattern.toCharArray())
        {
           uniqueCharsinPattern.add(c);
        }
        try(FileWriter writer = new FileWriter(outputFileName))
        {
            // write the alphabet (characters in the pattern)
            for( char c: uniqueCharsinPattern)
            {
              writer.write(c + " ");
            }
            writer.write("\n\n");

            // create the NDFSM transition table section
            for(int state = 1; state <= pattern.length()+1; state++)
            {
                // for each state, generate transitions for each character in the alphabet
                for(char currentChar:uniqueCharsinPattern)
                {
                    List<Integer> transitions = new ArrayList<>();
                    // determine the transitions for each state and character
                    if(state == 1)
                    {
                        if(currentChar == pattern.charAt(0))
                        {
                         transitions.add(state+1);
                        }
                        transitions.add(state);
                    }
                    if(state>1 && state<=pattern.length())
                    {
                      if(currentChar == pattern.charAt(state-1))
                      {
                        transitions.add(state+1); // move to the next state on match
                      }
                    }
                    if(state ==pattern.length()+1)
                    {
                      transitions.add(state);
                    }

                    String transitionString = transitions.toString().replaceAll(" ", "");
                    writer.write(transitionString + " ");
                }
                writer.write("\n");
            }

            // write the accepting state (final state)
            writer.write("\n[" + (pattern.length() + 1) + "]\n");

        }
        catch (IOException e)
        {
            System.out.println("Error writing NDFSM specification: " + e.getMessage());
        }
    }
    public static void main(String[] args)
    {
      Scanner sc = new Scanner(System.in);
      System.out.println("Enter a pattern : ");
      String patternString = sc.nextLine();
      NDFSMBuilder("ndfsmOutputFile.txt",patternString);
    }
}
