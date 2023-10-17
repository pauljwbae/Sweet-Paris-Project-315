import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NonFood extends JPanel {
    private OrdersPanel ordersPanel;

    public NonFood(OrdersPanel ordersPanel) {
        this.ordersPanel = ordersPanel;
        setLayout(new GridLayout(0, 3)); // 3 columns for buttons, adjust as needed

        // Fetch item data from the database and create buttons
        fetchItemsAndCreateButtons();

        int marginSize = 10; // Adjust the margin size as needed
        setBorder(BorderFactory.createEmptyBorder(marginSize, marginSize, marginSize, marginSize));
    }

    private void fetchItemsAndCreateButtons() {
        try {
            Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );

            String query = "SELECT name FROM items where not food and not drink";
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