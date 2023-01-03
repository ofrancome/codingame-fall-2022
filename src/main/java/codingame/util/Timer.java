package codingame.util;

import java.util.Date;

public class Timer {

    private static long start = getTime();

    public static void reset(){
        start = getTime();
    }

    public static long getMilliseconds() {
        return getTime() - start;
    }

    public static void print(){
        System.err.println("Execution time: " + getMilliseconds() + "ms");
    }

    private static long getTime(){
        return new Date().getTime();
    }

}
