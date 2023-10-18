import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class represents the graphical user interface for the manager system.
 */
public class ManagerGUI {
    private JFrame mainFrame;
    private JPanel menuPanel;
    private JLabel welcomeLabel;
    private JPanel currentFrame;

    /**
     * Constructor for ManagerGUI.
     *
     * @param userName The username of the manager.
     */
    public ManagerGUI(String userName) {
        mainFrame = new JFrame("Sweet Paris Manager System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 600);
        mainFrame.setLayout(new BorderLayout());

        welcomeLabel = new JLabel("Welcome, " + userName);
        mainFrame.add(welcomeLabel, BorderLayout.NORTH);

        menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 1));

        // Add buttons to the menu panel
        addButton("Inventory", new InventoryPanel()); 
        addButton("Sales Report", new SalesPanel());
        addButton("Excess Report", new ExcessRep());
        addButton("Product Usage Chart", new ReportsPanel());
        addButton("Restock Report", new RestockRep());
        addButton("Update Items", new NewSeasonal());
        addButton("Ordering Trend Report", new SettingsPanel());
        addButton("Popularity Analysis", new Popularity());

        mainFrame.add(menuPanel, BorderLayout.WEST);

        // Initialize the current frame with a default panel
        currentFrame = new JPanel();
        mainFrame.add(currentFrame, BorderLayout.CENTER);

        mainFrame.setVisible(true);
    }

    /**
     * Helper method to add a button to the menu panel.
     *
     * @param buttonLabel The label for the button.
     * @param targetPanel The panel to switch to when the button is clicked.
     */
    private void addButton(String buttonLabel, final JPanel targetPanel) {
        JButton button = new JButton(buttonLabel);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Switch the display to the target panel when the button is clicked
                currentFrame.removeAll();
                currentFrame.add(targetPanel);
                currentFrame.revalidate();
                currentFrame.repaint();
            }
        });

        menuPanel.add(button);
    }
}

/**
 * Panel class representing the Sales panel.
 */
class SalesPanel extends JPanel {
    public SalesPanel() {
        // Add components specific to the Sales panel here
    }
}

/**
 * Panel class representing the Reports panel.
 */
class ReportsPanel extends JPanel {
    public ReportsPanel() {
        // Add components specific to the Reports panel here
    }
}

/**
 * Panel class representing the Settings panel.
 */
class SettingsPanel extends JPanel {
    public SettingsPanel() {
        // Add components specific to the Settings panel here
    }
}
