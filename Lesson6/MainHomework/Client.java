package Lesson_6.Homework;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private final static String IP_ADRESS = "localhost";
    private final static int PORT = 8081;

    private final static String CONNECTED_MESSAGE = "Подключился к серверу " + IP_ADRESS + ":" + PORT;


    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket(IP_ADRESS, PORT);
            System.out.println(CONNECTED_MESSAGE);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            Scanner console = new Scanner(System.in);

            Output output = new Output(console, out);
            Thread threadOutput = new Thread(output);
            threadOutput.start();

            DataInputStream in = new DataInputStream(socket.getInputStream());

            Input input = new Input(in);
            Thread threadInput = new Thread(input);
            threadInput.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
