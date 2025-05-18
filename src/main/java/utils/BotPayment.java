package utils;

public interface BotPayment {
    void solicitarTransferencias(String oringem, String destino, double valor, String senha);
}
