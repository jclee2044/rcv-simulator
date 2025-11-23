//completed
package model;

// Provided imports
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
// Additional imports
import java.util.TreeMap;
import java.io.BufferedReader;
import java.io.FileReader;

public class RCVBallotReader {

     // Provided instance variables
    private static final String DATA_FILE_NAME = "ballots.txt";

    // Provided constructor
    public RCVBallotReader() {
    }

    // Provided getter method
    public String getDataFileName() {
        return DATA_FILE_NAME;
    }

    public List<Map<Character, List<Character>>> makeBallotsFromFile() {
        // Read the file using try with resources
        try (BufferedReader br = new BufferedReader(new FileReader(getDataFileName()))) {
            // Read lines from ballots.txt into an ArrayList of type String
            ArrayList<String> ballots = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                ballots.add(line.trim());
            }
            // Call makeListOfBallots method and return the result
            return makeListOfBallots(ballots);
        } catch (java.io.FileNotFoundException fnfEx) {  // Most specific exception type
            System.err.println("File not found: " + getDataFileName());
            System.err.println(fnfEx.getMessage());
        } catch (java.io.IOException ioEx) {  // Generic IO exception
            System.err.println("I/O error while reading ballots file: " + ioEx.getMessage());
        } catch (Exception ex) {  // Most general exception catch
            System.err.println("Unexpected error: " + ex.getMessage());
        }
        return null;
    }

    private List<Map<Character, List<Character>>> makeListOfBallots(ArrayList<String> ballotsFromFile){
        List<Map<Character, List<Character>>> ballotList = new ArrayList<>();
        Map<Character, List<Character>> ballot = new TreeMap<>();

        for (String ballotLine : ballotsFromFile) {
            if (ballotLine.equals("END")) {
                // Finish current ballot and add to list
                ballotList.add(ballot);
                ballot = new TreeMap<>();
                continue;
            }

            // Get the key and values
            String[] parts = ballotLine.split("\\|");
            char key = parts[0].trim().charAt(0);
            String[] tokens = parts[1].trim().split(" ");
            List<Character> values = new ArrayList<>();
            for (String token : tokens) {
                if (!token.isEmpty()) {
                    values.add(token.charAt(0));
                }
            }
            // Add key, values to the ballot
            ballot.put(key, values);
        }
        return ballotList;
    }
}