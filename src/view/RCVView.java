package view;

import controller.RCVController;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class RCVView extends JFrame {

    // NOTE: these constants are set so we don't need
    // to fiddle with GUI controls
    private static final int FRAME_WIDTH = 870;
    private static final int FRAME_HEIGHT = 650;
    private static final int AREA_ROWS = 30;
    private static final int AREA_COLUMNS = 85;
    private static final int FIELD_WIDTH = 15;
    private static final String BUTTON_NAME = "Do a Round of Voting";
    private static final String USER_PROMPT = "Enter 'ballots.txt' (no quotes) as name of data file: ";

    private final JTextArea resultArea;
    private final RCVController ctrl;

    private JLabel promptJLabel;
    private JTextField fileNameField;
    private JButton button;

    public RCVView(RCVController rankedChoiceController) {
        ctrl = rankedChoiceController;
        resultArea = new JTextArea(AREA_ROWS, AREA_COLUMNS);
        resultArea.setEditable(false);
        resultArea.setText("");

        createTextField();
        createButton();
        createPanel();

        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null); // centers frame
        setDefaultCloseOperation(EXIT_ON_CLOSE); // quits when frame closed
        getRootPane().setDefaultButton(button); // allows hitting Enter to 'click' button
        setTitle("Ranked Choice Voting Simulator");
    }

    private void createTextField() {
        promptJLabel = new JLabel(USER_PROMPT);
        promptJLabel.setFont(new Font("Serif", Font.BOLD, 24));
        fileNameField = new JTextField(FIELD_WIDTH);
    }

    private void createButton() {
        button = new JButton(BUTTON_NAME);
        button.setFont(new Font("Verdana", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(300, 80));
        // for the style of lambda expression below, see this video
        // at Chapter 6, Section 5, from minute 3:25 to minute 3:56.
        // https://catalog.libraries.psu.edu/catalog/37440551
        button.addActionListener((event) ->
                showResults(fileNameField.getText()));
    }

    private void createPanel() {
        JPanel panel = new JPanel();
        panel.add(promptJLabel);
        panel.add(fileNameField);

        JScrollPane scrollPane = new JScrollPane(resultArea);

        panel.add(scrollPane);
        panel.add(button);
        add(panel);
    }

    private void showResults(String fileNameFromUser) {
        Font font = new Font("Verdana", Font.BOLD, 24);
        resultArea.setFont(font);
        resultArea.append(ctrl.showResults(fileNameFromUser) + "\n\n");
    }

    public void showMessage(String message) {
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.BOLD, 20));
        javax.swing.JOptionPane.showMessageDialog(new javax.swing.JFrame(),
                message);
        fileNameField.requestFocus();
        resultArea.setText("");
    }

    public void displaySelf() {
        setVisible(true);
    }

}