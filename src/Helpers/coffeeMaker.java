package Helpers;

public class coffeeMaker implements Runnable{

    public boolean isRunning = false;
    @Override
    public void run() {
        isRunning=true;

        try {
            Thread.sleep(45000);

        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
        }

        isRunning=false;

    }
}