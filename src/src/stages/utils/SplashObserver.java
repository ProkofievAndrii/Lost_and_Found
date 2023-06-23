package stages.utils;

public class SplashObserver {

    public SplashObserver() {
        timeCounter = 0;
    }

    public static void setCounter(int time) {
        timeCounter += time;
    }

    public static int getCounter() {
        return timeCounter;
    }

    static int timeCounter;
}
