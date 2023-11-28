package Helpers;

public class teaMaker implements Runnable{

    public boolean isRunning = false;
    @Override
    public void run() {
        isRunning=true;

        try {
            Thread.sleep(30000);

        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
        }
        isRunning=false;
    }

}