import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;

/**
 * This class represents a JPanel that displays a table of orders and allows the user to edit the quantity of each item or delete an item from the order.
 * The class also provides a method to add a new row to the table and a method to submit the order to a database.
 * The table displays the item name and quantity, and the total price of the order is calculated based on the quantity of each item and its price in the database.
 * The class uses a DefaultTableModel to manage the data in the table and a JScrollPane to display the table.
 * The class also includes a JLabel to display the total price of the order and a JButton to submit the order to the database.
 */
class OrdersPanel extends JPanel {
    private DefaultTableModel orderTableModel;
    private JTable orderTable;
    private JScrollPane orderScrollPane;
    private JLabel totalPriceLabel;
    private JButton submitButton;

    /**
     * This class represents a panel for displaying and editing orders.
     * It contains a table for displaying order items, a label for displaying the total price,
     * and a submit button for submitting the order.
     * The table allows editing only for the "Quantity" column and double-clicking on a row in the table
     * opens a dialog for deleting the row.
     * The submit button inserts the order and its items into the database and clears the table.
     */
    public OrdersPanel() {
        setLayout(new BorderLayout());

        // Create a table to display order items
        orderTableModel = new DefaultTableModel();
        orderTableModel.addColumn("Item");
        orderTableModel.addColumn("Quantity");
        //orderTableModel.addColumn("Price"); 

        orderTable = new JTable(orderTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // Allow editing only for the "Quantity" column
            }
        };

        // Add a mouse listener to handle row editing
        orderTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double-click
                    int selectedRow = orderTable.getSelectedRow();
                    int selectedColumn = orderTable.getSelectedColumn();

                    if (selectedRow >= 0 && selectedColumn == 1) {
                        // Handle editing the quantity for the selected row
                        editRow(selectedRow);
                    }

