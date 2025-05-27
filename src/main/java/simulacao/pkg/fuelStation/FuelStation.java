package simulacao.pkg.fuelStation;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import simulacao.pkg.car.Car;

public class FuelStation extends Thread {

    private static FuelStation instance;
    private static Queue<Car> filaAbastecimento;
    private static final Semaphore bombas = new Semaphore(2);
    private static Object lockFila = new Object();

    private FuelStation() {
        filaAbastecimento = new LinkedList<>();
    }

    public static FuelStation getInstance() {
        if (instance == null) {
            instance = new FuelStation();
        }
        return instance;
    }

    @Override
    public void run() {
        while (true) {
            try {

                synchronized (lockFila) {
                    while (filaAbastecimento.isEmpty()) {
                        lockFila.wait();
                    }
                }

                bombas.acquire();
                Car car = removeFila();
                System.out.println(car.getIdCar() + " Liberado pra abstecer");

                new Thread(() -> {
                    try {

                        Thread.sleep(12000); // simula o tempo de abastecimento de 2min (240 steps)
                        car.abatecer(7);
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

    public static void abastecer(Car car, double litros) {
        addFila(car);
    }

}
