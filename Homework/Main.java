package Lesson_2.Homework;

public class Main {

    private final static int ARRAY_LENGTH = 4;
    private final static int WAIT_TIME_MILLS = 500;

    public static int sumOfArray(String[][] arr) throws MyArraySizeException, MyArrayDataException {

        if (arr.length != ARRAY_LENGTH) {
            throw new MyArraySizeException("Размерность основного массива не равна 4");
        } else {
            for (int i = 0; i < arr.length; i++) {
                if (arr[i].length != ARRAY_LENGTH) {
                    throw new MyArraySizeException("Размерность дополнительного массива с индексом " + i + " не равна 4");
                }
            }
        }

        int sum = 0;

        for (int i = 0; i < ARRAY_LENGTH; i++) {
            for (int j = 0; j < ARRAY_LENGTH; j++) {
                try {
                    sum += Integer.parseInt(arr[i][j]);
                } catch (NumberFormatException ex) {
                    throw new MyArrayDataException("В ячейке [" + i + "][" + j + "] лежат не верные данные");
                }
            }
        }

        return sum;
    }

    private static void calc(String[][] arr) {
        try {
            System.out.println("Сумма чисел в массиве равна " + sumOfArray(arr));
        } catch (MyArraySizeException ex) {
            System.out.println(ex.getMessage());
            try {
                Thread.sleep(WAIT_TIME_MILLS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ex.printStackTrace();
        } catch (MyArrayDataException ex) {
            System.out.println(ex.getMessage());
            try {
                Thread.sleep(WAIT_TIME_MILLS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {

        // Написал пару примеров.
        // В этом примере ошибок нет
        String[][] arr = {{"1", "2", "3", "3"},
                {"5", "6", "7", "8"},
                {"9", "10", "11", "12"},
                {"13", "14", "15", "16"}};
        calc(arr);

        //Тут ошибка в размерности основного массива
        String[][] arr1 = {{"1", "2", "3", "3"},
                {"5", "6", "7", "8"},
                {"9", "10", "11", "12"}};
        calc(arr1);

        // Тут ошибка в размерности дополнительного массива с индексом 1
        String[][] arr2 = {{"1", "2", "3", "3"},
                {"5", "6", "7", "8", "10"},
                {"9", "10", "11", "12"},
                {"13", "14", "15", "16"}};
        calc(arr2);

        // Тут ошибка в не правильно введеных данных в [3][2]
        String[][] arr3 = {{"1", "2", "3", "3"},
                {"5", "6", "7", "8"},
                {"9", "10", "11", "12"},
                {"13", "14", "sadfsdaf", "16"}};
        calc(arr3);

    }
}
