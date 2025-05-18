package utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente extends Thread{

    private Socket socket;
    private PrintWriter out;

    protected Cliente() {
        try {
            socket = new Socket("localhost", 4000);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
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
