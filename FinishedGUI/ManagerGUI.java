import javax.swing.*;  
import java.awt.*;
import java.awt.event.*;  
import java.util.Date;    
import java.text.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class ManagerGUI extends JFrame {
    // public ManagerNavigation(){
    //     JPanel panel = new JPanel(new BorderLayout());

    //     JPanel headerPanel = new JPanel(new BorderLayout());

    //     JButton orderButton = new JButton("Ordering Page"); // button leading to ordering page
    //     orderButton.setFont(new Font("Arial", Font.BOLD, 15));

    //     orderButton.addActionListener(new ActionListener() {
    //         @Override
    //         public void actionPerformed(ActionEvent e) {
    //             dispose();
    //             openPOSCashier();
    //         }
    //     });

    //     JLabel timeLabel = new JLabel(); //shows time bar at top right corner of page
    //     timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    //     timeLabel.setOpaque(true);
    //     timeLabel.setForeground(Color.black);
    //     timeLabel.setBackground(Color.decode("#D3D3D3"));
    //     timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));


    //     JTabbedPane tabbedPane = new JTabbedPane(); //manager's nav tabs

    //     // chaewon write code for these 2 panels -- order history && inventory
    //     // JPanel orderHistoryPanel = new OrderHistory(); 
    //     // tabbedPane.addTab("Order History", orderHistoryPanel);

    //     JPanel inventoryPanel = new Inventory();
    //     tabbedPane.addTab("Inventory", inventoryPanel);

    //      // I write code for this panel
    //     JPanel salesPanel = new SalesTrends();
    //     //System.out.println(salesPanel.getDates());
    //     tabbedPane.addTab("Sales/Trends", salesPanel);
    //     tabbedPane.setFont(new Font("Arial", Font.BOLD, 25));

    //     // apply custom tab UI for tabs navigation
    //     // tabbedPane.setUI(new CustomTabbedPaneUI()); 

    //     //making sure that the size of the frame is size of the screen
    //     //everything else should be sized accordingly
    //     Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
    //     setSize(screenSize.width, screenSize.height);
    //     timeLabel.setPreferredSize(new Dimension(screenSize.width, 30));
    //     panel.setPreferredSize(new Dimension(screenSize.width, screenSize.height));
    //     timeLabel.setFont(new Font("Arial", Font.BOLD, 15));

    //     //code to get the clock to work
    //     final DateFormat timeFormat = new SimpleDateFormat("hh:mm");
    //     ActionListener timerListener = new ActionListener() {
    //         public void actionPerformed(ActionEvent e) {
    //             Date date = new Date();
    //             String time = timeFormat.format(date);
    //             timeLabel.setText(time);
    //         }
    //     };
    //     Timer timer = new Timer(1000, timerListener);
    //     timer.setInitialDelay(0);
    //     timer.start();

    //     //add all elements to their parent
    //     headerPanel.add(orderButton, BorderLayout.WEST);
    //     headerPanel.add(timeLabel, BorderLayout.EAST);
    //     panel.add(headerPanel, BorderLayout.NORTH);
    //     panel.add(tabbedPane);
    //     add(panel);

    //     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //     pack();
    //     setVisible(true);
    // }

    // // opens ordering page
    // private void openPOSCashier() {
    //     POSCashier managerOrder = new POSCashier("Manager");
    //     managerOrder.setVisible(true);
    // }

    // public static void main(String[] args) {
    //     //Schedule a job for the event-dispatching thread:
    //     //creating and showing this application's GUI.
    //     javax.swing.SwingUtilities.invokeLater(new Runnable() {
    //         public void run() {
    //             JFrame here = new ManagerNavigation();
    //         }
    //     });
    // }
}