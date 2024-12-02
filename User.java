package DBD_env_unification;

import java.sql.*;
import java.util.Scanner;

public class User {
    private String user_id;
    private String name;
    private String email;
    private String department;
    private String status;

    public User(String user_id, Connection con){
        String query =
                "select User_ID, NAME, Mail, Department, Status from user where User_ID = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(query))
        {
            preparedStatement.setString(1, user_id);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                this.user_id = rs.getString("User_ID");
                name = rs.getString("NAME");
                email = rs.getString("Mail");
                department = rs.getString("Department");
                status = rs.getString("Status");
            }
        } catch (SQLException e) {
            System.out.println("Failed to select : " + e.getMessage());
        }
    }

    public void printUser(){
        System.out.println("사용자 정보");
        System.out.println("ID: "+user_id);
        System.out.println("name: "+name);
        System.out.println("email: "+email);
        System.out.println("dep: "+department);
        System.out.println("status: "+status);
    }



    public void deleteUser(Connection conn) {
        Scanner scanner = new Scanner(System.in);

        // 입력받기
        System.out.print("사용자 ID: ");
        String userId = scanner.nextLine();

        System.out.print("비밀번호: ");
        String password = scanner.nextLine();

        try {
            // 사용자 ID와 비밀번호 확인
            if (!isUserCredentialsValid(conn, password)) {
                System.out.println("잘못된 사용자 ID 또는 비밀번호입니다.");
                return;
            }

            // DELETE 쿼리 작성
            String sql = "DELETE FROM user WHERE User_ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userId);

                // 레코드 삭제
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("회원 탈퇴가 완료되었습니다.");
                } else {
                    System.out.println("회원 탈퇴에 실패했습니다.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateUser(Connection conn) {
        Scanner scanner = new Scanner(System.in);

        // 입력받기
        System.out.print("사용자 ID: ");
        String userId = scanner.nextLine();

        System.out.print("비밀번호: ");
        String password = scanner.nextLine();

        try {
            // 사용자 ID와 비밀번호 확인
            if (!isUserCredentialsValid(conn,password)) {
                System.out.println("잘못된 사용자 ID 또는 비밀번호입니다.");
                return;
            }

            // 수정할 필드와 값 입력받기
            System.out.print("수정할 필드명 (Name, Mail, Password, Department, Status 중 하나): ");
            String field = scanner.nextLine();

            System.out.print("새로운 값: ");
            String newValue = scanner.nextLine();

            // UPDATE 쿼리 작성
            String sql = "UPDATE user SET " + field + " = ? WHERE User_ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newValue);
                pstmt.setString(2, userId);

                // 레코드 업데이트
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("회원 정보가 성공적으로 수정되었습니다.");
                } else {
                    System.out.println("회원 정보 수정에 실패했습니다.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 사용자 인증 함수 호출
    public boolean isUserCredentialsValid(Connection conn, String password) {
        String sql = "{? = CALL isUserCredentialsValid(?, ?)}"; // MySQL 함수 호출 구문
        try (CallableStatement stmt = conn.prepareCall(sql)) {
            // 첫 번째 인자는 반환 값이므로 등록
            stmt.registerOutParameter(1, Types.BOOLEAN);
            // 두 번째, 세 번째 인자는 입력 값
            stmt.setString(2, user_id);
            stmt.setString(3, password);

            // 함수 실행
            stmt.execute();

            // 반환된 값 확인
            return stmt.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //개인 보관함에 추가
    public void addToStorage(Connection conn, String userId, int dataId, String folder) {
        String checkSql = "SELECT isDataExists(?)"; // isDataExists 함수 호출
        String addSql = "{CALL addToStorage(?, ?, ?)}"; // addToStorage 프로시저 호출

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            // 데이터 존재 여부 확인
            checkStmt.setInt(1, dataId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getBoolean(1)) {
                    // 데이터가 존재하면 addToStorage 호출
                    try (CallableStatement addStmt = conn.prepareCall(addSql)) {
                        addStmt.setString(1, userId);
                        addStmt.setInt(2, dataId);
                        addStmt.setString(3, folder);

                        addStmt.execute();
                        System.out.println("Data added to storage successfully.");
                    }
                } else {
                    System.out.println("Data with ID " + dataId + " does not exist.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //유저의 개인 보관함을 폴더별로 보여준다
    public void showUsersStorage(Connection conn, String userId) {
        String sql = "{CALL showUsersStorage(?)}"; // MySQL 함수 호출 구문
        String currentFolder = "";
        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, userId);

            ResultSet rs = stmt.executeQuery();

            // 결과 출력
            while (rs.next()) {
                String title = rs.getString("Title");
                String author = rs.getString("Author");
                String publisher = rs.getString("Publisher");
                String folder = rs.getString("Storage_folder");
                if(!currentFolder.equals(folder)){
                    System.out.println(folder);
                    currentFolder = folder;
                }
                System.out.println("Title: " + title + ", Author: " + author + ", Publisher: " + publisher);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //개인 보관함에서 삭제
    public void deleteStorage(Connection conn, int storageId) {
        String sql = "DELETE FROM storage WHERE Storage_ID = ?"; // MySQL 함수 호출 구문
        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, storageId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}