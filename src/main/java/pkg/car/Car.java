package pkg.car;

import de.tudresden.sumo.cmd.Vehicle;
import pkg.company.Route;
import utils.Cliente;

public class Car extends Vehicle implements Runnable {

    private static int qtdCars = 0;

    private Cliente clienteCar;
    private String idCar;
    private String idDriver;
    private Route rota;

    public Car(String idDriver) {
        qtdCars = +1;

        this.idCar = "car" + qtdCars;
        this.idDriver = idDriver;

        clienteCar = new Cliente(4001, idCar);
        clienteCar.start();
    }

    public void setRoute(Route rota) {
        this.rota = rota;
    }

    @Override
    public void run() {
        try {
            System.out.printf("EXECUTANDO %s\n", this.rota.getIdRoute());
            Thread.sleep(3000);

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public String getIdCar() {
        return idCar;
    }

    public String getIdDriver() {
        return idDriver;
    }

}
