package DBD_env_unification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainPageFrame extends JFrame {
    private Library library;

    public MainPageFrame(Library library) {
        // 프레임 기본 설정
        super("Library System - Main Page");
        this.library = library;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 배경 색상 설정
        getContentPane().setBackground(new Color(245, 245, 245));  // 배경을 밝은 회색으로 설정

        // 제목 라벨 설정 (글자 색깔을 검은색으로 변경)
        JLabel titleLabel = new JLabel("Library System - Main Menu");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));  // 한글 지원 폰트 사용
        titleLabel.setForeground(Color.BLACK);  // 제목 색깔을 검은색으로 설정
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 제목 위치 설정 (위쪽 여백을 추가하여 위로 이동)
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(40, 0, 20, 0);  // 위쪽 간격을 40으로 늘리고 아래쪽 간격을 20으로 설정
        add(titleLabel, gbc);

        // 버튼 스타일 설정 (어두운 파란색 계열로 통일)
        JButton searchButton = createStyledButton("도서 검색");
        JButton modifyInfoButton = createStyledButton("내 정보 수정");
        JButton borrowReturnButton = createStyledButton("대출/반납/연장");
        JButton boardButton = createStyledButton("게시판");
        JButton noticeButton = createStyledButton("공지사항");
        JButton storageButton = createStyledButton("개인 보관함");

        noticeButton.addActionListener(e -> {
            // AnnouncementListFrame을 띄우는 코드
            new AnnouncementListFrame(getThis(), library.showAnnouncement() );  // announcements는 공지사항 리스트입니다.
            setVisible(false);
        });

        modifyInfoButton.addActionListener(e -> {
            // myInfoFrame을 띄우는 코드
            new MyInfoFrame(this);
            setVisible(false);
        });

        storageButton.addActionListener(e -> {
            // userStorageFrame을 띄우는 코드
            new UserStorageFrame(this);
            setVisible(false);
        });

        // 각 버튼의 배치 (세로 간격을 띄움)
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 20, 10);  // 버튼 간 세로 간격을 20으로 설정
        add(searchButton, gbc);

        gbc.gridx = 1;
        add(modifyInfoButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(borrowReturnButton, gbc);

        gbc.gridx = 1;
        add(boardButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(noticeButton, gbc);

        gbc.gridx = 1;
        add(storageButton, gbc);

        // 프레임 보이기
        setVisible(true);
    }

    // 스타일링된 버튼 생성 메서드 (색상 통일)
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 16));  // 한글 지원 폰트 사용
        button.setForeground(Color.WHITE);  // 텍스트는 하얀색
        button.setBackground(new Color(21, 101, 192));  // 어두운 파란색 계열로 변경
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 50));

        // 버튼 둥글게 만들기
        button.setBorder(BorderFactory.createLineBorder(new Color(21, 101, 192), 2, true));  // 어두운 파란색 테두리
        button.setContentAreaFilled(false); // 배경을 투명하게
        button.setOpaque(true); // 배경이 투명하지 않도록 설정

        // 버튼에 테두리 추가
        button.setBorder(BorderFactory.createCompoundBorder(
                button.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // 마우스 오버 효과
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(26, 115, 176));  // 마우스를 올리면 색상 밝아짐
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(21, 101, 192));  // 마우스를 떼면 원래 색상
            }
        });


        return button;
    }

    private MainPageFrame getThis(){
        return this;
    }

    public Library getLibrary(){
        return  library;
    }
}
