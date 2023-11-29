package Helpers;

public class teaMaker implements Runnable{
    //simulates tea making process (30 secs long)
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