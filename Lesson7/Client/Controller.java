package Lesson_7.Homework.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Controller  {

    @FXML
    TextArea textArea;
    @FXML
    TextField textField;
    @FXML
    HBox bottomPanel;
    @FXML
    HBox upperPanel;
    @FXML
    TextField loginField;
    @FXML
    PasswordField passwordField;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private final static String IP_ADRESS = "localhost";
    private final static int PORT = 8080;

    private final static String STOP_CLIENT_COMMAND_MESSAGE = "/end";
    private final static String STOP_SERVER_COMMAND_MESSAGE = "/serverClosed";
    private final static String AUTH_SERVER_COMMAND_MESSAGE = "/auth";
    private final static String AUTH_OK_MESSAGE = "Вы успешно авторизовались";

    private final static String WELCOME = "Вы успешно авторизовались!\nДобро пожаловать в чат!\n";


    private boolean isAuthorized;


    public void setAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
        if (isAuthorized) {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
        } else {
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
        }
    }



    public void connect() {
        try {
            socket = new Socket(IP_ADRESS, PORT);
//            System.out.println("Подключились к серверу!");
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        //проверка авторизации
                        while (true) {
                            String msg = in.readUTF();
                            if (msg.equals(AUTH_OK_MESSAGE)) {
                                setAuthorized(true);
                                textArea.clear();
                                textArea.appendText(WELCOME);
                                break;
                            } else {
                                setAuthorized(false);
                                textArea.appendText(msg + "\n");
                            }
                        }


                        while (true) {
                            //сообщение, которое приходит от сервера добавляет в textArea
                            String msg = in.readUTF();
                            if (msg.equals(STOP_SERVER_COMMAND_MESSAGE)) {
                                break;
                            }
                            textArea.appendText(msg + "\n");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        textArea.clear();
                        setAuthorized(false);
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(ActionEvent actionEvent) {
        try {
            //отправляем сообщение серверу
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        if (out != null) {
            try {
                out.writeUTF(STOP_CLIENT_COMMAND_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            out.writeUTF(AUTH_SERVER_COMMAND_MESSAGE + " " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
