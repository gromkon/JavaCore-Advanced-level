package Lesson_5.Homework;

import java.util.Arrays;

import static java.lang.Math.floor;

public class Multithreading {

    float[] arrFirst;
    float[] arrSecond;

    static final int size = 1000000;

    private void firstMethod() {
        System.out.print("Первый метод (в один поток) обработал весь массив за ");

        arrFirst = new float[size];
        Arrays.fill(arrFirst, 1);

        long t = System.currentTimeMillis();

        for (int i = 0; i < size; i++) {
            arrFirst[i] = (float)(arrFirst[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }

        System.out.println(System.currentTimeMillis() - t);
    }

    private void secondMethod() {
        System.out.print("Второй метод (в 2 потока) обработал весь массив за ");

        arrSecond = new float[size];
        Arrays.fill(arrSecond, 1);

        final int h = size / 2;
        float[] arr1 = new float[h];
        float[] arr2 = new float[h];

        long t = System.currentTimeMillis();

        System.arraycopy(arrSecond, 0, arr1, 0, h);
        System.arraycopy(arrSecond, h, arr2, 0, h);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < arr1.length; i++) {
                    arr1[i] = (float)(arr1[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < arr2.length; i++) {
                    arr2[i] = (float)(arr2[i] * Math.sin(0.2f + (i + h) / 5) * Math.cos(0.2f + (i + h) / 5) * Math.cos(0.4f + (i + h) / 2));
                }
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.arraycopy(arr1, 0, arrSecond, 0, h);
        System.arraycopy(arr2, 0, arrSecond, h, h);

        System.out.println(System.currentTimeMillis() - t);
    }

    private void secondMethodUpdate(int n) {
        System.out.print("Второй метод (в " + n + " потоков) обработал весь массив за ");

        arrSecond = new float[size];
        Arrays.fill(arrSecond, 1);

        final int h = (int)floor(size / n);
        int modulo = size - h * n;

        int startPos = 0;
        int countCopyPos = h;

        long time = System.currentTimeMillis();

        for (int i = 0; i < n; i++) {

            if (i == n - 1) {
                countCopyPos += modulo;
            }

            float arr[] = new float[countCopyPos];

            System.arraycopy(arrSecond, startPos, arr, 0, countCopyPos);

            int finalStartPos = startPos;

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < arr.length; i++) {
                        arr[i] = (float)(arr[i] * Math.sin(0.2f + (i + finalStartPos) / 5) * Math.cos(0.2f + (i + finalStartPos) / 5) * Math.cos(0.4f + (i + finalStartPos) / 2));
                    }
                }
            });

            startPos += countCopyPos;

            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.arraycopy(arr, 0, arrSecond, startPos - countCopyPos, arr.length);
        }

        System.out.println(System.currentTimeMillis() - time);
    }

    boolean check() {
        return Arrays.equals(arrFirst, arrSecond);
    }



    public static void main(String[] args) {

        Multithreading multithreading = new Multithreading();
        multithreading.firstMethod();
        multithreading.secondMethod();

        multithreading.secondMethodUpdate(2);

        System.out.println("Массивы полученные первым и вторым способом равны? " + multithreading.check());

    }
}
