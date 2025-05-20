package pkg.company;

import java.util.ArrayList;
import java.util.HashMap;

import pkg.banco.Account;
import pkg.banco.AlphaBank;
import pkg.banco.Transacao;
import utils.BotPayment;
import utils.Crypto;
import utils.Json;
import utils.Server;

/**
 * Classe Company que atua como Cliente do AlphaBank e Server para Car
 * Implementa funcionalidades de comunicação com o banco e com os carros
 */
public class Company extends Thread {

    private ServerCompany serverCompany;
    private BotPayment botPayment;
    private Account contaCorrente;
    private String senha;

    private HashMap<Integer, Route> rotasExecutar;
    private HashMap<Integer, Route> rotasExecuntado;
    private HashMap<Integer, Route> rotasExecutadas;

    /**
     * Construtor da classe Company
     * 
     * @param login - Login da conta no AlphaBank
     * @param senha - Senha da conta no AlphaBank
     */
    public Company(String login, String senha) {

        // Cria uma Accont para company
        this.senha = senha;
        contaCorrente = AlphaBank.criarConta(login, senha, 1000000);

        // Inicializa o Serve para comunicação com os Cars
        serverCompany = new ServerCompany(4001, "Company");
        serverCompany.start();

        // Inicializa o BotPayment para realizar pagamentos aos Drivers
        botPayment = new BotPayment(4000, login);
        botPayment.start();

        rotasExecutar = new HashMap<>();
        rotasExecuntado = new HashMap<>();
        rotasExecutadas = new HashMap<>();
        
        for (int i = 1; i <= 200; i++) {
            rotasExecutar.put(i, new Route(i));
        }



    }

    public void pagarMotorista(String destino, double valor) {
        botPayment.solicitarTransferencia(getLogin(), destino, valor, senha);
    }

    public String getLogin() {
        return contaCorrente.getLogin();
    }

    /**
     * 
     */
    private class ServerCompany extends Server {

        public ServerCompany(int port, String name) {
            super(port, name);
        }

        @Override
        protected void processarMensagem(String msg) throws Exception {
            Json.fromJson(Crypto.descriptografar(msg), Transacao.class);
            // TODO Altera para tipo de mensagem correta (trocada com a classe Car) e
            // procesa-la
        }

    }

}
