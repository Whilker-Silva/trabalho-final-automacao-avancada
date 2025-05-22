package pkg.driver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import pkg.banco.BotPayment;
import pkg.car.Car;
import pkg.company.Company;
import pkg.company.Route;

public class Driver extends Thread {

    // Controle de lock dos metodos que acessam saldo e extrato
    private final Object lock = new Object();

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

        car = new Car();
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
    }

    @Override
    public void run() {
        synchronized (lock) {
            solicitaRota();

            while (!rotasExecutar.isEmpty()) {

                try {

                    // Move rota a executar para rotas em execução
                    rotasExecutando.add(rotasExecutar.remove());

                    // configura a rota a ser executa pelo
                    car.setRoute(rotasExecutando.getFirst());

                    // Lança a Thred car para executar a rota e aguarda a mesma finalizar
                    Thread threadCar = new Thread(car);
                    threadCar.start();
                    threadCar.join();

                    // Move rota para rotas executadas
                    rotasExecutadas.add(rotasExecutando.removeFirst());

                    // Solicita uma nova rota
                    solicitaRota();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void solicitaRota() {
        Route rota = Company.getInstance().getRoute();
        if (rota != null) {
            rotasExecutar.add(rota);
        }
    }

    public void pagarAbastecimento(double litros) {
        double valor = litros * 5.87;
        botPayment.solicitarTransferencia("fuelStation", valor, senha);
    }

    public String getLogin() {
        return login;
    }
}
