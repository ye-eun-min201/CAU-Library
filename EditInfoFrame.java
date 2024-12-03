package DBD_env_unification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EditInfoFrame extends JFrame {
    private JTextField idField, nameField, mailField, departmentField, positionField;
    private JPasswordField passwordField;
    private JButton saveButton;
    private MainPageFrame mainPageFrame;
    private User user;

    public EditInfoFrame(MainPageFrame mainPageFrame, User user, String user_id) {
        super("내 정보 수정");
        this.mainPageFrame = mainPageFrame;
        this.user = user;

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainPageFrame.setVisible(true);
            }
        });

        setLayout(new BorderLayout(10, 10));
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // ID (읽기 전용)
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(new JLabel("ID:"), gbc);
        idField = new JTextField(user_id);
        idField.setEditable(false);
        gbc.gridx = 1;
        infoPanel.add(idField, gbc);

        // 이름
        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(new JLabel("이름:"), gbc);
        nameField = new JTextField(user.getName());
        gbc.gridx = 1;
        infoPanel.add(nameField, gbc);

        // 메일
        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(new JLabel("메일:"), gbc);
        mailField = new JTextField(user.getEmail());
        gbc.gridx = 1;
        infoPanel.add(mailField, gbc);

        // 비밀번호
        gbc.gridx = 0;
        gbc.gridy = 3;
        infoPanel.add(new JLabel("비밀번호:"), gbc);
        passwordField = new JPasswordField();
        gbc.gridx = 1;
        infoPanel.add(passwordField, gbc);

        // 학과 (읽기 전용)
        gbc.gridx = 0;
        gbc.gridy = 4;
        infoPanel.add(new JLabel("학과:"), gbc);
        departmentField = new JTextField(user.getDepartment());
        departmentField.setEditable(false);
        gbc.gridx = 1;
        infoPanel.add(departmentField, gbc);

        // 지위 (읽기 전용)
        gbc.gridx = 0;
        gbc.gridy = 5;
        infoPanel.add(new JLabel("지위:"), gbc);
        positionField = new JTextField(user.getStatus());
        positionField.setEditable(false);
        gbc.gridx = 1;
        infoPanel.add(positionField, gbc);

        // 저장 버튼
        saveButton = new JButton("저장");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        infoPanel.add(saveButton, gbc);

        // 패널 추가
        add(infoPanel, BorderLayout.CENTER);

// 저장 버튼 동작
        saveButton.addActionListener(e -> {
            saveChanges(); // 정보 저장 처리

            // 저장 후 현재 프레임 닫기
            dispose();

            // 메인 프레임 다시 띄우기
            mainPageFrame.setVisible(true);
        });

        // 프레임 설정
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 가운데 표시
        setVisible(true);
    }

    private void saveChanges() {
        String updatedName = nameField.getText();
        String updatedMail = mailField.getText();
        String updatedPassword = new String(passwordField.getPassword());

        boolean updateComplete = user.updateUser("NAME", updatedName);
        updateComplete = updateComplete && user.updateUser("Mail", updatedMail);
        if(!updatedPassword.isEmpty()){
            updateComplete = updateComplete && user.updateUser("Password", updatedPassword);
        }

        if(!updateComplete){
            JOptionPane.showMessageDialog(this,
                    "회원정보를 변경하는 데 실패하였습니다.\n다시 시도해 주세요.",
                    "저장 실패",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

