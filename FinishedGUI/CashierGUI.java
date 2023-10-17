import javax.swing.*;
import javax.swing.border.Border;
import java.sql.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class CashierGUI extends JFrame {
    private String name; // for cashier name in top left

    private DefaultListModel<String> orderListModel;
    private JLabel discountDisplayLabel;
    private JLabel taxDisplayLabel;
    private JLabel subtotalDisplayLabel;
    private JLabel totalDisplayLabel;
    private JLabel chargeDisplayLabel;
    private JList<String> orderList;
    private Vector<String> orderDetails = new Vector<String>();
    private Vector<Double> subtotals = new Vector<Double>();
    private double subtotal = 0.0;
    private double discount = 0.0;
    private double totalCharge = 0.0;
    private int orderNumber = 0;

    private boolean isManager = false;

    // gets names of drink or topping items from database to populate menu buttons
    private Vector<String> getTabItems(String category) {
        Vector<String> menuitems = new Vector<String>();

        // building the connection
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db", "csce315_970_03user", "fourfsd");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        menuitems.clear();
        try{
            // create a statement object
            Statement stmt = conn.createStatement();
            // create a SQL statement
            String sqlStatement = "SELECT name FROM items WHERE "+category+" = true";
            // send statement to DBMS
            ResultSet result = stmt.executeQuery(sqlStatement);
            while (result.next()) {
                String item = result.getString(1);
                menuitems.add(item);
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(null,"Error accessing Database.");
        }

        return menuitems;
    }

    // gets item prices from database
    private Double getItemPrice(Vector<String> item) {

        // building the connection
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db", "csce315_970_03user", "fourfsd");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        double price = 0.0;
        try{
            // create a statement object
            Statement stmt = conn.createStatement();
            // create a SQL statement
            String sqlStatement = "SELECT price FROM menu WHERE id = '" + item.firstElement() + "'";

            // send statement to DBMS
            ResultSet result = stmt.executeQuery(sqlStatement);
            while (result.next()) {
                price += result.getDouble("price");
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(null,"Error accessing Database.");
        }
        
        return price;
    }
    
    public CashierGUI(String name) {
        this.name = name;

        // // for manager view only things later
        // if(name.equals("Manager")) {
        //     isManager = true;
        // }

        // setting up the frame
        setTitle("Point of Sale System");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
        setSize(screenSize.width, screenSize.height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // creating header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setOpaque(true);
        headerPanel.setForeground(Color.black);
        headerPanel.setBackground(Color.decode("#D3D3D3"));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(7, 25, 7, 25));

        // panel to hold logout button and cashier on left side of header
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(true);
        leftPanel.setForeground(Color.black);
        leftPanel.setBackground(Color.decode("#D3D3D3"));

        // logout button that brings user back to login screen
        JButton logoutButton = new JButton();
        logoutButton.setFont(new Font("Arial", Font.BOLD, 15));
        logoutButton.setText("Log Out");
        leftPanel.add(logoutButton);

        // when clicked, option pane pops up to confirm the logout process
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to log out?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (choice == JOptionPane.YES_OPTION) {
                        dispose();
                        openLoginPage();
                    }
            }
        });

        // if manager is logged in, show option to open manager view
        if(isManager) {
            JButton returnButton = new JButton();
            returnButton.setFont(new Font("Arial", Font.BOLD, 15));
            returnButton.setText("Enter Manager View");
            leftPanel.add(returnButton);
            returnButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    // openManagerView();
                }
            });
        } else { // cashier name if manager not logged in
            JLabel cashierLabel = new JLabel(name);
            cashierLabel.setFont(new Font("Arial", Font.BOLD, 15));
            cashierLabel.setHorizontalAlignment(SwingConstants.LEFT);
            leftPanel.add(cashierLabel);
        }

        headerPanel.add(leftPanel, BorderLayout.WEST);

        // title in center of header
        JLabel titleLabel = new JLabel("Enter Sale");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // time on right side of header
        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.BOLD, 15));
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        headerPanel.add(timeLabel, BorderLayout.EAST);

        // timer updates every second
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
            timeLabel.setText(dateFormat.format(new Date()));
        });
        timer.start();

        // add the header panel to top of frame
        add(headerPanel, BorderLayout.NORTH);

        
        // create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // add the tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        tabs.setFont(new Font("Arial", Font.BOLD, 18));
        tabs.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // changing the ui of tabs
        tabs.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) { // color when active
                    g.setColor(Color.decode("#FFAEAE"));
                    g.fillRect(x, y, w, h);
                }
                else { // color when not active
                    g.setColor(Color.decode("#D3D3D3"));
                    g.fillRect(x, y, w, h);
                }
            }

            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                // customize the tab height to be 50
                return 50;
            }

            @Override
            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                // customize the tab width
                int widthTabs = screenSize.width * 3 / 4;
                return (widthTabs/10) - 5;
            }
        });

        // create and add tabs of drink categories to the JTabbedPane
        JPanel tab1 = tabClassic();
        JPanel tab2 = tabMilkTea();
        JPanel tab3 = tabPunch();
        JPanel tab4 = tabMilkCap();
        JPanel tab5 = tabYogurt();
        JPanel tab6 = tabSlush();
        JPanel tab7 = tabMilkStrike();
        JPanel tab8= tabEspresso();
        JPanel tab9 = tabSeasonal();
        JPanel tab10 = tabNewDrinks();

        tabs.addTab("Classic", tab1);
        tabs.addTab("Milk Tea", tab2);
        tabs.addTab("Punch", tab3);
        tabs.addTab("Milk Cap", tab4);
        tabs.addTab("Yogurt", tab5);
        tabs.addTab("Slush", tab6);
        tabs.addTab("Milk Strike", tab7);
        tabs.addTab("Espresso", tab8);
        tabs.addTab("Seasonal", tab9);
        tabs.addTab("New Drinks", tab10);

        // add the JTabbedPane to the frame's content pane
        getContentPane().add(tabs);
        setLocationRelativeTo(null);
        add(mainPanel, BorderLayout.WEST);


        // order panel set up
        JPanel orderPanel = new JPanel();
        orderPanel.setSize(320, screenSize.height);
        orderPanel.setPreferredSize(new Dimension(300, screenSize.height)); 
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        orderPanel.setBackground(Color.white);
        
        // label set up that says Current Sale at top of panel
        JLabel orderPanelLabel = new JLabel();
        orderPanelLabel.setPreferredSize(new Dimension(orderPanel.getWidth(), orderPanel.getHeight()/16));
        orderPanelLabel.setText("Current Sale");
        orderPanelLabel.setFont(new Font("Arial", Font.BOLD, 20));
        orderPanelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // list model hold each drink that is ordered
        orderListModel = new DefaultListModel<>();
        orderList = new JList<>(orderListModel);
        orderList.setFont(new Font("Arial", Font.PLAIN, 13));
        orderList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            // apply a LineBorder to the label
            Border border = BorderFactory.createLineBorder(Color.BLACK);

            // wrap the text in HTML tags to enable HTML rendering
            label.setText("<html>" + label.getText() + "</html>");

            // add the border to the label
            label.setBorder(border);

            return label;
        }
        });

        // mouse listener that allows user to click on order list to delete specific drink
        orderList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && !orderList.isSelectionEmpty()) {
                    int selectedIndex = orderList.locationToIndex(e.getPoint());
                    if (selectedIndex >= 0) {
                        // display a dialog to ask the user if they want to delete
                        int choice = JOptionPane.showOptionDialog(
                                null,
                                "Do you wish to delete the selected order?",
                                "Order Options",
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                new String[]{"Yes", "No"},
                                "Delete");

                        if (choice == 0) { // delete item
                            orderListModel.remove(selectedIndex);
                            updateTransactionDetails(selectedIndex);
                        }
                    }
                }
            }
        });

        // add the order list to the panel
        orderPanel.add(orderList);

        // border used in transaction panel
        Border blackline = BorderFactory.createLineBorder(Color.black);
        // set up order scroll
        orderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane orderScrollPane = new JScrollPane(orderList);
        orderScrollPane.setPreferredSize(new Dimension(orderPanel.getWidth(), orderPanel.getHeight()/2));
        orderScrollPane.setOpaque(true);
        orderScrollPane.setBorder(blackline);
        
        // transaction panel set up
        JPanel transactionPanel = new JPanel();
        transactionPanel.setLayout(new BoxLayout(transactionPanel, BoxLayout.Y_AXIS));
        transactionPanel.setSize(orderPanel.getWidth(), (orderPanel.getHeight()*3)/5);
        transactionPanel.setPreferredSize(new Dimension(orderPanel.getWidth(), (orderPanel.getHeight()*3)/5));
        transactionPanel.setBackground(Color.white);
        transactionPanel.setFont(new Font("Arial", Font.PLAIN, 15));

        // panel for adding discount set up
        JPanel addDiscountPanel = new JPanel();
        addDiscountPanel.setLayout(new BoxLayout(addDiscountPanel, BoxLayout.X_AXIS));
        addDiscountPanel.setSize(orderPanel.getWidth(), (2*transactionPanel.getHeight())/10);
        addDiscountPanel.setPreferredSize(new Dimension(orderPanel.getWidth(), (2*transactionPanel.getHeight())/10));
        addDiscountPanel.setMaximumSize(new Dimension(orderPanel.getWidth(), (2*transactionPanel.getHeight())/10));
        addDiscountPanel.setBorder(blackline);
        addDiscountPanel.setBackground(Color.white);
       
        // label for add discount
        JLabel addDiscountLabel = new JLabel();
        addDiscountLabel.setText("Add Discount: ");
        addDiscountLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        // outer div for the buttons
        JPanel discountBtnsPanel = new JPanel();
        discountBtnsPanel.setSize(addDiscountPanel.getWidth(), addDiscountPanel.getHeight() - 20);
        discountBtnsPanel.setPreferredSize(new Dimension(addDiscountPanel.getWidth(), addDiscountPanel.getHeight() - 20));
        discountBtnsPanel.setMaximumSize(new Dimension(addDiscountPanel.getWidth(), addDiscountPanel.getHeight() - 20));
        discountBtnsPanel.setBackground(Color.white);
        //discountBtnsPanel.setFont(new Font("Arial", Font.PLAIN, 15));

        // 2 rows, 2 columns of buttons
        discountBtnsPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // buttons to indicate what percent discount is applied
        JButton noDiscBtn = new JButton("None", null);
        noDiscBtn.setSize(addDiscountPanel.getWidth()/5, addDiscountPanel.getHeight() - 20);
        noDiscBtn.setPreferredSize(new Dimension(addDiscountPanel.getWidth()/5, addDiscountPanel.getHeight() - 20));
        noDiscBtn.setMaximumSize(new Dimension(addDiscountPanel.getWidth()/5, addDiscountPanel.getHeight() - 20));
        noDiscBtn.setFont(new Font("Arial", Font.PLAIN, 15));

        // set grid position to be 2nd column first row
        c.gridwidth = 1;
        c.weightx = 0.2;
        discountBtnsPanel.add(noDiscBtn, c);

        JButton tenDiscBtn = new JButton("10%", null);
        tenDiscBtn.setSize(addDiscountPanel.getWidth()/5, addDiscountPanel.getHeight() - 20);
        tenDiscBtn.setPreferredSize(new Dimension(addDiscountPanel.getWidth()/5, addDiscountPanel.getHeight() - 20));
        tenDiscBtn.setMaximumSize(new Dimension(addDiscountPanel.getWidth()/5, addDiscountPanel.getHeight() - 20));
        tenDiscBtn.setFont(new Font("Arial", Font.PLAIN, 15));
        // set grid position to be 2nd column 1st row
        c.gridx = 1;
        c.gridwidth = 1;
        c.weightx = 0.6;
        discountBtnsPanel.add(tenDiscBtn, c);

        JButton fifteenDiscBtn = new JButton("15%", null);
        fifteenDiscBtn.setSize(addDiscountPanel.getWidth()/5, addDiscountPanel.getHeight() - 20);
        fifteenDiscBtn.setPreferredSize(new Dimension(addDiscountPanel.getWidth()/5, addDiscountPanel.getHeight() - 20));
        fifteenDiscBtn.setMaximumSize(new Dimension(addDiscountPanel.getWidth()/5, addDiscountPanel.getHeight() - 20));
        fifteenDiscBtn.setFont(new Font("Arial", Font.PLAIN, 15));
        // set grid position to be 1st column second row
        c.gridy = 1;
        c.gridx = 0;
        c.gridwidth = 1;
        c.weightx = 0.6;
        discountBtnsPanel.add(fifteenDiscBtn, c);

        JButton twentyDiscBtn = new JButton("20%", null);
        twentyDiscBtn.setSize(addDiscountPanel.getWidth()/5, addDiscountPanel.getHeight() - 20);
        twentyDiscBtn.setPreferredSize(new Dimension(addDiscountPanel.getWidth()/5, addDiscountPanel.getHeight() - 20));
        twentyDiscBtn.setMaximumSize(new Dimension(addDiscountPanel.getWidth()/5, addDiscountPanel.getHeight() - 20));
        twentyDiscBtn.setFont(new Font("Arial", Font.PLAIN, 15));
        // set grid position to be 2nd column, 2nd row
        c.gridy = 1;
        c.gridx = 1;
        c.gridwidth = 1;
        c.weightx = 0.6;
        discountBtnsPanel.add(twentyDiscBtn, c);
        // ^^ all discount buttons added to the panel

        // add up all components to create add discount panel
        addDiscountPanel.add(addDiscountLabel);
        addDiscountPanel.add(discountBtnsPanel);

        // panel to show subtotal
        JPanel subtotalPanel = new JPanel();
        subtotalPanel.setLayout(new BoxLayout(subtotalPanel, BoxLayout.X_AXIS));
        subtotalPanel.setSize(orderPanel.getWidth(), transactionPanel.getHeight()/10);
        subtotalPanel.setMaximumSize(new Dimension(orderPanel.getWidth(), transactionPanel.getHeight()/10));
        subtotalPanel.setPreferredSize(new Dimension(orderPanel.getWidth(), transactionPanel.getHeight()/10));
        subtotalPanel.setBorder(blackline);
        subtotalPanel.setBackground(Color.white);

        // subtotal label
        JLabel subtotalLabel = new JLabel();
        subtotalLabel.setText("Subtotal: ");
        subtotalLabel.setHorizontalAlignment(SwingConstants.LEFT);
        subtotalLabel.setSize(subtotalPanel.getWidth()/2, subtotalPanel.getHeight());
        subtotalLabel.setPreferredSize(new Dimension(subtotalPanel.getWidth()/2, subtotalPanel.getHeight()));
        subtotalLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        // subtotal actual value displayed here
        subtotalDisplayLabel = new JLabel("$0.00");
        subtotalDisplayLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        subtotalDisplayLabel.setSize(subtotalPanel.getWidth()/2, subtotalPanel.getHeight());
        subtotalDisplayLabel.setPreferredSize(new Dimension(subtotalPanel.getWidth()/2, subtotalPanel.getHeight()));
        subtotalDisplayLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        // add all components to make subtotal panel
        subtotalPanel.add(subtotalLabel);
        subtotalPanel.add(subtotalDisplayLabel);

        // discount display panel
        JPanel discountPanel = new JPanel();
        discountPanel.setLayout(new BoxLayout(discountPanel, BoxLayout.X_AXIS));
        discountPanel.setSize(orderPanel.getWidth(), transactionPanel.getHeight()/10);
        discountPanel.setPreferredSize(new Dimension(orderPanel.getWidth(), transactionPanel.getHeight()/10));
        discountPanel.setMaximumSize(new Dimension(orderPanel.getWidth(), transactionPanel.getHeight()/10));
        discountPanel.setBorder(blackline);
        discountPanel.setBackground(Color.white);

        // discount label
        JLabel discountLabel = new JLabel();
        discountLabel.setText("Discount: ");
        discountLabel.setHorizontalAlignment(SwingConstants.LEFT);
        discountLabel.setSize(discountPanel.getWidth()/2, discountPanel.getHeight());
        discountLabel.setPreferredSize(new Dimension(discountPanel.getWidth()/2, discountPanel.getHeight()));
        discountLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        // actual value of discount applied to order
        discountDisplayLabel = new JLabel("$0.00");
        discountDisplayLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        discountDisplayLabel.setSize(discountPanel.getWidth()/2, discountPanel.getHeight());
        discountDisplayLabel.setPreferredSize(new Dimension(discountPanel.getWidth()/2, discountPanel.getHeight()));
        discountDisplayLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        // add all components to make the discount panel
        discountPanel.add(discountLabel);
        discountPanel.add(discountDisplayLabel);

        // action listeners to discount buttons to change discount applied to total
        noDiscBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                discount = 0;
                double calculatedDiscount = 0;
                double tax = subtotal * 0.0825;
                calculatedDiscount = subtotal * discount;
                totalCharge = subtotal + tax - calculatedDiscount;

                subtotalDisplayLabel.setText(String.format("%.2f", subtotal));
                discountDisplayLabel.setText(String.format("%.2f", calculatedDiscount));
                taxDisplayLabel.setText(String.format("%.2f", tax));
                totalDisplayLabel.setText(String.format("%.2f", totalCharge));
                chargeDisplayLabel.setText(String.format("%.2f", totalCharge));
            }
        });

        tenDiscBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                discount = 0.10;
                double tax = subtotal * 0.0825;
                double calculatedDiscount = subtotal * discount;
                totalCharge = subtotal + tax - calculatedDiscount;

                subtotalDisplayLabel.setText(String.format("%.2f", subtotal));
                discountDisplayLabel.setText(String.format("%.2f", calculatedDiscount));
                taxDisplayLabel.setText(String.format("%.2f", tax));
                totalDisplayLabel.setText(String.format("%.2f", totalCharge));
                chargeDisplayLabel.setText(String.format("%.2f", totalCharge));
            }
        });

        fifteenDiscBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                discount = 0.15;
                double tax = subtotal * 0.0825;
                double calculatedDiscount = subtotal * discount;
                totalCharge = subtotal + tax - calculatedDiscount;

                subtotalDisplayLabel.setText(String.format("%.2f", subtotal));
                discountDisplayLabel.setText(String.format("%.2f", calculatedDiscount));
                taxDisplayLabel.setText(String.format("%.2f", tax));
                totalDisplayLabel.setText(String.format("%.2f", totalCharge));
                chargeDisplayLabel.setText(String.format("%.2f", totalCharge));
            }
        });

        twentyDiscBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                discount = 0.20;
                double tax = subtotal * 0.0825;
                double calculatedDiscount = subtotal * discount;
                totalCharge = subtotal + tax - calculatedDiscount;

                subtotalDisplayLabel.setText(String.format("%.2f", subtotal));
                discountDisplayLabel.setText(String.format("%.2f", calculatedDiscount));
                taxDisplayLabel.setText(String.format("%.2f", tax));
                totalDisplayLabel.setText(String.format("%.2f", totalCharge));
                chargeDisplayLabel.setText(String.format("%.2f", totalCharge));
            }
        });

        // tax panel
        JPanel taxPanel = new JPanel();
        taxPanel.setLayout(new BoxLayout(taxPanel, BoxLayout.X_AXIS));
        taxPanel.setSize(orderPanel.getWidth(), transactionPanel.getHeight()/10);
        taxPanel.setPreferredSize(new Dimension(orderPanel.getWidth(), transactionPanel.getHeight()/10));
        taxPanel.setMaximumSize(new Dimension(orderPanel.getWidth(), transactionPanel.getHeight()/10));
        taxPanel.setBorder(blackline);
        taxPanel.setBackground(Color.white);

        // label for tax panel
        JLabel taxLabel = new JLabel();
        taxLabel.setText("Tax: ");
        taxLabel.setHorizontalAlignment(SwingConstants.LEFT);
        taxLabel.setSize(taxPanel.getWidth()/2, taxPanel.getHeight());
        taxLabel.setPreferredSize(new Dimension(taxPanel.getWidth()/2, taxPanel.getHeight()));
        taxLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        // actual tax value applied to order
        taxDisplayLabel = new JLabel("$0.00");
        taxDisplayLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        taxDisplayLabel.setSize(taxPanel.getWidth()/2, taxPanel.getHeight());
        taxDisplayLabel.setPreferredSize(new Dimension(taxPanel.getWidth()/2, taxPanel.getHeight()));
        taxDisplayLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        // add all components to make tax panel
        taxPanel.add(taxLabel);
        taxPanel.add(taxDisplayLabel);

        // panel shows total
        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new BoxLayout(totalPanel, BoxLayout.X_AXIS));
        totalPanel.setSize(orderPanel.getWidth(), transactionPanel.getHeight()/10);
        totalPanel.setPreferredSize(new Dimension(orderPanel.getWidth(), transactionPanel.getHeight()/10));
        totalPanel.setMaximumSize(new Dimension(orderPanel.getWidth(), transactionPanel.getHeight()/10));
        totalPanel.setBorder(blackline);
        totalPanel.setBackground(Color.white);

        // label for total panel
        JLabel totalLabel = new JLabel();
        totalLabel.setText("Total: ");
        totalLabel.setHorizontalAlignment(SwingConstants.LEFT);
        totalLabel.setSize(totalPanel.getWidth()/2, totalPanel.getHeight());
        totalLabel.setPreferredSize(new Dimension(totalPanel.getWidth()/2, totalPanel.getHeight()));
        totalLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        // displays actual total value
        totalDisplayLabel = new JLabel("$0.00");
        totalDisplayLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalDisplayLabel.setSize(totalPanel.getWidth()/2, totalPanel.getHeight());
        totalDisplayLabel.setPreferredSize(new Dimension(totalPanel.getWidth()/2, totalPanel.getHeight()));
        totalDisplayLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        // add all components to make the total panel
        totalPanel.add(totalLabel);
        totalPanel.add(totalDisplayLabel);

        // charge button sends current order to order history
        JButton chargeButton = new JButton();
        chargeButton.setLayout(new BoxLayout(chargeButton, BoxLayout.X_AXIS));
        chargeButton.setSize(orderPanel.getWidth(), transactionPanel.getHeight()/10);
        chargeButton.setPreferredSize(new Dimension(orderPanel.getWidth(), (2*transactionPanel.getHeight())/10));
        chargeButton.setMaximumSize(new Dimension(orderPanel.getWidth(), (2*transactionPanel.getHeight())/10));
        chargeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = String.format("$%.2f", totalCharge);
                if(totalCharge != 0.0) {
                    int choice = JOptionPane.showConfirmDialog(
                        null,
                        "Charge: " + message + "?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (choice == JOptionPane.YES_OPTION) {
                        JOptionPane.showMessageDialog(null, "Order sent!");

                        // Delete items from the orderList
                        orderListModel.clear();
                        updateOrderHistory();
                        updateTransactionDetails(-1);
                        orderDetails.clear();
                        orderNumber++;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No order in progress");
                }
            }
        });

        // label for charge button
        JLabel chargeLabel = new JLabel("Charge: ");
        chargeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        chargeLabel.setSize(chargeButton.getWidth()/2, chargeButton.getHeight());
        chargeLabel.setPreferredSize(new Dimension(chargeButton.getWidth()/2, chargeButton.getHeight()));
        chargeLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // shows actual total charge value
        chargeDisplayLabel = new JLabel("$0.00");
        chargeDisplayLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        chargeDisplayLabel.setSize(chargeButton.getWidth()/2, chargeButton.getHeight());
        chargeDisplayLabel.setPreferredSize(new Dimension(chargeButton.getWidth()/2, chargeButton.getHeight()));
        chargeDisplayLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // add all components to make charge panel
        chargeButton.add(chargeLabel);
        chargeButton.add(chargeDisplayLabel);
        chargeButton.setBorder(blackline);

        // add all components to make up the transaction panel
        transactionPanel.add(addDiscountPanel);
        transactionPanel.add(subtotalPanel);
        transactionPanel.add(discountPanel);
        transactionPanel.add(taxPanel);
        transactionPanel.add(totalPanel);
        transactionPanel.add(chargeButton);

        // add discount, notes, other details, and order total
        orderPanel.add(orderPanelLabel);
        orderPanel.add(orderScrollPane);
        orderPanel.add(transactionPanel);
        add(orderPanel, BorderLayout.EAST);
        setVisible(true);
    }

    // adding items to the order list and calculating totals
    private void addToOrder(String drink, String size, String sweetness, String ice, Vector<String> toppings, String notes) {
        String toppingsList = String.join(", ", toppings); // join toppings into a comma-separated list
        String orderItem = String.format("<b style='font-size:14px;'>%s</b><br>%s<br>%s Sweetness<br>%s<br>%s<br>%s",
            drink, size, sweetness, ice, toppingsList, notes);
        String order = drink + " with " + toppingsList;

        orderListModel.addElement(orderItem);
        orderDetails.add(order);

        Vector<String> drinkName = new Vector<String>();
        drinkName.add(drink);

        Vector<String> drinkToppings = new Vector<String>();
        for(String each : toppings) {
            drinkToppings.add(each);
        }

        double drinkCost = getItemPrice(drinkName, false);
        double addOnsCost = getItemPrice(drinkToppings, true);

        // add single drink cost to subtotals for deleting capability
        subtotals.add(drinkCost + addOnsCost);

        subtotal += drinkCost + addOnsCost;
        double tax = subtotal * 0.0825;
        double calculatedDiscount = subtotal * discount;
        totalCharge = subtotal + tax - calculatedDiscount;

        subtotalDisplayLabel.setText(String.format("%.2f", subtotal));
        discountDisplayLabel.setText(String.format("%.2f", calculatedDiscount));
        taxDisplayLabel.setText(String.format("%.2f", tax));
        totalDisplayLabel.setText(String.format("%.2f", totalCharge));
        chargeDisplayLabel.setText(String.format("%.2f", totalCharge));
    }

    // adds current order to order history in database
    private void updateOrderHistory() {
        try {
            Date currentDate = new Date();
            java.sql.Date sqlDate = new java.sql.Date(currentDate.getTime());
            java.sql.Time sqlTime = new java.sql.Time(currentDate.getTime());
            String order = String.join(", ", orderDetails);
            
            Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_01r_db",
                "csce331_901_caseykung17",
                "password");
            String sqlInsert = "INSERT INTO order_history(order_id, order_date, order_time, order_details, price, tip, payment_method) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sqlInsert);
            preparedStatement.setInt(1, orderNumber);
            preparedStatement.setDate(2, sqlDate);
            preparedStatement.setTime(3, sqlTime);
            preparedStatement.setString(4, order);
            preparedStatement.setDouble(5, totalCharge);
            preparedStatement.setDouble(6, 0.0);
            preparedStatement.setString(7, "card");
            preparedStatement.executeUpdate();
            conn.close();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(null,"Error inserting item into database.");
        }
      }
    
    // remove order from order list and updates totals
    private void updateTransactionDetails(int index) {
        // update the order totals after removing the order
        double newSubtotal, newTax, newDiscount, newTotalCharge;

        if(index == -1) {
            newSubtotal = 0.0;
            newTax = 0.0;
            newDiscount = 0.0;
            newTotalCharge = 0.0;
        
            subtotal = newSubtotal;
            totalCharge = newTotalCharge;
            subtotals.clear();
        } else {
            newSubtotal = 0.0;
            for(double each : subtotals) {
                newSubtotal += each;
            }
            newSubtotal -= subtotals.get(index);
            newTax = newSubtotal * 0.0825;
            newDiscount = newSubtotal * discount;
            newTotalCharge = newSubtotal + newTax - newDiscount;
        
            subtotal = newSubtotal;
            totalCharge = newTotalCharge;
            subtotals.remove(index);
        }

        // update the corresponding JLabels with the new values
        subtotalDisplayLabel.setText(String.format("%.2f", newSubtotal));
        discountDisplayLabel.setText(String.format("%.2f", newDiscount));
        taxDisplayLabel.setText(String.format("%.2f", newTax));
        totalDisplayLabel.setText(String.format("%.2f", newTotalCharge));
        chargeDisplayLabel.setText(String.format("%.2f", newTotalCharge));
}

    // helper method to create drink buttons with text
    private JButton createDrinkButton(String text) {
        JButton button = new JButton(text);

        // display drink modifications when clicked
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDrinkModifications(text);
            }
        });

        return button;
    }

    // display drink modifications in a popup
    private void showDrinkModifications(String drinkName) {
        // create and show the popup with modification buttons (all added dynamically)
        JFrame popupFrame = new JFrame("Customize Your " + drinkName);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        popupFrame.setSize(screenSize.width-700, screenSize.height-200);
        popupFrame.setLocationRelativeTo(this);

        JPanel modificationPanel = new JPanel(new BorderLayout());

        JPanel optionsPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        JButton addToOrderButton = new JButton("Add to Order");
        addToOrderButton.setFont(new Font("Arial", Font.BOLD, 20));

        // size options
        JPanel sizePanel = new JPanel(new GridLayout(0,1));
        JLabel sizeLabel = new JLabel("Size:");
        sizeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        sizePanel.add(sizeLabel);

        Vector<String> sizes = getItemNames("Size", true);

        for (String each : sizes) {
            JRadioButton button = new JRadioButton(each);
            sizePanel.add(button);
        }
        
        // sweetness options
        JPanel sweetnessPanel = new JPanel(new GridLayout(0, 1));
        JLabel sweetnessLabel = new JLabel("Sweetness:");
        sweetnessLabel.setFont(new Font("Arial", Font.BOLD, 20));
        sweetnessPanel.add(sweetnessLabel);

        Vector<String> sweetness = getItemNames("Sweetness", true);

        for (String each : sweetness) {
            JRadioButton button = new JRadioButton(each);
            sweetnessPanel.add(button);
        }

        // ice level options
        JPanel icePanel = new JPanel(new GridLayout(0, 1));
        JLabel iceLabel = new JLabel("Ice:");
        iceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        icePanel.add(iceLabel);

        Vector<String> ice = getItemNames("Ice", true);

        for (String each : ice) {
            JRadioButton button = new JRadioButton(each);
            icePanel.add(button);
        }

        // toppings options
        JPanel toppingsPanel = new JPanel(new GridLayout(0, 1));
        JLabel toppingsLabel = new JLabel("Toppings:");
        toppingsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        toppingsPanel.add(toppingsLabel);

        Vector<String> toppings = getItemNames("Topping", true);

        JCheckBox noneCheckBox = new JCheckBox("None");
        toppingsPanel.add(noneCheckBox);

        for (String topping : toppings) {
            JCheckBox checkBox = new JCheckBox(topping);
            toppingsPanel.add(checkBox);
        }

        Font buttonFont = new Font("Arial", Font.PLAIN, 15);

        // set the font for radio buttons in sizePanel
        Component[] sizeComponents = sizePanel.getComponents();
        for (Component component : sizeComponents) {
            if (component instanceof JRadioButton) {
                JRadioButton button = (JRadioButton) component;
                button.setFont(buttonFont);
            }
        }

        // set the font for radio buttons in sweetnessPanel
        Component[] sweetnessComponents = sweetnessPanel.getComponents();
        for (Component component : sweetnessComponents) {
            if (component instanceof JRadioButton) {
                JRadioButton button = (JRadioButton) component;
                button.setFont(buttonFont);
            }
        }

        // set the font for radio buttons in icePanel
        Component[] iceComponents = icePanel.getComponents();
        for (Component component : iceComponents) {
            if (component instanceof JRadioButton) {
                JRadioButton button = (JRadioButton) component;
                button.setFont(buttonFont);
            }
        }

        // set the font for checkboxes in toppingsPanel
        Component[] toppingComponents = toppingsPanel.getComponents();
        for (Component component : toppingComponents) {
            if (component instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) component;
                checkBox.setFont(buttonFont);
            }
        }

        // panel for cashiers to add notes about the order
        JPanel notesPanel = new JPanel();
        notesPanel.setLayout(new GridLayout(0, 1));

        // text field that makes it editable by user
        JTextField notesField = new JTextField("Notes: \n");
        notesField.setSize(notesPanel.getWidth(), notesPanel.getHeight());
        notesField.setPreferredSize(new Dimension(notesPanel.getWidth(), notesPanel.getHeight()));
        notesField.setFont(new Font("Arial", Font.PLAIN, 15));

        // add the field to the notes panel
        notesPanel.add(notesField);

        optionsPanel.add(sizePanel);
        optionsPanel.add(icePanel);
        optionsPanel.add(sweetnessPanel);
        optionsPanel.add(toppingsPanel);
        optionsPanel.add(notesPanel);
        modificationPanel.add(optionsPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(modificationPanel);
        popupFrame.add(scrollPane, BorderLayout.CENTER);

        addToOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedSize = getSelectedMod(sizePanel);
                String selectedSweetness = getSelectedMod(sweetnessPanel);
                String selectedIce = getSelectedMod(icePanel);
                Vector<String> selectedToppings = getSelectedToppings(toppingsPanel);
                String notes = notesField.getText();

                addToOrder(drinkName, selectedSize, selectedSweetness, selectedIce, selectedToppings, notes);
                popupFrame.dispose();
            }
        });

        modificationPanel.add(addToOrderButton, BorderLayout.SOUTH);
        popupFrame.add(modificationPanel);
        popupFrame.setVisible(true);
    }


    // get label of selected button in ButtonGroup
    private String getSelectedMod(JPanel itemsPanel) {

        Component[] components = itemsPanel.getComponents();
    
        for (Component component : components) {
            if (component instanceof JRadioButton) {
                JRadioButton button = (JRadioButton) component;
                if (button.isSelected()) {
                    return button.getText();
                }
            }
        }

        return "";
    }

    // get selected toppings
    private Vector<String> getSelectedToppings(JPanel toppingsPanel) {
        Vector<String> selectedToppings = new Vector<String>();

        Component[] components = toppingsPanel.getComponents();
    
        for (Component component : components) {
            if (component instanceof JCheckBox) {
                JCheckBox checkbox = (JCheckBox) component;
                if (checkbox.isSelected()) {
                    selectedToppings.add(checkbox.getText());
                }
            }
        }

        return selectedToppings;
    }

    private void updateDrink(String drinkName) {
        try {
            String price = JOptionPane.showInputDialog("Update Drink Price:");
            double drinkPrice = Double.parseDouble(price);
            
            Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_01r_db",
                "csce331_901_caseykung17",
                "password");
                
            Statement stmt = conn.createStatement();
            String sqlStatement = "UPDATE menu SET price = " + drinkPrice + " WHERE tea_type = '" + drinkName + "'";
            stmt.executeUpdate(sqlStatement);

            conn.close();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(null,"Error inserting item into database.");
        }
    }

    // adds drink to database
    private void updateMenu(String category) {
        try {
            String drinkName = JOptionPane.showInputDialog("Enter Drink Name:");
            String price = JOptionPane.showInputDialog("Enter Drink Price:");
            double drinkPrice = Double.parseDouble(price);
            
            Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_01r_db",
                "csce331_901_caseykung17",
                "password");
            String sqlInsert = "INSERT INTO menu(category, tea_type, price) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sqlInsert);
            preparedStatement.setString(1, category);
            preparedStatement.setString(2, drinkName);
            preparedStatement.setDouble(3, drinkPrice);
            preparedStatement.executeUpdate();
            conn.close();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(null,"Error inserting item into database.");
        }
    }

    // fill tab with drink buttons
    private void fillTab(JPanel tab, Vector<String> drinks, String category) {
        for (String each : drinks) {
            JButton button = createDrinkButton(each);
            button.setFont(new Font("Arial", Font.BOLD, 20));
            tab.add(button);
            if(isManager) {
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            int choice = JOptionPane.showConfirmDialog(
                                null,
                                "Would you like to update this drink?",
                                "Confirmation",
                                JOptionPane.YES_NO_OPTION
                            );
            
                            if (choice == JOptionPane.YES_OPTION) {
                                updateDrink(each);
                                refreshTab(tab, drinks, category);
                            }
                        }
                    }
                });
            }
        }

        // if manager, add a button to be able to add drinks (one per category)
        if(isManager) {
            JButton addDrink = new JButton("Add a drink...");
            addDrink.setFont(new Font("Arial", Font.BOLD, 20));
            addDrink.setBackground(Color.PINK);
            tab.add(addDrink);
            addDrink.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateMenu(category);
                    refreshTab(tab, getItemNames(category, false), category);
                }
            });
        }

        tab.revalidate();
        tab.repaint();
    }

    // refresh tab
    private void refreshTab(JPanel tab, Vector<String> drinks, String category) {
        tab.removeAll();

        fillTab(tab, drinks, category);
    }


    // all the drink tabs below
    // create drink buttons dynamically based on what is in each category in the database
    private JPanel tabClassic() {
        JPanel classicPanel = new JPanel(new GridLayout(0, 4, 20, 20)); // 4 columns, spacing of 10 pixels
        
        Vector<String> drinks = getItemNames("Classic", false);

        fillTab(classicPanel, drinks, "Classic");

        // add button panel to a scrollable pane
        JScrollPane scrollPane = new JScrollPane(classicPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        return classicPanel;
    }

    private JPanel tabMilkTea() {
        JPanel milkTeaPanel = new JPanel(new GridLayout(0, 4, 20, 20));

        Vector<String> drinks = getItemNames("Milk Tea", false);

        fillTab(milkTeaPanel, drinks, "Milk Tea");

        JScrollPane scrollPane = new JScrollPane(milkTeaPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        return milkTeaPanel;
    }

    private JPanel tabPunch() {
        JPanel punchPanel = new JPanel(new GridLayout(0, 4, 20, 20));

        Vector<String> drinks = getItemNames("Punch", false);

        fillTab(punchPanel, drinks, "Punch");

        JScrollPane scrollPane = new JScrollPane(punchPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        return punchPanel;
    }

    private JPanel tabMilkCap() {
        JPanel milkCapPanel = new JPanel(new GridLayout(0, 4, 20, 20));

        Vector<String> drinks = getItemNames("Milk Cap", false);

        fillTab(milkCapPanel, drinks, "Milk Cap");

        JScrollPane scrollPane = new JScrollPane(milkCapPanel);
        add(scrollPane, BorderLayout.CENTER);

        return milkCapPanel;
    }

    private JPanel tabYogurt() {
        JPanel yogurtPanel = new JPanel(new GridLayout(0, 4, 20, 20));

        Vector<String> drinks = getItemNames("Yogurt", false);

        fillTab(yogurtPanel, drinks, "Yogurt");

        JScrollPane scrollPane = new JScrollPane(yogurtPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        return yogurtPanel;
    }

    private JPanel tabSlush() {
      JPanel slushPanel = new JPanel(new GridLayout(0, 4, 20, 20));

        Vector<String> drinks = getItemNames("Slush", false);

        fillTab(slushPanel, drinks, "Slush");

        JScrollPane scrollPane = new JScrollPane(slushPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        return slushPanel;
    }

    private JPanel tabMilkStrike() {
      JPanel milkStrikePanel = new JPanel(new GridLayout(0, 4, 20, 20));

        Vector<String> drinks = getItemNames("Milk Strike", false);

        fillTab(milkStrikePanel, drinks, "Milk Strike");

        JScrollPane scrollPane = new JScrollPane(milkStrikePanel);
        add(scrollPane, BorderLayout.CENTER);
        
        return milkStrikePanel;
    }

    private JPanel tabEspresso() {
      JPanel espressoPanel = new JPanel(new GridLayout(0, 4, 20, 20));

        Vector<String> drinks = getItemNames("Espresso", false);

        fillTab(espressoPanel, drinks, "Espresso");

        JScrollPane scrollPane = new JScrollPane(espressoPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        return espressoPanel;
    }

    private JPanel tabSeasonal() {
      JPanel seasonalPanel = new JPanel(new GridLayout(0, 4, 20, 20));

        Vector<String> drinks = getItemNames("Seasonal", false);

        fillTab(seasonalPanel, drinks, "Seasonal");

        JScrollPane scrollPane = new JScrollPane(seasonalPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        return seasonalPanel;
    }

    private JPanel tabNewDrinks() {
      JPanel newDrinksPanel = new JPanel(new GridLayout(0, 4, 20, 20));

        Vector<String> drinks = getItemNames("New Drinks", false);

        fillTab(newDrinksPanel, drinks, "New Drinks");

        JScrollPane scrollPane = new JScrollPane(newDrinksPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        return newDrinksPanel;
    }

    // open the login page after logging out
    private void openLoginPage() {
        LoginApp login = new LoginApp();
        login.setVisible(true);
    }

    // open the manager view
    // private void openManagerView() {
    //     ManagerNavigation manager = new ManagerNavigation();
    //     manager.setVisible(true);
    // }


    public static void main(String[] args) {
        // Example usage
        SwingUtilities.invokeLater(() -> {
            CashierGUI CashierGUI = new CashierGUI("Cashier Name");
            CashierGUI.setVisible(true);
        });
    }
}