import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;


/**
 * This class allows the Vendor to add items into the database system.
 */
public class NewSeasonal extends JPanel {
    private JTable itemsTable;
    private JButton updatePriceButton;
    private JButton addItemButton;
    private JButton deleteItemButton;
    private JButton refresh;

    /**
     * This class represents a GUI for displaying and managing seasonal items.
     * It contains a JTable to display the items, and buttons to perform operations such as updating the price, adding an item, and deleting an item.
     * The table is populated by fetching data from a database using the fetchDataFromDatabase() method.
     * The class also includes action listeners for the buttons to perform the necessary operations.
     */
    public NewSeasonal() {
        DefaultTableModel tableModel = new DefaultTableModel();

        // Set column names
        tableModel.addColumn("Name");
        tableModel.addColumn("Contains Wheat");
        tableModel.addColumn("Contains Milk");
        tableModel.addColumn("Contains Eggs");
        tableModel.addColumn("Contains Alcohol");
        tableModel.addColumn("Price");
        tableModel.addColumn("Calories");
        tableModel.addColumn("Drink");
        tableModel.addColumn("Food");

        // Fetch data from the database and populate the table
        fetchDataFromDatabase(tableModel);

        itemsTable = new JTable(tableModel);
        itemsTable.setPreferredScrollableViewportSize(new Dimension(800, 400)); 
        JScrollPane scrollPane = new JScrollPane(itemsTable);

        updatePriceButton = new JButton("Update Price");
        addItemButton = new JButton("Add Item");
        deleteItemButton = new JButton("Delete Item");
        refresh = new JButton("Refresh");

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.add(updatePriceButton);
        buttonsPanel.add(addItemButton);
        buttonsPanel.add(deleteItemButton);
        buttonsPanel.add(refresh);

        refresh.addActionListener(e -> {tableModel.setRowCount(0); fetchDataFromDatabase(tableModel);});
        

        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(buttonsPanel, BorderLayout.SOUTH);

        // Set action listeners for the buttons to perform the necessary operations
        updatePriceButton.addActionListener(e -> showUpdatePriceDialog());
        addItemButton.addActionListener(e -> addItem());
        deleteItemButton.addActionListener(e -> deleteItem());
    }

