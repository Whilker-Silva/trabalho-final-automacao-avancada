package simulacao.pkg.driver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


import simulacao.pkg.banco.BotPayment;
import simulacao.pkg.car.Car;
import simulacao.pkg.company.Company;
import simulacao.pkg.company.Route;

public class Driver extends Thread {

    private static int counter = 0;
    private static final Object lockCounter = new Object();
    private final Object lockRotas = new Object();

    private String login;
    private String senha;
    private BotPayment botPayment;
    private Car car;

    private final Queue<Route> rotasExecutar;
    private final ArrayList<Route> rotasExecutando;
    private final ArrayList<Route> rotasExecutadas;

    public Driver(String login, String senha) {

        setName(login);
        this.login = login;
        this.senha = senha;

        car = new Car(login);
        Company.getInstance().addCar(car);

        try {
            botPayment = new BotPayment(login, senha, 1000);
            botPayment.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        rotasExecutar = new LinkedList<>();
        rotasExecutando = new ArrayList<>();
        rotasExecutadas = new ArrayList<>();

        synchronized (lockCounter) {
            counter++;
        }
    }

    @Override
    public void run() {

        solicitaRota();

        while (!executarIsEmpty()) {

            try {

                // Move rota a executar para rotas em execução
                Route rotaExecutando = removeRotasExecutar();
                addRotasExecutando(rotaExecutando);

                // configura a rota a ser executa pelo
                car.setRoute(rotaExecutando);

                // Lança a Thred car para executar a rota e aguarda a mesma finalizar
                Thread threadCar = new Thread(car);
                threadCar.start();
                threadCar.join();

                // Move rota para rotas executadas
                Route finalizada = removeRotasExecutando(0);
                addRotasExecutadas(finalizada);

                // Solicita uma nova rota
                solicitaRota();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        synchronized (lockCounter) {
            counter--;
        }

        System.out.printf("%s encerrado\n", login);
        botPayment.closeSocket();
        // car.closeSocket();

    }

    private void solicitaRota() {
        Route rota = Company.getInstance().getRoute();
        if (rota != null) {
            addRotasExecutar(rota);
        }
    }

    public void pagarAbastecimento(double litros) {
        double valor = litros * 5.87;
        botPayment.solicitarTransferencia("fuelStation", valor, senha);
    }

    public String getLogin() {
        return login;
    }

    private void addRotasExecutar(Route route) {
        synchronized (lockRotas) {
            rotasExecutar.add(route);
        }
    }

    private void addRotasExecutando(Route route) {
        synchronized (lockRotas) {
            rotasExecutando.add(route);
        }
    }

    private void addRotasExecutadas(Route route) {
        synchronized (lockRotas) {
            rotasExecutadas.add(route);
        }
    }

    private Route removeRotasExecutar() {
        synchronized (lockRotas) {
            return rotasExecutar.poll();
        }
    }

    private Route removeRotasExecutando(int index) {
        synchronized (lockRotas) {
            return rotasExecutando.remove(index);
        }
    }

    private boolean executarIsEmpty() {
        synchronized (lockRotas) {
            return rotasExecutar.isEmpty();
        }
    }

    public static int getCounter() {
        synchronized (lockCounter) {
            return counter;
        }
    }

}
