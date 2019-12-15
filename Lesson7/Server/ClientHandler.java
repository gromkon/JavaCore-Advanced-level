package Lesson_7.Homework.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    private final static String STOP_CLIENT_COMMAND_MESSAGE = "/end";
    private final static String STOP_SERVER_COMMAND_MESSAGE = "/serverClosed";
    private final static String AUTH_SERVER_COMMAND_MESSAGE = "/auth";
    private final static String PRIVATE_MESSAGE_COMMAND_MESSAGE = "/w";

    private final static String AUTH_OK = "Вы успешно авторизовались";
    private final static String USER_ALREADY_ONLINE = "Этот пользователь уже онлайн!";
    private final static String INCORRECT_LOGIN_OR_PASSWORD = "Неверный логин или пароль";

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private MainServ serv;
    private String nick;

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
                                    if (serv.checkUser(newNick)) {
                                        sendMsg(USER_ALREADY_ONLINE);
                                    } else {
                                        nick = newNick;
                                        serv.broadcastMessage(nick + " подключился!");
                                        sendMsg(AUTH_OK);
                                        serv.addUser(ClientHandler.this);
                                        break;
                                    }
                                } else {
                                    sendMsg(INCORRECT_LOGIN_OR_PASSWORD);
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
                            } else {
                                //отправляем сообщение всем пользователям
                                serv.broadcastMessage( nick + ": " + msg);
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
}
