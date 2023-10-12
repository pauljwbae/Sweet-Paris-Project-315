import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class GUI extends JFrame implements ActionListener {
    private static JFrame frame;
    private static JTextArea orderTextArea;
    private static List<MenuItem> menuData;
    private static List<MenuItem> selectedItems;
    private static double totalOrderPrice;
    private JTextField customerNameField;

    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.menuData = readMenuItemsFromCSV("20items.csv"); // Initialize menuData
        gui.createGUI();
    }

    public void createGUI() {
        frame = new JFrame("Order Entry System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a panel for menu items
        JPanel menuPanel = new JPanel();

        // Create a text area for entering orders
        orderTextArea = new JTextArea(10, 40);
        JScrollPane scrollPane = new JScrollPane(orderTextArea);

        // Create a panel for customer name and order submission
        JPanel controlPanel = new JPanel();
        customerNameField = new JTextField(20);
        JButton submitOrderButton = new JButton("Submit Order");
        submitOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitOrder();
            }
        });

        controlPanel.add(new JLabel("Customer Name: "));
        controlPanel.add(customerNameField);
        controlPanel.add(submitOrderButton);

        // Create buttons for each menu item
        JPanel itemsPanel = new JPanel();
        for (MenuItem menuItem : menuData) {
            JButton itemButton = new JButton(menuItem.getName() + " - $" + menuItem.getPrice());
            itemButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addToOrder(menuItem);
                }
            });
            itemsPanel.add(itemButton);
        }

        // Add components to the frame
        frame.add(menuPanel);
        frame.add(scrollPane);
        frame.add(controlPanel);
        frame.add(itemsPanel);

        // Set layout and size
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    private void submitOrder() {
        String customerName = customerNameField.getText();
        // You can insert the order into the database with customerName and selectedItems
        // Display a confirmation message to the cashier
        JOptionPane.showMessageDialog(null, "Order submitted for " + customerName + ". Total price: $" + totalOrderPrice);

        // Clear selected items, total price, and customer name
        selectedItems.clear();
        totalOrderPrice = 0.0;
        orderTextArea.setText("");
        customerNameField.setText("");
    }

    private void addToOrder(MenuItem menuItem) {
        selectedItems.add(menuItem);
        totalOrderPrice += menuItem.getPrice();
        orderTextArea.append(menuItem.getName() + " - $" + menuItem.getPrice() + "\n");
    }

    // MenuItem class to store menu items with name and price
    static class MenuItem {
        private String name;
        private double price;

        public MenuItem(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        @Override
        public String toString() {
            return name + " - $" + price;
        }
    }

    private static List<MenuItem> readMenuItemsFromCSV(String filename) {
      List<MenuItem> items = new ArrayList<>();
      try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
          String line;
          // Skip the header line
          br.readLine();
          while ((line = br.readLine()) != null) {
              String[] data = line.split(",");
              String name = data[1];
              double price = Double.parseDouble(data[7]); // Assuming price is in the eighth column
              items.add(new MenuItem(name, price));
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
      return items;
    }

    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();
        if (s.equals("Close")) {
            frame.dispose();
        }
    }
}
