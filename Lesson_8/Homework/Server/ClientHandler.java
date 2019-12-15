package Lesson_8.Homework.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler {

    private final static String STOP_CLIENT_COMMAND_MESSAGE = "/end";
    private final static String STOP_SERVER_COMMAND_MESSAGE = "/serverClosed";
    private final static String AUTH_SERVER_COMMAND_MESSAGE = "/auth";
    private final static String REG_COMMAND_MESSAGE = "/reg";
    private final static String PRIVATE_MESSAGE_COMMAND_MESSAGE = "/w";
    private final static String ADD_BLACKLIST_COMMAND_MESSAGE = "/blacklist";
    private final static String DELETE_BLACKLIST_COMMAND_MESSAGE = "/dblacklist";
    private final static String SEE_BLACKLIST_COMMAND_MESSAGE = "/sblacklist";
    private final static String REGISTRATED_COMMAND_MESSAGE = "/regOk";
    private final static String NOT_REGISTRATED_COMMAND_MESSAGE = "/regNotOk";


    private final static String AUTH_OK = "Вы успешно авторизовались";
    private final static String USER_ALREADY_ONLINE = "Этот пользователь уже онлайн!";
    private final static String INCORRECT_LOGIN_OR_PASSWORD = "Неверный логин или пароль";
    private final static String USER_BLOCKED = "Пользователь заблокирован!";
    private final static String USER_UNBLOCKED = "Пользователь разблокирован!";
    private final static String INCORRECT_USER_NICKNAME = "Такого пользователя нет";
    private final static String YOU_BLOCKLIST_IS_EMPTY = "Ваш ЧС пуст";
    private final static String YOU_BLOCKLIST_IS = "Ваш ЧС: ";
    private final static String YOU_CANT_ADD_YOUR_SELF_IN_BLACKLIST = "Вы не можете добавить самого себя в черный список";
    private final static String THIS_NICK_IS_ALREADY_USED = " Это имя пользователя уже занято\n";
    private final static String THIS_LOGIN_IS_ALREADY_USED = " Этот логин уже занят\n";
    
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private MainServ serv;
    private String nick;

    private ArrayList<String> blocklist;

    public ArrayList<String> getBlocklist() {
        return blocklist;
    }

    public ClientHandler(MainServ serv, Socket socket) {
        try {
            this.serv = serv;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        //авторизация
                        while (true) {
                            String msg = in.readUTF();
                            if (msg.startsWith(AUTH_SERVER_COMMAND_MESSAGE)) {
                                String login = msg.split(" ")[1];
                                String pass = msg.split(" ")[2];
                                String newNick = AuthService.getNicknameByLoginAndPass(login, pass);
                                if (newNick != null) {
                                    if (serv.checkUserOnline(newNick)) {
                                        sendMsg(USER_ALREADY_ONLINE);
                                    } else {
                                        nick = newNick;
                                        blocklist = AuthService.getBlacklistByNickname(nick);
                                        serv.broadcastMessageIgnoreBlacklist(nick, " подключился!");
                                        sendMsg(AUTH_OK);
                                        serv.addUser(ClientHandler.this);
                                        break;
                                    }
                                } else {
                                    sendMsg(INCORRECT_LOGIN_OR_PASSWORD);
                                }
                            } else if (msg.startsWith(REG_COMMAND_MESSAGE)) {
                                String nickname = msg.split(" ")[1];
                                String login = msg.split(" ")[2];
                                String pass = msg.split(" ")[3];

                                if (!AuthService.checkNicknameInDB(nickname)) {
                                    if (!AuthService.checkLoginInDB(login)) {
                                        AuthService.regUser(nickname, login, pass);
                                        out.writeUTF(REGISTRATED_COMMAND_MESSAGE);
                                    } else {
                                        out.writeUTF(NOT_REGISTRATED_COMMAND_MESSAGE + THIS_LOGIN_IS_ALREADY_USED);
                                    }
                                } else {
                                    out.writeUTF(NOT_REGISTRATED_COMMAND_MESSAGE + THIS_NICK_IS_ALREADY_USED);
                                }


                            }
                        }


                        //обработка сообщений
                        while (true) {
                            //получаем сообщение от клиента
                            String msg = in.readUTF();
                            if (msg.equals(STOP_CLIENT_COMMAND_MESSAGE)) {
                                out.writeUTF(STOP_SERVER_COMMAND_MESSAGE);
                                break;

                            } else if (msg.startsWith(PRIVATE_MESSAGE_COMMAND_MESSAGE)) {
                                String nickTo = msg.split(" ")[1];
                                String messageToNick = msg.split(" ", 3)[2];
                                serv.sendMessageToNickname(messageToNick, nickTo, nick);

                            } else if (msg.startsWith(ADD_BLACKLIST_COMMAND_MESSAGE)) {
                                String nickBlocked = msg.split(" ")[1];
                                if (!nick.equals(nickBlocked)) {
                                    if (AuthService.checkNicknameInDB(nickBlocked)) {
                                        AuthService.addUserToBlacklist(nick, nickBlocked);
                                        blocklist = AuthService.getBlacklistByNickname(nick);
                                        serv.broadcastClientsList();
                                        out.writeUTF(USER_BLOCKED);
                                    } else {
                                        out.writeUTF(INCORRECT_USER_NICKNAME);
                                    }
                                } else {
                                    out.writeUTF(YOU_CANT_ADD_YOUR_SELF_IN_BLACKLIST);
                                }

                            } else if (msg.startsWith(DELETE_BLACKLIST_COMMAND_MESSAGE)) {
                                String nickBlocked = msg.split(" ")[1];
                                if (AuthService.checkNicknameInDB(nickBlocked)) {
                                    AuthService.deleteUserFromBlacklist(nick, nickBlocked);
                                    blocklist = AuthService.getBlacklistByNickname(nick);
                                    serv.broadcastClientsList();
                                    out.writeUTF(USER_UNBLOCKED);
                                } else {
                                    out.writeUTF(INCORRECT_USER_NICKNAME);
                                }

                            } else if (msg.equals(SEE_BLACKLIST_COMMAND_MESSAGE)) {
                                if (blocklist.size() == 0) {
                                    out.writeUTF(YOU_BLOCKLIST_IS_EMPTY);
                                } else {
                                    String blocklistToString = blocklist.toString();
                                    out.writeUTF(YOU_BLOCKLIST_IS + blocklistToString.substring(1, blocklistToString.length() - 1));

                                }

                            } else {
                                //отправляем сообщение всем пользователям
                                serv.broadcastMessage(nick, ": " + msg);
                            }
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            serv.deleteUser(ClientHandler.this, nick);
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNick() {
        return nick;
    }

    public boolean checkUserInBlacklist(String nick) {
        return blocklist.contains(nick);
    }
}
