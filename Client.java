import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author jonathangriffey
 * @version 1.0
 * @since 2023-01
 */
public class Client {

    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private String username;

    /**
     * this constructor is used to create a new Client object for each client that connects to the server,
     * and this object will handle all communication with the server.
     * @param socket
     * @param username
     */
    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, br, bw);
        }
    }

    /**
     * the sendMessage() method uses the write() method of the BufferedWriter object and writes the username, then uses newLine() method to insert a new line, then flush the buffer.
     * It creates a new Scanner object that reads input from the standard input (System.in).
     * It uses a while loop to continuously listen for messages from the client.
     * It uses the nextLine() method of the Scanner object to read a line of text from the standard input (System.in) and assigns it to the variable "messageToSend".
     * It again uses the write() method of the BufferedWriter object to write the message in the format of "username: messageToSend", then uses newLine() method to insert a new line, then flush the buffer.
     * It also has a try-catch block to handle IOException
     */
    public void sendMessage() {
        try {
            bw.write(username);
            bw.newLine();
            bw.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bw.write(username + ": " + messageToSend);
                bw.newLine();
                bw.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, br, bw);
        }
    }

    /**
     * The listenForMessage() method creates a new thread and starts it, this thread is used to listen for messages from the server.
     * It creates an anonymous Runnable object and override its run method.
     * It uses a while loop to continuously listen for messages from the server.
     * It uses the readLine() method of the BufferedReader object to read a line of text from the server.
     * It assigns the read message to the variable "msgFromGroupChat" and prints it to the console using System.out.println(msgFromGroupChat).
     * It also has a try-catch block to handle IOException
     */
    public void listenForMessage() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()) {
                    try {
                        msgFromGroupChat = br.readLine();
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        closeEverything(socket, br, bw);
                    }
                }
            }
        }).start();
    }

    /**
     * closeEverything() method is used to close the socket, BufferedReader, and BufferedWriter objects when a client disconnects or an error occurs.
     * It takes in 3 parameters, a Socket, a BufferedReader and a BufferedWriter
     * It uses the close() method of the BufferedReader, BufferedWriter, and Socket classes to close the objects.
     * It also has try-catch block to handle IOException
     * @param socket
     * @param br
     * @param bw
     */
    public void closeEverything(Socket socket, BufferedReader br, BufferedWriter bw) {
        try {
            if (br != null) {
                br.close();
            }
            if (bw != null) {
                bw.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * main method creates a new client object and connects it to the server running on the localhost at port 1234,
     * and starts listening and sending messages to the server.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the group chat: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }

}
