package DBD_env_unification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class AnnouncementListFrame extends JFrame {
    private ArrayList<Announcement> announcements;  // List에서 ArrayList로 변경
    private MainPageFrame mainPageFrame;

    public AnnouncementListFrame(MainPageFrame mainPageFrame, ArrayList<Announcement> announcements) {
        super("공지사항 목록");
        this.mainPageFrame = mainPageFrame;
        this.announcements = announcements;

        setLayout(new BorderLayout());

        // 제목 라벨 설정
        JLabel titleLabel = new JLabel("공지사항 목록", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        add(titleLabel, BorderLayout.NORTH);

        // 공지사항 목록을 표시할 JList
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Announcement announcement : announcements) {
            listModel.addElement(announcement.getTitle());  // 제목만 추가
        }

        JList<String> announcementList = new JList<>(listModel);
        announcementList.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        announcementList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(announcementList);
        add(scrollPane, BorderLayout.CENTER);

        // 창이 닫힐 때 메인 페이지 프레임을 다시 보이게 설정
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                mainPageFrame.setVisible(true);  // MainPageFrame을 다시 보이게 함
            }
        });

        // "뒤로 가기" 버튼 추가
        JButton backButton = new JButton("뒤로 가기");
        backButton.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        backButton.addActionListener(e -> {
            // 뒤로 가기 버튼 클릭 시, MainPageFrame을 다시 보이게 하고 현재 프레임을 닫기
            mainPageFrame.setVisible(true);
            dispose();
        });
        add(backButton, BorderLayout.SOUTH);

        // 공지사항 선택 시 본문을 보여주는 액션
        announcementList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = announcementList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Announcement selectedAnnouncement = announcements.get(selectedIndex);
                    showAnnouncementDetails(selectedAnnouncement);
                }
            }
        });



        // 프레임 설정
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);  // 화면 가운데에 표시
        setVisible(true);
    }

    // 선택한 공지사항의 본문을 보여주는 메서드
    private void showAnnouncementDetails(Announcement announcement) {
        JFrame detailFrame = new JFrame("공지사항 상세보기");
        detailFrame.setLayout(new BorderLayout());

        // 본문을 보여주는 JTextArea
        JTextArea contentArea = new JTextArea(announcement.getContent());
        contentArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setCaretPosition(0);
        contentArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(contentArea);
        detailFrame.add(scrollPane, BorderLayout.CENTER);

        // 공지사항 상세정보 패널
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(4, 1));

        // 공지사항 정보 출력
        infoPanel.add(new JLabel("작성자: " + announcement.getManagerName()));
        infoPanel.add(new JLabel("작성일: " + announcement.getCreateDate().toString()));
        infoPanel.add(new JLabel("조회수: " + announcement.getViews()));

        detailFrame.add(infoPanel, BorderLayout.NORTH);

        // 상세보기 창 설정
        detailFrame.setSize(600, 400);
        detailFrame.setLocationRelativeTo(null);  // 화면 가운데에 표시
        detailFrame.setVisible(true);
    }
}
