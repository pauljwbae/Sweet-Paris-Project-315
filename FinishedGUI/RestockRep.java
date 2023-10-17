import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.table.DefaultTableModel;

class RestockRep extends JPanel {
    private JTable inventoryTable;
    private JButton refresh;

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