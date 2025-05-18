package simulacao.banco;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import utils.Server;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import utils.Crypto;
import utils.Json;

public class AlphaBank extends Thread {

    private static AlphaBank instancia;
    private Map<String, Account> listaContas;
    private ServerBank serverBank;

    private AlphaBank() {        
        instancia = this;
        listaContas = new ConcurrentHashMap<>();
        serverBank = new ServerBank(4000, "AlphaBank");   
        this.start();     
    }

    /**
     * Método para instancia única de AlphaBank
     * 
     * @return AlphaBank
     */
    private static AlphaBank getInstancia() {
        if (instancia == null) {
            instancia = new AlphaBank();
        }
        return instancia;
    }

    public static Account criarConta(String login, String senha){
        return getInstancia().criarContaStatic(login, senha);
    }

    /**
     * 
     * @param login
     * @param senha
     * @return instancia de ContaCorrente
     */
    private synchronized Account criarContaStatic(String login, String senha) {
        Account novaConta = new Account(login, senha, 1000);
        listaContas.put(login, novaConta);
        return novaConta;
    }

    public static Account getConta(String login){
        return getInstancia().getContaStatic(login);
    }

    /**
     * 
     * @param login
     * @return instancia da ContaCorrente correspondente ao login
     */
    private synchronized Account getContaStatic(String login) {

        if (listaContas.get(login) == null) {
            throw new IllegalArgumentException("Login não encontrado.");
        }
        return listaContas.get(login);
    }

    @Override
    public void run() {
        serverBank.start();
    }


    private class ServerBank extends Server {

        public ServerBank(int port, String name) {
            super(port, name);
        }

        @Override
        protected void serverCallback(Socket clientSocket) {

            new Thread(() -> {
                try (
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String msg;
                    while ((msg = in.readLine()) != null) {
                        Transacao transacao = Json.fromJson(Crypto.descriptografar(msg), Transacao.class);
                        Thread transacaoThread = new Thread(transacao);
                        transacaoThread.start();
                    }
                }

                catch (Exception e) {
                    System.err.println("Erro ao tratar cliente: " + e.getMessage());
                }

                finally {
                    try {
                        clientSocket.close();
                    } catch (Exception e) {
                        System.err.println("Erro ao fechar socket do cliente");
                    }
                }
            }).start();
        }

    }

}