                    if (selectedRow >= 0 && selectedColumn == 0) { // Clicked on "Item" column
                    int option = JOptionPane.showConfirmDialog(
                            OrdersPanel.this,
                            "Delete this item?",
                            "Confirm Deletion",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (option == JOptionPane.YES_OPTION) {
                        orderTableModel.removeRow(selectedRow); // Delete the selected row
                    }
                }
                    
                }
            }
        });

        orderScrollPane = new JScrollPane(orderTable);
        orderScrollPane.setPreferredSize(new Dimension(200, 400)); // Adjust the dimensions as needed
        add(orderScrollPane, BorderLayout.CENTER);

        // Create a label for displaying the total price
        totalPriceLabel = new JLabel("Total Price: $0.00");
        add(totalPriceLabel, BorderLayout.NORTH);

        // Create a submit button
        submitButton = new JButton("Submit Order");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement order submission logic here
                double price = getPrice();
                int calories = getCals();
                insertOrderIntoDatabase(11111, price, calories);
                insertOrderItemsIntoDatabase(getHighestOrderID());
                orderTableModel.setRowCount(0); 

                JOptionPane.showMessageDialog(OrdersPanel.this, "Total Price: $" + String.format("%.2f", price), "Order Summary", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        add(submitButton, BorderLayout.SOUTH);
    }

    /**
     * Adds a new row to the order table model with the given item name and quantity.
     * 
     * @param itemName the name of the item to add to the order
     * @param quantity the quantity of the item to add to the order
     */
    public void addRow(String itemName, int quantity) {
        orderTableModel.addRow(new Object[]{itemName, quantity});
        updateTotalPrice();
    }

    /**
     * Edits the quantity of an item in the order table.
     * 
     * @param row the row index of the item to be edited
     */
    private void editRow(int row) {
        String itemName = (String) orderTableModel.getValueAt(row, 0);
        int currentQuantity = (int) orderTableModel.getValueAt(row, 1);
        String input = JOptionPane.showInputDialog("Edit quantity for " + itemName + ":", currentQuantity);

        try {
            int newQuantity = Integer.parseInt(input);
            orderTableModel.setValueAt(newQuantity, row, 1);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a number.");
        }

        updateTotalPrice();
    }
    
    /**
     * This method calculates the total price of the items in the order table model by querying the database for the price of each item.
     * It constructs and executes a query for each row in the order table model and handles the query results as needed.
     * The total price is returned as a double value.
     * @return the total price of the items in the order table model
     */
    private double getPrice() {
        double price = 0;
        try {
            Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );

            int rowCount = orderTableModel.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                String itemName = (String) orderTableModel.getValueAt(i, 0);
                int quantity = (int) orderTableModel.getValueAt(i, 1);
                        
                // Construct and execute a query for each row
                String query = "SELECT price FROM items WHERE name = '" + itemName + "'";
                        
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    // preparedStatement.setString(1, itemName);
                    // preparedStatement.setInt(2, quantity);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            // Handle the query results as needed
                            double result = resultSet.getDouble("price");
                            price += (result * quantity);

                            // System.out.println("Query " + (i + 1) + " Result: " + result);
                        }
                    }
                }
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // System.out.println("Price: "+price);
        return price;
    }

    // /**
    //  * Updates the "Total Price" label based on the current items in the order.
    //  */
    public void updateTotalPrice() {
        double total = getPrice();
        totalPriceLabel.setText("Total Price: $" + String.format("%.2f", total));
    }
    /**
     * This method retrieves the total calories of the items in the order table model by querying a PostgreSQL database.
     * It constructs and executes a query for each row in the order table model, retrieves the calories of the item from the
     * database, and multiplies it by the quantity of the item in the order table model. The total calories of all items in the
     * order table model is returned.
     *
     * @return the total calories of all items in the order table model
     */
    private int getCals() {
        int calories = 0;
        try {
            Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );

            int rowCount = orderTableModel.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                String itemName = (String) orderTableModel.getValueAt(i, 0);
                int quantity = (int) orderTableModel.getValueAt(i, 1);
                        
                // Construct and execute a query for each row
                String query = "SELECT calories FROM items WHERE name = '" + itemName + "'";
                        
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    // preparedStatement.setString(1, itemName);
                    // preparedStatement.setInt(2, quantity);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            // Handle the query results as needed
                            int result = resultSet.getInt("calories");
                            calories += (result * quantity);

                            // System.out.println("Query " + (i + 1) + " Result: " + result);
                        }
                    }
                }
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // System.out.println("Price: "+calories);
        return calories;
    }

    /**
     * Inserts the order items into the database and returns the total calories of the order.
     * @param orderid the ID of the order to insert the items for
     * @return the total calories of the order
     */
    private int insertOrderItemsIntoDatabase(int orderid) {
        int calories = 0;
        try {
            Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );

            int rowCount = orderTableModel.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                String itemName = (String) orderTableModel.getValueAt(i, 0);
                int quantity = (int) orderTableModel.getValueAt(i, 1);
                        
                // Construct and execute a query for each row
                String query = "INSERT INTO orderitems (order_id, item_name, quantity) VALUES (?, ?, ?)";
                        
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, orderid);
                    preparedStatement.setString(2, itemName);
                    preparedStatement.setInt(3, quantity);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            // Handle the query results as needed
                            int result = resultSet.getInt("calories");
                            calories += (result * quantity);

                            // System.out.println("Query " + (i + 1) + " Result: " + result);
                        }
                    }
                }
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // System.out.println("Price: "+calories);
        return calories;
    }
    
    
    /**
     * Inserts an order into the database with the given customer ID, price, and calories.
     *
     * @param customerid the ID of the customer placing the order
     * @param price the price of the order
     * @param calories the number of calories in the order
     */
    private void insertOrderIntoDatabase(int customerid, double price, int calories) {
        // Replace these values with your actual database connection details
        try (Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    )) {

            LocalDateTime currentDateTime = LocalDateTime.now();
            Timestamp timestamp = Timestamp.valueOf(currentDateTime);
            String query = "INSERT INTO orders (orderdatetime, customerid, price, calories) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setTimestamp(1, timestamp);
                preparedStatement.setInt(2, customerid);
                preparedStatement.setDouble(3, price);
                preparedStatement.setInt(4, calories);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the highest order ID from the orders table in the database.
     * If no orders exist, returns -1.
     *
     * @return the highest order ID
     */
    private int getHighestOrderID() {
        int highestOrderID = -1; // Default value if no orders exist

        try (Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    )) {
            String query = "SELECT MAX(orderid) FROM orders";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        highestOrderID = resultSet.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return highestOrderID;
    }
    // Add methods to update the order list and total price
    // For example, you can have a method like addOrderItem(String item, double price) to add items to the order
}