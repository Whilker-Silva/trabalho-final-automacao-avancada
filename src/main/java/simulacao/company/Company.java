package simulacao.company;

import simulacao.banco.Account;
import simulacao.banco.AlphaBank;
import simulacao.banco.Transacao;
import utils.Crypto;
import utils.Json;
import utils.Server;

/**
 * Classe Company que atua como Cliente do AlphaBank e Server para Car
 * Implementa funcionalidades de comunicação com o banco e com os carros
 */
public class Company extends Thread {

    private BotPaymentCompany botPayment;
    private Account contaCorrente;
    private String senha;
    private ServerCompany serverCompany;

    /**
     * Construtor da classe Company
     * 
     * @param login - Login da conta no AlphaBank
     * @param senha - Senha da conta no AlphaBank
     * @param port  - Porta para o servidor que atenderá os carros
     */
    public Company(String login, String senha) {

        this.senha = senha;
        contaCorrente = AlphaBank.criarConta(login, senha, 100000);

        serverCompany = new ServerCompany(4001, "Company");
        botPayment = new BotPaymentCompany(login);
        
        serverCompany.start();
        botPayment.start();
    }


    public void pagarMotorista(String destino, double valor){
        botPayment.solicitarTransferencia(getLogin(), destino, valor, senha);
    }

    public String getLogin(){
        return contaCorrente.getLogin();
    }
  


    /**
     * 
     */
    private class ServerCompany extends Server {

        public ServerCompany(int port, String name) {
            super(port, name);
        }

       
        @Override
        protected void processarMensagem(String msg) throws Exception {
            Json.fromJson(Crypto.descriptografar(msg), Transacao.class);
            // TODO Altera para tipo de mensagem correta (trocada com a classe Car)  e procesa-la           
        }

    }

}
