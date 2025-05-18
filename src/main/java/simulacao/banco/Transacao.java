package simulacao.banco;

public class Transacao implements Runnable {

    // Atributos
    private long timestamp;
    private String origem;
    private String destino;
    private double valor;
    private String senha;

    /**
     * Método construtor da classe Transacao
     * 
     * @param origem  - String
     * @param destino - String
     * @param valor   - String
     * @param senha   - String
     */
    public Transacao(String origem, String destino, double valor, String senha) {
        this.origem = origem;
        this.senha = senha;
        this.destino = destino;
        this.valor = valor;
    }

    @Override
    public void run() {

        try {
            setTimestamp();
            processarTransacao();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private synchronized void processarTransacao() {        

        Account contaOrigem = AlphaBank.getConta(origem);
        Account contaDestino = AlphaBank.getConta(destino);

        if (!contaOrigem.autenticaSenha(senha)) {
            throw new IllegalArgumentException("Senha inválida.");
        }

        Account firstLock = origem.compareTo(destino) < 0 ? contaOrigem : contaDestino;
        Account secondLock = origem.compareTo(destino) < 0 ? contaDestino : contaOrigem;

        synchronized (firstLock) {
            synchronized (secondLock) {
                if (contaOrigem.debitar(this)) {
                    contaDestino.depositar(this);
                    System.out.printf("Saldo conta de origem: %.2f\n", contaOrigem.getSaldo());
                    System.out.printf("Saldo conta de destino: %.2f\n", contaDestino.getSaldo());
                } else {
                    System.out.println("SALDO INSUFICIENTE");
                }
            }
        }
    }

    public String getOrigem() {
        return origem;
    }

    public String getDestino() {
        return destino;
    }

    public double getValor() {
        return valor;
    }

    public long getTimestamp() {
        return timestamp;
    }

    private void setTimestamp() {
        this.timestamp = System.nanoTime();
    }

}