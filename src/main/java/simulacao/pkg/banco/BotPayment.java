package simulacao.pkg.banco;

import utils.Cliente;
import utils.Json;

/**
 * Classe {@code BotPayment} representa um cliente automatizado que interage com o sistema bancário
 * para realizar transferências via mensagens JSON.
 * <p>
 * Herda de {@code Cliente} que é uma {@code Thread} e encapsula uma conta corrente que é criada no momento da instância.
 * </p>
 */
public class BotPayment extends Cliente {

    private final Account contaCorrente;

     /**
     * Construtor do {@code BotPayment}.
     * @param login        String
     * @param senha        String
     * @param saldoInical  double
     * @throws InterruptedException 
     */
    public BotPayment(String login, String senha, double saldoInical) throws InterruptedException {
        super(4000, login);
        contaCorrente = AlphaBank.criarConta(login, senha, saldoInical);
    }

    /**
     * Solicita uma transferência bancária para outro cliente.
     * A transação é serializada em JSON e enviada por meio do método {@code enviaMensagem}.
     *
     * @param destino  Login do destinatário.
     * @param valor    Valor a ser transferido.
     * @param senha    Senha do remetente para autenticação.
     */
    public void solicitarTransferencia(String destino, double valor, String senha) {
        Transacao novaTransacao = new Transacao(contaCorrente.getLogin(), destino, valor, senha);
        String msg = Json.toJson(novaTransacao);
        try {
            enviaMensagem(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
