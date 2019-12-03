package Lesson_6.Homework;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    private final static int PORT = 8081;
    private final static String START_MESSAGE = "Сервер запущен на порту " + PORT;
    private final static String CLIENT_CONNECTED = "Клиент подключился";

    public static void main(String[] args) {
        ServerSocket server = null;
        Socket socket = null;

        try {
            server = new ServerSocket(PORT);
            System.out.println(START_MESSAGE);
            socket = server.accept();
            System.out.println(CLIENT_CONNECTED);

            DataInputStream in = new DataInputStream(socket.getInputStream());

            Input input = new Input(in);
            Thread threadInput = new Thread(input);
            threadInput.start();

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            Scanner console = new Scanner(System.in);

            Output output = new Output(console, out);
            Thread threadOutput = new Thread(output);
            threadOutput.start();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket.isClosed()) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
