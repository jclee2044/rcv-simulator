//completed
package controller;

import model.RCVSimulator;
import model.RCVBallotReader;
import view.RCVView;

public class RCVController {

    // Provided instance variables
    private final RCVBallotReader reader;
    private final RCVView view;
    private final RCVSimulator sim;

    // Provided constructor
    public RCVController() {
        reader = new RCVBallotReader();
        sim = new RCVSimulator(reader);
        view = new RCVView(this);
    }

    public String showResults(String fileNameFromUser) {
        // True only when the fileNameFromUser equals the file name in the RCVBallotReader class.
        if (fileNameFromUser.equals(reader.getDataFileName())) {
            // True only when a winner is found
            if (sim.isWinnerFound()) {
                view.showMessage("Winner " + sim.getWinner()
                        + " already found");
                System.exit(0); // program quits
                return "";
            } else {
                return sim.doOneRound();
            }
        } else {
            view.showMessage("Wrong file name; reread prompt and try again");
            return "";
        }
    }

    // As provided
    public void showView() {
        view.displaySelf();
    }
}