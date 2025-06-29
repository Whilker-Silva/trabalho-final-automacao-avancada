package reconciliacao;

public class Principal {
    public static void main(String[] args) {
        Reconciliacao reconciliacao = new Reconciliacao( "data/Dados_carros.xlsx");
        reconciliacao.start();
    }
}
