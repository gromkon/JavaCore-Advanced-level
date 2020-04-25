package Lesson_8.Homework.Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

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

    public static ArrayList<String> getBlacklistByNickname(String nickname) {
        ArrayList<String> iDsBlockedUsers = new ArrayList<>();
        ArrayList<String> loginsBlockedUsers = new ArrayList<>();

        String sqlGetLoginId = String.format("SELECT id FROM users WHERE nickname = '%s'", nickname);

        try {
            ResultSet rsGetLoginId = stmt.executeQuery(sqlGetLoginId);
            if (rsGetLoginId.next()) {
                String sqlGetBlockIDs = String.format("SELECT idBlockedUser FROM blacklist WHERE idUser = '%s'", rsGetLoginId.getString("id"));

                ResultSet rsGetBlockIDs = stmt.executeQuery(sqlGetBlockIDs);

                while (rsGetBlockIDs.next()) {
                    iDsBlockedUsers.add(rsGetBlockIDs.getString("idBlockedUser"));
                }

                for (String idBlockedUser : iDsBlockedUsers) {
                    String sqlGetBlockedLogin = String.format("SELECT nickname FROM users WHERE id = '%s'", idBlockedUser);
                    ResultSet rsGetBlockedLogin = stmt.executeQuery(sqlGetBlockedLogin);
                    loginsBlockedUsers.add(rsGetBlockedLogin.getString("nickname"));

                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loginsBlockedUsers;

    }

    public static void addUserToBlacklist(String nickUser, String nickBlockedUser) {
        String sqlGetLoginBlockedUserId = String.format("SELECT id FROM users WHERE nickname = '%s'", nickBlockedUser);
        String sqlGetLoginUserId = String.format("SELECT id FROM users WHERE nickname = '%s'", nickUser);

        try {
            ResultSet rsGetLoginBlockedUserId = stmt.executeQuery(sqlGetLoginBlockedUserId);
            String idBlockedUser = rsGetLoginBlockedUserId.getString("id");

            ResultSet rsGetLoginUserId = stmt.executeQuery(sqlGetLoginUserId);
            String idUser = rsGetLoginUserId.getString("id");

            String sqlCheckAlreadyOnBlacklist = String.format("SELECT * FROM blacklist WHERE idUser = '%s' AND idBlockedUser = '%s'", idUser, idBlockedUser);
            ResultSet rsCheckAlreadyOnBlacklist = stmt.executeQuery(sqlCheckAlreadyOnBlacklist);

            if (rsCheckAlreadyOnBlacklist.next()) {
                return;
            }

            String sqlAddToBlacklist = String.format("INSERT INTO blacklist (idUser, idBlockedUser)\n" +
                    "VALUES (%s, %s)", idUser, idBlockedUser);

            stmt.execute(sqlAddToBlacklist);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteUserFromBlacklist(String nickUser, String nickBlockedUser) {
        String sqlGetLoginBlockedUserId = String.format("SELECT id FROM users WHERE nickname = '%s'", nickBlockedUser);
        String sqlGetLoginUserId = String.format("SELECT id FROM users WHERE nickname = '%s'", nickUser);


        try {
            ResultSet rsGetLoginBlockedUserId = stmt.executeQuery(sqlGetLoginBlockedUserId);
            String idBlockedUser = rsGetLoginBlockedUserId.getString("id");

            ResultSet rsGetLoginUserId = stmt.executeQuery(sqlGetLoginUserId);
            String idUser = rsGetLoginUserId.getString("id");

            String sqlDeleteUserFromBlacklist = String.format("DELETE FROM blacklist\n" +
                    "WHERE idUser = %s AND idBlockedUser = %s", idUser, idBlockedUser);

            stmt.execute(sqlDeleteUserFromBlacklist);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkNicknameInDB(String nick) {
        String sqlCheckNickname = String.format("SELECT * FROM users WHERE nickname = '%s'", nick);
        try {
            ResultSet rsCheckNickname = stmt.executeQuery(sqlCheckNickname);
            return rsCheckNickname.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean checkLoginInDB(String login) {
        String sqlCheckLogin = String.format("SELECT * FROM users WHERE login = '%s'", login);
        try {
            ResultSet rsCheckLogin = stmt.executeQuery(sqlCheckLogin);
            return rsCheckLogin.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void regUser(String nickname, String login, String pass) {
        String sqlGetLastId = String.format("SELECT id FROM users");

        try {
            ResultSet rsGetLastId = stmt.executeQuery(sqlGetLastId);
            String id = rsGetLastId.getString("id");
            while (rsGetLastId.next()) {
                id = rsGetLastId.getString("id");
            }
            int newId = Integer.parseInt(id) + 1;

            String sqlRegUser = String.format("INSERT INTO users (id, login, password, nickname)\n" +
                    "VALUES (%s, '%s', '%s', '%s')", newId, login, pass, nickname);

            stmt.execute(sqlRegUser);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
