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
    static Map<String,ArrayList<String[]>> brewingArea = new HashMap<>();
    static ArrayList<String> trayArea = new ArrayList<>();
    static String currentUser;
    static PrintWriter output;
    public void run(){
        while (true) {
            if (System.currentTimeMillis() % 50 == 0) {

                if (Barista1.getState() == Thread.State.TERMINATED) {
                    Barista1 = new Thread(new coffeeMaker());
                    updateBrewingArea(currentUser,"coffee");
                    drinkRecipients[0] = null;
                }
                if (Barista2.getState() == Thread.State.TERMINATED) {
                    Barista2 = new Thread(new coffeeMaker());
                    updateBrewingArea(currentUser,"coffee");
                    drinkRecipients[1] = null;
                }
                if (Barista3.getState() == Thread.State.TERMINATED) {
                    Barista3 = new Thread(new teaMaker());
                    updateBrewingArea(currentUser,"tea");
                    drinkRecipients[2]=null;
                }
                if (Barista4.getState() == Thread.State.TERMINATED) {
                    Barista4 = new Thread(new teaMaker());
                    updateBrewingArea(currentUser,"tea");
                    drinkRecipients[3]=null;
                }

                for (Map.Entry<String, ArrayList<String[]>> entry : brewingArea.entrySet()){
                    if (areAllElementsNull(entry.getValue().get(0))){
                        int tea = 0,coffee = 0;
                        for (String order : entry.getValue().get(1)){
                            if (order.equals("order tea")){
                                tea++;
                            }
                            else coffee++;
                        }
                        output.println("order delivered to " + entry.getKey() + " ( " + tea + " teas and " + coffee + " coffees )");
                        brewingArea.remove(currentUser);
                    }
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
                output = new PrintWriter(clientSocket.getOutputStream(), true);
                currentUser = input.nextLine();

                while (true) {
                    String line;
                    line = input.nextLine();
                    boolean br = false;
                    if (line.equals("exit")) {

                        Barista1.interrupt();Barista2.interrupt();
                        Barista3.interrupt();Barista4.interrupt();
                        brewingArea = new HashMap<>();
                        trayArea =new ArrayList<>();
                        waitingArea = new ArrayList<>();
                        break;
                    }
                    if (line.equals("order status")){

                        int trayCoffee=0,trayTea=0;
                        int brewingCoffee=0,brewingTea=0;
                        for (var item : brewingArea.entrySet()){
                            if (item.getKey().equals(currentUser)){
                                String[] arr1 = item.getValue().get(0);
                                String[] arr2 = item.getValue().get(1);
                                for (int i = 0;i < arr1.length; i++){
                                    if (arr1[i] !=null && arr1[i].equals(arr2[i])){
                                        if (arr1[i].equals("order tea"))
                                            brewingTea++;
                                        else brewingCoffee++;
                                    }
                                    else if (arr2[i].equals("order tea"))
                                        trayTea++;
                                    else trayCoffee++;
                                }
                            }
                        }
                        output.println("\nOrder status for " + currentUser + ":\n" +
                            brewingCoffee + " cofees being prepared and " + brewingTea + " teas being prepared\n" +
                            trayCoffee + " coffees in the tray and "+ trayTea + " teas in the tray");
                        continue;
                    }
                    int orderCount = Integer.parseInt(line);
                    String[] order = new String[orderCount];
                           for (int i =0; i < orderCount; i ++){
                        line = input.nextLine();
                        order[i] = line;
                        output.println(processCommand(line,true));
                    }
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
    private static void updateBrewingArea(String user, String drink){
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
    }
    private static <T> boolean areAllElementsNull(T[] array) {
        for (T element : array) {
            if (element != null) {
                return false; // If any element is not null, return false
            }
        }
        return true; // All elements are null
    }
    public static String[] concatenateArrays(String[] array1, String[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;

        // Create a new array with the combined length
        String[] resultArray = new String[length1 + length2];

        // Copy elements from the first array
        System.arraycopy(array1, 0, resultArray, 0, length1);

        // Copy elements from the second array
        System.arraycopy(array2, 0, resultArray, length1, length2);

        return resultArray;
    }


}
