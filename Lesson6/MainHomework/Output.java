package Lesson_6.Homework;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Output implements Runnable {

    Scanner console;
    DataOutputStream out;

    public Output(Scanner console, DataOutputStream out) {
        this.console = console;
        this.out = out;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String msg = console.nextLine();
                out.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

