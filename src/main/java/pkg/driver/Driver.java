package pkg.driver;

import pkg.banco.BotPayment;

public class Driver extends Thread {

    private final String login;
    private final String senha;
    private final BotPayment botPayment;    

    public Driver(String login, String senha) {
        this.login =login;
        this.senha = senha;
        
        botPayment = new BotPayment(4000,login, senha, 0);
        botPayment.start();
    }

    public void abastacer(String destino, double valor) {
        botPayment.solicitarTransferencia(destino, valor, senha);
    }

    public String getLogin() {
        return login;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
    }

}
