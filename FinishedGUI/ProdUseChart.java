import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * The `ProdUseChart` class represents a JPanel for displaying product usage charts based on sales data.
 * It provides the ability to select a date range and retrieve sales data within that range.
 * 
 * The class contains a JTable to display the product names and quantities sold, as well as
 * components for selecting a date range and a "Submit" button to update the displayed data.
 */
public class ProdUseChart extends JPanel {
    private JTable salesTable;
    private JButton submitButton;
    private JComboBox<String> startDayComboBox;
    private JComboBox<String> startMonthComboBox;
    private JComboBox<String> startYearComboBox;
    private JComboBox<String> endDayComboBox;
    private JComboBox<String> endMonthComboBox;
    private JComboBox<String> endYearComboBox;
    private Map<String, String> monthMap;
    
    /**
     * Constructs a new `ProdUseChart` panel with a JTable to display product usage data and components
     * for selecting a date range.
     */
    public ProdUseChart() {
        monthMap = new HashMap<>();
        monthMap.put("January", "01");
        monthMap.put("February", "02");
        monthMap.put("March", "03");
        monthMap.put("April", "04");
        monthMap.put("May", "05");
        monthMap.put("June", "06");
        monthMap.put("July", "07");
        monthMap.put("August", "08");
        monthMap.put("September", "09");
        monthMap.put("October", "10");
        monthMap.put("November", "11");
        monthMap.put("December", "12");
        // Create a new table model
        DefaultTableModel tableModel = new DefaultTableModel();

        // Set column names
        tableModel.addColumn("Product Name");
        tableModel.addColumn("Quantity Sold");

        // Fetch sales data from the database and populate the table (you can add your data retrieval logic here)
        fetchDataFromDatabase(tableModel, "2001-1-1 00:00:00", "2222-1-1 00:00:00");

        // Create the table with the model
        salesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(salesTable);

        // Create date selection components
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
                JOptionPane.showMessageDialog(ProdUseChart.this, "Selected Date Range:\nStart Date: " + startDate + "\nEnd Date: " + endDate);

                tableModel.setRowCount(0);
                fetchDataFromDatabase(tableModel, startDate, endDate);
            }
        });

        // Set the layout for this panel
        setLayout(new BorderLayout());

        // Add components to the panel
        add(scrollPane, BorderLayout.CENTER);
        add(dateSelectionPanel, BorderLayout.SOUTH);
    }
    /**
     * Fetches product usage data from the database and populates the JTable with the retrieved data
     * based on the specified date range.
     *
     * @param tableModel The DefaultTableModel to populate with product usage data.
     * @param startDate The start date of the date range.
     * @param endDate   The end date of the date range.
     */
    private void fetchDataFromDatabase(DefaultTableModel tableModel, String startDate, String endDate) {
        try {
            Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );
            Statement stmt = connection.createStatement();
            String query = "SELECT orderitems.order_id, SUM(orderitems.quantity) AS total_inventory FROM orders INNER JOIN orderitems ON orders.orderid = orderitems.order_id where orders.orderdatetime between '"+startDate+" 00:00:00' and '"+endDate+" 23:59:59' GROUP BY orderitems.order_id order by  orderitems.order_id";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String productName = rs.getString("order_id");
                int quantitySold = rs.getInt("total_inventory");
                tableModel.addRow(new Object[]{productName, quantitySold});
            }

            rs.close();
            stmt.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves an array of day values (1-31) as strings.
     *
     * @return An array of day values.
     */
    private String[] getDays() {
        String[] days = new String[31];
        for (int i = 0; i < 31; i++) {
            days[i] = Integer.toString(i + 1);
        }
        return days;
    }

    /**
     * Retrieves an array of month names.
     *
     * @return An array of month names.
     */
    private String[] getMonths() {
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        return months;
    }

    /**
     * Retrieves an array of year values for a limited range as strings.
     *
     * @return An array of year values.
     */
    private String[] getYears() {
        String[] years = new String[2];
        for (int i = 0; i < 2; i++) {
            years[i] = Integer.toString(2022 + i);
        }
        return years;
    }
}