/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.libmansys;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.swing.table.DefaultTableModel;


public class Libmansys extends JFrame implements ActionListener {
    private JLabel label1, label2, label3, label4, label5, label6, label7;
    private JTextField textField1, textField2, textField3, textField4, textField5, textField6, textField7;
    private JButton addButton, viewButton, editButton, deleteButton, clearButton,exitButton;
    private JPanel panel;
    private ArrayList<String[]> books = new ArrayList<String[]>();

    public Libmansys() {
        setTitle("Library Management System");
        setSize(600, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        label1 = new JLabel("Book ID");
        label2 = new JLabel("Book Title");
        label3 = new JLabel("Author");
        label4 = new JLabel("Publisher");
        label5 = new JLabel("Year of Publication");
        label6 = new JLabel("ISBN");
        label7 = new JLabel("Number of Copies");

        textField1 = new JTextField(10);
        textField2 = new JTextField(20);
        textField3 = new JTextField(20);
        textField4 = new JTextField(20);
        textField5 = new JTextField(10);
        textField6 = new JTextField(20);
        textField7 = new JTextField(10);

        addButton = new JButton("Add");
        viewButton = new JButton("View");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");
        exitButton=new JButton("Exit");

        addButton.addActionListener(this);
        viewButton.addActionListener(this);
        editButton.addActionListener(this);
        deleteButton.addActionListener(this);
        clearButton.addActionListener(this);
        exitButton.addActionListener(this);

        panel = new JPanel(new GridLayout(10,2));
        panel.add(label1);
        panel.add(textField1);
        panel.add(label2);
        panel.add(textField2);
        panel.add(label3);
        panel.add(textField3);
        panel.add(label4);
        panel.add(textField4);
        panel.add(label5);
        panel.add(textField5);
        panel.add(label6);
        panel.add(textField6);
        panel.add(label7);
        panel.add(textField7);
        panel.add(addButton);
        panel.add(viewButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(clearButton);
        panel.add(exitButton);


        add(panel);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Create a PreparedStatement and execute an INSERT query
                String query = "INSERT INTO books (book_id, book_title, author, publisher, year_of_publication, isbn, num_copies) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, textField1.getText());
                preparedStatement.setString(2, textField2.getText());
                preparedStatement.setString(3, textField3.getText());
                preparedStatement.setString(4, textField4.getText());
                preparedStatement.setInt(5, Integer.parseInt(textField5.getText()));
                preparedStatement.setString(6, textField6.getText());
                preparedStatement.setInt(7, Integer.parseInt(textField7.getText()));
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Book added successfully");
                clearFields();
            } catch (SQLException r) {
                r.printStackTrace();
            }
        }
        else if (e.getSource() == viewButton) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String query = "SELECT * FROM books";
                    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                        ResultSet resultSet = preparedStatement.executeQuery();

                        // Create a JTable model
                        DefaultTableModel tableModel = new DefaultTableModel();
                        tableModel.addColumn("Book ID");
                        tableModel.addColumn("Book Title");
                        tableModel.addColumn("Author");
                        tableModel.addColumn("Publisher");
                        tableModel.addColumn("Year of Publication");
                        tableModel.addColumn("ISBN");
                        tableModel.addColumn("Number of Copies");

                        // Populate the table model with data from the database
                        while (resultSet.next()) {
                            Object[] rowData = {
                                    resultSet.getString("book_id"),
                                    resultSet.getString("book_title"),
                                    resultSet.getString("author"),
                                    resultSet.getString("publisher"),
                                    resultSet.getInt("year_of_publication"),
                                    resultSet.getString("isbn"),
                                    resultSet.getInt("num_copies")
                            };
                            tableModel.addRow(rowData);
                        }

                        // Create and display a JTable
                        JTable table = new JTable(tableModel);
                        JScrollPane scrollPane = new JScrollPane(table);
                        JFrame frame = new JFrame("View Books");
                        frame.add(scrollPane);
                        frame.setSize(800, 400);
                        frame.setVisible(true);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error viewing books.");
                }
            }

        else if (e.getSource() == editButton) {
            String bookID = JOptionPane.showInputDialog(this, "Enter book ID to edit:");
            if (bookID != null) { // Check if the user canceled the input dialog
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String query = "SELECT * FROM books WHERE book_id = ?";
                    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                        preparedStatement.setString(1, bookID);
                        ResultSet resultSet = preparedStatement.executeQuery();

                        if (resultSet.next()) {
                            // The book with the specified ID was found; now you can edit its details
                            String newTitle = JOptionPane.showInputDialog(this, "Enter new book title:", resultSet.getString("book_title"));
                            String newAuthor = JOptionPane.showInputDialog(this, "Enter new author:", resultSet.getString("author"));
                            String newPublisher = JOptionPane.showInputDialog(this, "Enter new publisher:", resultSet.getString("publisher"));
                            int newYear = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter new year of publication:", resultSet.getInt("year_of_publication")));
                            String newISBN = JOptionPane.showInputDialog(this, "Enter new ISBN:", resultSet.getString("isbn"));
                            int newCopies = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter new number of copies:", resultSet.getInt("num_copies")));

                            // Update the book's details in the database
                            String updateQuery = "UPDATE books SET book_title = ?, author = ?, publisher = ?, year_of_publication = ?, isbn = ?, num_copies = ? WHERE book_id = ?";
                            try (PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
                                updateStatement.setString(1, newTitle);
                                updateStatement.setString(2, newAuthor);
                                updateStatement.setString(3, newPublisher);
                                updateStatement.setInt(4, newYear);
                                updateStatement.setString(5, newISBN);
                                updateStatement.setInt(6, newCopies);
                                updateStatement.setString(7, bookID);

                                int rowsAffected = updateStatement.executeUpdate();
                                if (rowsAffected > 0) {
                                    JOptionPane.showMessageDialog(this, "Book edited successfully");
                                } else {
                                    JOptionPane.showMessageDialog(this, "Failed to edit the book");
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Book not found");
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error editing book.");
                }
            }
        }

        else if (e.getSource() == deleteButton) {
            String bookID = JOptionPane.showInputDialog(this, "Enter book ID to delete:");
            if (bookID != null) { // Check if the user canceled the input dialog
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String query = "DELETE FROM books WHERE book_id = ?";
                    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                        preparedStatement.setString(1, bookID);
                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Book deleted successfully");
                        } else {
                            JOptionPane.showMessageDialog(this, "Book not found");
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting book.");
                }
            }
        }

        else if (e.getSource() == clearButton) {
        clearFields();
        }

        else if (e.getSource() == exitButton) {
            System.exit(0);
        }
}
    private void clearFields() {
    textField1.setText("");
    textField2.setText("");
    textField3.setText("");
    textField4.setText("");
    textField5.setText("");
    textField6.setText("");
    textField7.setText("");
}

public static void main(String[] args) {
    new Libmansys();
}
}