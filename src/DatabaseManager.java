import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String URL =
            "jdbc:mysql://localhost:3306/securepayment";

    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void savePayment(String customerName, BigDecimal amount)
            throws SQLException {

        String sql =
                "INSERT INTO payments (customer_name, amount) VALUES (?, ?)";

        try (Connection connection =
                     DriverManager.getConnection(URL, USER, PASSWORD);

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            // Parameters keep user input separate from the SQL command.
            statement.setString(1, customerName);
            statement.setBigDecimal(2, amount);

            statement.executeUpdate();
        }
    }
}