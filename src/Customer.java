import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static Helpers.helperMethods.*;

public class Customer extends Thread {
    //regex for orders
    private static String regex = "order \\d (tea|coffee|teas|coffees)";
    //input scanner for this customer
    static Scanner input;
    //always running in simultaneously running thread to check for any replies from the server at any time
    public void run(){
        String line;
        while (input.hasNext()){
            line = input.nextLine();
            System.out.println("Server Reply: " + line);
        }
    }

    public static void main(String[] args) {

        try {
            //sets up connnection to server
            Socket socket = new Socket("localhost", 5000);
            input = new Scanner(new InputStreamReader(socket.getInputStream()));
            PrintWriter output =  new PrintWriter(socket.getOutputStream(), true);

            //gets user input for name and sends to server
            Scanner userInput = new Scanner(new InputStreamReader(System.in));
            System.out.println("Enter your name please: ");
            String name = userInput.nextLine();
            output.println(name);
            //starts run method
            Customer thread2 = new Customer();
            thread2.start();

            while (true) {
                //asks for command
                System.out.println("Enter a command (order tea (max:9) , order coffee (max:9) , order status, exit):");
                int orderCount = 1;
                String command = userInput.nextLine();
                command = command.toLowerCase();
                //sends command to server
                if (command.equals("exit")) {
                    output.println(command);
                    break;
                }
                else if (command.equals("order status")) {
                    output.println(command);
                }
                else {
                    ArrayList<String> orders = findRegexMatches(regex, command);
                    if (orders.isEmpty()) {
                        System.out.println("Invalid command");
                        continue;
                    }
                    ArrayList<String> refinedOrders = new ArrayList<>();
                    for (String o : orders) {
                        String[] words = o.split("\\s+");
                        int count = Integer.parseInt(words[1]);
                        String newOrder = words[0] + " " + words[2];
                        for (int i = 0; i < count; i++) {
                            refinedOrders.add(newOrder);
                        }
                    }
                    orderCount = refinedOrders.size();
                    output.println(orderCount);
                    for (String o : refinedOrders) {
                        output.println(o);
                    }
                }



            }
            input.close();
            output.close();
            userInput.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
