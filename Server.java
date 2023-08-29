import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author jonathangriffey
 * @version 1.0
 * @since 2023-01
 *
 */
public class Server {
    private ServerSocket serverSocket;

    /**
     * This is a constructor for class "Server" that takes in a parameter "ServerSocket"
     * and assigns it to an instance variable called "serverSocket".
     * This constructor is used to create a new Server object with a specific ServerSocket
     * @param serverSocket
     */
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * startServer() uses a while loop to continuously listen for incoming connections to the server socket.
     * It uses accept() method of the ServerSocket class to wait for a client to connect.
     * Once a client connects, it prints a message "A new client has connected!" and creates a new ClientHandler object by passing the connected socket as a parameter.
     * It starts a new thread with the clienthandler object and runs the start method of thread.
     * It also has a try-catch block to handle IOException
     */
    public void startServer() {

        try {
            while (!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected!");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {

        }
    }

    /**
     * closeServerSocket() t's used to close the server socket that was created in the constructor.
     * It checks if the server socket is not null, if true, it uses the close() method of the ServerSocket class to close the server socket.
     * It also has a try-catch block to handle IOException, and it will print the stack trace if exception occurs
     * Once the server socket is closed, the server will stop listening for new connections, and the startServer method will exit the while loop and end its execution
     */
    public void closeServerSocket() {

        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is the main method of the Server class. It creates a new ServerSocket object by passing in the port number 1234 as a parameter.
     * Then it creates a new Server object and passes the ServerSocket object as a parameter.
     * Finally, it calls the startServer() method on the server object to start listening for incoming connections on the specified port.
     * The main method also throws an IOException, which means that any IO errors that occur within the method will be passed up to the caller of the method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }

}
