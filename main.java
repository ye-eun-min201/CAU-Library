package DBD_env_unification;
import java.sql.*;
import java.util.Scanner;

public class main {
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
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        // 간단한 SELECT 쿼리 실행
        String query = "SELECT * FROM storage"; // 테이블 이름과 컬럼명은 실제 DB 구조에 맞게 수정하세요.

        // 메인 프레임 생성

        try{
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            Library library = new Library(conn);
            LoginFrame loginFrame = new LoginFrame(library);
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


}