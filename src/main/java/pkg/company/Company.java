package pkg.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import pkg.banco.Account;
import pkg.banco.AlphaBank;
import pkg.banco.BotPayment;
import pkg.banco.Transacao;
import utils.Crypto;
import utils.Json;
import utils.Server;

/**
 * Classe Company que atua como Cliente do AlphaBank e Server para Car
 * Implementa funcionalidades de comunicação com o banco e com os carros
 */
public class Company extends Thread {

    private final String login;
    private final String senha;

    private final ServerCompany serverCompany;
    private final BotPayment botPayment;

    private final Queue<Integer> filaDedados;

    private final Queue<Route> rotasExecutar;
    private final ArrayList<Route> rotasExecuntado;
    private final ArrayList<Route> rotasExecutadas;

    /**
     * Construtor da classe Company
     * 
     * @param login - Login da conta no AlphaBank
     * @param senha - Senha da conta no AlphaBank
     */
    public Company(String login, String senha) {

        this.login = login;
        this.senha = senha;

        // Inicializa o Serve para comunicação com os Cars
        serverCompany = new ServerCompany(4001, "Company");
        serverCompany.start();

        // Inicializa o BotPayment para realizar pagamentos aos Drivers
        botPayment = new BotPayment(4000, login, senha, 10000000);
        botPayment.start();

        // Ininicailiza fila de dados a processar
        filaDedados = new LinkedList<>();

        // Inicializa lista de rotas (A executar, execuntado e executadas)
        rotasExecutar = new LinkedList<>();
        rotasExecuntado = new ArrayList<>();
        rotasExecutadas = new ArrayList<>();

        this.importarRotas(200);
    }

    @Override
    public void run() {
        // TODO Processar fila de dados recebido pelos car e gerara relatórios
    }

    private void importarRotas(int qtd) {
        for (int i = 1; i <= qtd; i++) {
            rotasExecutar.add(new Route(i));
        }
    }

    public String getLogin() {
        return login;
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
            // adicionar na fila de processamento
        }

    }

}
