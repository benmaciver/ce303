package Helpers;

public class teaMaker implements Runnable{

    public boolean isRunning = false;
    @Override
    public void run() {
        isRunning=true;
        System.out.println("Making tea");
        try {
            Thread.sleep(6000);
            System.out.println("Tea made");
        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
        }
        isRunning=false;
    }

}