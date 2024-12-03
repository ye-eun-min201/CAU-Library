package DBD_env_unification;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class UserStorageFrame extends JFrame {
    private JList<String> storageList;
    private DefaultListModel<String> listModel;
    private User user;
    private ArrayList<String> storage_id_list;

    private MainPageFrame mainPageFrame;  // 이전 화면을 참조

    public UserStorageFrame(MainPageFrame mainPageFrame) {
        super("User Storage");

        this.mainPageFrame = mainPageFrame;  // 이전 화면 참조 저장
        this.user = mainPageFrame.getLibrary().getUser();
        this.storage_id_list = new ArrayList<>();

        // 프레임 설정
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 배경 색상 설정
        getContentPane().setBackground(new Color(240, 240, 240));  // 밝은 회색 배경

        // 제목 라벨
        JLabel titleLabel = new JLabel("User Storage", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));  // 제목 라벨에 한글 폰트 사용
        titleLabel.setForeground(new Color(59, 89, 182));  // 파란색 텍스트
        titleLabel.setPreferredSize(new Dimension(getWidth(), 50));
        add(titleLabel, BorderLayout.NORTH);

        // 버튼들 (삭제 및 뒤로가기)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));  // 버튼들을 중앙에 배치하고 간격 조정

        // 뒤로가기 버튼
        JButton backButton = new JButton("Back");
        styleButton(backButton);  // 버튼 스타일 적용
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 뒤로가기 버튼 동작
                mainPageFrame.setVisible(true);  // 이전 화면 보이기
                dispose();  // 현재 화면 종료
            }
        });

        // 삭제 버튼
        JButton deleteButton = new JButton("Delete Selected");
        styleButton(deleteButton);  // 버튼 스타일 적용
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedItem();  // 선택된 항목 삭제
            }
        });

        // 버튼들을 패널에 추가
        buttonPanel.add(backButton);
        buttonPanel.add(deleteButton);

        // 버튼 패널을 화면 하단에 추가
        add(buttonPanel, BorderLayout.SOUTH);

        // 리스트 모델 설정
        listModel = new DefaultListModel<>();
        storageList = new JList<>(listModel);
        storageList.setFont(new Font("맑은 고딕", Font.PLAIN, 16));  // JList에 한글 폰트 사용

        // 텍스트 색상 설정 (배경과 대비를 높이기 위해)
        storageList.setForeground(Color.BLACK);  // 텍스트는 검은색
        storageList.setBackground(Color.WHITE);  // 리스트 배경은 흰색

        // 선택된 항목의 색상 설정
        storageList.setSelectionBackground(new Color(59, 89, 182));  // 선택된 항목은 파란색 배경
        storageList.setSelectionForeground(Color.WHITE);  // 선택된 항목 텍스트는 흰색

        storageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        storageList.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JScrollPane scrollPane = new JScrollPane(storageList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 데이터 불러오기
        loadUserStorage();

        // 리스트 추가
        add(scrollPane, BorderLayout.CENTER);

        // 프레임 표시
        setLocationRelativeTo(null);  // 화면 가운데 배치
        setVisible(true);
    }

    private void loadUserStorage() {
        List<String> storageItems = user.showUsersStorage();

        for (int i = 0; i < storageItems.size(); i++) {
            String item = storageItems.get(i);

            // 홀수 번째 항목에 Storage_ID 추가
            if (i % 2 == 1) {
                listModel.addElement(item);  // Storage_ID
            } else {
                storage_id_list.add(item);
            }
        }
    }

    private void deleteSelectedItem() {
        // 선택된 항목의 인덱스 가져오기
        int selectedIndex = storageList.getSelectedIndex();

        if (selectedIndex != -1) {  // 항목이 선택되었을 경우
            String selectedItem = listModel.get(selectedIndex);
            int selectedStorageId = Integer.parseInt(storage_id_list.get(selectedIndex));

            // 폴더 항목인 경우 삭제하지 않음
            if (selectedItem.startsWith("==")) {
                JOptionPane.showMessageDialog(this, "폴더는 삭제할 수 없습니다.", "삭제 실패", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 선택된 항목을 리스트에서 제거
            listModel.remove(selectedIndex);

            // 실제 데이터에서 항목 삭제 (예: 데이터베이스에서 삭제)
            user.deleteStorage(selectedStorageId);  // 이 메서드는 실제 데이터베이스나 사용자 저장소에서 항목을 삭제하는 메서드입니다.
        } else {
            JOptionPane.showMessageDialog(this, "선택된 항목이 없습니다.", "삭제 실패", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 버튼 스타일을 설정하는 메서드
    private void styleButton(JButton button) {
        button.setFont(new Font("맑은 고딕", Font.PLAIN, 16));  // 버튼 폰트 설정
        button.setPreferredSize(new Dimension(180, 40));  // 버튼 크기 설정
        button.setBackground(new Color(59, 89, 182));  // 버튼 배경색 파란색
        button.setForeground(Color.WHITE);  // 버튼 텍스트는 흰색
        button.setFocusPainted(false);  // 포커스 효과 없애기
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));  // 버튼 안쪽 여백 추가
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));  // 마우스 커서 손 모양으로 변경
        button.setFont(new Font("맑은 고딕", Font.BOLD, 16));  // 폰트 굵기 설정
    }
}
