import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SalesRep extends JPanel {
    private JTable orderItemsTable;
    private JButton submitButton;
    private JComboBox<String> startDayComboBox;
    private JComboBox<String> startMonthComboBox;
    private JComboBox<String> startYearComboBox;
    private JComboBox<String> endDayComboBox;
    private JComboBox<String> endMonthComboBox;
    private JComboBox<String> endYearComboBox;

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
        fetchDataFromDatabase(tableModel, "2001-1-1 00:00:00", "2222-1-1 00:00:00");
        JPanel dateSelectionPanel = new JPanel();
        dateSelectionPanel.setLayout(new FlowLayout());

        startDayComboBox = new JComboBox<>(getDays());
        startMonthComboBox = new JComboBox<>(getMonths());
        startYearComboBox = new JComboBox<>(getYears());
        endDayComboBox = new JComboBox<>(getDays());
        endMonthComboBox = new JComboBox<>(getMonths());
        endYearComboBox = new JComboBox<>(getYears());

        dateSelectionPanel.add(new JLabel("Start Date:"));
        dateSelectionPanel.add(startDayComboBox);
        dateSelectionPanel.add(startMonthComboBox);
        dateSelectionPanel.add(startYearComboBox);

        dateSelectionPanel.add(new JLabel("End Date:"));
        dateSelectionPanel.add(endDayComboBox);
        dateSelectionPanel.add(endMonthComboBox);
        dateSelectionPanel.add(endYearComboBox);

        submitButton = new JButton("Submit");
        dateSelectionPanel.add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle the date selection and data retrieval here (modify as needed)
                String startDate = startYearComboBox.getSelectedItem() + "-" +
                        (startMonthComboBox.getSelectedIndex() + 1) + "-" +
                        startDayComboBox.getSelectedItem();
                String endDate = endYearComboBox.getSelectedItem() + "-" +
                        (endMonthComboBox.getSelectedIndex() + 1) + "-" +
                        endDayComboBox.getSelectedItem();

                // For demonstration, display the selected date range
                JOptionPane.showMessageDialog(SalesRep.this, "Selected Date Range:\nStart Date: " + startDate + "\nEnd Date: " + endDate);

                tableModel.setRowCount(0);
                fetchDataFromDatabase(tableModel, startDate, endDate);
            }
        });
        // Add the table to the panel
        add(scrollPane, BorderLayout.CENTER);
        add(dateSelectionPanel, BorderLayout.SOUTH);
    }

    private void fetchDataFromDatabase(DefaultTableModel tableModel, String start, String end) {
        try {
            Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );
            Statement stmt = connection.createStatement();
            String query = "SELECT orderitems.order_id, orderitems.item_name, orderitems.quantity FROM orderitems inner join orders on orders.orderid = orderitems.order_id where orders.orderdatetime between '"+start+" 00:00:00' and '"+end+" 23:59:59' order by orders.orderdatetime"; // Query to select all columns from the table
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

    private String[] getDays() {
        String[] days = new String[31];
        for (int i = 0; i < 31; i++) {
            days[i] = Integer.toString(i + 1);
        }
        return days;
    }

    private String[] getMonths() {
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        return months;
    }

    private String[] getYears() {
        String[] years = new String[2];
        for (int i = 0; i < 2; i++) {
            years[i] = Integer.toString(2022 + i);
        }
        return years;
    }
}