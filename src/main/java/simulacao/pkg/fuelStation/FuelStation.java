package simulacao.pkg.fuelStation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import simulacao.pkg.banco.BotPayment;
import simulacao.pkg.car.Car;

public class FuelStation extends Thread {

    private String login;
    private String senha;
    private BotPayment botPayment;

    private static FuelStation instance;
    private static Queue<Car> filaAbastecimento;
    private static HashMap<String, Double> listaCarros;
    private static final Semaphore bombas = new Semaphore(2);
    private static Object lockFila = new Object();
    private static Object lockHash = new Object();
    private boolean viva;

    private FuelStation() {

        

        login = "fuel-station";
        senha = "fuel-station";

        setName(login);

        filaAbastecimento = new LinkedList<>();
        listaCarros = new HashMap<>();

        try {
            botPayment = new BotPayment(login, senha, 0);
            botPayment.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        viva = true;
    }

    public static FuelStation getInstance() {
        if (instance == null) {
            instance = new FuelStation();
        }
        return instance;
    }

    @Override
    public void run() {
        while (viva) {
            try {

                synchronized (lockFila) {
                    while (filaAbastecimento.isEmpty()) {
                        lockFila.wait();
                    }
                }

                bombas.acquire();
                Car car = removeFila();

                new Thread(() -> {
                    try {
                        double litros = removeHash(car.getIdCar());
                        System.out.printf("Abastecendo %.2f litros no %s\n", litros, car.getIdCar());

                        Thread.sleep(12000); // simula o tempo de abastecimento de 2min (240 steps)

                        car.abatecer(litros);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        bombas.release(); // libera a bomba
                    }
                }).start();

            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void addFila(Car car) {
        synchronized (lockFila) {
            filaAbastecimento.add(car);
            lockFila.notifyAll();
        }
    }

    private Car removeFila() {
        synchronized (lockFila) {
            Car car = filaAbastecimento.remove();
            lockFila.notifyAll();
            return car;
        }
    }

    private static void addHash(String idCar, double litros) {
        synchronized (lockHash) {
            listaCarros.put(idCar, litros);
        }
    }

    private double removeHash(String idCar) {
        synchronized (lockHash) {
            return listaCarros.remove(idCar);
        }
    }

    public static void abastecer(Car car, double litros) {
        addFila(car);
        addHash(car.getIdCar(), litros);
    }

    public void shutdown(){
        botPayment.closeSocket();
    }

}
