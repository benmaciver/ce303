import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Customer {
    private static String regex = "Order \\d (tea|coffee)";


    public static void main(String[] args) {
        String serverAddress = "localhost"; // Change this to the actual server address
        int portNumber = 12345;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try (Socket socket = new Socket(serverAddress, portNumber)) {
                System.out.println("Connected to the Virtual Café!");
                System.out.println("What would you like to do : Order tea/coffee,get order status or exit ? ");
                String order = scanner.nextLine();

                ArrayList<String> orders = findRegexMatches(regex, order);
                if (orders.isEmpty()) {
                    System.out.println("Invalid command");
                    continue;
                }
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                for (String o : orders)
                    writer.println(o);


                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String confirmation = reader.readLine();
                System.out.println("Server says: " + confirmation);


            } catch (IOException e) {
                System.err.println("Error occurred while connecting to the Virtual Café: " + e.getMessage());
            }
        }
    }
    private static ArrayList<String> findRegexMatches(String regex, String inputText)
    {
        ArrayList<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputText);

        // Find all matches
        while (matcher.find()) {
            String match = matcher.group();
            result.add(match);
        }
        return result;

    }
}