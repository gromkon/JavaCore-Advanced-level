package Lesson_8.Homework.Client;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

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
    @FXML
    ListView<String> clientsList;

    @FXML
    HBox regPanel;
    @FXML
    TextField regNicknameField;
    @FXML
    TextField regLoginField;
    @FXML
    PasswordField regPasswordField;
    @FXML
    PasswordField regCheckPasswordField;


    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private final static String IP_ADRESS = "localhost";
    private final static int PORT = 8080;

    private final static String STOP_CLIENT_COMMAND_MESSAGE = "/end";
    private final static String STOP_SERVER_COMMAND_MESSAGE = "/serverClosed";
    private final static String AUTH_SERVER_COMMAND_MESSAGE = "/auth";
    private final static String CLIENTS_LIST_BROADCAST_COMMAND_MESSAGE = "/clientslist ";
    private final static String REG_COMMAND_MESSAGE = "/reg";
    private final static String REGISTRATED_COMMAND_MESSAGE = "/regOk";
    private final static String NOT_REGISTRATED_COMMAND_MESSAGE = "/regNotOk";

    private final static String BLACKLIST_MARK = "/b";

    private final static String AUTH_OK = "Вы успешно авторизовались";
    private final static String WELCOME = "Вы успешно авторизовались!\nДобро пожаловать в чат!\n";
    private final static String YOU_ARE_REGISTR = "Вы успешно зарегестрировались!";
    private final static String PASSWORDS_DONT_EQUALS = "Введеные пароли не совпадают\n";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthorized(false);
    }

    public void setAuthorized(boolean isAuthorized) {
        if (isAuthorized) {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            regPanel.setVisible(false);
            regPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
            clientsList.setVisible(true);
            clientsList.setManaged(true);
        } else {
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            regPanel.setVisible(false);
            regPanel.setManaged(false);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
            clientsList.setVisible(false);
            clientsList.setManaged(false);
        }
    }

    public void changeRegToUpperAndBack(boolean isReg) {
        if (isReg) {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            regPanel.setVisible(true);
            regPanel.setManaged(true);
        } else {
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            regPanel.setVisible(false);
            regPanel.setManaged(false);
        }

    }

    public void connect() {

        try {
            socket = new Socket(IP_ADRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        //проверка авторизации
                        while (true) {
                            String msg = in.readUTF();
                            if (msg.equals(AUTH_OK)) {
                                setAuthorized(true);
                                textArea.clear();
                                textArea.appendText(WELCOME);
                                break;
                            } else if (msg.equals(REGISTRATED_COMMAND_MESSAGE)) {
                                textArea.appendText(YOU_ARE_REGISTR);
                                setAuthorized(false);
                                disableRegPanel();
                            } else if (msg.startsWith(NOT_REGISTRATED_COMMAND_MESSAGE)) {
                                String msgText = msg.split(" ", 2)[1];
                                textArea.appendText(msgText);
                                setAuthorized(false);
                                activeRegPanel();
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
                            if (msg.startsWith(CLIENTS_LIST_BROADCAST_COMMAND_MESSAGE)) {
                                String[] nicknames = msg.split(" ");
                                Platform.runLater(
                                        () -> {
                                            clientsList.getItems().clear();
                                            for (int i = 1; i < nicknames.length; i++) {
                                                if (nicknames[i].endsWith(BLACKLIST_MARK)) {
                                                    clientsList.getItems().add(nicknames[i].substring(0, nicknames[i].length() - 2) + " (ЧС)");
                                                } else {
                                                    clientsList.getItems().add(nicknames[i]);
                                                }
                                            }
                                        }
                                );

                            } else {
                                textArea.appendText(msg + "\n");
                            }
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
            out.writeUTF(AUTH_SERVER_COMMAND_MESSAGE + " " + loginField.getText() + " " + passwordField.getText().hashCode());
            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void activeRegPanel() {
        changeRegToUpperAndBack(true);
    }

    public void disableRegPanel() {
        changeRegToUpperAndBack(false);
    }

    public void regUser() {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            String nickname = regNicknameField.getText();
            String login = regLoginField.getText();
            int pass = regPasswordField.getText().hashCode();
            int passCheck = regCheckPasswordField.getText().hashCode();
            if (pass == passCheck) {
                out.writeUTF(REG_COMMAND_MESSAGE + " " + nickname + " " + login + " " + pass);
            } else {
                textArea.appendText(PASSWORDS_DONT_EQUALS);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }




}
