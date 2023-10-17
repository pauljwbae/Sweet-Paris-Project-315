import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.*;

/*
  TODO:
  1) Change credentials for your own team's database
  2) Change SQL command to a relevant query that retrieves a small amount of data
  3) Create a JTextArea object using the queried data
  4) Add the new object to the JPanel p
*/

public class GUI extends JFrame implements ActionListener {
    static JFrame f;
    static JTextArea orderTextArea;
    static List<MenuItem> menuData;
    static JComboBox<MenuItem> menuItems;
    static List<MenuItem> selectedItems;
    static double totalOrderPrice;

    public static void main(String[] args)
    {
      // Read menu items from the CSV file
      menuData = readMenuItemsFromCSV("20items.csv");
      //Building the connection
      Connection conn = null;
      //TODO STEP 1 (see line 7)
      String database_name = "csce315_970_03db";
      String database_user = "csce315_970_03user";
      String database_password = "fourfsd";
      String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", database_name);
      try {
        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db", database_user, database_password);
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
      }
      JOptionPane.showMessageDialog(null,"Opened database successfully");

      String name = "";
      try{
        //create a statement object
        Statement stmt = conn.createStatement();
        //create a SQL statement
        //TODO Step 2 (see line 8)
        String sqlStatement = "select name from customers";
        //send statement to DBMS
        ResultSet result = stmt.executeQuery(sqlStatement);
        while (result.next()) {
          name += result.getString("name")+"\n";
        }
      } catch (Exception e){
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }

      // create a new frame
//       f = new JFrame("DB GUI");

//       // create a object
//       GUI s = new GUI();

//       // create a panel
//       JPanel p = new JPanel();

//       JButton b = new JButton("Close");

//       // add actionlistener to button
//       b.addActionListener(s);

//       //TODO Step 3 (see line 9)
      

//       //TODO Step 4 (see line 10)

//       // add button to panel
//       p.add(b);

//       // add panel to frame
//       f.add(p);

//       // set the size of frame
//       f.setSize(400, 400);

//       f.setVisible(true);

//       //closing the connection
//       try {
//         conn.close();
//         JOptionPane.showMessageDialog(null,"Connection Closed.");
//       } catch(Exception e) {
//         JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
//       }
//     }

//     // if button is pressed
//     public void actionPerformed(ActionEvent e)
//     {
//         String s = e.getActionCommand();
//         if (s.equals("Close")) {
//             f.dispose();
//         }
//     }
// }

        f = new JFrame("Order Entry System");

        // Create a panel
        JPanel p = new JPanel();

        // Create a text area for entering orders
        orderTextArea = new JTextArea(10, 40);
        JScrollPane scrollPane = new JScrollPane(orderTextArea);
        p.add(scrollPane);

        // Create a button to submit the order
        JButton submitButton = new JButton("Submit Order");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Retrieve the order text from the text area and process it
                String orderText = orderTextArea.getText();
                processOrder(orderText);
                // You can implement order processing logic here
                // For example, insert the order into the database
            }
        });
        p.add(submitButton);

        // Create a button to close the application
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                f.dispose();
            }
        });
        p.add(closeButton);

        // Add the panel to the frame
        f.add(p);

        // Set the size of the frame
        f.setSize(500, 300);

        // Make the frame visible
        f.setVisible(true);

        // Closing the connection
        try {
            conn.close();
            JOptionPane.showMessageDialog(null, "Connection Closed.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Connection NOT Closed.");
        }

        orderTextArea = new JTextArea(10, 40);
        JScrollPane sPane = new JScrollPane(orderTextArea);
        p.add(sPane);

        // Create a dropdown to select menu items
        menuItems = new JComboBox<>(menuData.toArray(new MenuItem[0]));
        p.add(menuItems);

        // Create a button to add the selected item to the order
        JButton addButton = new JButton("Add to Order");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MenuItem selectedItem = (MenuItem) menuItems.getSelectedItem();
                selectedItems.add(selectedItem);
                totalOrderPrice += selectedItem.getPrice();
                orderTextArea.append(selectedItem.getName() + " - $" + selectedItem.getPrice() + "\n");
            }
        });
        p.add(addButton);

        // Create a button to submit the order
        JButton subButton = new JButton("Submit Order");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Process the order, display or save it as needed
                JOptionPane.showMessageDialog(null, "Order submitted. Total price: $" + totalOrderPrice);
                // Clear selected items and total price
                selectedItems.clear();
                totalOrderPrice = 0.0;
                orderTextArea.setText("");
            }
        });
        p.add(subButton);
    }

    // Process the order entered by the cashier
    private static void processOrder(String orderText) {
        // You can implement order processing logic here
        // For example, parse the order and insert it into the database
        // Display a confirmation message to the cashier
        JOptionPane.showMessageDialog(null, "Order submitted: " + orderText);
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
    
    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();
        if (s.equals("Close")) {
            f.dispose();
        }
    }
}

