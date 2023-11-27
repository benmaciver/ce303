import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Customer {
    private static String regex = "order \\d (tea|coffee)";

    public static void main(String[] args) {

        try {

            Socket socket = new Socket("localhost", 5000);

            Scanner input = new Scanner(new InputStreamReader(socket.getInputStream()));
            PrintWriter output =  new PrintWriter(socket.getOutputStream(), true);

            Scanner userInput = new Scanner(new InputStreamReader(System.in));

            while (true) {
                System.out.println("Enter a command (order tea, order coffee, order status, exit):");
                output.println("new request incoming");
                String command = userInput.nextLine();
                command = command.toLowerCase();
                //output.println("new request incoming");
                ArrayList<String> orders = findRegexMatches(regex, command);
                if (orders.isEmpty()) {
                    System.out.println("Invalid command");
                    continue;
                }
                ArrayList<String> refinedOrders = new ArrayList<>();
                for (String o : orders)
                {
                    String[] words = o.split("\\s+");
                    int count = Integer.parseInt(words[1]);
                    String newOrder = words[0] + " " + words[2];
                    for (int i = 0; i < count; i++){
                        refinedOrders.add(newOrder);
                    }
                }
                int orderCount = refinedOrders.size();
                output.println(orderCount);
                for (String o : refinedOrders) {
                    output.println(o);
                }

                String line;
                for (int i =0; i < orderCount; i ++){
                    line = input.nextLine();
                    System.out.println("Server Reply: " + line);
                }

                if (command.equalsIgnoreCase("exit")) {
                    break;
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
    private static ArrayList<String> findRegexMatches(String regex, String inputText)
    {
        ArrayList<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputText);

        while (matcher.find()) {
            String match = matcher.group();
            result.add(match);
        }
        return result;
    }

}
