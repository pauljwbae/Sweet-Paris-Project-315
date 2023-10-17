import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManagerGUI {
    private JFrame mainFrame;
    private JPanel menuPanel;
    private JLabel welcomeLabel;
    private JPanel currentFrame;

    public ManagerGUI(String userName) {
        mainFrame = new JFrame("POS Manager System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLayout(new BorderLayout());

        welcomeLabel = new JLabel("Welcome, " + userName);
        mainFrame.add(welcomeLabel, BorderLayout.NORTH);

        menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 1));

        // Add buttons to the menu panel
        addButton("Inventory", new InventoryPanel()); 
        addButton("Sales Report", new SalesPanel());
        addButton("Excess Report", new InventoryPanel());
        addButton("Product Usage Chart", new ReportsPanel());
        addButton("Restock Report", new SettingsPanel());
        addButton("Update Items", new SettingsPanel());
        addButton("Ordering Trend Report", new SettingsPanel());
        addButton("Popularity Analysis", new SettingsPanel());

        mainFrame.add(menuPanel, BorderLayout.WEST);

        // Initialize the current frame with a default panel
        currentFrame = new JPanel();
        mainFrame.add(currentFrame, BorderLayout.CENTER);

        mainFrame.setVisible(true);
    }

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String userName = "John Doe"; // Replace with the actual username
                new ManagerGUI(userName);
            }
        });
    }
}

class SalesPanel extends JPanel {
    public SalesPanel() {
        // Add components specific to the Sales panel here
    }
}

class ReportsPanel extends JPanel {
    public ReportsPanel() {
        // Add components specific to the Reports panel here
    }
}

class SettingsPanel extends JPanel {
    public SettingsPanel() {
        // Add components specific to the Settings panel here
    }
}