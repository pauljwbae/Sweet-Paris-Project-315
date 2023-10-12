import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class GUImanager extends JFrame {
    private JTextField productIdField;
    private JTextField productNameField;
    private JTextField quantityField;
    private JTextField itemNameField;
    private JTextField itemIDField;
    private JTextField priceField;
    private JButton loadButton;
    private JButton updateButton;
    private JButton itemloadButton;
    private JButton itemupdateButton;
    private JButton itemaddButton;
    private Connection connection;

    public GUImanager() {
        super("SWEET PARIS POS FOR MANAGERS");

        // Initialize the GUI components
        productIdField = new JTextField(10);
        productNameField = new JTextField(20);
        quantityField = new JTextField(5);
        loadButton = new JButton("Load Data from Inventory");
        updateButton = new JButton("Update Inventory");

        itemNameField = new JTextField(10);
        itemIDField = new JTextField(10);
        priceField = new JTextField(5);
        itemloadButton = new JButton("Load Data from Item");
        itemupdateButton = new JButton("Update Item");
        itemaddButton = new JButton("Add Item");

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

        itemupdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateItemData();
            }
        });

        itemloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadItemData();
            }
        });

        itemaddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItemData();
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
        
        panel.add(new JLabel("Item ID:"));
        panel.add(itemIDField);
        panel.add(new JLabel("Item Name:"));
        panel.add(itemNameField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(itemloadButton);
        panel.add(itemupdateButton);
        panel.add(itemaddButton);

        // Add the panel to the frame
        add(panel);
    }

    private void loadInventoryData() {
        // Implement code to retrieve data from the "inventory" table and display it in the text fields.
        String productID = productIdField.getText();
        String productName = productNameField.getText();

        try {
            String query = "SELECT "+ productName +" FROM inventory WHERE id = "+ productID;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            // preparedStatement.setString(1, productID);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                quantityField.setText(resultSet.getString(productName));
                // quantityField.setText(String.valueOf(resultSet.getInt("quantity")));
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

    private void loadItemData() {
        // Implement code to retrieve data from the "inventory" table and display it in the text fields.
        String itemName = itemNameField.getText();

        // System.out.println(itemName);

        try {
            String query = "SELECT price FROM items WHERE name = '"+ itemName + "'";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            // preparedStatement.setString(1, productID);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                priceField.setText(resultSet.getString("price"));
                // quantityField.setText(String.valueOf(resultSet.getInt("quantity")));
            } else {
                JOptionPane.showMessageDialog(this, "Product not found.");
            }

            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateItemData() {
        // Implement code to update the "inventory" table with the data in the text fields.
        String itemName = itemNameField.getText();
        // int quantity = Integer.parseInt(quantityField.getText());
        String priceString = priceField.getText();

        // System.out.println(productID + " " + productName + " " + quantity);

        try {
            String updateQuery = "UPDATE items SET price = "+ priceString +" WHERE name = '" + itemName + "'";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);

            int rowsUpdated = updateStatement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Item updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Product not found. No update performed.");
            }

            updateStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addItemData() {
        // Implement code to update the "inventory" table with the data in the text fields.
        String itemID = itemIDField.getText();
        String itemName = itemNameField.getText();
        // int quantity = Integer.parseInt(quantityField.getText());
        String priceString = priceField.getText();

        // System.out.println(productID + " " + productName + " " + quantity);

        try {
            String updateQuery = "insert into items table (itemid, name, price) values ("+ itemID +", "+ itemName +", "+ priceString +")";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);

            int rowsUpdated = updateStatement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Item added successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Product not found. No add performed.");
            }

            updateStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUImanager app = new GUImanager();
            app.setSize(1000, 150);
            app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            app.setVisible(true);
        });
    }
}
