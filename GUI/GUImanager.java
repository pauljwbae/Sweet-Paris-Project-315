import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class GUImanager extends JFrame {
    private JTextField productIdField;
    private JTextField productNameField;
    private JTextField quantityField;
    private JButton loadButton;
    private JButton updateButton;
    private Connection connection;

    public GUImanager() {
        super("SWEET PARIS POS FOR MANAGERS");

        // Initialize the GUI components
        productIdField = new JTextField(10);
        productNameField = new JTextField(20);
        quantityField = new JTextField(5);
        loadButton = new JButton("Load Data");
        updateButton = new JButton("Update");

        // Create the database connection
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db", "csce315_970_03user", "fourfsd");
        } 
        catch (Exception e) {
          e.printStackTrace();
          System.err.println(e.getClass().getName()+": "+e.getMessage());
          System.exit(0);
        }

        // Add action listeners to the buttons
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadInventoryData();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateInventoryData();
            }
        });

        // Create a panel to hold the components
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel("Inventory ID:"));
        panel.add(productIdField);
        panel.add(new JLabel("Product Type:"));
        panel.add(productNameField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(loadButton);
        panel.add(updateButton);

        // Add the panel to the frame
        add(panel);
    }

    private void loadInventoryData() {
        // Implement code to retrieve data from the "inventory" table and display it in the text fields.
        String productID = productIdField.getText();

        try {
            String query = "SELECT * FROM inventory WHERE id = "+ productID;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            // preparedStatement.setString(1, productID);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                productNameField.setText(resultSet.getString("product_name"));
                quantityField.setText(String.valueOf(resultSet.getInt("quantity")));
            } else {
                JOptionPane.showMessageDialog(this, "Product not found.");
            }

            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // FIX THIS
    private void updateInventoryData() {
        // Implement code to update the "inventory" table with the data in the text fields.
        String productID = productIdField.getText();
        String productName = productNameField.getText();
        // int quantity = Integer.parseInt(quantityField.getText());
        String quantity = quantityField.getText();

        // System.out.println(productID + " " + productName + " " + quantity);

        try {
            String updateQuery = "UPDATE inventory SET "+ productName +" = "+ quantity +" WHERE id = " + productID;
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            // updateStatement.setString(1, productName);
            // updateStatement.setInt(2, Integer.parseInt(quantity));
            // updateStatement.setString(3, productID);

            int rowsUpdated = updateStatement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Inventory updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Product not found. No update performed.");
            }

            updateStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUImanager app = new GUImanager();
            app.setSize(400, 150);
            app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            app.setVisible(true);
        });
    }
}
