import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * The `ManagerGUI` class represents a graphical user interface for the manager's dashboard of the Sweet Paris
 * management system. It provides access to various functionality and reports related to the management of the
 * business.
 */
public class ManagerGUI {
    private JFrame mainFrame;
    private JPanel menuPanel;
    private JLabel welcomeLabel;
    private JPanel currentFrame;

    /**
     * Constructs a new manager GUI with the specified user name.
     *
     * @param userName The user name of the manager.
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
        addButton("Sales Report", new SalesRep());
        addButton("Excess Report", new ExcessRep());
        addButton("Product Usage Chart", new ProdUseChart());
        addButton("Restock Report", new RestockRep());
        addButton("Update Items", new NewSeasonal());
        addButton("Ordering Trend Report", new Together());
        addButton("Popularity Analysis", new Popularity());

        mainFrame.add(menuPanel, BorderLayout.WEST);

        // Initialize the current frame with a default panel
        currentFrame = new JPanel();
        mainFrame.add(currentFrame, BorderLayout.CENTER);

        mainFrame.setVisible(true);
    }

    /**
     * Adds a button to the menu panel that, when clicked, switches the display to the specified target panel.
     *
     * @param buttonLabel  The label text for the button.
     * @param targetPanel  The panel to display when the button is clicked.
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
