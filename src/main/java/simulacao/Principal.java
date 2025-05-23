package simulacao;

public class Principal {

    public static void main(String[] args) {

        EnvSimulator ev = new EnvSimulator();
        ev.setName("MAIN");
        ev.start();

        try {
            ev.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
