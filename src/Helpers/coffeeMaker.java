package Helpers;

public class coffeeMaker implements Runnable{

    public boolean isRunning = false;
    @Override
    public void run() {
        isRunning=true;
        System.out.println("Making coffee");
        try {
            Thread.sleep(6000);
            System.out.println("Coffee made");
        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
        }

        isRunning=false;

    }
}