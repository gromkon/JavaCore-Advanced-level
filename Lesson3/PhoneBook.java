package Lesson_3.Homework;

import java.util.ArrayList;
import java.util.HashMap;

public class PhoneBook {

    private HashMap<String, ArrayList<String>> data = new HashMap<>();

    public void add(String surname, String phoneNubmer) {
        ArrayList<String> phones = data.containsKey(surname) ? data.get(surname) : new ArrayList<>();
        phones.add(phoneNubmer);
        data.put(surname, phones);
    }

    public ArrayList<String> get(String surname) {
        return data.get(surname);
    }


    public static void main(String[] args) {
        PhoneBook phoneBook = new PhoneBook();

        phoneBook.add("Иванов", "89251234567");
        phoneBook.add("Иванов", "89251234568");
        phoneBook.add("Иванов", "89251234569");
        phoneBook.add("Иванов", "89251234560");

        phoneBook.add("Петров", "89252344567");

        phoneBook.add("Сидоров", "89253454567");

        phoneBook.add("Воробьев", "89254564565");
        phoneBook.add("Воробьев", "89254564564");
        phoneBook.add("Воробьев", "89254564563");

        phoneBook.add("Куралев", "89256784567");

        System.out.println(phoneBook.get("Иванов"));
        System.out.println(phoneBook.get("Петров"));
        System.out.println(phoneBook.get("Сидоров"));
        System.out.println(phoneBook.get("Воробьев"));
        System.out.println(phoneBook.get("Куралев"));


    }



}
