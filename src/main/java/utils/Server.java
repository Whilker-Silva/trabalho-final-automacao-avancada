package utils;

import java.net.ServerSocket;
import java.net.Socket;

public abstract class Server extends Thread {

    // Atributos
    private ServerSocket serverSocket;
    private boolean running;
    private String name;
    private int port;

    public Server(int port, String name) {
        this.running = false;
        this.name = name;
        this.port = port;
    }

    /**
     * Método que inicia o servidor para atender os clientes
     */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("Server " + name + " iniciada na porta " + port);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                serverCallback(clientSocket);
            }
        }

        catch (Exception e) {
            System.err.println("Erro no servidor " + name + ": " + e.getMessage());
        }

        finally {
            stopServer();
        }
    }

    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println(getClass().getSimpleName() + " encerrado.");
            }
        }

        catch (Exception e) {
            System.err.println("Erro ao fechar o servidor: " + e.getMessage());
        }
    }

    protected abstract void serverCallback(Socket socket);
}