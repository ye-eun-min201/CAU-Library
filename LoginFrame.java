package DBD_env_unification;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private Library library;
    public LoginFrame(Library library) {
        // 프레임 기본 설정
        super("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(null);

        // 제목 라벨
        JLabel titleLabel = new JLabel("Library System");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(50, 20, 300, 30);
        add(titleLabel);

        // ID 라벨과 입력 필드
        JLabel idLabel = new JLabel("ID:");
        idLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        idLabel.setBounds(50, 70, 80, 25);
        add(idLabel);

        JTextField idField = new JTextField();
        idField.setBounds(150, 70, 200, 25);
        add(idField);

        // Password 라벨과 입력 필드
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setBounds(50, 110, 80, 25);
        add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(150, 110, 200, 25);
        add(passwordField);

        // 버튼 스타일
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginButton.setBounds(50, 160, 130, 35);
        loginButton.setBackground(new Color(59, 89, 182));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String password = new String(passwordField.getPassword());
                if(library.login(id, password)){
                    new MainPageFrame(library);
                    dispose();  // 로그인 창 닫기
                }else{
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "아이디 또는 비밀번호가 일치하지 않습니다.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(loginButton);

        registerButton.setBounds(220, 160, 130, 35);
        registerButton.setBackground(new Color(76, 175, 80));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new RegisterFrame(getThis(), library); // 회원가입 창 띄우기
            }
        });
        add(registerButton);
        setVisible(true);
    }

    public LoginFrame getThis(){
        return this;
    }

}
