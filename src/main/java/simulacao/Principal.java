package simulacao;

public class Principal {

    public static void main(String[] args) {

        try {
            EnvSimulator ev = new EnvSimulator();
            ev.start();
            ev.join();
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
