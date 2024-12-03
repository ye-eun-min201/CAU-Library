import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;



public class User {
    private String user_id;
    private String name;
    private String email;
    private String department;
    private String status;
    private List<Integer> loanIds = new ArrayList<Integer>();   // 대출 중인 자료 list
    private List<Integer> overdueIds = new ArrayList<Integer>();    // 연체 자료 list


    public User(String user_id, Connection con){
        String query =
                "select User_ID, NAME, Mail, Department, Status from user where User_ID = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(query))
        {
            boolean userFound = false;
            while (!userFound) {
                preparedStatement.setString(1, user_id);  // user_id로 쿼리 실행
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    // 결과가 있으면 데이터를 설정하고 반복 종료
                    this.user_id = rs.getString("User_ID");
                    name = rs.getString("NAME");
                    email = rs.getString("Mail");
                    department = rs.getString("Department");
                    status = rs.getString("Status");
                    userFound = true;  // 사용자 정보를 찾았으므로 반복 종료
                } else {
                    // 결과가 없으면 사용자에게 새로운 ID를 입력받음
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("사용자 id 입력 (ex: 20230840) : ");
                    user_id = scanner.nextLine();  // 새로 입력받은 user_id로 쿼리 실행
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to select : " + e.getMessage());
        }
        initLoanList(con);
        initExtendList(con);
    }

    public void printUser(Connection con){
        System.out.println("사용자 정보");
        System.out.println("ID: "+user_id);
        System.out.println("name: "+name);
        System.out.println("email: "+email);
        System.out.println("dep: "+department);
        System.out.println("status: "+status);
        printLoanList(con);
    }

    public void borrowBook(int book_id, Connection con) {

        String checkStatusQuery = "SELECT Book_status FROM book WHERE Data_ID = ?";
        String insertLoanQuery = "INSERT INTO loan (User_ID, Data_ID, Start_time, End_time) VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY))";
        if(loanIds.size()>=15){
            System.out.println("대출 가능한 자료 개수를 초과하였습니다. 대출이 불가능합니다.");
            return;
        }

        // 1. 도서 상태 확인
        try (PreparedStatement checkStatusStmt = con.prepareStatement(checkStatusQuery)) {
            checkStatusStmt.setInt(1, book_id);
            ResultSet resultSet = checkStatusStmt.executeQuery();
            if (resultSet.next()) {
                String status = resultSet.getString("Book_status");
                if (!"대출 가능".equals(status)) {
                    System.out.println("해당 도서는 현재 대출이 불가능합니다.");
                    return;
                }
            } else {
                System.out.println("해당 도서는 존재하지 않습니다.");
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2. 대출 처리
        try (PreparedStatement insertLoanStmt = con.prepareStatement(insertLoanQuery)) {
            con.setAutoCommit(false);

            // 대출 기록 추가
            insertLoanStmt.setString(1, this.user_id);
            insertLoanStmt.setInt(2, book_id);
            insertLoanStmt.executeUpdate();

            con.commit();
            System.out.println("도서가 성공적으로 대출되었습니다.");

            String selectQuery = "SELECT loan_ID FROM loan WHERE User_ID = ? ORDER BY Start_time DESC LIMIT 1";
            PreparedStatement selectStmt = con.prepareStatement(selectQuery);
            selectStmt.setString(1, this.user_id);
            ResultSet rs1 = selectStmt.executeQuery();

            if (rs1.next()) {
                int loanID = rs1.getInt("loan_ID");
                loanIds.add(loanID);
            }
        } catch (SQLException e) {
            try {
                con.rollback();
                System.out.println("대출 처리 중 오류 발생, 롤백 수행");
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // 반납하기
    public void returnBook(int loan_id, Connection con) {
        if(loanIds.size()<1){
            System.out.println("대충 중인 자료가 존재하지 않습니다.");
            return;
        }

        // 대출 중인지 확인
        int i;
        for(i = 0; i<loanIds.size(); i++){
            if(loanIds.get(i) == loan_id)
                break;
        }
        if(i==loanIds.size()){
            System.out.println("이미 반납이 완료되었습니다.");
            return;
        }

        String insertReturnQuery = "INSERT INTO `return` (Loan_ID, End_time) VALUES (?, NOW())";
        try (PreparedStatement insertReturnStmt = con.prepareStatement(insertReturnQuery)) {
            con.setAutoCommit(false);

            // 대출 기록 추가
            insertReturnStmt.setInt(1, loan_id);
            insertReturnStmt.executeUpdate();

            con.commit();
            System.out.println("도서가 성공적으로 반납되었습니다.");

            for (i = 0; i < loanIds.size(); i++) {
                if(loanIds.get(i)==loan_id){
                    loanIds.remove(i);
                    break;
                }
            }

        } catch (SQLException e) {
            try {
                con.rollback();
                System.out.println("반납 처리 중 오류 발생, 롤백 수행");
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // 연장하기
    public void ExtendBook(int loan_id, Connection con) {
        String checksQuery = "SELECT Start_time FROM overdue WHERE Overdue_ID = ?";
        if(loanIds.size()<1){
            System.out.println("대충 중인 자료가 존재하지 않습니다.");
            return;
        }

        // 대출 중인지 확인
        int i;
        for(i = 0; i<loanIds.size(); i++){
            if(loanIds.get(i) == loan_id)
                break;
        }
        if(i==loanIds.size()){
            System.out.println("이미 반납이 완료되었습니다.");
            return;
        }

        // 1. 연장 가능한지 -> 오늘 날짜가 반납일로부터 3일전이면 연장 가능 -- 해당 코드 삭제 시 연장 확인 가능
        try (PreparedStatement checkStmt = con.prepareStatement(checksQuery)) {
            checkStmt.setInt(1, loan_id);
            ResultSet resultSet = checkStmt.executeQuery();
            if (resultSet.next()) {
                Date sqlDate = resultSet.getDate("Start_time"); // 예제 날짜 (YYYY-MM-DD)
                LocalDate today = LocalDate.now();
                LocalDate targetDate = sqlDate.toLocalDate();
                long daysDifference = ChronoUnit.DAYS.between(today, targetDate);
                if (daysDifference > 3) {
                    System.out.println("반납 예정일 3일전부터 연장이 가능합니다.");
                    return;
                } else if (daysDifference < 0) {
                    System.out.println("이미 반납 예정일이 지났습니다.");
                    return;
                }
            } else {
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        String insertExtendQuery = "INSERT INTO `extend` (Extend_ID, Extend_time, New_End_time) VALUES (?, NOW(), DATE_ADD((SELECT End_time FROM loan WHERE Loan_ID = ?), INTERVAL 7 DAY))";
        try (PreparedStatement insertExtendStmt = con.prepareStatement(insertExtendQuery)) {
            con.setAutoCommit(false);

            // 대출 기록 추가
            insertExtendStmt.setInt(1, loan_id);
            insertExtendStmt.setInt(2, loan_id);
            insertExtendStmt.executeUpdate();

            con.commit();
            System.out.println("도서의 반납일자가 성공적으로 연장되었습니다.");

        } catch (SQLException e) {
            try {
                con.rollback();
                System.out.println("연장 처리 중 오류 발생, 롤백 수행");
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }




    // 대출 현황 리스트 만들기
    public void initLoanList(Connection con) {
        // 대출 중인 도서의 Loan_ID를 조회하기 위한 쿼리
        String checkLoanQuery = "SELECT Loan_ID FROM loan WHERE User_ID = ? ";
        String checkReturnQuery = "SELECT Loan_ID FROM `return` WHERE Loan_ID = ? ";

        // 대출 중인 Loan_ID를 저장할 리스트

        try (PreparedStatement checkLoanStmt = con.prepareStatement(checkLoanQuery)) {
            checkLoanStmt.setString(1, user_id);
            ResultSet ln_rs = checkLoanStmt.executeQuery();

            // 결과에서 Loan_ID를 가져와 ArrayList에 저장
            while (ln_rs.next()) {
                int loan_id = ln_rs.getInt("Loan_ID");
                try (PreparedStatement checkReturnStmt = con.prepareStatement(checkReturnQuery)) {
                    checkReturnStmt.setInt(1, loan_id);
                    ResultSet rt_rs = checkReturnStmt.executeQuery();
                    if (!rt_rs.next()){
                        loanIds.add(loan_id); // Loan_ID를 ArrayList에 추가
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 대출 현황 출력하기
    public void printLoanList(Connection con) {
        System.out.println("대출 현황");
        for (int i = 0; i < loanIds.size(); i++) {
            System.out.println("<"+(i+1)+">");
            String query =
                    "select Loan_ID, Start_time, End_time, Data_ID from loan where Loan_ID = ?";
            String query1 =
                    "select Title, Author from data where Data_ID = ?";

            String query2 =
                    "select Start_time from overdue where Overdue_ID = ?";

            try (PreparedStatement preparedStatement = con.prepareStatement(query);
                 PreparedStatement preparedStatement1 = con.prepareStatement(query1);
                 PreparedStatement preparedStatement2 = con.prepareStatement(query2))
            {
                preparedStatement.setInt(1, loanIds.get(i));  // user_id로 쿼리 실행
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {

                    System.out.println("대출 ID: " + rs.getInt("Loan_ID"));
                    System.out.println("대출일: " + rs.getDate("Start_time"));

                    preparedStatement2.setInt(1, rs.getInt("Loan_ID"));  // user_id로 쿼리 실행
                    ResultSet rs2 = preparedStatement2.executeQuery();

                    if (rs2.next()) {
                        System.out.println("반납 예정일: " + rs2.getDate("Start_time"));
                    }

                    System.out.println("대출 자료");
                    System.out.println("자료 ID: " + rs.getInt("Data_ID"));

                    preparedStatement1.setInt(1, rs.getInt("Data_ID"));  // user_id로 쿼리 실행
                    ResultSet rs1 = preparedStatement1.executeQuery();

                    if (rs1.next()) {
                        System.out.println("제목: " + rs1.getString("Title"));
                        System.out.println("작가: " + rs1.getString("Author"));
                    }
                }

            } catch (SQLException e) {
                System.out.println("Failed to select : " + e.getMessage());
            }
        }
    }


    // 연체 리스트 초기화
    public void initExtendList(Connection con) {
        overdueIds = new ArrayList<>();
        String query = "SELECT Overdue_ID FROM overdue WHERE End_time IS NULL AND Start_time < NOW()";
        try (PreparedStatement pstmt = con.prepareStatement(query))
        {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int overdueId = rs.getInt("Overdue_ID");
                overdueIds.add(overdueId);

                String updateStatusQuery = "UPDATE overdue SET Status = 1 WHERE Overdue_ID = ?";
                try (PreparedStatement updateStmt = con.prepareStatement(updateStatusQuery)) {
                    updateStmt.setInt(1, overdueId);
                    updateStmt.executeUpdate();
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }



    // 연체 자료 print 함수
    public void printOverdueList(Connection con) {
        if (overdueIds.isEmpty()) {
            System.out.println("연체된 자료가 없습니다.");
        } else {
            System.out.println("연체 자료 목록:");
            for (int i = 0; i < overdueIds.size(); i++) {
                System.out.println("<"+(i+1)+">");
                String query =
                        "select Loan_ID, Start_time, End_time, Data_ID from loan where Loan_ID = ?";
                String query1 =
                        "select Title, Author from data where Data_ID = ?";

                String query2 =
                        "select Start_time from overdue where Overdue_ID = ?";

                try (PreparedStatement preparedStatement = con.prepareStatement(query);
                     PreparedStatement preparedStatement1 = con.prepareStatement(query1);
                     PreparedStatement preparedStatement2 = con.prepareStatement(query2))
                {
                    preparedStatement.setInt(1, overdueIds.get(i));  // user_id로 쿼리 실행
                    ResultSet rs = preparedStatement.executeQuery();

                    if (rs.next()) {

                        System.out.println("대출 ID: " + rs.getInt("Loan_ID"));
                        System.out.println("대출 자료");
                        System.out.println("자료 ID: " + rs.getInt("Data_ID"));

                        preparedStatement1.setInt(1, rs.getInt("Data_ID"));  // user_id로 쿼리 실행
                        ResultSet rs1 = preparedStatement1.executeQuery();

                        if (rs1.next()) {
                            System.out.println("제목: " + rs1.getString("Title"));
                            System.out.println("작가: " + rs1.getString("Author"));
                        }
                        preparedStatement2.setInt(1, rs.getInt("Loan_ID"));  // user_id로 쿼리 실행
                        ResultSet rs2 = preparedStatement2.executeQuery();

                        if (rs2.next()) {
                            System.out.println("반납 예정일: " + rs2.getDate("Start_time"));
                        }
                        System.out.println("연체료: " + calFine(overdueIds.get(i), con));
                    }

                } catch (SQLException e) {
                    System.out.println("Failed to select : " + e.getMessage());
                }
            }
        }
    }

    // 연체료 계산하기
    public int calFine(int loan_id, Connection con) {
        int fine = -1;
        String fineQuery = "SELECT calculate_fine(Start_time, NOW()) AS Fine FROM overdue WHERE Overdue_ID = ?";
        try (PreparedStatement pstmt = con.prepareStatement(fineQuery)) {
            pstmt.setInt(1, loan_id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                fine = rs.getInt("Fine");
            }
        } catch (SQLException e) {
            System.out.println("Failed to calculate : " + e.getMessage());
        }
        return fine;
    }

    // 연체료 결제하기

    public void payFine(int loanId, Connection con){
        if (!overdueIds.contains(loanId)) {
            System.out.println("해당 대출 ID는 연체료 결제 대상이 아닙니다.");
            return;
        }
        if(loanIds.contains(loanId))
        {
            System.out.println("반납 후 연체료 결제가 가능합니다.");
            return;
        }

        int fine = calFine(loanId, con);

        String updateFineQuery = "UPDATE overdue SET Fine = ?, Status = 1, End_time = NOW() WHERE Overdue_ID = ?";

        try (PreparedStatement updateStmt = con.prepareStatement(updateFineQuery)) {
            updateStmt.setInt(1, fine);
            updateStmt.setInt(2, loanId);
            updateStmt.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Failed to calculate : " + e.getMessage());
        }

        System.out.println("연체료 " + fine + "원 결제 완료되었습니다.");
        overdueIds.remove((Integer) loanId);
    }

}
