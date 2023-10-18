import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SalesRep extends JPanel {
    private JTable orderItemsTable;

    public SalesRep() {
        setLayout(new BorderLayout());

        // Create a table model to hold the data
        DefaultTableModel tableModel = new DefaultTableModel();

        // Create the JTable with the table model
        orderItemsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderItemsTable);

        // Set column names (you should adapt these to match your actual table columns)
        tableModel.addColumn("Order ID");
        tableModel.addColumn("Item Name");
        tableModel.addColumn("Quantity");
        // Add more columns as needed

        // Fetch data from the database and populate the table
        fetchDataFromDatabase(tableModel);

        // Add the table to the panel
        add(scrollPane, BorderLayout.CENTER);
    }

    private void fetchDataFromDatabase(DefaultTableModel tableModel) {
        try {
            Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );
            Statement stmt = connection.createStatement();
            String query = "SELECT * FROM orderitems"; // Query to select all columns from the table
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                // Add rows to the table model
                tableModel.addRow(new Object[]{
                    rs.getObject(1), // Adjust these indices based on your table structure
                    rs.getObject(2),
                    rs.getObject(3) // Adjust these indices based on your table structure
                    // Add more columns as needed
                });
            }

            rs.close();
            stmt.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}