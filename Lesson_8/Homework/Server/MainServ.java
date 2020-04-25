package Lesson_8.Homework.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Vector;

// TODO выводить последние 10  сообщений при подключении нового клиента

public class MainServ {

    private final static String CLIENTS_LIST_BROADCAST_MESSAGE = "/clientslist ";
    private final static String BLACKLIST_MARK = "/b";

    private final static String SERVER_ONLINE_MESSAGE = "Cервер запущен!";

    private Vector<ClientHandler> clients;

    public MainServ(int port)  {
        clients = new Vector<>();
        ServerSocket server = null;

        try {
            AuthService.connect();
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(SERVER_ONLINE_MESSAGE);


        try {
            while (true) {
                Socket socket = server.accept();
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuthService.dissconnect();
        }

    }

    public void broadcastMessage(String nickFrom, String msg) {
        String outMsg = nickFrom + msg;
        for (ClientHandler ch: clients) {
            if (clientSearchByNick(nickFrom) != null && !ch.checkUserInBlacklist(clientSearchByNick(nickFrom).getNick())) {
                ch.sendMsg(outMsg);
            }
        }
    }

    public void broadcastMessageIgnoreBlacklist(String nickFrom, String msg) {
        String outMsg = nickFrom + msg;
        for (ClientHandler ch: clients) {
            ch.sendMsg(outMsg);
        }
    }

    //проверяет, активен ли пользователь с данным никнеймом
    public boolean checkUserOnline(String nick) {
        for (ClientHandler ch: clients) {
            if (ch.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public ClientHandler clientSearchByNick(String nick) {
        for (ClientHandler client: clients) {
            if (client.getNick().equals(nick)) {
                return client;
            }
        }
        return null;
    }

    //отправляет сообщение выбранному пользователю
    public void sendMessageToNickname(String msg, String nickTo, String nickFrom) {
        ClientHandler clientTo = clientSearchByNick(nickTo);
        ClientHandler clientFrom = clientSearchByNick(nickFrom);
        if (clientTo != null) {
            if (clientTo.checkUserInBlacklist(nickFrom)) {
                clientFrom.sendMsg("Пользователь " + nickTo + " Вас заблокировал");
            } else {
                clientTo.sendMsg("От " + nickFrom + ": " + msg);
                clientFrom.sendMsg("К " + nickTo + ": " + msg);
            }
        } else {
            clientFrom.sendMsg("Пользователь с ником " + nickTo + " не найден");
        }

    }

    public void addUser(ClientHandler ch) {
        clients.add(ch);
        broadcastClientsList();
    }

    public void deleteUser(ClientHandler ch, String nick) {
        clients.remove(ch);
        broadcastClientsList();
        broadcastMessage(nick, " отключился");
    }

    public void broadcastClientsList() {
        for (ClientHandler chAllUsers : clients) {
            StringBuilder bl = new StringBuilder();
            bl.append(CLIENTS_LIST_BROADCAST_MESSAGE);
            for (ClientHandler chUser : clients) {
                if (chAllUsers.checkUserInBlacklist(chUser.getNick())) {
                    bl.append(chUser.getNick() + BLACKLIST_MARK +" ");
                } else {
                    bl.append(chUser.getNick() + " ");
                }
            }
            chAllUsers.sendMsg(bl.toString());
        }
    }





}
