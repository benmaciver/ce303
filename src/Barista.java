import Helpers.coffeeMaker;
import Helpers.teaMaker;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
public class Barista {

    static Runnable teaMaker1 = new Thread(new teaMaker());
    static Runnable teaMaker2 = new Thread(new teaMaker());
    static Runnable coffeeMaker1 = new Thread(new coffeeMaker());
    static Runnable coffeeMaker2 = new Thread(new coffeeMaker());
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server is waiting for customers...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Customer connected.");

                Scanner input = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);


                while (true) {
                    String line;
                    boolean br = false;
                    int orderCount = Integer.parseInt(input.nextLine());
                    for (int i =0; i < orderCount; i ++){
                        line = input.nextLine();
                        if (line.equalsIgnoreCase("exit")) {
                            br=true;
                        }

                        System.out.println(line);
                        output.println(processCommand(line));

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

    private static String processCommand(String command) {
        switch (command.toLowerCase()) {
            case "order tea":
                teaMaker1;
            case "order coffee":
                return "Coffee ordered. In process.";
            case "order status":
                return "Your order status is being checked.";
            case "exit":
                return "Thank you for visiting. Goodbye!";
            default:
                return "Invalid command. Please try again.";
        }
    }
}
