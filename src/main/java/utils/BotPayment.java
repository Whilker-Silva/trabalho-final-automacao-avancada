package utils;

import simulacao.banco.Transacao;

public class BotPayment extends Thread{

    private ClientBank clientBank;

    public BotPayment(String login) {
        clientBank = new ClientBank(4000, login);
    }

    @Override
    public void run() {
        try {
            clientBank.start();
        } catch (Exception e) {
           System.out.println("Erro ao criar se conectar com servido do banco na classe BotPayment");
        }
    }

    public synchronized void solicitarTransferencia(String oringem, String destino, double valor, String senha) {

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
