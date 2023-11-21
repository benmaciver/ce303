import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Barista {

    public static void main(String[] args) {
        int portNumber = 12345;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            ArrayList<Runnable> makers = new ArrayList<>();
            coffeeMaker coffeeMaker1 = new coffeeMaker();
            coffeeMaker coffeeMaker2 = new coffeeMaker();
            teaMaker teaMaker1 = new teaMaker();
            teaMaker teaMaker2 = new teaMaker();
            Thread teaThread1=null,teaThread2=null,coffeeThread1=null,coffeeThread2 = null; //first 2 for tea and last 2 for coffee
            ArrayList<String> teaWaitingArea = new ArrayList<>(), coffeeWaitingArea = new ArrayList<>();
            ArrayList<String> tray = new ArrayList<>();
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New order received from: " + clientSocket.getInetAddress());

                // Handle the order processing, tea/coffee preparation, and delivery here
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String order = (reader.readLine());
                if (order.equalsIgnoreCase("order tea")) {
                    if (!teaMaker1.isRunning) {
                        teaThread1 = new Thread(teaMaker1);
                        teaThread1.start();
                    }
                    else if (!teaMaker2.isRunning) {
                        teaThread2 = new Thread(teaMaker2);
                        teaThread2.start();
                    }
                    else teaWaitingArea.add(order);
                }
                if (order.equalsIgnoreCase("order coffee")) {
                    if (!coffeeMaker1.isRunning) {
                        coffeeThread1 = new Thread(coffeeMaker1);
                        coffeeThread1.start();
                    }
                    else if (!coffeeMaker2.isRunning) {
                        coffeeThread2 = new Thread(coffeeMaker2 );
                        coffeeThread2.start();
                    }
                    else coffeeWaitingArea.add(order);
                }

                else {
                    // Handle invalid option
                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                    writer.println("Invalid option. Please choose either tea or coffee.");
                    // Close the connection with the current customer
                    clientSocket.close();
                    continue; // Move to the next iteration of the while loop
                }
                for (Runnable maker : makers){

                }


                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                writer.println("Your order is ready! Enjoy your drink");



                // Close the connection with the current customer
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error occurred while running the Barista server: " + e.getMessage());
        }
    }

    static class coffeeMaker implements Runnable{

        public boolean isRunning = false;
        @Override
        public void run() {
            isRunning=true;
            System.out.println("Making coffee");
            try {
                Thread.sleep(450);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Coffee made");
            isRunning=false;
        }
    }
    static class teaMaker implements Runnable{

        public boolean isRunning = false;
        @Override
        public void run() {
            isRunning=true;
            System.out.println("Making tea");
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Tea made");
            isRunning=false;

        }
    }

}
