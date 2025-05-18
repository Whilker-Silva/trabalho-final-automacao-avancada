package simulacao.Driver;

import simulacao.banco.AlphaBank;
import simulacao.banco.Account;
import simulacao.banco.Transacao;
import utils.Cliente;
import utils.Json;

public class Driver extends Cliente {

    private AlphaBank bank;
    private Account contaCorrente;
    private String senha;

    public Driver(String login, String senha) {
        this.senha = senha;
        this.bank = AlphaBank.getInstancia();
        contaCorrente = bank.criarConta(login, senha);
    }

    public synchronized void solicitarTransferencias(String destino, double valor) {
        Transacao novaTransacao = new Transacao(contaCorrente.getLogin(), destino, valor, senha);
        String msg = Json.toJson(novaTransacao);        
        try {
            enviaMensagem(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getLogin() {
        return contaCorrente.getLogin();
    }

}
