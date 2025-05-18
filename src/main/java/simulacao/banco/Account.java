package simulacao.banco;

import java.util.ArrayList;

public class Account extends Thread {

    // Atributos
    private String login;
    private String senha;
    private double saldo;
    private ArrayList<Transacao> extrato;

    /**
     * Método construtor da classe Conta corrente
     * 
     * @param login       - String
     * @param senha       - String
     * @param saldoIncial - double
     */
    public Account(String login, String senha, double saldoInical) {
        extrato = new ArrayList<>();
        this.login = login;
        this.senha = senha;
        this.saldo = saldoInical;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
    }

    /**
     * 
     * @param transacao
     */
    public synchronized void depositar(Transacao transacao) {
        saldo += transacao.getValor();
        extrato.add(transacao);
    }

    /**
     * 
     * @param transacao
     * @return
     *         TRUE para transação realizada com sucesso /
     *         FALSE para saldo insuficiente
     */
    public synchronized boolean debitar(Transacao transacao) {
        if (!verificaSaldo(transacao.getValor())) {
            return false;
        }
        saldo -= transacao.getValor();
        extrato.add(transacao);
        return true;
    }

    /**
     * 
     * @param senha - String
     */
    public synchronized boolean autenticaSenha(String senha) {
        return senha.equals(this.senha);
    }

    public synchronized double getSaldo() {
        return saldo;
    }

    /**
     * 
     * @param valor
     */
    private boolean verificaSaldo(double valor) {
        return saldo >= valor ? true : false;
    }

    public String getLogin() {
        return login;
    }

}
