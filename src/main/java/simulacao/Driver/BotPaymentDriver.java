package simulacao.Driver;

import simulacao.banco.Transacao;
import utils.BotPayment;
import utils.Cliente;
import utils.Json;

public class BotPaymentDriver extends Thread implements BotPayment {

    private ClientBank clientBank;

    public BotPaymentDriver(String login) {
        clientBank = new ClientBank(4000, login);

    }

    @Override
    public void run() {
        try {
            clientBank.start();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public synchronized void solicitarTransferencias(String oringem, String destino, double valor, String senha) {

        Transacao novaTransacao = new Transacao(oringem, destino, valor, senha);
        String msg = Json.toJson(novaTransacao);

        try {
            clientBank.enviaMensagem(msg);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ClientBank extends Cliente {

        public ClientBank(int port, String name) {
            super(port, name);
        }

        public void enviaMensagem(String msg) {
            super.enviaMensagem(msg);
        }
    }
}
