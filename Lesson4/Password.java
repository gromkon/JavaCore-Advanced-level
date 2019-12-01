package Lesson_4.Homework;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Password {

    private static boolean checkPassword(String password) {
        Pattern patternCount = Pattern.compile("^.{8,20}$");
        Pattern patternNubmer = Pattern.compile("^.*\\d.*$");
        Pattern patternLowerCase = Pattern.compile("^.*[a-z].*$");
        Pattern patternUpperCase = Pattern.compile("^.*[A-Z].*$");
        Pattern patternSpace = Pattern.compile("^.*\\s.*$");

        Matcher matcherCount = patternCount.matcher(password);
        Matcher matcherNubmer = patternNubmer.matcher(password);
        Matcher matcherLowerCase = patternLowerCase.matcher(password);
        Matcher matcherUpperCase = patternUpperCase.matcher(password);
        Matcher matcherSpace = patternSpace.matcher(password);

        return matcherCount.matches() && matcherNubmer.matches()
                && matcherLowerCase.matches() && matcherUpperCase.matches()
                && !matcherSpace.matches();
    }


    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Введите пароль\n" +
                "Требования:\n" +
                "1. Пароль должен быть не менее 8ми символов.\n" +
                "2. В пароле должно быть число\n" +
                "3. В пароле должна быть хотя бы 1 строчная буква\n" +
                "4. В пароле должна быть хотя бы 1 заглавная буква\n" +
                "5. Не должно быть пробелов\n");
        String password = "";

        do {
            password = in.nextLine();
            if (checkPassword(password)) {
                System.out.println("У вас хороший пароль!");
                return;
            } else {
                System.out.println("Вы ввели некоректный пароль!");
            }
        } while (true);

    }

}