    /**
     * Fetches data from the database and populates the given table model with the retrieved data.
     * 
     * @param tableModel the table model to populate with the retrieved data
     */
    private void fetchDataFromDatabase(DefaultTableModel tableModel) {
        // Fetch data from the database and populate the table here
        // You should replace the example below with actual database retrieval code
        // and iterate over the result set to add rows to the table model
        try {
            Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM items order by name");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("name"),
                    rs.getBoolean("containswheat"),
                    rs.getBoolean("containsmilk"),
                    rs.getBoolean("containseggs"),
                    rs.getBoolean("containsalcohol"),
                    rs.getDouble("price"),
                    rs.getInt("calories"),
                    rs.getBoolean("drink"),
                    rs.getBoolean("food")
                });
            }

            rs.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays a dialog box to update the price of an item selected from a dropdown list.
     * The dialog box contains a dropdown list of items and a text field to enter the new price.
     * If the user clicks OK, the selected item's price is updated in the database and the table is refreshed.
     */
    private void showUpdatePriceDialog() {
        JComboBox<String> itemDropdown = new JComboBox<>();
        JTextField priceField = new JTextField(10);

        for (int row = 0; row < itemsTable.getRowCount(); row++) {
            String itemName = (String) itemsTable.getValueAt(row, 0);
            itemDropdown.addItem(itemName);
        }

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Select Item:"));
        panel.add(itemDropdown);
        panel.add(new JLabel("New Price:"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Update Item Price",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String selectedItem = itemDropdown.getSelectedItem().toString();
            String newPrice = priceField.getText();

            updateItemPrice(selectedItem, newPrice);

            DefaultTableModel tableModel = (DefaultTableModel) itemsTable.getModel();
            tableModel.setRowCount(0);  // Clear the table
            fetchDataFromDatabase(tableModel); // Fetch and display updated data
        }
    }

    /**
     * Updates the price of a selected item in the database.
     * 
     * @param selectedItem the name of the item to update
     * @param newPrice the new price of the item
     */
    private void updateItemPrice(String selectedItem, String newPrice) {
        try {
            Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );

            String updateQuery = "UPDATE items SET price = "+newPrice+" WHERE name = '"+selectedItem+"'";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Price updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update price.");
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the inventory by inserting a new row with a quantity of 100 into the inventory table of the database.
     * Displays a success message if the update is successful, or a failure message if it fails.
     */
    private void updateInventory() {
        try {
            Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );

            String updateQuery = "INSERT INTO inventory (quantity) VALUES (100)";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Inventory updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update Inventory.");
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes inventory from the database by inserting a new row with a quantity of 100.
     * Displays a success message if the update was successful, or an error message if it failed.
     * @throws SQLException if there is an error with the database connection or query execution.
     */
    private void deleteInventory() {
        try {
            Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );

            String updateQuery = "INSERT INTO inventory (quantity) VALUES (100)";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Inventory updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update Inventory.");
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method creates a panel for entering item details and inserts a new item into the database.
     * It retrieves values from the input fields and checkboxes, and uses a PreparedStatement to insert a new item into the database.
     * If the item is added successfully, it displays a success message and updates the inventory.
     * If the item fails to be added, it displays an error message.
     * Finally, it refreshes the items table to display the new item.
     */
    private void addItem() {
        // Create a panel for entering item details
        JPanel addItemPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(addItemPanel, BoxLayout.Y_AXIS);
        addItemPanel.setLayout(boxLayout);
        JTextField nameField = new JTextField(20);
        JCheckBox wheatCheckBox = new JCheckBox("Contains Wheat");
        JCheckBox milkCheckBox = new JCheckBox("Contains Milk");
        JCheckBox eggsCheckBox = new JCheckBox("Contains Eggs");
        JCheckBox alcoholCheckBox = new JCheckBox("Contains Alcohol");
        JTextField priceField = new JTextField(10);
        JTextField caloriesField = new JTextField(10);
        JCheckBox drinkCheckBox = new JCheckBox("Is a Drink");
        JCheckBox foodCheckBox = new JCheckBox("Is Food");
    
        addItemPanel.add(new JLabel("Name:"));
        addItemPanel.add(nameField);
        addItemPanel.add(new JLabel("Contains Wheat:"));
        addItemPanel.add(wheatCheckBox);
        addItemPanel.add(new JLabel("Contains Milk:"));
        addItemPanel.add(milkCheckBox);
        addItemPanel.add(new JLabel("Contains Eggs:"));
        addItemPanel.add(eggsCheckBox);
        addItemPanel.add(new JLabel("Contains Alcohol:"));
        addItemPanel.add(alcoholCheckBox);
        addItemPanel.add(new JLabel("Price:"));
        addItemPanel.add(priceField);
        addItemPanel.add(new JLabel("Calories:"));
        addItemPanel.add(caloriesField);
        addItemPanel.add(new JLabel("Is a Drink:"));
        addItemPanel.add(drinkCheckBox);
        addItemPanel.add(new JLabel("Is Food:"));
        addItemPanel.add(foodCheckBox);
    
        int result = JOptionPane.showConfirmDialog(
            this,
            addItemPanel,
            "Add Item",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
    
        if (result == JOptionPane.OK_OPTION) {
            // Retrieve values from the input fields and checkboxes
            String name = nameField.getText();
            boolean containsWheat = wheatCheckBox.isSelected();
            boolean containsMilk = milkCheckBox.isSelected();
            boolean containsEggs = eggsCheckBox.isSelected();
            boolean containsAlcohol = alcoholCheckBox.isSelected();
            String price = priceField.getText();
            String calories = caloriesField.getText();
            boolean isDrink = drinkCheckBox.isSelected();
            boolean isFood = foodCheckBox.isSelected();
    
            try {
                Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );
    
                // Use a PreparedStatement to insert a new item into the database
                String insertQuery = "INSERT INTO items (name, containswheat, containsmilk, containseggs, containsalcohol, price, calories, drink, food) " +
                        "VALUES ('"+name+"', "+containsWheat+", "+containsMilk+", "+containsEggs+", "+containsAlcohol+", "+price+", "+calories+", "+isDrink+", "+isFood+")";
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

                int rowsAffected = preparedStatement.executeUpdate();
    
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Item added successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add item.");
                }

                updateInventory();
    
                preparedStatement.close();
                connection.close();
    
                // Refresh the items table to display the new item
                DefaultTableModel tableModel = (DefaultTableModel) itemsTable.getModel();
                tableModel.setRowCount(0);  // Clear the table
                fetchDataFromDatabase(tableModel); // Fetch and display updated data
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
        }
    }

    /**
     * This method deletes the selected item from the itemsTable and the database.
     * If no item is selected, it displays a message asking the user to select an item.
     * If the user confirms the deletion, it deletes the item from the database and the table.
     * If the deletion is successful, it displays a message indicating the success.
     * If the deletion fails, it displays a message indicating the failure.
     */
    private void deleteItem() {
        int selectedRow = itemsTable.getSelectedRow();
    
        if (selectedRow != -1) {
            String itemName = (String) itemsTable.getValueAt(selectedRow, 0);
            int confirmDialogResult = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
    
            if (confirmDialogResult == JOptionPane.YES_OPTION) {
                try {
                    Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );
    
                    String deleteQuery = "DELETE FROM items WHERE name = '"+itemName+"'";
                    PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
                    // preparedStatement.setString(1, itemName);
    
                    int rowsAffected = preparedStatement.executeUpdate();
    
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Item deleted successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete item.");
                    }

                    preparedStatement.close();
                    connection.close();
    
                    // Remove the selected row from the table
                    DefaultTableModel tableModel = (DefaultTableModel) itemsTable.getModel();
                    tableModel.removeRow(selectedRow);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
        }
    }

    /**
     * This method is used to fit the column widths of a JTable based on the preferred width of the contents in each cell.
     * It iterates through each column and row of the table, gets the preferred width of the cell contents, and sets the
     * preferred width of the column to the maximum preferred width of all the cells in that column.
     * 
     * @param table The JTable for which the column widths need to be adjusted.
     */
    private void fitColumnWidths(JTable table) {
        for (int column = 0; column < table.getColumnCount(); column++) {
            int maxwidth = 0;
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                maxwidth = Math.max(comp.getPreferredSize().width, maxwidth);
            }
            table.getColumnModel().getColumn(column).setPreferredWidth(maxwidth);
        }
    }
}
