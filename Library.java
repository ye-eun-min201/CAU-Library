package DBD_env_unification;

import java.sql.*;
import java.util.Scanner;

public class Library {
    public static void main(String[] args) {
        // DB 인증 정보를 담은 객체 생성 및 파일에서 정보 파싱
        DatabaseAuthInformation dbAuth = new DatabaseAuthInformation();
        boolean isParsed = dbAuth.parse_auth_info("C:\\Users\\fossj\\IdeaProjects\\java-MySQL-gradle\\src\\main\\resources\\mysql.auth");

        if (!isParsed) {
            System.out.println("DB 인증 정보를 파싱하는 데 실패했습니다.");
            return;
        }

        // DB 연결 정보 설정
        String url = "jdbc:mysql://" + dbAuth.getHost() + ":" + dbAuth.getPort() + "/" + dbAuth.getDatabase_name()
                + "?useSSL=false&serverTimezone=UTC";  // SSL 설정과 타임존 추가
        String username = dbAuth.getUsername();
        String password = dbAuth.getPassword();


        // 간단한 SELECT 쿼리 실행
        String query = "SELECT * FROM storage"; // 테이블 이름과 컬럼명은 실제 DB 구조에 맞게 수정하세요.

        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            showAnnouncement(conn);

            // 결과 출력
            while (rs.next()) {
                String id = rs.getString("Data_ID");        // 예시: 학번
                String userid = rs.getString("User_ID");      // 예시: 이름
                String major = rs.getString("Storage_folder");    // 예시: 전공
                int storage_id = rs.getInt("Storage_ID");

                System.out.println("ID: " + userid + ", 책: " + id + ", 폴더: " + major + ", 저장 id: " + storage_id);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //유저 등록(회원가입)
    public static void registerUser(Connection conn) {
        Scanner scanner = new Scanner(System.in);

        // 입력받기
        System.out.print("사용자 ID: ");
        String userId = scanner.nextLine();

        System.out.print("비밀번호: ");
        String password = scanner.nextLine();

        System.out.print("회원 이름: ");
        String name = scanner.nextLine();

        System.out.print("메일: ");
        String mail = scanner.nextLine();


        System.out.print("소속 학부: "); // 다른 테이블로부터 받아오는데 입력 필요..?
        String department = scanner.nextLine();

        System.out.print("신분: ");  // 다른 테이블로부터 받아오는데 입력 필요..?
        String status = scanner.nextLine();

        try {
            // 사용자 ID 중복 확인
            if (isUserIdDuplicate(conn, userId)) {
                System.out.println("중복된 사용자 ID입니다. 다른 ID를 사용해주세요.");
                return;
            }

            // INSERT 쿼리 작성
            String sql = "INSERT INTO user (User_ID, NAME, Mail, Password, Department, Status) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userId);
                pstmt.setString(2, name);
                pstmt.setString(3, mail);
                pstmt.setString(4, password);
                pstmt.setString(5, department);
                pstmt.setString(6, status);

                // 데이터베이스에 삽입
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("회원 등록이 완료되었습니다!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 사용자 ID 중복 확인 함수 호출
    private static boolean isUserIdDuplicate(Connection conn, String userId) {
        String sql = "{? = CALL isUserIdDuplicate(?)}"; // MySQL 함수 호출 구문
        try (CallableStatement stmt = conn.prepareCall(sql)) {
            // 첫 번째 인자는 반환 값이므로 등록
            stmt.registerOutParameter(1, Types.BOOLEAN);
            // 두 번째 인자는 입력 값
            stmt.setString(2, userId);

            // 함수 실행
            stmt.execute();

            // 반환된 값 확인
            return stmt.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //모든 공지사항을 보여줌
    public static void showAnnouncement(Connection conn){
        String sql = "{CALL showAnnouncement()}";  // 저장 프로시저 호출
        try (CallableStatement stmt = conn.prepareCall(sql)) {
            // 저장 프로시저 실행
            ResultSet rs = stmt.executeQuery();
            // 결과 처리
            while (rs.next()) {
                int textNumber = rs.getInt("Text_number");
                String title = rs.getString("Title");
                String managerName = rs.getString("NAME");
                String type = rs.getString("Type");
                Timestamp createDate = rs.getTimestamp("Create_date");
                int views = rs.getInt("Views");
                String content = rs.getString("Content");

                // 출력
                System.out.println("Text Number: " + textNumber);
                System.out.println("Title: " + title);
                System.out.println("Manager Name: " + managerName);
                System.out.println("Type: " + type);
                System.out.println("Create Date: " + createDate);
                System.out.println("Views: " + views);
                System.out.println("Content: " + content);
                System.out.println("---------------------------------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

