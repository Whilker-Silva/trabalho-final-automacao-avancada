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

    protected Cliente(int port, String name) {
        this.port = port;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            socket = new Socket("localhost", port);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            System.out.println("Cliente " + name + " iniciada na porta " + port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void enviaMensagem(String mensagem) {
        try {
            out.println(Crypto.criptografar(mensagem));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
