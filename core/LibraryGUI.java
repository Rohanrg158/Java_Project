package com.library.core;

import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

interface IUserInterface {
    void start(LibraryService service);
    void displayMessage(String message);
    void showError(String error);
    void refreshDataDisplay(List<LibraryItem> items, List<Person> members);
}

public class LibraryGUI extends JFrame implements IUserInterface {
    private LibraryService service;
    private JTable bookTable;
    private DefaultTableModel bookTableModel;
    private JTable memberTable;
    private DefaultTableModel memberTableModel;

    private JTextField memberIdField;
    private JTextField itemIdField;

    private JTextField addBookIdField;
    private JTextField addBookTitleField;
    private JTextField addBookAuthorField;
    private JTextField addMemberIdField;
    private JTextField addMemberNameField;
    private JTextField addStudentIdField;
    private JRadioButton isStudentRadio;
    private JRadioButton isMemberRadio;

    public LibraryGUI() {
        setTitle("Library Management System (Swing GUI)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout(10, 10));
    }

    private boolean isStringEmptyOrNull(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    public void start(LibraryService service) {
        this.service = service;

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("View & Operations", setupOperationsPanel());
        tabbedPane.addTab("Add Data", setupAddDataPanel());

        add(tabbedPane, BorderLayout.CENTER);

        refreshDataDisplay(service.getAllItems(), service.getAllMembers());

        setVisible(true);
    }

    private JPanel setupOperationsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        setupTables();

        JPanel controlPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Borrow/Return Operations"));

        memberIdField = new JTextField();
        itemIdField = new JTextField();
        JButton borrowButton = new JButton("Borrow Item");
        JButton returnButton = new JButton("Return Item");

        controlPanel.add(new JLabel("Member ID:"));
        controlPanel.add(memberIdField);
        controlPanel.add(new JLabel("Item ID:"));
        controlPanel.add(itemIdField);

        controlPanel.add(new JPanel());
        controlPanel.add(borrowButton);
        controlPanel.add(new JPanel());
        controlPanel.add(returnButton);

        borrowButton.addActionListener(e -> handleBorrow());
        returnButton.addActionListener(e -> handleReturn());

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6); // Books take 60%
        splitPane.setTopComponent(new JScrollPane(bookTable));
        splitPane.setBottomComponent(new JScrollPane(memberTable));

        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel setupAddDataPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel bookPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        bookPanel.setBorder(BorderFactory.createTitledBorder("Add New Book"));

        addBookIdField = new JTextField();
        addBookTitleField = new JTextField();
        addBookAuthorField = new JTextField();
        JButton addBookButton = new JButton("Add Book");

        bookPanel.add(new JLabel("Book ID (Int):"));
        bookPanel.add(addBookIdField);
        bookPanel.add(new JLabel("Title:"));
        bookPanel.add(addBookTitleField);
        bookPanel.add(new JLabel("Author:"));
        bookPanel.add(addBookAuthorField);
        bookPanel.add(new JPanel());
        bookPanel.add(addBookButton);

        addBookButton.addActionListener(e -> handleAddBook());
        mainPanel.add(bookPanel);

        JPanel memberPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        memberPanel.setBorder(BorderFactory.createTitledBorder("Add New Member"));

        addMemberIdField = new JTextField();
        addMemberNameField = new JTextField();
        addStudentIdField = new JTextField();
        JButton addMemberButton = new JButton("Add Member");

        isStudentRadio = new JRadioButton("Student");
        isMemberRadio = new JRadioButton("Generic Member", true);
        ButtonGroup group = new ButtonGroup();
        group.add(isStudentRadio);
        group.add(isMemberRadio);
        
        addStudentIdField.setEnabled(false);
        isStudentRadio.addActionListener(e -> addStudentIdField.setEnabled(true));
        isMemberRadio.addActionListener(e -> addStudentIdField.setEnabled(false));

        memberPanel.add(new JLabel("Member ID (Int):"));
        memberPanel.add(addMemberIdField);
        memberPanel.add(new JLabel("Name:"));
        memberPanel.add(addMemberNameField);
        memberPanel.add(isMemberRadio);
        memberPanel.add(isStudentRadio);
        memberPanel.add(new JLabel("Student ID (if Student):"));
        memberPanel.add(addStudentIdField);
        memberPanel.add(new JPanel());
        memberPanel.add(addMemberButton);

        addMemberButton.addActionListener(e -> handleAddMember());
        mainPanel.add(memberPanel);

        return mainPanel;
    }

    private void setupTables() {
        bookTableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Status"}, 0);
        bookTable = new JTable(bookTableModel);
        
        memberTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Type", "Details"}, 0);
        memberTable = new JTable(memberTableModel);
    }

