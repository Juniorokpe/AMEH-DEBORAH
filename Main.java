import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Random;

public class Main extends JFrame {
        // =========================
        // COLORS
        // =========================
        private final Color SIDEBAR_BLUE = new Color(8, 52, 105);
        private final Color PRIMARY_BLUE = new Color(37, 99, 190);
        private final Color LIGHT_BLUE = new Color(235, 242, 253);
        private final Color GREEN = new Color(35, 180, 145);
        private final Color LIGHT_GREEN = new Color(235, 249, 244);
        private final Color LIGHT_YELLOW = new Color(255, 248, 226);
        private final Color TEXT_DARK = new Color(30, 41, 59);
        private final Color BORDER = new Color(215, 220, 228);
        private final Color BACKGROUND = new Color(248, 250, 253);

        // =========================
        // FORM COMPONENTS
        // =========================
        private static final String RECORDS_FILE = "student_records.txt";
        private JTextField surNameField;
        private JTextField firstNameField;
        private JTextField otherNameField;
        private JTextField matricField;
        private JComboBox<String> departmentBox;
        private String generatePassword = "";
        private JLabel emailLabel;
        private JLabel passwordLabel;

        private boolean passwordVisible = true;

        public Main() {
                setTitle("Automated Student Email Generation System");

                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                setSize(1500, 950);
                setMinimumSize(new Dimension(1100, 700));

                setLocationRelativeTo(null);

                createInterface();
        }
        private void showAdminLogin(){
            JDialog loginDialog = new JDialog(this, "Admin Login", true);
            loginDialog.setSize(350, 200);
            loginDialog.setLocationRelativeTo(this);
            loginDialog.setLayout(new GridLayout(3, 2, 10, 10));

            JTextField userField = new JTextField();
            JPasswordField passField = new JPasswordField();
            JButton loginBtn = new JButton("Login");
            JButton cancelBtn = new JButton("Cancel");

            loginDialog.add(new JLabel(" Username:"));
            loginDialog.add(userField);
            loginDialog.add(new JLabel(" Password:"));
            loginDialog.add(passField);
            loginDialog.add(loginBtn);
            loginDialog.add(cancelBtn);

            loginBtn.addActionListener(e -> {
                String username = userField.getText().trim();
                String password = new String(passField.getPassword());

                //CHANGE THE USERNAME AND PASSWORD HERE
                if (username.equals("admin") && password.equals("admin123")) {
                    loginDialog.dispose();
                    showAdminPanel();
                } else {
                    JOptionPane.showMessageDialog(loginDialog, "Incorrect Login Information", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            });
            cancelBtn.addActionListener(e -> loginDialog.dispose());
            loginDialog.setVisible(true);
        }

        private void showAdminPanel() {
    JFrame adminFrame = new JFrame("Admin Dashboard");
    adminFrame.setSize(900, 500); // Made wider so columns fit nicely
    adminFrame.setLocationRelativeTo(this);
    adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    // 1. Define the columns exactly as you requested
    String[] columnNames = {"Surname", "First Name", "Other Name", "Department", "Matric Number", "Email", "Password"};

    // 2. Create the table model and table
    DefaultTableModel model = new DefaultTableModel(columnNames, 0);
    JTable table = new JTable(model);
    table.setFont(new Font("Arial", Font.PLAIN, 14));
    table.setRowHeight(30);
    table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

    // 3. Load and parse the records from the text file into the table
    try {
        File file = new File(RECORDS_FILE);
        if (!file.exists()) {
            // No file, no records
        } else {
            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            String[] records = content.split("-------------------------");
            
            for (String record : records) {
                if (record.trim().isEmpty()) continue;
                
                // Create an empty row for this student
                String[] row = new String[7];
                
                // Break the record into lines (e.g., "Surname: Okpe")
                String[] lines = record.trim().split("\n");
                for (String line : lines) {
                    if (line.contains(": ")) {
                        String[] parts = line.split(": ", 2);
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        
                        // Fill the correct column based on the key
                        if (key.equals("Surname")) row[0] = value;
                        else if (key.equals("First Name")) row[1] = value;
                        else if (key.equals("Other Name")) row[2] = value;
                        else if (key.equals("Department")) row[3] = value;
                        else if (key.equals("Matric")) row[4] = value;
                        else if (key.equals("Email")) row[5] = value;
                        else if (key.equals("Password")) row[6] = value;
                    }
                }
                model.addRow(row); // Add the student as a new row
            }
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(adminFrame, "Error reading records: " + ex.getMessage());
    }

    JScrollPane scrollPane = new JScrollPane(table);
    adminFrame.add(scrollPane, BorderLayout.CENTER);

    // 4. Button Panel
    JPanel buttonPanel = new JPanel();

    JButton closeBtn = new JButton("Close");
    closeBtn.addActionListener(e -> adminFrame.dispose());
    buttonPanel.add(closeBtn);

    JButton deleteBtn = new JButton("Delete Selected");
    deleteBtn.setForeground(Color.RED);
    deleteBtn.addActionListener(e -> {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(adminFrame, "Please click on a row to select it, then click Delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(adminFrame, "Are you sure you want to delete this specific student?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(selectedRow); // Remove from Excel table

            // Rewrite the file with the remaining rows (preserving Excel format)
            try {
                FileWriter writer = new FileWriter(RECORDS_FILE);
                for (int i = 0; i < model.getRowCount(); i++) {
                    writer.write("Surname: " + model.getValueAt(i, 0) + "\n");
                    writer.write("First Name: " + model.getValueAt(i, 1) + "\n");
                    writer.write("Other Name: " + model.getValueAt(i, 2) + "\n");
                    writer.write("Department: " + model.getValueAt(i, 3) + "\n");
                    writer.write("Matric: " + model.getValueAt(i, 4) + "\n");
                    writer.write("Email: " + model.getValueAt(i, 5) + "\n");
                    writer.write("Password: " + model.getValueAt(i, 6) + "\n");
                    writer.write("-------------------------\n");
                }
                writer.close();
                JOptionPane.showMessageDialog(adminFrame, "Record deleted successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(adminFrame, "Error saving file: " + ex.getMessage());
            }
        }
    });
    buttonPanel.add(deleteBtn);

    JButton clearBtn = new JButton("Clear All Records");
    clearBtn.setForeground(Color.RED);
    clearBtn.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(adminFrame, "Are you sure you want to delete ALL saved records?", "Confirm Clear All", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.setRowCount(0); // Clear all rows from Excel table
            File file = new File(RECORDS_FILE);
            if (file.exists()) file.delete();
            JOptionPane.showMessageDialog(adminFrame, "All records have been cleared.");
        }
    });
    buttonPanel.add(clearBtn);

    adminFrame.add(buttonPanel, BorderLayout.SOUTH);
    adminFrame.setVisible(true);
}

        private JLabel createImageLabel(String imagePath, int width, int height) {
                try {
                        File file = new File(imagePath);
                        if (!file.exists()) {
                                return new JLabel("Image not found: " + imagePath);
                        }
                        java.awt.image.BufferedImage originalImage = ImageIO.read(file);
                        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        ImageIcon icon = new ImageIcon(scaledImage);
                        return new JLabel(icon);
                } catch (IOException e) {
                        System.out.println("Error loading image: " + e.getMessage());
                        return new JLabel("Image Error");
                }
        }

        private ImageIcon createIcon(String path, int width, int height) {
                ImageIcon original = new ImageIcon(path);
                Image scaled = original.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
        }

        // ============================================================
        // CREATE MAIN INTERFACE
        // ============================================================

        private void createInterface() {

                JPanel mainPanel = new JPanel(new BorderLayout());
                mainPanel.setBackground(BACKGROUND);

                // LEFT SIDEBAR
                mainPanel.add(createSidebar(), BorderLayout.WEST);

                // MAIN CONTENT
                mainPanel.add(createMainContent(), BorderLayout.CENTER);

                add(mainPanel);
        }

        // ============================================================
        // SIDEBAR
        // ============================================================

        private JPanel createSidebar() {

                JPanel sidebar = new JPanel(new BorderLayout());
                sidebar.setPreferredSize(new Dimension(285, 0));
                sidebar.setBackground(SIDEBAR_BLUE);

                // -----------------------------
                // Logo and title
                // -----------------------------

                JPanel top = new JPanel();
                top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
                top.setBackground(SIDEBAR_BLUE);
                top.setBorder(new EmptyBorder(30, 20, 20, 20));

                // SCHOOL LOGO
                JLabel logo = createImageLabel("C:\\Users\\Lenovo\\Desktop\\school_logo.jpeg\\", 200, 200);

                logo.setAlignmentX(Component.CENTER_ALIGNMENT);

                top.add(logo);
                top.add(Box.createVerticalStrut(50));

                JLabel title = new JLabel(
                                "<html><div style='text-align: center;'>"
                                                + "AUTOMATED<br>"
                                                + "STUDENT EMAIL<br>"
                                                + "GENERATION<br>"
                                                + "SYSTEM"
                                                + "</div></html>");

                title.setForeground(Color.WHITE);
                title.setFont(new Font("Arial", Font.BOLD, 20));
                title.setAlignmentX(Component.CENTER_ALIGNMENT);

                top.add(title);

                sidebar.add(top, BorderLayout.NORTH);

                // -----------------------------
                // Navigation
                // -----------------------------

                JPanel navigation = new JPanel();
                navigation.setLayout(new BoxLayout(navigation, BoxLayout.Y_AXIS));
                navigation.setBackground(SIDEBAR_BLUE);
                navigation.setBorder(new EmptyBorder(15, 15, 15, 15));

                JButton generateButton = createSidebarButton("Generate Email", true);
                generateButton.setIcon(createIcon("C:\\Users\\Lenovo\\Pictures\\home.jpg\\", 20, 20));
                generateButton.setIconTextGap(10);
                generateButton.addActionListener(e -> { /*Already on generate page */ });

                JButton aboutButton = createSidebarButton("About", false);
                aboutButton.setIcon(createIcon("C:\\Users\\Lenovo\\Pictures\\about.png\\", 20, 20));
                aboutButton.setIconTextGap(10);
                generateButton.addActionListener(e -> {
                });
                aboutButton.addActionListener(e -> {
                    JOptionPane.showMessageDialog(this,
                                "Automated Student Email Generation System\nBuilt entirely with Java Swing.",
                                "About",
                                JOptionPane.INFORMATION_MESSAGE);
                    });
                JButton adminButton = createSidebarButton("Admin", false);
                adminButton.setIcon(createIcon("C:\\Users\\Lenovo\\Pictures\\double user.jpg\\", 20, 20));
                adminButton.setIconTextGap(10);
                adminButton.addActionListener(e -> showAdminLogin());

                // Adding them to the navigation panel in the NEW ORDER:
                navigation.add(generateButton);
                navigation.add(Box.createVerticalStrut(10));
                navigation.add(aboutButton);
                navigation.add(Box.createVerticalStrut(10));
                navigation.add(adminButton);
        
        sidebar.add(navigation,BorderLayout.CENTER);

        // -----------------------------
        // Footer
        // -----------------------------

        JLabel footer = new JLabel("© 2026 FCCIB"); 

        footer.setForeground(new Color(220,230,245));footer.setFont(new Font("Arial",Font.PLAIN,14));footer.setBorder(new EmptyBorder(20,25,30,20));

        sidebar.add(footer, BorderLayout.SOUTH);

        return sidebar;
        }

        // ============================================================
        // MAIN CONTENT
        // ============================================================

    private JPanel createMainContent() {

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BACKGROUND);
        content.setBorder(new EmptyBorder(40,  50,  40,  50));

        // -----------------------------
        // HEADER
        // -----------------------------

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel,  BoxLayout.Y_AXIS));
        welcomePanel.setOpaque(false);

