package simulacao;

public class Principal {

    public static void main(String[] args) {
        try {

            /* Simulação com 100 drivers e 200 rotas */
            // EnvSimulator ev = new EnvSimulator(100, 200,false, false);

            /* Simulação para realizar a reconcilição de dados */
            // EnvSimulator ev = new EnvSimulator(1, 10, true, true);

            /* Simulação para com um unico carro e sem trasito */
            EnvSimulator ev = new EnvSimulator(1, 1, false, false);

            ev.start();
            ev.join();
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
