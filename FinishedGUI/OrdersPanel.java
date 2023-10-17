import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

    // Add methods to update the order list and total price
    // For example, you can have a method like addOrderItem(String item, double price) to add items to the order
}