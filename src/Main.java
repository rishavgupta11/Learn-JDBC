import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final String url = "jdbc:mysql://localhost:3306/lenden"; // jdbc connection string to clipboard

    private static final String username = "root"; // database username

    private static final String password = "Rishav@123"; // database password

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);
            String debit_query = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
            String credit_query = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
            PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);
            PreparedStatement creditPreparedStatement = connection.prepareStatement(credit_query);

            Scanner sc = new Scanner(System.in);
            System.out.println("Enter Account Number: ");
            int account_number = sc.nextInt();

            System.out.println("Enter Amount: ");
            double amount = sc.nextDouble();

            debitPreparedStatement.setDouble(1, amount);
            debitPreparedStatement.setInt(2, 101);
            creditPreparedStatement.setDouble(1, amount);
            creditPreparedStatement.setInt(2, 102);
            debitPreparedStatement.executeUpdate();
            creditPreparedStatement.executeUpdate();

            if (isSufficient(connection, 101, amount)) {
               connection.commit();
                System.out.println("Transaction Successfully");
            }else {
                connection.rollback();
                System.out.println("Transaction Failed!!");
            }

        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static boolean isSufficient(Connection connection, int account_number, double amount) {
        try {
            String query = "SELECT balance FROM accounts WHERE account_number = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, account_number);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double current_balance = resultSet.getDouble("balance");
                if (amount > current_balance) {
                    return false;
                }else {
                    return true;
                }
            }

        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
