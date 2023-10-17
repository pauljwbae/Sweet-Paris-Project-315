import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginApp extends JFrame {
    private JTextField idField;
    private JTextField passwordField;

    public LoginApp() {
        setTitle("Login Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create a welcome message as a JLabel
        JLabel welcomeLabel = new JLabel("Welcome to SWEET PARIS POS");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial",Font.BOLD, 25));
        welcomeLabel.setBorder(new EmptyBorder(20, 0, 30, 0));


        JPanel loginPanel = new JPanel(new GridLayout(3, 2));

        JLabel idLabel = new JLabel("ID:");
        idLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        idField = new JTextField(10);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        passwordField = new JTextField(10);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String password = passwordField.getText();

                try {
                    // System.out.println(id + " " + password);
                    Connection connection = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db", "csce315_970_03user", "fourfsd");
                    String query = "SELECT position FROM employees WHERE id = "+id+" AND password = '"+password+"'";
                    PreparedStatement statement = connection.prepareStatement(query);

                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        JOptionPane.showMessageDialog(null, "Login successful!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Login failed. Invalid credentials.");
                        idField.setText("");
                        passwordField.setText("");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "An error occurred while processing your request.");
                    idField.setText("");
                    passwordField.setText("");
                }
            }
        });

        // Create and open the POS page
        private void doCashier(String username) {
            String actualName = userToNameMap.get(username);
            POSCashier cashier = new POSCashier(actualName);
            cashier.setVisible(true);
        }

        private void doManager() {
            ManagerNavigation manager = new ManagerNavigation();
            manager.setVisible(true);
        }


        loginPanel.add(idLabel);
        loginPanel.add(idField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel());
        loginPanel.add(loginButton);

        // Add the welcome message at the top
        add(welcomeLabel, BorderLayout.NORTH);
        add(loginPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        // Load the PostgreSQL JDBC driver
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unable to load the PostgreSQL JDBC driver.");
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> new LoginApp());
    }
}