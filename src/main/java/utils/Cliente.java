package utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente extends Thread {

    private Socket socket;
    private PrintWriter out;
    private int port;
    private String name;

    public Cliente(int port, String name) {
        this.port = port;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            socket = new Socket("localhost", port);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            //System.out.println("Cliente " + name + " iniciada na porta " + port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() {
        try {
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Socket do cliente " + name + " encerrado.");
            }
        } catch (IOException e) {
            System.err.println("Erro ao fechar o socket do cliente " + name + ": " + e.getMessage());
        }
    }

    public void enviaMensagem(String mensagem) {
        try {
            out.println(Crypto.criptografar(mensagem));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
