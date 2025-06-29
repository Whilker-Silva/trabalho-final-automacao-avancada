package simulacao;

public class Principal {

    public static void main(String[] args) {

        try {

            /* Simulão para realizar a reconcilição de dados */
            EnvSimulator ev = new EnvSimulator(1, 10, true, true);

            /* Simulação com 100 drivers e 200 rotas */
            // EnvSimulator ev = new EnvSimulator(100, 200,false, false);

            ev.start();
            ev.join();
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
