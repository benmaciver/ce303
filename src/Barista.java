import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import Helpers.*;

public class Barista extends Thread{
    static Thread Barista1 = new Thread(new coffeeMaker());
    static Thread Barista2 = new Thread(new coffeeMaker());
    static Thread Barista3 = new Thread(new teaMaker());
    static Thread Barista4 = new Thread(new teaMaker());
    static ArrayList<String> waitingArea = new ArrayList<>();
    public void run(){
        while (true) {
            if (System.currentTimeMillis() % 1500 == 0) {

                if (Barista1.getState() == Thread.State.TERMINATED)
                    Barista1 = new Thread(new coffeeMaker());
                if (Barista2.getState() == Thread.State.TERMINATED)
                    Barista2 = new Thread(new coffeeMaker());
                if (Barista3.getState() == Thread.State.TERMINATED)
                    Barista3 = new Thread(new teaMaker());
                if (Barista4.getState() == Thread.State.TERMINATED)
                    Barista4 = new Thread(new teaMaker());
                ArrayList<String> copy = (ArrayList<String>) waitingArea.clone();
                for (String request : copy) {
                    processCommand(request, false);
                }

            }
        }
    }
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server is waiting for customers...");
            Barista thread2 = new Barista();
            thread2.start();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Customer connected.");

                Scanner input = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);



                while (true) {

                    if (Barista1.getState() == Thread.State.TERMINATED)
                        Barista1 = new Thread(new coffeeMaker());
                    if (Barista2.getState() == Thread.State.TERMINATED)
                        Barista2 = new Thread(new coffeeMaker());
                    if (Barista3.getState() == Thread.State.TERMINATED)
                        Barista3 = new Thread(new teaMaker());
                    if (Barista4.getState() == Thread.State.TERMINATED)
                        Barista4 = new Thread(new teaMaker());

                    String line;
                    line = input.nextLine();
                    if (line == "new request incoming")
                        continue;
                    boolean br = false;
                    int orderCount = Integer.parseInt(input.nextLine());
                    for (int i =0; i < orderCount; i ++){
                        line = input.nextLine();
                        output.println(processCommand(line,true));
                    }
                    if (br)
                        break;

                }


                input.close();
                output.close();
                clientSocket.close();
                System.out.println("Customer disconnected.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String processCommand(String command,boolean changingList) {
        switch (command.toLowerCase()) {
            case "order tea":
                synchronized (Barista3) {
                    if (Barista3.getState() == Thread.State.NEW) {
                        if (!changingList)
                            waitingArea.remove(command);
                        Barista3.start();
                        return "Tea ordered. In process.";
                    }
                }
                synchronized (Barista4) {
                    if (Barista4.getState() == Thread.State.NEW) {
                        if (!changingList)
                            waitingArea.remove(command);
                        Barista4.start();
                        return "Tea ordered. In process.";
                    }
                }
                if (changingList)
                    waitingArea.add(command);
                return "Tea ordered. Will be prepared as soon as a barista is available";
            case "order coffee":
                synchronized (Barista1) {
                    if (Barista1.getState() == Thread.State.NEW) {
                        if (!changingList)
                            waitingArea.remove(command);
                        Barista1.start();
                        return "Coffee ordered. In process.";
                    }
                }
                synchronized (Barista2) {
                    if (Barista2.getState() == Thread.State.NEW) {
                        if (!changingList)
                            waitingArea.remove(command);
                        Barista2.start();
                        return "Coffee ordered. In process.";
                    }
                }
                if (changingList)
                    waitingArea.add(command);
                return "Coffee ordered. Will be prepared as soon as a barista is available";
            default:
                return "Invalid command";
        }
    }

}
