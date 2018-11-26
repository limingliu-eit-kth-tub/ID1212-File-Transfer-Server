package Server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;



public class FileTransferServer{
	private static final int LINGER_TIME = 5000;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private final List<ClientHandler> clientHandlers = new ArrayList<>();
    private int portNo = 8080;
	
    public static void main(String[] args) {
        FileTransferServer server = new FileTransferServer();
        server.parseArguments(args);
        server.serve();
    }
    void removeHandler(ClientHandler handler) {
        synchronized (clientHandlers) {
            clientHandlers.remove(handler);
        }
    }
    private void serve() {
        try {
            ServerSocket listeningSocket = new ServerSocket(portNo);
            while (true) {
                Socket clientSocket = listeningSocket.accept();
                startHandler(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Server failure.");
        }
    }
    private void startHandler(Socket clientSocket) throws SocketException {
        clientSocket.setSoLinger(true, LINGER_TIME);
        clientSocket.setSoTimeout(TIMEOUT_HALF_HOUR);
        ClientHandler handler = new ClientHandler(this, clientSocket);
        synchronized (clientHandlers) {
            clientHandlers.add(handler);
        }
        Thread handlerThread = new Thread(handler);
        handlerThread.setPriority(Thread.MAX_PRIORITY);
        handlerThread.start();
    }
    private void parseArguments(String[] arguments) {
        if (arguments.length > 0) {
            try {
                portNo = Integer.parseInt(arguments[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number, using default.");
            }
        }
    }

}
