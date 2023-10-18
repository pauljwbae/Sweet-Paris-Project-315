import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * The CashierGUI class functions as the interface through which a cashier would navigate the menu and create orders at Sweet Paris Cafe and Creperie.
 * @author Simon Vadarahaj
 */
public class CashierGUI {
    private JFrame mainFrame;
    private JPanel menuPanel;
    private JLabel welcomeLabel;
    private OrdersPanel ordersPanel;
    private JPanel currentFrame;

    /**
     * The CashierGUI constructor creates a window and creates a panel for the menu as well as four buttons to flip between different pages of the menu.
     * @param userName The name of the employee that is signed in to the system currently
     */

    public CashierGUI(String userName) {
        mainFrame = new JFrame("Sweet Paris Cashier System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 600); // Wider main frame
        mainFrame.setLayout(new BorderLayout());

        welcomeLabel = new JLabel("Welcome, " + userName);
        mainFrame.add(welcomeLabel, BorderLayout.NORTH);

        menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 1));

        ordersPanel = new OrdersPanel();
        mainFrame.add(ordersPanel, BorderLayout.EAST);

        // Add buttons to the menu panel
        addButton("Food", new Food(ordersPanel));
        addButton("Drinks", new Drink(ordersPanel));
        addButton("Low Calorie", new LowCalorie(ordersPanel));
        addButton("Non Food", new NonFood(ordersPanel));

        mainFrame.add(menuPanel, BorderLayout.WEST);

        currentFrame = new JPanel();
        mainFrame.add(currentFrame, BorderLayout.CENTER);
        // Create and add the OrdersPanel on the right side
        

        mainFrame.setVisible(true);
    }

    /**
     * addButton creates a button and puts it onto a JPanel.
     * @param buttonLabel The name the button is given, and the name displayed on the button
     * @param targetPanel The panel the button is added to
     */
    private void addButton(String buttonLabel, final JPanel targetPanel) {
        JButton button = new JButton(buttonLabel);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Switch the display to the target frame when the button is clicked
                currentFrame.removeAll();
                currentFrame.add(targetPanel);
                currentFrame.revalidate();
                currentFrame.repaint();
            }
        });

        menuPanel.add(button);
    }

}

// Define OrdersPanel to display orders on the right side
