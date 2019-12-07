package Lesson_7.Homework.Server;

import java.sql.*;
import java.util.HashSet;

public class AuthService {

    private static Connection connection;
    private static Statement stmt;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");

            try {
                connection = DriverManager.getConnection("jdbc:sqlite:main.db");
                stmt = connection.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void dissconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getNicknameByLoginAndPass(String login, String pass) {
        String sql = String.format("SELECT nickname FROM users WHERE login = '%s' and password = '%s'", login, pass);
        try {
            ResultSet rs = stmt.executeQuery(sql);

            //вернуть после запроса может либо 1 строка (пользователь найден), либо ничего (пользователь найден)
            if (rs.next()) { //проверяем, что что-то нашлось
                return rs.getString(1); //возвращает первый столбец (нумерация столбцов с ЕДЕНИЦЫ)
//            return rs.getString("nickaname"); //возвращает столбец "nickname"
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
