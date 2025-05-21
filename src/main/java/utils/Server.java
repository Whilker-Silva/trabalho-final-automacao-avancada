package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

    private void serverCallback(Socket socket) {

        new Thread(() -> {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String msg;
                while ((msg = in.readLine()) != null) {
                   processarMensagem(msg);
                }
            }

            catch (Exception e) {
                System.err.println("Erro ao tratar cliente: " + e.getMessage());
            }

            finally {
                try {
                    socket.close();
                } catch (Exception e) {
                    System.err.println("Erro ao fechar socket do cliente");
                }
            }
        }, "clt_"+socket.getLocalPort()).start();

    }

    protected abstract void processarMensagem(String msg) throws Exception ;
}