package Lesson_7.Homework.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class MainServ {

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

    public void broadcastMessage(String msg) {
        for (ClientHandler ch: clients) {
            ch.sendMsg(msg);
        }
    }

    public void addUser(ClientHandler ch) {
        clients.add(ch);
    }

    //проверяет, активен ли пользователь с данным никнеймом
    public boolean checkUser(String nick) {
        for (ClientHandler ch: clients) {
            if (ch.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    private ClientHandler clientSearchByNick(String nick) {
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
            clientTo.sendMsg("От " + nickFrom + ": " + msg);
            clientFrom.sendMsg("К " + nickTo + ": " + msg);
        } else {
            clientFrom.sendMsg("Пользователь " + nickTo + " не в сети");
        }

    }

    public void deleteUser(ClientHandler ch, String nick) {
        clients.remove(ch);
        broadcastMessage(nick + " отключился");
    }



}
