package pkg.driver;

import pkg.banco.AlphaBank;
import pkg.banco.Account;
import utils.BotPayment;

public class Driver extends Thread {

    private BotPayment botPayment;
    private Account contaCorrente;
    private String senha;

    public Driver(String login, String senha) {
        this.senha = senha;
        contaCorrente = AlphaBank.criarConta(login, senha, 0);
        botPayment = new BotPayment(4000,login);
        botPayment.start();
    }

    public void abastacer(String destino, double valor) {
        botPayment.solicitarTransferencia(getLogin(), destino, valor, senha);
    }

    public String getLogin() {
        return contaCorrente.getLogin();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();
    }

}
