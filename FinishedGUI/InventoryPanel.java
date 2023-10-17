import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

class InventoryPanel extends JPanel {
    private JTable inventoryTable;
    private JComboBox<String> productDropdown;
    private JTextField quantityField;
    private JButton updateButton;

    public InventoryPanel() {
        // Create a new table model
        DefaultTableModel tableModel = new DefaultTableModel();

        // Set column names
        tableModel.addColumn("Product Name");
        tableModel.addColumn("Quantity");
        tableModel.addColumn("Price");

        // Fetch data from the database and populate the table
        fetchDataFromDatabase(tableModel);

        // Create the table with the model
        inventoryTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);

        JPanel updateInventoryPanel = new JPanel();
        updateInventoryPanel.setLayout(new FlowLayout());

        productDropdown = new JComboBox<>();
        quantityField = new JTextField(5);
        updateButton = new JButton("Update Inventory");

        // Add components to the update inventory panel
        updateInventoryPanel.add(productDropdown);
        updateInventoryPanel.add(quantityField);
        updateInventoryPanel.add(updateButton);
        
        updateButton.addActionListener(e -> {
            // Handle inventory update here
            String selectedProduct = productDropdown.getSelectedItem().toString();
            String newQuantity = quantityField.getText();

            // You should implement the logic to update the database with the new quantity here
            // Update the database with the new quantity
            try {
                Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );

                // Use a PreparedStatement to prevent SQL injection
                String updateQuery = "UPDATE inventory SET quantity = "+newQuantity+" FROM items WHERE items.name = '"+selectedProduct+"' AND items.itemid = inventory.itemid";
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                // preparedStatement.setInt(1, newQuantity);
                // preparedStatement.setString(2, selectedProduct);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Inventory updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update inventory.");
                }

                preparedStatement.close();
                connection.close();

                // After updating the database, refresh the table
                tableModel.setRowCount(0);
                fetchDataFromDatabase(tableModel);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // After updating the database, refresh the table
            tableModel.setRowCount(0);
            fetchDataFromDatabase(tableModel);
        });

        // Populate the product dropdown with data from the database
        populateProductDropdown();

        // Add the table and the update inventory panel to the main panel
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(updateInventoryPanel, BorderLayout.SOUTH);
    }

    private void fetchDataFromDatabase(DefaultTableModel tableModel) {
        try {
            Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );
            Statement stmt = connection.createStatement();
            String query = "SELECT items.name, inventory.quantity, items.price FROM inventory inner join items on items.itemid = inventory.itemid";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String productName = rs.getString("name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                tableModel.addRow(new Object[]{productName, quantity, price});
            }

            rs.close();
            stmt.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateProductDropdown() {
        // Fetch product names from the database and populate the dropdown
        try {
            Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );
            Statement stmt = connection.createStatement();
            String query = "SELECT items.name FROM inventory inner join items on items.itemid = inventory.itemid";
            ResultSet rs = stmt.executeQuery(query);

            Vector<String> productNames = new Vector<>();
            while (rs.next()) {
                productNames.add(rs.getString("name"));
            }
            rs.close();
            stmt.close();
            connection.close();

            productDropdown.setModel(new DefaultComboBoxModel<>(productNames));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}