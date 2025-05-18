package simulacao.driver;

import simulacao.banco.AlphaBank;
import simulacao.banco.Account;

public class Driver extends Thread {

    private BotPaymentDriver botPayment;
    private Account contaCorrente;
    private String senha;

    public Driver(String login, String senha) {
        this.senha = senha;
        contaCorrente = AlphaBank.criarConta(login, senha, 0);
        botPayment = new BotPaymentDriver(login);
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
