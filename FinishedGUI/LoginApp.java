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

/**
 * Represents a simple login application for a POS (Point of Sale) system.
 */
public class LoginApp extends JFrame {
    private JTextField idField;
    private JTextField passwordField;

    /**
     * Constructs the LoginApp GUI.
     */
    public LoginApp() {
        setTitle("Login Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create a welcome message as a JLabel
        JLabel welcomeLabel = new JLabel("Welcome to SWEET PARIS POS");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 25));
        welcomeLabel.setBorder(new EmptyBorder(20, 15, 30,15));

        JPanel loginPanel = new JPanel(new GridLayout(3, 2));

        JLabel idLabel = new JLabel("ID:");
        idLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        idField = new JTextField(10);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        passwordField = new JTextField(10);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            /**
             * Handles the login button click event.
             * @param e The action event.
             */
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String password = passwordField.getText();

                try {
                    Connection connection = DriverManager.getConnection(
                            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315_970_03db",
                            "csce315_970_03user",
                            "fourfsd"
                    );
                    String query = "SELECT position, name FROM employees WHERE id = " + id + " AND password = '" + password + "'";
                    PreparedStatement statement = connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        // System.out.print(resultSet.getString(2));
                        if ("manager".equals(resultSet.getString(1))) {
                            dispose();
                            doManager(resultSet.getString(2));
                        } else {
                            dispose();
                            doCashier(resultSet.getString(2));
                        }
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

    /**
     * Create and open the Cashier GUI.
     * @param name The name of the user.
     */
    private void doCashier(String name) {
        // CashierGUI cashier = new CashierGUI(name);
        // cashier.setVisible(true);
    }

    /**
     * Create and open the Manager GUI.
     * @param name The name of the user.
     */
    private void doManager(String name) {
        ManagerGUI manager = new ManagerGUI(name);
    }

    /**
     * The main entry point of the application.
     * @param args The command-line arguments.
     */
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
