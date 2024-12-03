package DBD_env_unification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class RegisterFrame extends JFrame {
    private Library library;
    private Connection conn;
    public RegisterFrame(LoginFrame loginFrame, Library library) {
        // 프레임 기본 설정
        super("Register");
        this.library = library;
        this.conn = library.getConn();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창 닫기 설정
        setSize(400, 400);
        setLayout(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                loginFrame.setVisible(true);
            }
        });

        // 제목 라벨
        JLabel titleLabel = new JLabel("Register New User");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(50, 20, 300, 30);
        add(titleLabel);

        // 사용자 정보 입력 필드
        JLabel idLabel = new JLabel("ID:");
        idLabel.setBounds(50, 70, 80, 25);
        add(idLabel);

        JTextField idField = new JTextField();
        idField.setBounds(150, 70, 200, 25);
        add(idField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 110, 80, 25);
        add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(150, 110, 200, 25);
        add(passwordField);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 150, 80, 25);
        add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(150, 150, 200, 25);
        add(nameField);

        JLabel mailLabel = new JLabel("Mail:");
        mailLabel.setBounds(50, 190, 80, 25);
        add(mailLabel);

        JTextField mailField = new JTextField();
        mailField.setBounds(150, 190, 200, 25);
        add(mailField);

        JLabel departmentLabel = new JLabel("Department:");
        departmentLabel.setBounds(50, 230, 80, 25);
        add(departmentLabel);

        ArrayList<String> departmentList = library.getDepartmentListFromDatabase();

        // JComboBox 생성
        JComboBox<String> departmentComboBox = new JComboBox<>();
        departmentComboBox.setBounds(150, 230, 200, 25); // 위치와 크기 설정
        add(departmentComboBox);
        // 학부 목록을 JComboBox에 추가
        for (String department : departmentList) {
            departmentComboBox.addItem(department);
        }

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setBounds(50, 270, 80, 25);
        add(statusLabel);

        // DB에서 status 값 가져오기
        ArrayList<String> statusList = library.getStatusListFromDatabase();
        // 상태를 선택할 수 있는 JComboBox
        JComboBox<String> statusComboBox = new JComboBox<>();
        statusComboBox.setBounds(150, 270, 200, 25);
        add(statusComboBox);

        for (String status : statusList) {
            statusComboBox.addItem(status);
        }

        // 회원가입 버튼
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(150, 320, 100, 30);
        registerButton.setBackground(new Color(76, 175, 80));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 입력 값 가져오기
                String id = idField.getText();
                String password = new String(passwordField.getPassword());
                String name = nameField.getText();
                String mail = mailField.getText();
                String department = (String) departmentComboBox.getSelectedItem();
                String status = (String) statusComboBox.getSelectedItem();

                // Library 객체의 회원가입 메서드 호출
                try {
                    // 입력값이 비어 있는지 체크
                    if (id.isEmpty() || password.isEmpty() || name.isEmpty() || mail.isEmpty()) {
                        JOptionPane.showMessageDialog(RegisterFrame.this,
                                "모든 필드를 입력해주세요.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else if (library.isUserIdDuplicate(id)) { //중복 ID 확인
                            // 중복된 ID가 있을 경우 경고 메시지 출력
                            JOptionPane.showMessageDialog(RegisterFrame.this,
                                    "중복된 사용자 ID입니다. 다른 ID를 사용해주세요.",
                                    "ID 중복 오류",
                                    JOptionPane.WARNING_MESSAGE);
                            return;
                        }else {
                        // 모든 필드가 비어있지 않고 중복 ID가 아니면 회원가입 진행
                        library.registerUser(id, name, mail, password, department, status);
                        JOptionPane.showMessageDialog(RegisterFrame.this,
                                "회원가입이 완료되었습니다.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        dispose(); // 회원가입 창 닫기
                        loginFrame.setVisible(true);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RegisterFrame.this,
                            "회원가입 중 오류가 발생했습니다.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(registerButton);

        setVisible(true);
    }




}
