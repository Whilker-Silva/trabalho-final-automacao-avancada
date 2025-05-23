package pkg.banco;

import utils.Server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import utils.Crypto;
import utils.Json;

/**
 * Classe {@code AlphaBank} representa uma instância única do banco que gerencia
 * contas de clientes,
 * processa transações financeiras e escuta requisições através de um servidor
 * próprio.
 *
 * <p>
 * Utiliza o padrão Singleton para garantir uma única instância da aplicação
 * bancária.
 * A lista de contas é gerenciada por um {@code ConcurrentHashMap} para garantir
 * segurança em ambientes concorrentes.
 * Transações são processadas de forma assíncrona por meio de pool de threads.
 * </p>
 */
public class AlphaBank extends Thread {

    // Atributo estático para instancia única
    private static AlphaBank instancia;

    // Atributos
    private final Queue<Transacao> filaTransacoes;
    private final HashMap<String, Account> listaContas;
    private final ExecutorService poolTransacoes;
    private final ServerBank serverBank;

    private boolean isOn;

    // Controle de lock dos metodos que acessam saldo e extrato
    private final Object lockTransacoes = new Object();
    private final Object lockContas = new Object();

    /**
     * Retorna a instância única do {@code AlphaBank}, implementando o padrão
     * Singleton.
     *
     * <p>
     * Este método é sincronizado para garantir que, em ambientes multithread,
     * apenas uma instância do banco seja criada. Sem a palavra-chave
     * {@code synchronized}, múltiplas threads poderiam verificar que
     * {@code instancia == null} ao mesmo
     * tempo e criar mais de uma instância, violando o padrão Singleton.
     * </p>
     *
     * @return instância única de {@code AlphaBank}
     */
    public static synchronized AlphaBank getInstancia() {
        if (instancia == null) {
            instancia = new AlphaBank();
        }
        return instancia;
    }

    /**
     * Construtor privado. Inicializa a estrutura de contas, o servidor e o pool de
     * threads.
     */
    private AlphaBank() {
        this.setName("AlphaBank");
        this.poolTransacoes = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 10L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>());
        this.filaTransacoes = new LinkedList<>();
        this.listaContas = new HashMap<>();

        this.serverBank = new ServerBank(4000, "AlphaBank");
        try {
            if (serverBank.begin()) {
                serverBank.start();
            }

        } catch (Exception e) {
            System.err.println("Erro no servidor AlphaBank: " + e.getMessage());
        }

        this.isOn = true;

    }

    /**
     * Executa a thread do banco.
     * Inicia o servidor e despacha transações da fila para o pool.
     */
    @Override
    public void run() {

        while (this.isOn) {
            try {
                Transacao transacao = null;

                synchronized (lockTransacoes) {
                    while (filaTransacoes.isEmpty()) {
                        if (!this.isOn) {
                            System.out.println("FINALIZANDO ALPHABANK");
                            break;
                        }
                        lockTransacoes.wait();
                    }

                    transacao = filaTransacoes.poll();
                }

                if (transacao != null) {
                    poolTransacoes.submit(transacao);
                }

            }

            catch (Exception e) {
                System.err.println("Erro ao processar transação: " + e.getMessage());
            }
        }
        poolTransacoes.shutdown();
    }

    public void shutdown() {
        synchronized (lockTransacoes) {
            serverBank.stopServer();
            this.isOn = false;
            lockTransacoes.notifyAll();
        }
    }

    /**
     * Cria uma nova conta, adiciona ao banco e retorna para o titular.
     * 
     * @param login       String
     * @param senha       String
     * @param saldoIncial double
     * @return Instancia de Accout
     * @throws InterruptedException
     */
    public static Account criarConta(String login, String senha, double saldoIncial) throws InterruptedException {
        return getInstancia().criarContaStatic(login, senha, saldoIncial);
    }

    private Account criarContaStatic(String login, String senha, double saldoIncial) throws InterruptedException {
        synchronized (this.lockContas) {
            Account novaConta = new Account(login, senha, saldoIncial);
            listaContas.put(login, novaConta);
            return novaConta;
        }
    }

    /**
     * Retorna a conta associada ao login informado.
     * 
     * @param login String
     * @return instância correspondente de Account
     */
    public static Account getConta(String login) {
        return getInstancia().getContaStatic(login);
    }

    private Account getContaStatic(String login) throws IllegalArgumentException {
        synchronized (lockContas) {
            Account conta = listaContas.get(login);
            if (conta == null) {
                throw new IllegalArgumentException("Login não encontrado.");
            }
            return conta;
        }
    }

    private void adicionarTransacao(Transacao transacao) {
        synchronized (lockTransacoes) {
            filaTransacoes.add(transacao);
            lockTransacoes.notify();
        }
    }

    /**
     * Classe interna para lidar com mensagens recebidas do servidor.
     */
    private class ServerBank extends Server {

        public ServerBank(int port, String name) {
            super(port, name);
        }

        @Override
        protected void processarMensagem(String msg) throws Exception {
            try {
                String mensagemDescriptografada = Crypto.descriptografar(msg);
                Transacao transacao = Json.fromJson(mensagemDescriptografada, Transacao.class);
                adicionarTransacao(transacao);
            }

            catch (Exception e) {
                System.err.println("Erro ao processar mensagem: " + e.getMessage());
                throw e;
            }
        }
    }
}
