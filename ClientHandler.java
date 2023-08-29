import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author jonathangriffey
 * @version 1.0
 * @since 2023-01
 */
public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private String clientUsername;

    /**
     * This is a constructor of class "ClientHandler" that takes in a parameter "Socket" and assigns it to an instance variable called "socket".
     * It creates a BufferedWriter object by passing a new OutputStreamWriter object, created using the socket's output stream.
     * It creates a BufferedReader object by passing a new InputStreamReader object, created using the socket's input stream.
     * It reads the first line sent by the client, which is assumed to be the client's username, and assigns it to the instance variable "clientUsername".
     * It adds the current ClientHandler object to a list of "clientHandlers", which is an Array list used to keep track of all connected clients
     * It then sends a message to all connected clients, "SERVER: " + clientUsername + " Has entered the chat!" to broadcast the client's entry to the chat room.
     * It also has a try-catch block to handle IOException
     * @param socket
     */
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = br.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + clientUsername + " Has entered the chat!");
        } catch (IOException e) {
            closeEverything(socket, br, bw);
        }
    }

    /**
     * run() method overrides the run method of the Runnable interface.
     * It's executed when the thread starts, It uses a while loop to continuously listen for messages from the client.
     * It uses the readLine() method of the BufferedReader object to read a line of text from the client.
     * It assigns the read message to the variable "messageFromClient" and calls the broadcastMessage method, passing the message as a parameter,
     * which sends the message to all connected clients.
     * It also has a try-catch block to handle IOException
     */
    @Override
    public void run() {

        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = br.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, br, bw);
                break;
            }
        }
    }

    /**
     * method broadcastMessage() which is used to send a message to all connected clients.
     * It takes in a parameter "messageToSend" which is the message to be broadcasted.
     * It uses a for-each loop to iterate over the "clientHandlers" list, which is probably a static list that holds all connected clients.
     * It uses the write() method of the BufferedWriter object and writes the message, then uses newLine() method to insert a new line, then flush the buffer.
     * It also has a try-catch block to handle IOException and it calls the closeEverything method
     * @param messageToSend
     */
    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bw.write(messageToSend);
                    clientHandler.bw.newLine();
                    clientHandler.bw.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, br, bw);
            }
        }
    }

    /**
     * removeClientHandler() method is used when a client disconnects from the server.
     * It removes the client from the list of connected clients and broadcast a message to the other clients that the client has left the chat.
     */
    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " Has left the chat!");
    }

    /**
     * closeEverything() method is used to close the socket, BufferedReader, and BufferedWriter objects when a client disconnects or an error occurs.
     * It takes in 3 parameters, a Socket, a BufferedReader and a BufferedWriter.
     * It first calls the removeClientHandler method to remove the current client from the list of connected clients and broadcast a message to other clients that the client has left the chat.
     * It uses the close() method of the BufferedReader, BufferedWriter, and Socket classes to close the objects.
     * It also has try-catch block to handle IOException
     * @param socket
     * @param bufferedReader
     * @param bufferedWriter
     */
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
