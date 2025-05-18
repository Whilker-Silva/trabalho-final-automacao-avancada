package simulacao.company;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import simulacao.banco.Transacao;
import utils.Cliente;
import utils.Crypto;
import utils.Json;
import utils.Server;

/**
 * Classe Company que atua como Cliente do AlphaBank e Server para Car
 * Implementa funcionalidades de comunicação com o banco e com os carros
 */
public class Company extends Thread {

    private String login;
    private String senha;
    private ClienteBanco clienteBanco;
    private ServerCompany serverCompany;

    /**
     * Construtor da classe Company
     * 
     * @param login - Login da conta no AlphaBank
     * @param senha - Senha da conta no AlphaBank
     * @param port  - Porta para o servidor que atenderá os carros
     */
    public Company(String login, String senha, int port) {
        
        this.login = login;
        this.senha = senha;
        this.serverCompany = new ServerCompany(port, "Company");
        this.clienteBanco = new ClienteBanco(port);
        

        this.clienteBanco.start();
    }

    

    /**
     * Método que processa as mensagens recebidas dos carros
     * 
     * @param mensagem - Mensagem recebida do carro
     * @param out      - Stream para enviar resposta ao carro
     */
    private void processarMensagemCarro(String mensagem, PrintWriter out) {
        try {
            // Aqui você pode implementar a lógica de processamento das mensagens dos carros
            // Por exemplo, receber pedidos de serviço, informações de status, etc.
            System.out.println("Mensagem recebida do carro: " + mensagem);

            // Exemplo de resposta para o carro
            String resposta = "Mensagem recebida com sucesso pela Company";
            out.println(Crypto.criptografar(resposta));
        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem: " + e.getMessage());
        }
    }

    /**
     * Método para realizar transferência bancária através do AlphaBank
     * 
     * @param destino - Conta de destino
     * @param valor   - Valor a ser transferido
     * @return boolean - Sucesso da operação
     */
    public boolean realizarTransferencia(String destino, double valor) {
        try {
            Transacao transacao = new Transacao(this.login, destino, valor, this.senha);
            String transacaoJson = Json.toJson(transacao);
            clienteBanco.enviaMensagem(transacaoJson);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao realizar transferência: " + e.getMessage());
            return false;
        }
    }

    /**
     * Classe interna que implementa o cliente para comunicação com o AlphaBank
     */
    private class ClienteBanco extends Cliente {
        public ClienteBanco(int port) {
            super(port);
        }

        public void enviaMensagem(String mensagem) {
            super.enviaMensagem(mensagem);
        }
    }

    /**
     * Classe interna que implementa o cliente para comunicação com o AlphaBank
     */
    private class ServerCompany extends Server {

        public ServerCompany(int port, String name) {
            super(port, name);
        }

        /**
         * Método que processa as requisições dos carros
         */
        @Override
        protected void serverCallback(Socket socket) {
            new Thread(() -> {
                try (
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    String mensagemCriptografada;
                    while ((mensagemCriptografada = in.readLine()) != null) {
                        try {
                            String mensagemDescriptografada = Crypto.descriptografar(mensagemCriptografada);
                            processarMensagemCarro(mensagemDescriptografada, out);
                        } catch (Exception e) {
                            System.err.println("Erro ao processar mensagem do carro: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Erro na comunicação com o carro: " + e.getMessage());
                }
            }).start();
        }
    }

}
