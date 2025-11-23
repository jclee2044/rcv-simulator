package model;

// Provided imports
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
// Additional imports
import java.util.concurrent.ThreadLocalRandom;
import java.util.Iterator;

public class RCVSimulator {

    // Provided instance variables
    private static final double WINNING_THRESHOLD = 0.5; // 50%, i.e., winner needs the majority of votes
    private static final Character VOTE = '+';
    private final List<Map<Character, List<Character>>> listOfBallots;
    private final Set<Character> continuingCandidates;
    private final Map<Character, Integer> voteMap;
    private final RCVBallotReader reader;
    private int currentRound;
    private Character winner;
    // Additional variable declarations
    private boolean winnerDecidedByLot;


    // Provided constructor
    public RCVSimulator(RCVBallotReader reader) {
        this.reader = reader;
        listOfBallots = new ArrayList<>();
        continuingCandidates = new TreeSet<>();
        voteMap = new TreeMap<>();
        currentRound = 0;
        winner = ' ';
        // Additional lines corresponding to additional variable declarations
        winnerDecidedByLot = false;
    }


    public boolean isWinnerFound() {
        return !winner.equals(' ');
    }

     public String doOneRound() {
        if (currentRound == 0) {
            // Populate the listOfBallots using the reader
            listOfBallots.addAll(reader.makeBallotsFromFile());
            // Populate the continuingCandidates
            continuingCandidates.addAll(listOfBallots.getFirst().keySet());
            // Create the initial voteMap
            createInitialVoteMap();
        } else {
            if (!isWinnerFound()) {
                rebuildVoteMap();
            }
        }

        // check for winner
        evaluateWinner();

        currentRound++;
        System.out.println("--------------------------------------------------------------------------------");

        return showResultsOfOneRound();
    }

    public Character getWinner() {
        return winner;
    }

    private void evaluateWinner() {
        winnerDecidedByLot = false;

        int needed = findVotesNeededToWin();

        // Check for majority winner
        for (Map.Entry<Character, Integer> entry : voteMap.entrySet()) {
            int count = entry.getValue();
            if (count >= needed) {
                winner = entry.getKey();
                return;
            }
        }

        // Resolve the election between last two candidates
        if (voteMap.size() == 2) {
            Iterator<Map.Entry<Character, Integer>> it = voteMap.entrySet().iterator();

            Map.Entry<Character, Integer> firstEntry = it.next();
            Map.Entry<Character, Integer> secondEntry = it.next();

            if (firstEntry.getValue() > secondEntry.getValue()) {
                winner = firstEntry.getKey();
            }
            else if (secondEntry.getValue() > firstEntry.getValue()) {
                winner = secondEntry.getKey();
            }
            else {
                // Candidates are tied; decide by lot
                Set<Character> tied = voteMap.keySet();
                winner = selectRandomCandidate(tied);
                winnerDecidedByLot = true;
            }
        }
    }

    private void createInitialVoteMap() {
        // Set up the empty voteMap
        for (char c : continuingCandidates) {
            voteMap.put(c, 0);
        }

        // Count votes and update the voteMap
        for (Map<Character, List<Character>> ballot : listOfBallots) {
            for (Map.Entry<Character, List<Character>> ballotEntry : ballot.entrySet()) {
                if (ballotEntry.getValue().getFirst().equals(VOTE)) {
                    voteMap.put(ballotEntry.getKey(), voteMap.get(ballotEntry.getKey()) + 1);
                }
            }
        }
    }

    private void rebuildVoteMap() {
        // Determine character to drop
        Character eliminated = findCandidateToDrop();
        // Now reset the voteMap
        reallocateVotes(eliminated);
    }

    // Reallocate all ballots after eliminating one candidate.
    private void reallocateVotes(Character eliminatedCandidate) {
        // Remove the eliminated candidate from play
        continuingCandidates.remove(eliminatedCandidate);

        // Reset counts and drop eliminated keys from voteMap to keep it aligned
        for (Character candidate : new ArrayList<>(voteMap.keySet())) {
            if (continuingCandidates.contains(candidate)) {
                voteMap.put(candidate, 0);
            } else {
                voteMap.remove(candidate);
            }
        }

        // Remove exhausted ballots before continuing
        removeExhaustedBallots();

        // Reassign vote value to highest continuing candidate
        for (Map<Character, List<Character>> ballot : listOfBallots) {
            Character target = findHighestContinuingCandidate(ballot);
            voteMap.put(target, voteMap.get(target) + 1);
        }
    }

    private Character findHighestContinuingCandidate(Map<Character, List<Character>> ballot) {
        int bestRank = Integer.MAX_VALUE;
        Character bestCandidate = null;
        int countAtBestRank = 0;

        for (Map.Entry<Character, List<Character>> entry : ballot.entrySet()) {
            Character candidate = entry.getKey();
            if (!continuingCandidates.contains(candidate)){
                continue;
            }

            int rank = entry.getValue().indexOf(VOTE); // -1 if not ranked
            if (rank < 0){
                continue;
            }

            if (rank < bestRank) {
                bestRank = rank;
                bestCandidate = candidate;
                countAtBestRank = 1;
            } else if (rank == bestRank) {
                // overvote at the current highest continuing rank
                countAtBestRank++;
            }
        }

        if (bestCandidate == null) {
            // no continuing candidate ranked
            return null;
        }
        if (countAtBestRank > 1) {
            // overvote at the highest continuing rank => exhausted
            return null;
        }
        if (hasTwoSkips(ballot)) {
            return null;
        }
        return bestCandidate;
    }

