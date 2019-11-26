package Lesson_3.Homework;

import java.util.*;

public class TaskOne {

    public static void main(String[] args) {
        ArrayList<String> arr = new ArrayList<>();

        String randomWords = "счёт\n" +
                "счёт\n" +
                "счёт\n" +
                "счёт\n" +
                "аист\n" +
                "луна\n" +
                "путешествие\n" +
                "автобус\n" +
                "ромашка\n" +
                "команда\n" +
                "успех\n" +
                "щенок\n" +
                "автобус\n" +
                "фломастер\n" +
                "меню\n" +
                "пират\n" +
                "заяц\n" +
                "аист\n" +
                "автобус\n" +
                "автобус\n" +
                "аист\n" +
                "аист\n" +
                "успех\n" +
                "успех\n" +
                "успех\n" +
                "пират";

        String[] rnd = randomWords.split("\n");

        Collections.addAll(arr, rnd);

        System.out.println(arr);

        HashMap<String, Integer> result = new HashMap<>();

        for (String word : arr) {
            int count = 1;

            Set<String> unicWords =  result.keySet();

            for (String unicWord : unicWords) {
                if (unicWord.equals(word)) {
                    count += result.get(word);
                }
            }

            result.put(word, count);
        }

        System.out.println(result);
    }
}