    private void handleAddBook() {
        try {
            int id = Integer.parseInt(addBookIdField.getText());
            String title = addBookTitleField.getText();
            String author = addBookAuthorField.getText();

            if (isStringEmptyOrNull(title) || isStringEmptyOrNull(author)) {
                throw new Exception("Title and Author cannot be empty.");
            }
            if (service.isItemIdDuplicate(id)) {
                throw new Exception("Item ID " + id + " already exists. Please use a unique ID.");
            }

            Book newBook = new Book(id, title, author);
            service.addItem(newBook);
            displayMessage("Book added: " + title);
            refreshDataDisplay(service.getAllItems(), service.getAllMembers());

            addBookIdField.setText("");
            addBookTitleField.setText("");
            addBookAuthorField.setText("");

        } catch (NumberFormatException ex) {
            showError("Book ID must be a valid number.");
        } catch (Exception ex) {
            showError("Add Book Failed: " + ex.getMessage());
        }
    }

    private void handleAddMember() {
        try {
            int id = Integer.parseInt(addMemberIdField.getText());
            String name = addMemberNameField.getText();
            
            if (isStringEmptyOrNull(name)) {
                throw new Exception("Member name cannot be empty.");
            }
            if (service.isMemberIdDuplicate(id)) {
                throw new Exception("Member ID " + id + " already exists. Please use a unique ID.");
            }

            Person newMember;
            if (isStudentRadio.isSelected()) {
                String studentId = addStudentIdField.getText();
                if (isStringEmptyOrNull(studentId)) {
                    throw new Exception("Student ID cannot be empty.");
                }
                newMember = new Student(id, name, studentId);
            } else {
                newMember = new Member(id, name);
            }

            service.addMember(newMember);
            displayMessage("Member added: " + name);
            refreshDataDisplay(service.getAllItems(), service.getAllMembers());
            
            addMemberIdField.setText("");
            addMemberNameField.setText("");
            addStudentIdField.setText("");
            isMemberRadio.setSelected(true);

        } catch (NumberFormatException ex) {
            showError("Member ID must be a valid number.");
        } catch (Exception ex) {
            showError("Add Member Failed: " + ex.getMessage());
        }
    }

    private void handleBorrow() {
        try {
            int memberId = Integer.parseInt(memberIdField.getText());
            int itemId = Integer.parseInt(itemIdField.getText());

            service.borrowItem(memberId, itemId);
            displayMessage("Item " + itemId + " successfully borrowed.");
            refreshDataDisplay(service.getAllItems(), service.getAllMembers());
            memberIdField.setText("");
            itemIdField.setText("");

        } catch (NumberFormatException ex) {
            showError("Please enter valid numeric IDs for Member and Item.");
        } catch (Exception ex) {
            showError("Borrow Failed: " + ex.getMessage());
        }
    }

    private void handleReturn() {
        try {
            int itemId = Integer.parseInt(itemIdField.getText());

            service.returnItem(itemId);
            displayMessage("Item " + itemId + " successfully returned.");
            refreshDataDisplay(service.getAllItems(), service.getAllMembers());
            itemIdField.setText("");

        } catch (NumberFormatException ex) {
            showError("Please enter a valid numeric ID for the Item.");
        } catch (Exception ex) {
            showError("Return Failed: " + ex.getMessage());
        }
    }

    @Override
    public void displayMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showError(String error) {
        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void refreshDataDisplay(List<LibraryItem> items, List<Person> members) {
        bookTableModel.setRowCount(0);
        memberTableModel.setRowCount(0);

        for (LibraryItem item : items) {
            String status = item.isBorrowed() ? "BORROWED" : "Available";
            String author = "N/A";
            if (item instanceof Book) {
                author = ((Book)item).getAuthor();
            }

            bookTableModel.addRow(new Object[]{item.getId(), item.getTitle(), author, status});
        }

        for (Person person : members) {
            String details = "N/A";
            String memberType = person.getMemberType();
            if (person instanceof Student) {
                details = ((Student)person).getStudentId();
            }

            memberTableModel.addRow(new Object[]{person.getId(), person.getName(), memberType, details});
        }
    }

    public static class LibraryCore {
        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                try {
                    IDatabaseDAO dao = new DatabaseDAO();
                    LibraryService service = new LibraryService(dao);

                    Book book1 = new Book(101, "Java Programming", "J. Gosling");
                    Book book2 = new Book(102, "OOP Design Patterns", "E. Gamma");
                    Student student1 = new Student(501, "Alice Smith", "S12345");
                    Member member2 = new Member(502, "Bob Johnson");

                    service.addItem(book1);
                    service.addItem(book2);
                    service.addMember(student1);
                    service.addMember(member2);

                    LibraryGUI gui = new LibraryGUI();
                    gui.start(service);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Critical Startup Error: " + e.getMessage(), "Fatal Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            });
        }
    }
}