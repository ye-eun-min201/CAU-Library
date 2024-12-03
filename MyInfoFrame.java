package DBD_env_unification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MyInfoFrame extends JFrame {
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton authButton;

    public MyInfoFrame(MainPageFrame mainPageFrame) {
        super("내 정보 수정");

        // 전체 레이아웃 설정
        setLayout(new BorderLayout(10, 10));
        JPanel authPanel = new JPanel();
        authPanel.setLayout(new GridBagLayout());
        authPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // GridBagConstraints 설정
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // 컴포넌트 간 간격

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainPageFrame.setVisible(true);
            }
        });

        // ID 레이블 및 텍스트 필드
        gbc.gridx = 0;
        gbc.gridy = 0;
        authPanel.add(new JLabel("ID:"), gbc);

        idField = new JTextField(15);
        gbc.gridx = 1;
        authPanel.add(idField, gbc);

        // 비밀번호 레이블 및 텍스트 필드
        gbc.gridx = 0;
        gbc.gridy = 1;
        authPanel.add(new JLabel("비밀번호:"), gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        authPanel.add(passwordField, gbc);

        // 인증 버튼
        authButton = new JButton("인증");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // 버튼을 두 열에 걸치게 배치
        gbc.fill = GridBagConstraints.CENTER;

        // 버튼 크기 및 스타일 설정
        authButton.setPreferredSize(new Dimension(100, 30));
        authButton.addActionListener(e -> {
            String id = idField.getText();
            String password = new String(passwordField.getPassword());
            if (mainPageFrame.getLibrary().login(id, password)) {
                dispose();
                new EditInfoFrame(mainPageFrame, mainPageFrame.getLibrary().getUser(), id);
            } else {
                JOptionPane.showMessageDialog(this,
                        "ID 또는 비밀번호가 잘못되었습니다.",
                        "인증 오류",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        authPanel.add(authButton, gbc);

        // 패널 추가
        add(authPanel, BorderLayout.CENTER);

        // 프레임 설정
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 가운데에 표시
        setVisible(true);
    }
}
