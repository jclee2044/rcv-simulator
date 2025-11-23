# RCV-Simulator  
*A Swing GUI Ranked-Choice Voting simulator using the MVC architectural pattern*

## Overview
RCV-Simulator is a Java application that implements a fully functional Ranked-Choice Voting (RCV) tabulation engine with a Swing-based graphical user interface. This project was developed for App Development Studio in the Human-Centered Design & Development program at Penn State University, built across six iterative deliverables that expanded functionality, data structures, and compliance with strict grading criteria and real-world requirements.

The project showcases my ability to design and implement maintainable systems from the ground up, apply formal specifications, and integrate backend logic with a user-centered interface—skills that support my long-term goal of developing AI-powered automation tools for businesses.

## Ranked-Choice Voting Summary
Ranked-skip Voting allows voters to rank candidates in order of preference. Votes are counted in rounds:

- If a candidate receives **more than 50%** of first-choice votes, they win.  
- Otherwise, the **last-place candidate is eliminated**, and ballots transfer to the next highest-ranked *continuing* candidate.  
- Ballots with **overvotes**, **skipped rankings**, or **no remaining continuing candidates** become **exhausted**.  
- The process continues until a candidate receives a majority or only two candidates remain.

This simulator closely follows the procedures outlined in **Maine’s Ranked-Choice Voting law (Title 21-A §723-A)**, including the handling of exhausted ballots, skipped rankings, overvotes, and tie-breaking by lot.

## Project Structure and Development
This assignment was structured over six deliverables, with each stage increasing complexity—from simple ballot parsing to full tabulation logic, adherence to Maine RCV procedures, and integration into a complete Swing GUI built using the MVC architectural pattern.

The GUI guidelines were provided, but the implementation of all backend logic, rules, validations, and system structure was fully self-directed.

## Technical Overview

### Architecture (MVC)
- **Model** – Core RCV engine, ballot structures, candidate data, ranking and exhaustion rules, round logic.  
- **View** – Swing GUI responsible for ballot loading, simulation control, and visualizing tabulation results.  
- **Controller** – Coordinates system behavior, processes user interactions, and bridges model and view.

### Key Features
- Accurate adherence to Maine RCV rules (Title 21-A §723-A).  
- Detection and handling of:
  - **Overvotes**
  - **Skipped rankings**
  - **Exhausted ballots**
- Proper round-by-round elimination with vote transfers.  
- Tie-breaking **“by lot”**, recorded for recount consistency.  
- User-friendly GUI for loading ballots and running simulations.

## Alignment with Maine RCV Procedures
The implementation is compliant with the definitions and rules found in Maine Title 21-A §723-A, including:

- An **overvote** invalidates that ranking.  
- **Sequential skipped rankings** may exhaust a ballot.  
- A ballot with **no continuing candidates** becomes exhausted.  
- **Ties for last place** must be resolved by lot, with the outcome recorded.  
- Rounds continue until a majority is reached or two candidates remain.

These rules were derived directly from the statute’s definitions of “overvote,” “exhausted ballot,” “continuing candidate,” and related terms.

## Running the Project

### Option 1 — Using an IDE
1. Clone the repository.  
2. Open it in IntelliJ, Eclipse, or another Java IDE.  
3. Run the main class.  
4. Use the GUI to load ballot files and simulate elections.

### Option 2 — Using a JAR
If a JAR is provided:
```bash
java -jar rcv-simulator.jar
```

## What I Learned
This project strengthened my ability to:

- Build complex systems iteratively, planning ahead for scalability and maintainability.  
- Translate formal written specifications—like Maine’s RCV procedures—into precise software behavior.  
- Balance user-facing design with robust backend engineering.  
- Create a clean MVC architecture suitable for continuous improvement and extension.  

These skills continue to support my work on more advanced software projects, including AI-driven systems and automation tools.
