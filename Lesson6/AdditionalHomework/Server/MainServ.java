package Lesson_6.OnLesson.Server;

import Lesson_6.OnLesson.Client.Main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class MainServ {

    private Vector<ClientHandler> clients;

    public MainServ(int port)  {
        clients = new Vector<>();
        ServerSocket server = null;

        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Сервер запущен на порту " + port);

        int socketID = 1;

        try {
            while (true) {
                Socket socket = server.accept();
                broadcastMessage("Клиент" + socketID + " подключился!");
                clients.add(new ClientHandler(this, socket, socketID));
                socketID++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void broadcastMessage(String msg) {
        for (ClientHandler ch: clients) {
            ch.sendMsg(msg);
        }
    }

    public void deleteUser(ClientHandler ch, int socketID) {
        clients.remove(ch);
        broadcastMessage("Клиент" + socketID + " отключился");
    }



}
