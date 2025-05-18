package utils;

import java.net.ServerSocket;
import java.net.Socket;

public abstract class Server extends Thread {

    protected int port;
    protected ServerSocket serverSocket;
    protected boolean running;

    public Server(int port) {
        this.port = port;
        this.running = false;
    }

    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println(getClass().getSimpleName() + " encerrado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao fechar o servidor: " + e.getMessage());
        }
    }

    protected abstract void serverCallback(Socket socket);
}