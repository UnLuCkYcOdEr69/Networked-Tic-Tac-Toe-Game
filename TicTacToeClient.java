import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TicTacToeClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            String role = in.readLine();
            System.out.println("You are player " + (role.equals("PLAYER_X") ? "X" : "O"));
            char playerSymbol = (role.equals("PLAYER_X") ? 'X' : 'O');

            while (true) {
                String message = in.readLine();
                if (message.equals("YOUR_TURN")) {
                    System.out.println("Your turn. Enter position (1-9): ");
                    int move = scanner.nextInt();
                    out.println(move);
                } else if (message.equals("WAIT")) {
                    System.out.println("Waiting for opponent's move...");
                } else if (message.startsWith("UPDATE_BOARD")) {
                    String[] parts = message.split(":", 2);
                    if (parts.length == 2) {
                        String boardStr = parts[1];
                        displayBoard(boardStr);
                    }
                } else if (message.equals("WIN")) {
                    System.out.println("Congratulations! You won!");
                    break;
                } else if (message.equals("LOSE")) {
                    System.out.println("Sorry! You lost!");
                    break;
                } else if (message.equals("DRAW")) {
                    System.out.println("It's a draw!");
                    break;
                }
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void displayBoard(String boardStr) {
        System.out.println("Current Board:");
        System.out.println("-------------");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int index = i * 3 + j;
                System.out.print("| " + boardStr.charAt(index) + " ");
            }
            System.out.println("|");
            System.out.println("-------------");
        }
    }
}
