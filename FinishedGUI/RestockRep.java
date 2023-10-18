import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.table.DefaultTableModel;

/**
 * This class represents a JPanel that displays a table of inventory items that need to be restocked.
 * The table is populated by fetching data from a PostgreSQL database using the given credentials.
 * The table has two columns: "Product Name" and "Quantity".
 * The table is refreshed by clicking the "Refresh" button.
 */
class RestockRep extends JPanel {
    /**
     * The JTable that displays the inventory data.
     */
    private JTable inventoryTable;
    private JButton refresh;


    
    /**
     * This class represents a report of the current inventory stock levels.
     * It creates a table with the product names and their corresponding quantities,
     * fetched from the database. It also provides a refresh button to update the table
     * with the latest data from the database.
     */
    public RestockRep() {
        // Create a new table model
        DefaultTableModel tableModel = new DefaultTableModel();

        // Set column names
        tableModel.addColumn("Product Name");
        tableModel.addColumn("Quantity");

        // Fetch data from the database and populate the table
        fetchDataFromDatabase(tableModel);

        // Create the table with the model
        inventoryTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);

        JPanel updateRestockRep = new JPanel();
        updateRestockRep.setLayout(new FlowLayout());

        refresh = new JButton("Refresh");

        // Add components to the update inventory panel
        updateRestockRep.add(refresh);

        refresh.addActionListener(e -> {tableModel.setRowCount(0); fetchDataFromDatabase(tableModel);});

        // Add the table and the update inventory panel to the main panel
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(updateRestockRep, BorderLayout.SOUTH);
    }

    /**
     * Fetches data from the database and populates the given table model with the results.
     * The table model should have two columns: "name" and "quantity".
     * The method connects to a PostgreSQL database using the given credentials and executes a query to retrieve
     * the name and quantity of all items whose quantity is less than 20.
     * The retrieved data is then added to the table model as a new row.
     *
     * @param tableModel the table model to populate with the retrieved data
     */
    private void fetchDataFromDatabase(DefaultTableModel tableModel) {
        try {
            Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );
            Statement stmt = connection.createStatement();
            String query = "SELECT items.name, inventory.quantity FROM inventory inner join items on items.itemid = inventory.itemid where inventory.quantity < 20";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String productName = rs.getString("name");
                int quantity = rs.getInt("quantity");
                tableModel.addRow(new Object[]{productName, quantity});
            }

            rs.close();
            stmt.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}