package Lesson_6.Homework;

import java.io.DataInputStream;
import java.io.IOException;

public class Input implements Runnable {

    DataInputStream in;

    public Input(DataInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String msg = in.readUTF();
                System.out.println(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
