package simulacao.banco;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import utils.Server;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import utils.Crypto;
import utils.Json;

public class AlphaBank extends Server {

    private static AlphaBank instancia;
    private Map<String, Account> listaContas;

    private AlphaBank() {
        super(4000);
        instancia = this;
        listaContas = new ConcurrentHashMap<>();
    }

    /**
     * Método para instancia única de AlphaBank
     * 
     * @return AlphaBank
     */
    public static AlphaBank getInstancia() {
        if (instancia == null) {
            instancia = new AlphaBank();
        }
        return instancia;
    }

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(port);
            running = true;

            while (running) {
                Socket clientSocket = serverSocket.accept();
                serverCallback(clientSocket);                
                // TODO verificar necessidade de colocar a Thread para dormir
            }
        }

        catch (Exception e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }

        finally {
            stopServer();
        }
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

    /**
     * 
     * @param login
     * @param senha
     * @return instancia de ContaCorrente
     */
    public synchronized Account criarConta(String login, String senha) {
        Account novaConta = new Account(login, senha, 1000);
        listaContas.put(login, novaConta);
        return novaConta;
    }

    /**
     * 
     * @param login
     * @return instancia da ContaCorrente correspondente ao login
     */
    public synchronized Account getConta(String login) {

        if (listaContas.get(login) == null) {
            throw new IllegalArgumentException("Login não encontrado.");
        }
        return listaContas.get(login);
    }

}
