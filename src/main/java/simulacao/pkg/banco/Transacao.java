package simulacao.pkg.banco;

/**
 * Classe que representa uma transação bancária entre duas contas.
 * 
 * A classe implementa a interface Runnable para que a transação possa ser
 * executada em uma thread separada. Cada transação contém informações
 * sobre a conta de origem, a conta de destino, o valor a ser transferido
 * e a senha da conta de origem para autenticação.
 * 
 * A transação é processada de forma segura com sincronização para evitar
 * condições de corrida.
 */
public class Transacao implements Runnable {

    // Atributos
    private long timestamp;
    private final String origem;
    private final String destino;
    private final double valor;
    private final String senha;

    /**
     * Construtor da classe {@code Transacao}.
     * 
     * @param origem  String - login da conta de origem.
     * @param destino String - login da conta de destino.
     * @param valor   double - Valor a ser transferido.
     * @param senha   String - Senha da conta de origem.
     */
    public Transacao(String origem, String destino, double valor, String senha) {
        this.origem = origem;
        this.senha = senha;
        this.destino = destino;
        this.valor = valor;
    }

    /**
     * Define o timestamp e chama o método de processamento da transação.
     * Executa a transação em uma thread separada.
     */
    @Override
    public void run() {
        try {
            this.setTimestamp();
            this.processarTransacao();
        }

        catch (Exception e) {
            System.out.printf("Erro no método run da transação entre %s e %s\n", origem, destino);
            e.printStackTrace();
        }
    }

    /**
     * Processa a transação entre a conta de origem e a conta de destino.
     * Autentica a senha, sincroniza o acesso às contas envolvidas para evitar
     * condições de corrida e executa a transferência, se possível.
     * 
     * @throws InterruptedException
     * 
     * @throws IllegalArgumentException se a senha da conta de origem for inválida.
     */
    private void processarTransacao() throws InterruptedException {

        Account contaOrigem = AlphaBank.getConta(origem);
        Account contaDestino = AlphaBank.getConta(destino);

        // Ordenação para evitar deadlocks
        Account firstLock = origem.compareTo(destino) < 0 ? contaOrigem : contaDestino;
        Account secondLock = origem.compareTo(destino) < 0 ? contaDestino : contaOrigem;

        synchronized (firstLock) {
            synchronized (secondLock) {
                // Realiza a transação
                contaOrigem.debitar(this, senha);
                contaDestino.depositar(this);
                //System.out.printf("Transferencia de R$%.2f realizada de %s para %s\n", valor, origem, destino);
            }
        }
    }

    /**
     * @return String com o login da conta de origem.
     */
    public String getOrigem() {
        return origem;
    }

    /**
     * @return String com o login da conta de destino.
     */
    public String getDestino() {
        return destino;
    }

    /**
     * @return double com o valor da transferencia.
     */
    public double getValor() {
        return valor;
    }

    /**
     * @return long com o timestamp da trasnsação em nanossegundos.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Define o timestamp da transação com a hora atual em nanossegundos.
     */
    private synchronized void setTimestamp() {
        this.timestamp = System.nanoTime();
    }

}