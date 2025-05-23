package simulacao.pkg.banco;

import java.util.ArrayList;

/**
 * Classe que representa uma conta bancária com suporte a operações
 * concorrentes.
 * Estende Thread para permitir execução paralela de transações.
 */
public class Account extends Thread {

    // Atributos
    private ArrayList<Transacao> extrato;
    private String login;
    private String senha;
    private double saldo;

    // Controle de lock dos metodos que acessam saldo e extrato
    private final Object lock = new Object();

    /**
     * Método construtor da classe {@code Account}
     * 
     * @param login       String
     * @param senha       String
     * @param saldoIncial double
     */
    public Account(String login, String senha, double saldoInical) {
        this.setName("Account_" + login);
        this.extrato = new ArrayList<>();
        this.login = login;
        this.senha = senha;
        this.saldo = saldoInical;
        this.start();
    }

    @Override
    public void run() {
        // TODO implementar geração de extrato
    }

    /**
     * Debita uma quantia da conta, se houver saldo suficiente e autenticação
     * válida.
     * 
     * @param transacao Transação a ser realizada
     * @param senha     Senha da conta para autenticação
     * @throws InterruptedException
     * @throws IllegalArgumentException
     */
    public void debitar(Transacao transacao, String senha) throws InterruptedException {
        synchronized (this.lock) {
            if (!this.autenticaSenha(senha)) {
                throw new IllegalArgumentException("Senha incorreta");
            }
            if (!transacao.getOrigem().equals(login)) {
                throw new IllegalArgumentException("Conta de origem incorreta");
            }
            if (!this.verificaSaldo(transacao.getValor())) {
                throw new IllegalArgumentException("Saldo insuficiente!");
            }

            this.saldo -= transacao.getValor();
            this.extrato.add(transacao);
        }
    }

    /**
     * Deposita uma quantia na conta.
     *
     * @param transacao Transação a ser realizada
     * @throws InterruptedException
     * @throws IllegalArgumentException
     */
    public void depositar(Transacao transacao) throws InterruptedException {
        synchronized (this.lock) {
            if (!transacao.getDestino().equals(login)) {
                throw new IllegalArgumentException("Conta de destino incorreta");
            }
            this.saldo += transacao.getValor();
            this.extrato.add(transacao);
        }
    }

    /**
     * Autentica a senha fornecida com a senha da conta.
     *
     * @param senha String - Senha a ser verificada
     * @return true se a senha for correta, false caso contrário
     */
    private boolean autenticaSenha(String senha) {
        return this.senha.equals(senha);
    }

    /**
     * Verifica se há saldo suficiente para uma transação.
     *
     * @param valor Valor a ser verificado
     * @return true se houver saldo suficiente, false caso contrário
     */
    private boolean verificaSaldo(double valor) {
        return this.saldo >= valor;
    }

    /**
     * @return Saldo da conta
     * @throws InterruptedException
     */
    public double getSaldo() throws InterruptedException {
        synchronized (this.lock) {
            return this.saldo;
        }
    }

    /**
     * Retorna o login da conta.
     *
     * @return Login da conta
     */
    public String getLogin() {
        return this.login;
    }
}