        JLabel welcome = new JLabel("Welcome!");

        welcome.setFont(new Font("Arial",  Font.BOLD,  38));
        welcome.setForeground(TEXT_DARK);

        JLabel instruction = new JLabel(
                "Enter student details and click generate to create email and password."
        );

        instruction.setFont(new Font("Arial",  Font.PLAIN,  20));
        instruction.setForeground(new Color(70,  80,  95));

        welcomePanel.add(welcome);
        welcomePanel.add(Box.createVerticalStrut(10));
        welcomePanel.add(instruction);

        header.add(welcomePanel,  BorderLayout.WEST);

        // Graduation image
        JLabel graduation = createImageLabel(
                "C:\\Users\\Lenovo\\Desktop\\graduation logo_jpeg.jpg", 
                180, 
                130
        );

        header.add(graduation,  BorderLayout.EAST);

        content.add(header,  BorderLayout.NORTH);

        // -----------------------------
        // CARDS
        // -----------------------------

        JPanel cards = new JPanel(new GridLayout(1,  2,  25,  0));
        cards.setOpaque(false);
        cards.setBorder(new EmptyBorder(35,  0,  0,  0));

        cards.add(createStudentCard());
        cards.add(createCredentialsCard());

        content.add(cards,  BorderLayout.CENTER);

