package DBD_env_unification;

import java.sql.*;

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

}



