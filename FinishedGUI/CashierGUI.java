import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CashierGUI {
    private JFrame mainFrame;
    private JPanel menuPanel;
    private JLabel welcomeLabel;
    private OrdersPanel ordersPanel;
    private JPanel currentFrame;

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
