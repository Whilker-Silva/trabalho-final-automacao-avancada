package reconciliacao;

public class Principal {
    public static void main(String[] args) {
        Reconciliacao reconciliacao = new Reconciliacao( "data/dados simulacao/Dados_simulacao.xlsx");
        reconciliacao.start();
    }
}
