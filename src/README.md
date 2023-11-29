# Running the Barista and Customer Applications

## Prerequisites

Before you can run the Barista and Customer applications, make sure you have the following installed on your system:

- Java Development Kit (JDK) 17 or higher
- A Java IDE (optional)

## Compiling the java classes

1. Open a terminal window and navigate to the directory containing the `Barista.java` and `Customer.java` file and Helpers package.

2. Compile the classes by using the following command:

   ```bash
   javac Helpers/*.java Barista.java Customer.java

## Running the Barista Server Application

1. Open a terminal window and navigate to the directory containing the `Barista.java` file.

2. Run the barista server application using the following command:

   ```bash
   javac -cp "." Barista
## Running the Customer Client Application

1. Open a terminal window and navigate to the directory containing the `Customer.java` file.

2. Run the Barista server application using the following command:

   ```bash
   javac -cp "." Customer