    private boolean hasOvervoteAtHighestContinuingRank(Map<Character, List<Character>> ballot) {
        int bestRank = Integer.MAX_VALUE;
        int countAtBestRank = 0;

        for (Map.Entry<Character, List<Character>> entry : ballot.entrySet()) {
            Character candidate = entry.getKey();
            if (!continuingCandidates.contains(candidate)){
                continue;
            }

            int rank = entry.getValue().indexOf(VOTE);
            if (rank < 0){
                continue;
            }

            if (rank < bestRank) {
                bestRank = rank;
                countAtBestRank = 1;
            } else if (rank == bestRank) {
                countAtBestRank++;
            }
        }
        return (bestRank != Integer.MAX_VALUE) && (countAtBestRank > 1);
    }

    private boolean hasTwoSkips(Map<Character, List<Character>> ballot) {
        // Find the highest continuing rank
        int highestContinuingRank = Integer.MAX_VALUE;
        for (Map.Entry<Character, List<Character>> entry : ballot.entrySet()) {
            Character candidate = entry.getKey();
            if (!continuingCandidates.contains(candidate)){
                continue;
            }
            int rank = entry.getValue().indexOf(VOTE);
            if (rank >= 0 && rank < highestContinuingRank) {
                highestContinuingRank = rank;
            }
        }

        if (highestContinuingRank == Integer.MAX_VALUE) {
            return false; // no continuing candidate ranked
        }

        // Check for 2+ sequential skipped rankings before the highest continuing rank
        int consecutiveSkips = 0;
        for (int i = 0; i < highestContinuingRank; i++) {
            boolean rankOccupiedByContinuingCandidate = false;
            for (Map.Entry<Character, List<Character>> entry : ballot.entrySet()) {
                Character candidate = entry.getKey();
                List<Character> rankings = entry.getValue();
                if (i < rankings.size() && rankings.get(i).equals(VOTE)
                        && continuingCandidates.contains(candidate)) {
                    rankOccupiedByContinuingCandidate = true;
                    break;
                }
            }
            if (!rankOccupiedByContinuingCandidate) {
                consecutiveSkips++;
                if (consecutiveSkips >= 2) {
                    return true;
                }
            } else {
                consecutiveSkips = 0; // reset counter if rank is occupied
            }
        }
        return false;
    }


    private Character findCandidateToDrop() {
        // Find the minimum value
        int minVote = voteMap.get(continuingCandidates.iterator().next());
        Character eliminated = continuingCandidates.iterator().next(); // use a stub value for now
        for (char candidate : continuingCandidates) {
            int count = voteMap.get(candidate);
            if (count < minVote) {
                minVote = count;
                eliminated = candidate;
            }
        }

        // Check how many candidates have that number of votes
        Set<Character> candidatesWithMinVote = new TreeSet<>();
        for (char candidate : continuingCandidates) {
            if (voteMap.get(candidate) == minVote) {
                candidatesWithMinVote.add(candidate);
            }
        }

        if (candidatesWithMinVote.size() != 1) {
            // reassign eliminated variable to randomly selected candidate
            eliminated = selectRandomCandidate(candidatesWithMinVote);
        }
        return eliminated;
    }

    private char selectRandomCandidate(Set<Character> tiedCandidates) {
        System.out.println("deciding by lot"); // required print statement
        // generate a random index
        int index = ThreadLocalRandom.current().nextInt(tiedCandidates.size());
        // use an Iterator of Character to advance to that index
        Iterator<Character> it = tiedCandidates.iterator();
        for (int i = 0; i < index; i++) {
            it.next();
        }
        // return the associated value (i.e., the candidate)
        return it.next();
    }

    private void removeExhaustedBallots() {
        Iterator<Map<Character, List<Character>>> it = listOfBallots.iterator();
        while (it.hasNext()) {
            Map<Character, List<Character>> ballot = it.next();
            Character target = findHighestContinuingCandidate(ballot);
            if (target == null) {
                System.out.println("continuing candidates: " + continuingCandidates);
                System.out.println("ballot " + ballot + " removed");
                if (hasOvervoteAtHighestContinuingRank(ballot)) {
                    System.out.println("   because it contains an overvote at the highest continuing ranking");
                } else if (hasTwoSkips(ballot)) {
                    System.out.println("   because it contains 2 or more sequential skipped rankings before its highest continuing ranking");
                } else {
                    System.out.println("   because it ranks no continuing candidate");
                }
                it.remove();
            }
        }
    }

    private String showResultsOfOneRound() {
        String firstLineDisplay;
        if (currentRound == 1) { // (1 because we need to execute currentRound++ during doOneRound)
            firstLineDisplay = "Round 0 votes at start: ";
        } else {
            firstLineDisplay = "Round " + (currentRound - 1) + " votes at end: ";
        }
        firstLineDisplay += voteMap + "\n";

        String secondLineDisplay;
        if (winnerDecidedByLot){
            secondLineDisplay = "winner decided by lot\n";
        } else {
            secondLineDisplay = "votes needed to win: " + findVotesNeededToWin() + "\n";
        }

        String thirdLineDisplay;
        if (winner == ' ') {
            thirdLineDisplay = "winning candidate: none";
        } else {
            thirdLineDisplay = "winning candidate: " + winner;
        }

        return firstLineDisplay + secondLineDisplay + thirdLineDisplay;
    }

    private int findVotesNeededToWin() {
        int sum = 0;
        // Iterate through the numbers of votes; add to sum
        for (int vote : voteMap.values()) {
            sum += vote;
        }
        return (int) (sum * WINNING_THRESHOLD) + 1; // one greater than half the total votes
    }
}