        return content;
    }

        // ============================================================
        // STUDENT INFORMATION CARD
        // ============================================================

    private JPanel createStudentCard() {

        JPanel card = createCard();

        card.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(
                FlowLayout.LEFT, 
                20, 
                15
        ));

        header.setBackground(LIGHT_BLUE);
        header.setBorder(new EmptyBorder(5,  10,  5,  10));

        JLabel icon = new JLabel(createIcon("C:\\Users\\Lenovo\\Pictures\\user.jpg\\", 25, 25));

        icon.setFont(new Font("Arial",  Font.BOLD,  25));
        icon.setForeground(TEXT_DARK);

        JLabel title = new JLabel("Student Information");

        title.setFont(new Font("Arial",  Font.BOLD,  21));
        title.setForeground(TEXT_DARK);

        header.add(icon);
        header.add(title);

        card.add(header,  BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form,  BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(25,  30,  25,  30));

        // Surname
        form.add(createFieldLabel("C:\\Users\\Lenovo\\Desktop\\user.png\\",  "Surname"));

        surNameField = createTextField();
        form.add(surNameField);

        form.add(Box.createVerticalStrut(18));
        // First Name
        form.add(createFieldLabel("C:\\Users\\Lenovo\\Desktop\\user.png\\",  "First Name"));

        firstNameField = createTextField();
        form.add(firstNameField);

        form.add(Box.createVerticalStrut(18));

        // Other Name
        form.add(createFieldLabel("C:\\Users\\Lenovo\\Desktop\\user.png\\",  "Other Name"));

        otherNameField = createTextField();
        form.add(otherNameField);

        form.add(Box.createVerticalStrut(18));

        // Matric
        form.add(createFieldLabel("C:\\Users\\Lenovo\\Pictures\\document.jpg\\",  "Matric Number"));

        matricField = createTextField();
        form.add(matricField);

        form.add(Box.createVerticalStrut(18));

        // Department
        form.add(createFieldLabel("C:\\Users\\Lenovo\\Pictures\\document.jpg\\",  "Department"));

        String[] departments = {
                "Computing and Informatics", 
                "Accountancy",
                "Agricultural Technology",
                "Banking and Finance",
                "Business Administration and Management",
                "Co-operative Economics and Management",
                "Home and Rural Economics",
                "Marketing",
        };

        departmentBox = new JComboBox<>(departments);
        departmentBox.setFont(new Font("Arial",  Font.PLAIN,  17));
        departmentBox.setPreferredSize(new Dimension(100,  48));
        departmentBox.setMaximumSize(new Dimension(Integer.MAX_VALUE,  48));

        form.add(departmentBox);

        form.add(Box.createVerticalStrut(25));

        // Generate button
        JButton generate = createPrimaryButton("GENERATE");
        generate.setIcon(createIcon("C:\\Users\\Lenovo\\Pictures\\telegram.jpg\\", 20, 20));

        generate.setAlignmentX(Component.CENTER_ALIGNMENT);

        generate.addActionListener(e -> generateCredentials());

        form.add(generate);

        form.add(Box.createVerticalStrut(15));

        // Clear and save buttons
        JPanel bottomButtons = new JPanel(new GridLayout(1,  2,  20,  0));
        bottomButtons.setOpaque(false);
        bottomButtons.setMaximumSize(
                new Dimension(Integer.MAX_VALUE,  55)
        );

        JButton clear = createSecondaryButton("CLEAR");
        clear.setIcon(createIcon("C:\\Users\\Lenovo\\Pictures\\broom.jpg\\", 20, 20));
        clear.setIconTextGap(8);

        JButton save = createSaveButton("SAVE");
        save.setIcon(createIcon("C:\\Users\\Lenovo\\Pictures\\save.jpg\\", 20, 20));
        save.setIconTextGap(8); 

        clear.addActionListener(e -> clearForm());

        save.addActionListener(e -> saveCredentials());

        bottomButtons.add(clear);
        bottomButtons.add(save);

        form.add(bottomButtons);

        card.add(form,  BorderLayout.CENTER);

        return card;
    }

        // ============================================================
        // GENERATED CREDENTIALS CARD
        // ============================================================

    private JPanel createCredentialsCard() {

        JPanel card = createCard();

        card.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(
                FlowLayout.LEFT, 
                20, 
                15
        ));

        header.setBackground(LIGHT_GREEN);
        header.setBorder(new EmptyBorder(5,  10,  5,  10));

        JLabel icon = new JLabel(createIcon("C:\\Users\\Lenovo\\Pictures\\envelope.jpg\\", 25, 25));

        icon.setFont(new Font("Arial",  Font.BOLD,  25));
        icon.setForeground(new Color(30,  120,  90));

        JLabel title = new JLabel("Generated Credentials");

        title.setFont(new Font("Arial",  Font.BOLD,  21));
        title.setForeground(new Color(30,  120,  90));

        header.add(icon);
        header.add(title);

        card.add(header,  BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body,  BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(35,  30,  30,  30));

        // EMAIL
        JLabel emailTitle = new JLabel("Email Address");

        emailTitle.setFont(new Font("Arial",  Font.BOLD,  17));
        emailTitle.setForeground(TEXT_DARK);

        body.add(emailTitle);

        body.add(Box.createVerticalStrut(10));

        JPanel emailBox = new JPanel(new BorderLayout());
        emailBox.setBackground(LIGHT_GREEN);
        emailBox.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(205,  225,  215)
                        ), 
                        new EmptyBorder(17,  18,  17,  10)
                )
        );

        JLabel emailIcon = new JLabel(createIcon("C:\\Users\\Lenovo\\Pictures\\envelope.jpg\\", 23, 23));

        emailIcon.setFont(new Font("Arial",  Font.PLAIN,  23));

        emailLabel = new JLabel("student@fccib.edu.ng");

        emailLabel.setFont(
                new Font("Arial",  Font.BOLD,  19)
        );

        emailLabel.setForeground(
                new Color(30,  120,  90)
        );

        JButton copyEmail = new JButton(createIcon("C:\\Users\\Lenovo\\Pictures\\copy.jpg\\", 20, 20));

        styleSmallButton(copyEmail);

        copyEmail.addActionListener(e ->
                copyToClipboard(emailLabel.getText())
        );

        emailBox.add(emailIcon,  BorderLayout.WEST);
        emailBox.add(emailLabel,  BorderLayout.CENTER);
        emailBox.add(copyEmail,  BorderLayout.EAST);

        body.add(emailBox);

        body.add(Box.createVerticalStrut(35));

        // PASSWORD
        JLabel passwordTitle = new JLabel("Password");

        passwordTitle.setFont(new Font("Arial",  Font.BOLD,  17));
        passwordTitle.setForeground(TEXT_DARK);

        body.add(passwordTitle);

        body.add(Box.createVerticalStrut(10));

        JPanel passwordBox = new JPanel(new BorderLayout());

        passwordBox.setBackground(LIGHT_YELLOW);

        passwordBox.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(235,  220,  180)
                        ), 
                        new EmptyBorder(17,  18,  17,  10)
                )
        );

        JLabel lock = new JLabel(createIcon("C:\\Users\\Lenovo\\Pictures\\padlock.jpg\\", 20, 20));

        lock.setFont(new Font("Arial",  Font.PLAIN,  20));

        passwordLabel = new JLabel("••••••••••");

        passwordLabel.setFont(
                new Font("Arial",  Font.BOLD,  19)
        );

        passwordLabel.setForeground(
                new Color(150,  110,  30)
        );

        JButton eye = new JButton(createIcon("C:\\Users\\Lenovo\\Pictures\\eye.jpg\\", 20, 20));

        styleSmallButton(eye);

        eye.addActionListener(e -> {

            passwordVisible = !passwordVisible;

            String current = generatePassword;

            if (passwordVisible) {
                passwordLabel.setText(current);
            } else {
                passwordLabel.setText(maskPassword(current));
            }
        });

        JButton copyPassword = new JButton(createIcon("C:\\Users\\Lenovo\\Pictures\\copy.jpg\\", 20, 20));

        styleSmallButton(copyPassword);

        copyPassword.addActionListener (e -> {

            copyToClipboard(generatePassword);
        });

        JPanel rightButtons = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT, 
                        5, 
                        0
                )
        );

        rightButtons.setOpaque(false);

        rightButtons.add(eye);
        rightButtons.add(copyPassword);

        passwordBox.add(lock,  BorderLayout.WEST);
        passwordBox.add(passwordLabel,  BorderLayout.CENTER);
        passwordBox.add(rightButtons,  BorderLayout.EAST);

        body.add(passwordBox);

        body.add(Box.createVerticalStrut(35));

        // SECURITY MESSAGE
        JPanel security = new JPanel(
                new BorderLayout()
        );

        security.setBackground(
                new Color(235,  242,  253)
        );

        security.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(205,  220,  240)
                        ), 
                        new EmptyBorder(18,  18,  18,  18)
                )
        );

        JLabel shield = new JLabel(createIcon("C:\\Users\\Lenovo\\Pictures\\shield.jpg\\", 20, 20));

        shield.setFont(
                new Font("Arial",  Font.BOLD,  25)
        );

        shield.setForeground(PRIMARY_BLUE);

        JLabel securityText = new JLabel(
                "<html>"
                        + "<b>This password is strong and secure.</b>"
                        + "<br>"
                        + "Keep it confidential."
                        + "</html>"
        );

        securityText.setFont(
                new Font("Arial",  Font.PLAIN,  16)
        );

        security.add(shield,  BorderLayout.WEST);
        security.add(securityText,  BorderLayout.CENTER);

        body.add(security);

        card.add(body,  BorderLayout.CENTER);

        return card;
    }

        // ============================================================
        // GENERATE CREDENTIALS
        // ============================================================

    private void generateCredentials() {

        String surName = surNameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String otherName = otherNameField.getText().trim();
        String matric = matricField.getText().trim();

        if (surName.isEmpty()
                || firstName.isEmpty()
                || otherName.isEmpty()
                || matric.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, 
                    "Please fill in all student information.", 
                    "Missing Information", 
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // ----------------------------------
        // Generate email
        // ----------------------------------


        String email =
                firstName.toLowerCase()
                        + "."
                        + surName.toLowerCase()
                        + matric.substring(
                        Math.max(0,  matric.length() - 3)
                        )
                        + "@fccib.edu.ng";

        // ----------------------------------
        // Generate password
        // ----------------------------------

        String password = generatePassword();

        emailLabel.setText(email);

        passwordLabel.putClientProperty("password", password);

        passwordVisible = true;

        passwordLabel.setText(password);
    }

        // ============================================================
        // PASSWORD GENERATOR
        // ============================================================

    private String generatePassword() {

        String characters =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        + "abcdefghijklmnopqrstuvwxyz"
                        + "0123456789"
                        + "@#$%&";

        Random random = new Random();

        StringBuilder password =
                new StringBuilder();

        for (int i = 0; i < 10; i++) {

            int index =
                    random.nextInt(
                            characters.length()
                    );

            password.append(
                    characters.charAt(index)
            );
        }

        return password.toString();
    }

        // ============================================================
        // CLEAR
        // ============================================================

    private void clearForm() {
        surNameField.setText("");
        firstNameField.setText("");
        otherNameField.setText("");
        matricField.setText("");
        departmentBox.setSelectedIndex(0);
        emailLabel.setText("student@fccib.edu.ng");
        passwordLabel.setText("**********");
        generatePassword = ""; // Clear the saved password
        passwordVisible = true;
    }

        // ============================================================
        // SAVE CREDENTIALS
        // ============================================================
private void saveCredentials() {
    String surName = surNameField.getText().trim();
    String firstName = firstNameField.getText().trim();
    String otherName = otherNameField.getText().trim();
    String matric = matricField.getText().trim();
    String dept = (String) departmentBox.getSelectedItem();

    if (surName.isEmpty() || firstName.isEmpty() || matric.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in at least Surname, First Name, and Matric Number.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
    }
    try {
            // 💡 CRITICAL FIX: The ", true" tells Java to APPEND to the file!
            FileWriter writer = new FileWriter(RECORDS_FILE, true); 
            
            writer.write("Surname: " + surName + "\n");
            writer.write("First Name: " + firstName + "\n");
            writer.write("Other Name: " + otherName + "\n");
            writer.write("Matric: " + matric + "\n");
            writer.write("Department: " + dept + "\n");
            writer.write("Email: " + emailLabel.getText() + "\n");
            writer.write("Password: " + generatePassword + "\n");
            writer.write("maskPassword\n");
            writer.close();

            JOptionPane.showMessageDialog(this, "Record saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage());
    }
}

        // ============================================================
        // UTILITY METHODS
        // ============================================================

    private JButton createSidebarButton(String text,  boolean active) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial",  Font.PLAIN,  18));
        button.setForeground(Color.WHITE);
        button.setBackground(active ? PRIMARY_BLUE :  SIDEBAR_BLUE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(15,  20,  15,  20));
        return button;
    }

    private JPanel createCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), 
                new EmptyBorder(0,  0,  0,  0)
        ));
        return card;
    }

    private JPanel createFieldLabel(String imagePath, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 15));
        label.setForeground(TEXT_DARK);
        label.setIcon(createIcon(imagePath, 18, 18));
        label.setIconTextGap(8);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        panel.add(label);
        return panel;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial",  Font.PLAIN,  17));
        field.setPreferredSize(new Dimension(100,  48));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE,  48));
        return field;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial",  Font.BOLD,  18));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_BLUE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200,  55));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE,  55));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial",  Font.BOLD,  18));
        button.setForeground(TEXT_DARK);
        button.setBackground(new Color(240,  242,  245));
        button.setBorder(BorderFactory.createLineBorder(BORDER));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(100,  55));
        return button;
    }

    private JButton createSaveButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial",  Font.BOLD,  18));
        button.setForeground(Color.WHITE);
        button.setBackground(GREEN);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(100,  55));
        return button;
    }

    private void styleSmallButton(JButton button) {
        button.setFont(new Font("Arial",  Font.BOLD,  18));
        button.setForeground(TEXT_DARK);
        button.setBackground(new Color(245,  247,  250));
        button.setBorder(BorderFactory.createLineBorder(BORDER));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(45,  45));
        button.setMaximumSize(new Dimension(45,  45));
    }

    private String maskPassword(String password) {
        return "•".repeat(Math.max(0,  password.length()));
    }

    private void copyToClipboard(String text) {
        try {
            StringSelection stringSelection = new StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(stringSelection,  null);
            JOptionPane.showMessageDialog(
                    this,  
                    "Copied to clipboard!",  
                    "Copy",  
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,  
                    "Failed to copy to clipboard: " + e.getMessage(),  
                    "Copy Error",  
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        // Check if we're in a headless environment
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Cannot run GUI application in headless environment.");
            System.out.println("This application requires a graphical display.");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Main().setVisible(true);
        });
    }
}
