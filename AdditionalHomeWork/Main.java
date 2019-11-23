package Lesson_2.AdditionalHomeWork;

public class Main {

    private final static int COUNT_WORKS_DAY = 5;
    private final static int WORKING_HOURS_IN_ONE_DAY = 8;

    private static void calcHoursUntilTheEndOfWorkWeek(DayOfWeek day) {

        switch (day) {
            case SATURDAY:
                System.out.println("Cегодня суббота, можно отдохнуть!");
                break;
            case SUNDAY:
                System.out.println("Cегодня воскресенье, можно отдохнуть!");
                break;
            default:
                int countRemainingWorkingDays = COUNT_WORKS_DAY - DayOfWeek.valueOf(day.toString()).ordinal();
                System.out.println("До конца рабочей недели осталось " + countRemainingWorkingDays * WORKING_HOURS_IN_ONE_DAY + " рабочих часов");
                break;
        }

    }

    public static void main(String[] args) {

        calcHoursUntilTheEndOfWorkWeek(DayOfWeek.MONDAY);
        calcHoursUntilTheEndOfWorkWeek(DayOfWeek.TUESDAY);
        calcHoursUntilTheEndOfWorkWeek(DayOfWeek.WESNESDAY);
        calcHoursUntilTheEndOfWorkWeek(DayOfWeek.THURSDAY);
        calcHoursUntilTheEndOfWorkWeek(DayOfWeek.FRIDAY);
        calcHoursUntilTheEndOfWorkWeek(DayOfWeek.SATURDAY);
        calcHoursUntilTheEndOfWorkWeek(DayOfWeek.SUNDAY);

    }
}
