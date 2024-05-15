import java.io.*;
import java.net.*;

public class TicTacToeServer {
    private static char[][] board = new char[3][3];
    private static char currentPlayer = 'X';
    private static Socket player1;
    private static Socket player2;
    private static PrintWriter player1Out;
    private static PrintWriter player2Out;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server is running...");
            player1 = serverSocket.accept();
            player1Out = new PrintWriter(player1.getOutputStream(), true);
            player1Out.println("PLAYER_X");

            player2 = serverSocket.accept();
            player2Out = new PrintWriter(player2.getOutputStream(), true);
            player2Out.println("PLAYER_O");

            initializeBoard();

            while (true) {
                if (currentPlayer == 'X') {
                    player1Out.println("YOUR_TURN");
                    player2Out.println("WAIT");
                    receiveMove(player1, player1Out);
                    if (checkWin('X')) {
                        player1Out.println("WIN");
                        player2Out.println("LOSE");
                        break;
                    } else if (isBoardFull()) {
                        player1Out.println("DRAW");
                        player2Out.println("DRAW");
                        break;
                    }
                    currentPlayer = 'O';
                } else {
                    player2Out.println("YOUR_TURN");
                    player1Out.println("WAIT");
                    receiveMove(player2, player2Out);
                    if (checkWin('O')) {
                        player2Out.println("WIN");
                        player1Out.println("LOSE");
                        break;
                    } else if (isBoardFull()) {
                        player1Out.println("DRAW");
                        player2Out.println("DRAW");
                        break;
                    }
                    currentPlayer = 'X';
                }
            }

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
    }

    private static void receiveMove(Socket playerSocket, PrintWriter playerOut) throws IOException {
        BufferedReader playerIn = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
        int move = Integer.parseInt(playerIn.readLine());
        int row = (move - 1) / 3;
        int col = (move - 1) % 3;
        if (board[row][col] == '-') {
            board[row][col] = currentPlayer;
            sendBoardState();
        } else {
            playerOut.println("INVALID_MOVE");
            receiveMove(playerSocket, playerOut);
        }
    }

    private static void sendBoardState() {
        StringBuilder boardStr = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardStr.append(board[i][j]);
            }
        }
        player1Out.println("UPDATE_BOARD:" + boardStr.toString());
        player2Out.println("UPDATE_BOARD:" + boardStr.toString());
    }

    private static boolean checkWin(char symbol) {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == symbol && board[i][1] == symbol && board[i][2] == symbol) {
                return true;
            }
        }
        // Check columns
        for (int j = 0; j < 3; j++) {
            if (board[0][j] == symbol && board[1][j] == symbol && board[2][j] == symbol) {
                return true;
            }
        }
        // Check diagonals
        if ((board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol) ||
                (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol)) {
            return true;
        }
        return false;
    }

    private static boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }
}
