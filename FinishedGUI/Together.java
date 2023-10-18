import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
     * This class displays a list of pairs of menu items that sell together often (popular or not), sorted by most frequent, based on a given date and time. 
     */
public class Together extends JPanel {
    private JTable orderItemsTable;
    private JButton submitButton;
    private JComboBox<String> startDayComboBox;
    private JComboBox<String> startMonthComboBox;
    private JComboBox<String> startYearComboBox;
    private JComboBox<String> endDayComboBox;
    private JComboBox<String> endMonthComboBox;
    private JComboBox<String> endYearComboBox;

    /**
     * This class creates a GUI with a JTable that displays data fetched from a database based on a date range selected by the user.
     * The table has columns for Item 1, Item 2, and Number of Pairs, and can be modified to include additional columns as needed.
     * The user selects the date range using JComboBoxes for day, month, and year, and clicks the Submit button to retrieve the data.
     * The selected date range is displayed in a JOptionPane for demonstration purposes.
     * The fetchDataFromDatabase() method is called to retrieve the data from the database and populate the table.
     */
    public Together() {
        setLayout(new BorderLayout());

        // Create a table model to hold the data
        DefaultTableModel tableModel = new DefaultTableModel();

        // Create the JTable with the table model
        orderItemsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderItemsTable);

        // Set column names (you should adapt these to match your actual table columns)
        tableModel.addColumn("Item 1");
        tableModel.addColumn("Item 2");
        tableModel.addColumn("Number of Pairs");
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
                JOptionPane.showMessageDialog(Together.this, "Selected Date Range:\nStart Date: " + startDate + "\nEnd Date: " + endDate);

                tableModel.setRowCount(0);
                fetchDataFromDatabase(tableModel, startDate, endDate);
            }
        });
        // Add the table to the panel
        add(scrollPane, BorderLayout.CENTER);
        add(dateSelectionPanel, BorderLayout.SOUTH);
    }

    /**
     * Fetches data from a PostgreSQL database and populates a given table model with the results.
     * 
     * @param tableModel The table model to populate with the results.
     * @param start The start date to filter the results by.
     * @param end The end date to filter the results by.
     */
    private void fetchDataFromDatabase(DefaultTableModel tableModel, String start, String end) {
        try {
            Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );
            Statement stmt = connection.createStatement();
            String query = "SELECT t1.item_name AS item1, t2.item_name AS item2, COUNT(*) AS pair_count FROM orderitems t1 JOIN orderitems t2 ON t1.order_id = t2.order_id AND t1.item_name < t2.item_name JOIN orders o1 ON t1.order_id = o1.orderid JOIN orders o2 ON t2.order_id = o2.orderid WHERE o1.orderdatetime BETWEEN '"+start+" 00:00:00' AND '"+end+" 23:59:59' AND o2.orderdatetime BETWEEN '2022-01-01 00:00:00' AND '2023-10-10 23:59:59' GROUP BY t1.item_name, t2.item_name ORDER BY pair_count DESC"; // Query to select all columns from the table
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

    /**
     * Returns an array of strings representing the days of the month.
     * The array contains 31 elements, each element representing a day of the month as a string.
     *
     * @return an array of strings representing the days of the month.
     */
    private String[] getDays() {
        String[] days = new String[31];
        for (int i = 0; i < 31; i++) {
            days[i] = Integer.toString(i + 1);
        }
        return days;
    }

    /**
     * Returns an array of month names.
     *
     * @return an array of month names
     */
    private String[] getMonths() {
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        return months;
    }

    /**
     * Returns an array of two years starting from the current year.
     * @return an array of two years as strings
     */
    private String[] getYears() {
        String[] years = new String[2];
        for (int i = 0; i < 2; i++) {
            years[i] = Integer.toString(2022 + i);
        }
        return years;
    }
}