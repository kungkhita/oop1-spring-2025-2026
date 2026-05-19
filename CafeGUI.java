package cafemanagementsystem.gui;
import cafemanagementsystem.entity.Cafe;
import cafemanagementsystem.fileio.CafeFileIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;

public class CafeGUI extends JFrame {
    private JTextField itemIdField;
    private JTextField itemNameField;
    private JTextField priceField;
    private JTextField categoryField;
    private JTextField searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public CafeGUI() {
        setTitle("Cafe Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));

        JPanel searchPanel = new JPanel(new BorderLayout(6, 6));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search (by Item ID or Item Name)"));
        searchField = new JTextField();
        JButton searchBtn = new JButton("Search");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Cafe Details"));

        inputPanel.add(new JLabel(" Item ID (exactly 8 digits):"));
        itemIdField = new JTextField();
        inputPanel.add(itemIdField);

        inputPanel.add(new JLabel("Item Name:"));
        itemNameField = new JTextField();
        inputPanel.add(itemNameField);

        inputPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        inputPanel.add(priceField);

        inputPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        inputPanel.add(categoryField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton viewAllBtn = new JButton("View All");
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        buttonPanel.add(viewAllBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);

        JPanel topPanel = new JPanel(new BorderLayout(6, 6));
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.NORTH);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        String[] columns = { "Item ID", "Item Name", "Price", "Category" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(22);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Cafe Records"));
        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        addBtn.addActionListener(e -> addCafe());
        updateBtn.addActionListener(e -> updateCafe());
        deleteBtn.addActionListener(e -> deleteCafe());
        searchBtn.addActionListener(e -> searchCafe());
        viewAllBtn.addActionListener(e -> {
            searchField.setText("");
            viewAll();
        });
        clearBtn.addActionListener(e -> clearFields());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                itemIdField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
                itemNameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
                priceField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
                categoryField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
            }
        });

        try {
            CafeFileIO.createFileIfNotExists();
        } catch (IOException ex) {
            showError("Error creating file: " + ex.getMessage());
        }
        viewAll();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private boolean isValidId(String itemId) {
        if (itemId.isEmpty()) {
            showError("Item ID is required!");
            return false;
        }
        if (!itemId.matches("\\d{8}")) {
            showError("Item ID must be exactly 8 digits (numbers only).\n"
                    + "Minimum: 8 digits, Maximum: 8 digits.");
            return false;
        }
        return true;
    }

    private boolean isValidAllFields(String itemId, String itemName, String price, String category) {
        if (itemName.isEmpty() || price.isEmpty() || category.isEmpty()) {
            showError("All fields are required!");
            return false;
        }
        if (!isValidId(itemId))
            return false;
        if (itemName.contains(",") || price.contains(",") || category.contains(",")) {
            showError("Commas are not allowed in any field!");
            return false;
        }
        try {
            Integer.parseInt(price);
        } catch (NumberFormatException ex) {
            showError("Price must be a number!");
            return false;
        }
        return true;
    }

    private void addCafe() {
        String itemId = itemIdField.getText().trim();
        String itemName = itemNameField.getText().trim();
        String price = priceField.getText().trim();
        String category = categoryField.getText().trim();

        if (!isValidAllFields(itemId, itemName, price, category))
            return;

        if (CafeFileIO.itemIdExists(itemId)) {
            showError("Duplicate ID! A Item with ID " + itemId + " already exists.");
            return;
        }

        try {
            CafeFileIO.addCafe(new Cafe(itemId, itemName, price, category));
            showInfo("Item added successfully!");
            clearFields();
            viewAll();
        } catch (IOException ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    private void updateCafe() {
        String itemId = itemIdField.getText().trim();
        String itemName = itemNameField.getText().trim();
        String price = priceField.getText().trim();
        String category = categoryField.getText().trim();

        if (!isValidAllFields(itemId, itemName, price, category))
            return;

        try {
            boolean updated = CafeFileIO.updateCafe(
                    new Cafe(itemId, itemName, price, category));
            if (updated) {
                showInfo("Item updated successfully!");
                clearFields();
                viewAll();
            } else {
                showError("Item ID not found!");
            }
        } catch (IOException ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    private void deleteCafe() {
        String itemId = itemIdField.getText().trim();
        if (!isValidId(itemId))
            return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Item ID: " + itemId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION)
            return;

        try {
            boolean deleted = CafeFileIO.deleteCafe(itemId);
            if (deleted) {
                showInfo("Item deleted successfully!");
                clearFields();
                viewAll();
            } else {
                showError("Item ID not found!");
            }
        } catch (IOException ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    private void searchCafe() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            showError("Enter Item ID or Item Name to search!");
            return;
        }
        Object[][] results = CafeFileIO.searchCafes(keyword);
        tableModel.setRowCount(0);
        for (int i = 0; i < results.length; i++) {
            tableModel.addRow(results[i]);
        }
        if (results.length == 0)
            showInfo("No matching item found.");
    }

    private void viewAll() {
        Object[][] rows = CafeFileIO.getAllCafes();
        tableModel.setRowCount(0);
        for (int i = 0; i < rows.length; i++) {
            if (rows[i][0] != null)
                tableModel.addRow(rows[i]);
        }
    }

    private void clearFields() {
        itemIdField.setText("");
        itemNameField.setText("");
        priceField.setText("");
        categoryField.setText("");
        searchField.setText("");
        table.clearSelection();
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
