package com.userservice.user;

import com.userservice.registration.token.ConfirmationToken;
import com.userservice.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService {
    private static String dbUrl;
    @Value("${spring.datasource.url}")
    public void setDbUrl(String dbUrl) { AppUserService.dbUrl = dbUrl; }
    private static String dbUser;
    @Value("${spring.datasource.username}")
    public void setDbUser(String dbUser) { AppUserService.dbUser = dbUser; }

    private static String dbPassword;
    @Value("${spring.datasource.password}")
    public void setDbPassword(String dbPassword) { AppUserService.dbPassword = dbPassword; }

    private static final String SELECT_ALL_QUERY = "select * from app_user";

    public static AppUser getUserByEmail(String userEmail) {

        final String GET_USER_QUERY = "select * from app_user where email = ?";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_QUERY)) {
            preparedStatement.setString(1,userEmail);
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()) {

                int id = rs.getInt("id");
                String userRole = rs.getString("app_user_role");
                String displayName = rs.getString("display_name");
                String email = rs.getString("email");
                boolean enabled = rs.getBoolean("enabled");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                boolean locked = rs.getBoolean("locked");
                String password = rs.getString("password");

                return AppUser.builder()
                        .id((long) id)
                        .appUserRole(AppUserRole.valueOf(userRole))
                        .displayName(displayName)
                        .email(email)
                        .enabled(enabled)
                        .firstName(firstName)
                        .lastName(lastName)
                        .locked(locked)
                        .password(password)
                        .build();

            }

        } catch (SQLException e) {
            printSQLException(e);
        }

        return null;
    }

    public static void updateUserPassword() {
        // TODO: Put mapping /user/password
    }

    public static void updateDisplayName() {
        // TODO: Put mapping /user/displayname
    }

    public static void updateEmail() {
        // TODO: Put mapping /user/email
    }

    public static void updateName() {
        // TODO: Put mapping /user/name
    }

    public static void lockUser() {
        // TODO: Find correct method? Changed locked status
    }


    public static void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}