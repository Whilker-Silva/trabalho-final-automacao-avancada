package simulacao.pkg.driver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import simulacao.pkg.banco.BotPayment;
import simulacao.pkg.car.Car;
import simulacao.pkg.car.DataCar;
import simulacao.pkg.company.Company;
import simulacao.pkg.company.Route;
import simulacao.pkg.fuelStation.FuelStation;
import utils.Crypto;
import utils.Json;

public class Driver extends Thread {

    private static int counter = 0;
    private static final Object lockCounter = new Object();
    private final Object lockRotas = new Object();

    private String login;
    private String senha;
    private BotPayment botPayment;
    private Car car;
    private DataCar carData;
    private Thread dataThread;

    private final Queue<Route> rotasExecutar;
    private final ArrayList<Route> rotasExecutando;
    private final ArrayList<Route> rotasExecutadas;

    private final Object lockAbastece = new Object();

    public Driver(String login, String senha) {

        setName(login);
        this.login = login;
        this.senha = senha;

        car = new Car(login, lockAbastece);
        Company.getInstance().addCar(car);

        this.carData = new DataCar(car.getIdCar(), login);
        dataThread = new Thread(carData);
        dataThread.setName("data_" + car.getIdCar());
        dataThread.start();

        car.setCarData(carData);

        try {
            botPayment = new BotPayment(login, senha, 10);
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
        try {
            // Lança a Thred car para executar a rota e aguarda a mesma finalizar
            solicitaRota();

            while (!executarIsEmpty()) {

                // Move rota a executar para rotas em execução
                Route rotaExecutando = removeRotasExecutar();
                addRotasExecutando(rotaExecutando);

                // configura a rota a ser executa pelo
                car.setRoute(rotaExecutando);

                Thread threadCar = new Thread(car);
                threadCar.setName(car.getIdCar());
                threadCar.start();

                while (threadCar.isAlive()) {

                    synchronized (lockAbastece) {
                        while (!car.getAbastecendo() && car.excutandoRota()) {
                            lockAbastece.wait();
                        }
                    }

                    if (car.excutandoRota()) {
                        abastecer();

                        synchronized (lockAbastece) {
                            while (car.getAbastecendo()) {
                                lockAbastece.wait();
                            }
                        }
                    }

                }

                // Move rota para rotas executadas
                Route finalizada = removeRotasExecutando(0);
                addRotasExecutadas(finalizada);

                // Solicita uma nova rota
                solicitaRota();
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        synchronized (lockCounter) {
            counter--;
        }

        // System.out.printf("%s encerrado\n", login);
        botPayment.closeSocket();
        carData.closeSocket();
    }

    private void solicitaRota() {
        String rotaCriptografada = Company.getInstance().getRoute();
        Route rota;
        try {
            rota = Json.fromJson(Crypto.descriptografar(rotaCriptografada), Route.class);
            if (rota != null) {
                addRotasExecutar(rota);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void abastecer() {

        double litros;
        double valor;

        double capacidade = (10 - car.getFuelTank());
        if ((capacidade * 5.87) < botPayment.getSaldo()) {
            litros = capacidade;
            valor = litros * 5.87;
        } else {
            valor = botPayment.getSaldo();
            litros = valor / 5.87;
        }

        FuelStation.abastecer(car, litros);
        botPayment.solicitarTransferencia("fuel-station", valor, senha);        
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
