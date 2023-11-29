import java.io.*;
import java.net.*;
import java.util.*;

import Helpers.*;
import static Helpers.helperMethods.*;

public class Barista extends Thread{
    // Barista threads for coffee and tea
    static Thread Barista1 = new Thread(new coffeeMaker());
    static Thread Barista2 = new Thread(new coffeeMaker());
    static Thread Barista3 = new Thread(new teaMaker());
    static Thread Barista4 = new Thread(new teaMaker());
    // Arrays and collections to manage orders and users
    static String[] drinkRecipients = new String[4];
    static ArrayList<String[]> waitingArea = new ArrayList<>();
    static Map<String,ArrayList<String[]>> brewingArea = new HashMap<>();
    static ArrayList<String> users = new ArrayList<>();
    static Map<String,PrintWriter> outputs = new HashMap<>();
    // Main thread execution, updates brewing area if drink has finished brewing
    public void run(){
        while (true) {
            if (System.currentTimeMillis() % 50 == 0) {

                if (Barista1.getState() == Thread.State.TERMINATED) {
                    Barista1 = new Thread(new coffeeMaker());
                    for (String user : users) {
                        if (updateBrewingArea(user, "coffee"))
                            break;
                    }
                    drinkRecipients[0] = null;
                }
                if (Barista2.getState() == Thread.State.TERMINATED) {
                    Barista2 = new Thread(new coffeeMaker());
                    for (String user : users) {
                        if (updateBrewingArea(user, "coffee"))
                            break;
                    }
                    drinkRecipients[1] = null;
                }
                if (Barista3.getState() == Thread.State.TERMINATED) {
                    Barista3 = new Thread(new teaMaker());
                    for (String user : users) {
                        if (updateBrewingArea(user, "tea"))
                            break;
                    }
                    drinkRecipients[2]=null;
                }
                if (Barista4.getState() == Thread.State.TERMINATED) {
                    Barista4 = new Thread(new teaMaker());
                    for (String user : users) {
                        if (updateBrewingArea(user, "tea"))
                            break;
                    }
                    drinkRecipients[3]=null;
                }
                //If all drinks have been brewed in an order the order is returned to the user and marked as compelte
                Map<String,ArrayList<String[]>> clone = new HashMap<>(brewingArea);
                for (Map.Entry<String, ArrayList<String[]>> entry : clone.entrySet()){
                    if (areAllElementsNull(entry.getValue().get(0))){
                        int tea = 0,coffee = 0;
                        for (String order : entry.getValue().get(1)){
                            if (order !=null && order.equals("order tea")){
                                tea++;
                            }
                            else coffee++;
                        }
                        PrintWriter outStream = outputs.get(entry.getKey());
                        outStream.println("order delivered to " + entry.getKey() + " ( " + tea + " teas and " + coffee + " coffees )");
                        brewingArea.remove(entry.getKey());
                    }
                }
                //sorts through the waiting area
                ArrayList<String[]> copy = new ArrayList<>(waitingArea.size());
                copy.addAll(waitingArea);

                for (String[] request : copy) {
                    processCommand(request, false);
                }


            }
        }
    }
    public static void main(String[] args) {
        int user = 0;
        try {
            //opens server
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server is waiting for customers...");
            //starts run method from above, this run basically maintains the server in a simultaneously running thread
            Barista thread2 = new Barista();
            thread2.start();

            while (true) {
                //Runs when a customer connects to server
                Socket clientSocket = serverSocket.accept();
                System.out.println("Customer connected.");
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //processes specific commands of an order, strating the relevant thread or adds to waiting area
    private static String processCommand(String[] command,boolean changingList){
        switch (command[0].toLowerCase()) {
            case "order tea":
                synchronized (Barista3) {
                    if (Barista3.getState() == Thread.State.NEW) {
                        if (!changingList)
                            waitingArea.remove(command);
                        printServerState();
                        Barista3.start();
                        drinkRecipients[2] = command[1];

                        return "Tea ordered. In process.";
                    }
                }
                synchronized (Barista4) {
                    if (Barista4.getState() == Thread.State.NEW) {
                        if (!changingList)
                            waitingArea.remove(command);
                        printServerState();
                        Barista4.start();
                        drinkRecipients[3] = command[1];

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
                        printServerState();
                        Barista1.start();
                        drinkRecipients[0] = command[1];

                        return "Coffee ordered. In process.";
                    }
                }
                synchronized (Barista2) {
                    if (Barista2.getState() == Thread.State.NEW) {
                        if (!changingList)
                            waitingArea.remove(command);
                        printServerState();
                        Barista2.start();
                        drinkRecipients[1] = command[1];

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

    //updates the brewing area with the drink thats been made and the user its for
    private static boolean updateBrewingArea(String user, String drink){
        boolean br = false;
        for (Map.Entry<String, ArrayList<String[]>> entry : brewingArea.entrySet()){
            String key = entry.getKey();
            ArrayList<String[]> value = entry.getValue();
            String[] orders = value.get(0);
            if (key.equals(user)){
                for (int i = 0; i < orders.length ; i++){
                    if (orders[i]!=null && orders[i].equals("order " + drink)) {
                        orders[i] = null;
                        br = true;
                        break;
                    }
                }
            }
            if (br)
                break;
        }
        return br;
    }
    //This class handles an instance of a clients connection and communication with the Barista server
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                //i/o streams for this instance of customer connection
                Scanner input = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter outStream = new PrintWriter(clientSocket.getOutputStream(), true);
                String currentUser = input.nextLine();


                //adds new user to list of users and the users outstream to outputs list
                users.add(currentUser);
                outputs.put(currentUser,outStream);

                while (true) {
                    printServerState();
                    String line;
                    line = input.nextLine();
                    boolean br = false;
                    //Cancels all operations for said user
                    if (line.equals("exit")) {
                        if (drinkRecipients[0] != null && drinkRecipients[0].equals(currentUser)) {
                            Barista1.interrupt();
                        }
                        if (drinkRecipients[1] != null && drinkRecipients[1].equals(currentUser)) {
                            Barista2.interrupt();
                        }
                        if (drinkRecipients[2] != null && drinkRecipients[2].equals(currentUser)) {
                            Barista3.interrupt();
                        }
                        if (drinkRecipients[3] != null && drinkRecipients[3].equals(currentUser)) {
                            Barista4.interrupt();
                        }
                        brewingArea.remove(currentUser);
                        String finalCurrentUser = currentUser;
                        waitingArea.removeIf(s -> s[1].equals(finalCurrentUser));
                        users.remove(currentUser);
                        break;
                    }
                    //returns order status to user
                    if (line.equals("order status")) {
                        int trayCoffee = 0, trayTea = 0;
                        int brewingCoffee = 0, brewingTea = 0;
                        int waitTea = 0, waitCoffee = 0;
                        for (var item : brewingArea.entrySet()) {
                            if (item.getKey().equals(currentUser)) {
                                String[] arr1 = item.getValue().get(0);
                                String[] arr2 = item.getValue().get(1);
                                for (int i = 0; i < arr1.length; i++) {
                                    if (arr1[i] != arr2[i] && arr2[i].equals("order tea"))
                                        trayTea++;
                                    else if (arr1[i] != arr2[i] && arr2[i].equals("order coffee"))
                                        trayCoffee++;
                                }
                            }
                        }
                        if (drinkRecipients[0] != null && drinkRecipients[0].equals(currentUser)) {
                            brewingCoffee++;
                        }
                        if (drinkRecipients[1] != null && drinkRecipients[1].equals(currentUser)) {
                            brewingCoffee++;
                        }
                        if (drinkRecipients[2] != null && drinkRecipients[2].equals(currentUser)) {
                            brewingTea++;
                        }
                        if (drinkRecipients[3] != null && drinkRecipients[3].equals(currentUser)) {
                            brewingTea++;
                        }
                        for (String[] item : waitingArea) {
                            if (item[1].equals(currentUser)) {
                                if (item[0].equals("order tea"))
                                    waitTea++;
                                else waitCoffee++;
                            }
                        }
                        boolean idle = true;
                        if (trayTea>0 || trayCoffee>0 || brewingTea>0 || brewingCoffee>0 || waitTea>0 || waitCoffee>0)
                            idle = false;
                        if (!idle){
                            outStream.println("\nOrder status for " + currentUser + ":\n" +
                                    waitCoffee + " coffees waiting and " + waitTea + " teas waiting\n" +
                                    brewingCoffee + " cofees being prepared and " + brewingTea + " teas being prepared\n" +
                                    trayCoffee + " coffees in the tray and " + trayTea + " teas in the tray");
                        }
                        else outStream.println("No orders found for " + currentUser);
                        continue;
                    }
                    //else process the users order
                    int orderCount = Integer.parseInt(line);
                    String[] order = new String[orderCount];
                    int teas=0,coffees=0;
                    for (int i =0; i < orderCount; i ++){
                        line = input.nextLine();
                        order[i] = line;
                        String[] command =  {line,currentUser};
                        processCommand(command,true);
                    }
                    outStream.println("order recieved for " + currentUser + " (" + teas + " teas and " + coffees + "coffees)");
                    //checks if the user is appending to exisiting order or if this is a new order and executes correct logic for that purpose
                    boolean appendingExistingOrder = false;
                    for (var item : brewingArea.entrySet()) {
                        if (item.getKey().equals(currentUser)){
                            item.getValue().set(0,concatenateArrays(item.getValue().get(0),order ));
                            item.getValue().set(1,item.getValue().get(0).clone());
                            appendingExistingOrder = true;
                        }

                    }
                    if (!appendingExistingOrder){
                        ArrayList<String[]> list = new ArrayList<>();
                        list.add(order);
                        list.add(Arrays.copyOf(order,order.length));
                        brewingArea.put(currentUser,list);
                    }

                }

                input.close();
                outStream.close();
                clientSocket.close();
                System.out.println("Customer disconnected.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //Merthod prints server state to Barista terminal
    static void printServerState(){
        System.out.println("There is " + users.size() + " customer(s) in the cafe");
        ArrayList<String> usersWaiting = new ArrayList<>();
        for (var order : waitingArea){
            if (!usersWaiting.contains(order[1]))
                usersWaiting.add(order[1]);
        }
        System.out.println("There is " + usersWaiting.size() + " customer(s) waiting for their orders");
        int teaCount=0,coffeeCount=0;
        for (var item : waitingArea){
            if (item[0].equals("order tea"))
                teaCount++;
            else coffeeCount++;
        }
        System.out.println("There is " + teaCount + " tea(s) and " + coffeeCount + " coffee(s) waiting to be prepared");
        int teaBrewing=0,coffeeBrewing=0;

        if (drinkRecipients[0]!=null)
            coffeeBrewing++;
        if (drinkRecipients[1]!=null)
            coffeeBrewing++;
        if (drinkRecipients[2]!=null)
            teaBrewing++;
        if (drinkRecipients[3]!=null)
            teaBrewing++;
        System.out.println("There is " + teaBrewing + " tea(s) and " + coffeeBrewing + " coffee(s) being prepared");
        int teaTray=0,coffeeTray=0;
        for (var item : brewingArea.entrySet()){
            String[] arr1 = item.getValue().get(0);
            String[] arr2 = item.getValue().get(1);
            for (int i = 0; i < arr1.length; i++){
                if (arr1[i]!=arr2[i] && arr2[i].equals("order tea"))
                    teaTray++;
                else if (arr1[i]!=arr2[i] && arr2[i].equals("order coffee"))
                    coffeeTray++;
            }
        }
        System.out.println("There is " + teaTray + " tea(s) and " + coffeeTray + " coffee(s) in the tray");
    }

}

