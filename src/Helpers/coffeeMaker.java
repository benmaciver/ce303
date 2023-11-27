package Helpers;

public class coffeeMaker implements Runnable{

    public boolean isRunning = false;
    @Override
    public void run() {
        isRunning=true;
        System.out.println("Making coffee");
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Coffee made");
        isRunning=false;

    }
}