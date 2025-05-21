package pkg.banco;

import utils.Server;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

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
    private final BlockingQueue<Transacao> filaTransacoes;
    private final Map<String, Account> listaContas;
    private final ExecutorService poolTransacoes;
    private final ServerBank serverBank;

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
        this.poolTransacoes = Executors.newFixedThreadPool(20);
        this.filaTransacoes = new LinkedBlockingQueue<>();
        this.listaContas = new ConcurrentHashMap<>();

        this.serverBank = new ServerBank(4000, "AlphaBank");
        this.start();
    }

    /**
     * Executa a thread do banco.
     * Inicia o servidor e despacha transações da fila para o pool.
     */
    @Override
    public void run() {
        serverBank.start();

        while (serverBank.isAlive()) {
            try {
                // Aguarda uma transação da fila (Take ao invez de peek para Thread dormir)
                Transacao transacao = filaTransacoes.take();

                if (transacao != null) {
                    /*
                     * Uso do submit é similar ao start,
                     * Contudo, permite melhor controle de concorrência
                     */
                    poolTransacoes.submit(transacao);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread do AlphaBank interrompida.");
            } catch (Exception e) {
                System.err.println("Erro ao processar transação: " + e.getMessage());
            }
        }
    }

    /**
     * Cria uma nova conta, adiciona ao banco e retorna para o titular.
     * 
     * @param login       String
     * @param senha       String
     * @param saldoIncial double
     * @return Instancia de Accout
     */
    public static Account criarConta(String login, String senha, double saldoIncial) {
        return getInstancia().criarContaStatic(login, senha, saldoIncial);
    }

    private synchronized Account criarContaStatic(String login, String senha, double saldoIncial) {
        Account novaConta = new Account(login, senha, saldoIncial);
        listaContas.put(login, novaConta);
        return novaConta;
    }

    /**
     * Retorna a conta associada ao login informado.
     * @param login String
     * @return instância correspondente de Account
     */
    public static Account getConta(String login) {
        return getInstancia().getContaStatic(login);
    }
   
    private synchronized Account getContaStatic(String login) {
        Account conta = listaContas.get(login);
        if (conta == null) {
            throw new IllegalArgumentException("Login não encontrado.");
        }
        return conta;
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
                filaTransacoes.put(transacao);
            }

            catch (Exception e) {
                System.err.println("Erro ao processar mensagem: " + e.getMessage());
                throw e;
            }
        }
    }

}
