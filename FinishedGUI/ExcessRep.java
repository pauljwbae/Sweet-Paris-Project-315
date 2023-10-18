import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * The ExcessRep class functions as the gui, querying, and logic for the excess report query -- in which we had to return the items that sold less than 10% of its total inventory within a specified time.
 * @author Simon Vadarahaj, Paul Bae
 */
public class ExcessRep extends JPanel {
    private JTable salesTable;
    private JButton submitButton;
    private JComboBox<String> startDayComboBox;
    private JComboBox<String> startMonthComboBox;
    private JComboBox<String> startYearComboBox;
    
    /**
     * The constructor sets up a JTable -- to display the results of the query, a submission JButton -- to submit the query or refresh the table, and three JComboBox elements -- to enter the month, date, and year respectively.
     */
    public ExcessRep() {
        // Create a new table model
        DefaultTableModel tableModel = new DefaultTableModel();

        // Set column names
        tableModel.addColumn("Product Name");
        tableModel.addColumn("Quantity Sold");

        // Fetch sales data from the database and populate the table (you can add your data retrieval logic here)
        fetchDataFromDatabase(tableModel, "2024-1-1");

        // Create the table with the model
        salesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(salesTable);

        // Create date selection components
        JPanel dateSelectionPanel = new JPanel();
        dateSelectionPanel.setLayout(new FlowLayout());

        startDayComboBox = new JComboBox<>(getDays());
        startMonthComboBox = new JComboBox<>(getMonths());
        startYearComboBox = new JComboBox<>(getYears());

        dateSelectionPanel.add(new JLabel("Threshold Date:"));
        dateSelectionPanel.add(startDayComboBox);
        dateSelectionPanel.add(startMonthComboBox);
        dateSelectionPanel.add(startYearComboBox);

        submitButton = new JButton("Submit");
        dateSelectionPanel.add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle the date selection and data retrieval here (modify as needed)
                String startDate = startYearComboBox.getSelectedItem() + "-" +
                        (startMonthComboBox.getSelectedIndex() + 1) + "-" +
                        startDayComboBox.getSelectedItem();

                // For demonstration, display the selected date range
                JOptionPane.showMessageDialog(ExcessRep.this, "Selected Date Range:\nStart Date: " + startDate + "\nEnd Date: Present");

                tableModel.setRowCount(0);
                fetchDataFromDatabase(tableModel, startDate);
            }
        });

        // Set the layout for this panel
        setLayout(new BorderLayout());

        // Add components to the panel
        add(scrollPane, BorderLayout.CENTER);
        add(dateSelectionPanel, BorderLayout.SOUTH);
    }

    /**
     * fetchDataFromDatabase connects to the SQL database and selects all items from the items table that have less than < 10 sells within the given time period.
     * @param tableModel the tableModel that will be modified and populated by the query.
     * @param startDate A string that follows the XX-XX-XX XX:XX:XX format for datetime objects in SQL. This param specifies the time that the query will go back to.
     */
    private void fetchDataFromDatabase(DefaultTableModel tableModel, String startDate) {
        try {
            Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );
            Statement stmt = connection.createStatement();
            String query = "SELECT orderitems.item_name, SUM(orderitems.quantity) AS total_sold FROM orders  INNER JOIN orderitems ON orders.orderid = orderitems.order_id where orders.orderdatetime between '"+startDate+" 00:00:00' and 2023-10-17  GROUP BY orderitems.item_name having SUM(orderitems.quantity) < 10 order by total_sold asc";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String productName = rs.getString("item_name");
                int quantitySold = rs.getInt("total_sold");
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
     * getDays populates a string array from 1-31 and returns it.
     */
    private String[] getDays() {
        String[] days = new String[31];
        for (int i = 0; i < 31; i++) {
            days[i] = Integer.toString(i + 1);
        }
        return days;
    }

    /**
     * getMonths populates a string array with the names of months and returns it.
     */
    private String[] getMonths() {
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        return months;
    }

    /**
     * getYears populates a string array with two years, 2022 and 2023 and returns it.
     */
    private String[] getYears() {
        String[] years = new String[2];
        for (int i = 0; i < 2; i++) {
            years[i] = Integer.toString(2022 + i);
        }
        return years;
    }
}