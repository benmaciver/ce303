import java.io.*;
import java.net.*;
import java.util.*;

import Helpers.*;

public class Barista extends Thread{
    static Thread Barista1 = new Thread(new coffeeMaker());
    static Thread Barista2 = new Thread(new coffeeMaker());
    static Thread Barista3 = new Thread(new teaMaker());
    static Thread Barista4 = new Thread(new teaMaker());
    static String[] drinkRecipients = new String[4];
    static ArrayList<String> waitingArea = new ArrayList<>();
    static Map<String,ArrayList<Object>> trayArea = new HashMap<>();
    static int orderSize;
    static String currentUser;
    public void run(){
        while (true) {
            if (System.currentTimeMillis() % 500 == 0) {

                if (Barista1.getState() == Thread.State.TERMINATED) {
                    Barista1 = new Thread(new coffeeMaker());
                    drinkRecipients[0] = null;
                }
                if (Barista2.getState() == Thread.State.TERMINATED) {
                    Barista2 = new Thread(new coffeeMaker());
                    drinkRecipients[1] = null;
                }
                if (Barista3.getState() == Thread.State.TERMINATED) {
                    Barista3 = new Thread(new teaMaker());
                    drinkRecipients[2]=null;
                }
                if (Barista4.getState() == Thread.State.TERMINATED) {
                    Barista4 = new Thread(new teaMaker());
                    drinkRecipients[3]=null;
                }
                ArrayList<String> copy = (ArrayList<String>) waitingArea.clone();
                for (String request : copy) {
                    processCommand(request, false);
                }

            }
        }
    }
    public static void main(String[] args) {
        int user = 0;
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
                currentUser = input.nextLine();

                while (true) {
                    String line;
                    line = input.nextLine();
                    boolean br = false;
                    if (line.equals("exit"))
                        break;
                    if (line.equals("order status")){
                        Boolean complete = true;
                        for (String recipient : drinkRecipients){
                            if (recipient !=null && recipient.equals(currentUser)) {
                                complete = false;
                            }
                        }
                        if (complete)
                            output.println("order complete");
                        else output.println("order being processed");
                        continue;

                    }
                    int orderCount = Integer.parseInt(line);
                    String[] order = new String[orderCount];
                    for (int i =0; i < orderCount; i ++){
                        line = input.nextLine();
                        order[i] = line;
                        output.println(processCommand(line,true));
                    }
                    ArrayList<Object> list = new ArrayList<>();
                    list.add(order);
                    list.add(order.length);
                    trayArea.put(currentUser,list);

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
                        drinkRecipients[2] = currentUser;

                        return "Tea ordered. In process.";
                    }
                }
                synchronized (Barista4) {
                    if (Barista4.getState() == Thread.State.NEW) {
                        if (!changingList)
                            waitingArea.remove(command);
                        Barista4.start();
                        drinkRecipients[3] = currentUser;

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
                        drinkRecipients[0] = currentUser;

                        return "Coffee ordered. In process.";
                    }
                }
                synchronized (Barista2) {
                    if (Barista2.getState() == Thread.State.NEW) {
                        if (!changingList)
                            waitingArea.remove(command);
                        Barista2.start();
                        drinkRecipients[1] = currentUser;

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
