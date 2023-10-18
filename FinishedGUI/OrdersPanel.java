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

class OrdersPanel extends JPanel {
    private DefaultTableModel orderTableModel;
    private JTable orderTable;
    private JScrollPane orderScrollPane;
    private JLabel totalPriceLabel;
    private JButton submitButton;

    public OrdersPanel() {
        setLayout(new BorderLayout());

        // Create a table to display order items
        orderTableModel = new DefaultTableModel();
        orderTableModel.addColumn("Item");
        orderTableModel.addColumn("Quantity");

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
        add(totalPriceLabel, BorderLayout.SOUTH);

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
            }
        });
        add(submitButton, BorderLayout.SOUTH);
    }

    public void addRow(String itemName, int quantity) {
        orderTableModel.addRow(new Object[]{itemName, quantity});
    }

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
    }
    
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