import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The Drink class functions as the menu of all menu items less than 900 calories that are served at Sweet Paris Cafe and Creperie.
 * @author Simon Vadarahaj
 */
public class LowCalorie extends JPanel {
    private OrdersPanel ordersPanel;

    /**
     * The constructor sets up a grid and makes buttons along the grid for each drink item.
     * @param ordersPanel The window onto which the grid is placed.
     */
    public LowCalorie(OrdersPanel ordersPanel) {
        this.ordersPanel = ordersPanel;
        setLayout(new GridLayout(0, 3)); // 3 columns for buttons, adjust as needed

        // Fetch item data from the database and create buttons
        fetchItemsAndCreateButtons();

        int marginSize = 10; // Adjust the margin size as needed
        setBorder(BorderFactory.createEmptyBorder(marginSize, marginSize, marginSize, marginSize));
    }

    /**
     * fetchItemsAndCreateButtons connects to the SQL database and selects all items from the items table that are low-calorie.
     * It then makes a button for each drink item to put onto the grid made in the constructor.
     */
    private void fetchItemsAndCreateButtons() {
        try {
            Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );

            String query = "SELECT name FROM items where calories < 900 and (food or drink)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String itemName = resultSet.getString("name");
                JButton itemButton = new JButton(itemName);
                
                Insets margins = new Insets(10, 10, 10, 10); // Top, left, bottom, right margins
                itemButton.setMargin(margins);

                itemButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int quantity = askForQuantity(itemName);
                        if (quantity >= 0) {
                            // Handle the selected item and quantity here
                            // You can add the item to an order or perform any other action
                            ordersPanel.addRow(itemName, quantity);
                        }
                    }
                });

                add(itemButton);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Whenever a low-calorie item button is pressed, askForQuantity is called to ask the user how many of the item selected are in the order.
     * @param itemName The name of the low-calorie item selected.
     * @return The number of the item ordered.
     */
    private int askForQuantity(String itemName) {
        String input = JOptionPane.showInputDialog("Enter quantity for " + itemName + ":");
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a number.");
            return -1;
        }
    }

}