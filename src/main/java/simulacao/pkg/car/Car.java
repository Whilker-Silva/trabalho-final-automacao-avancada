package simulacao.pkg.car;


import de.tudresden.sumo.cmd.Vehicle;
import simulacao.pkg.company.Route;
import utils.Cliente;
import utils.Json;

public class Car extends Vehicle implements Runnable {

    private static int qtdCars = 0;

    private Cliente clienteCar;
    private String idCar;
    private String idDriver;
    private Route rota;

    public Car(String idDriver) {
        qtdCars += 1;

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
            sendDataCar();
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            System.out.printf("%s finalizada\n", rota.getIdRoute());
            rota.finish();
            sendDataCar();
            rota = null;
        }
    }

    public void closeSocket() {
        clienteCar.closeSocket();
    }

    public String getIdCar() {
        return idCar;
    }

    public String getIdDriver() {
        return idDriver;
    }

    private void sendDataCar() {
        DataCar dataCar = new DataCar(idCar, rota.getIdRoute(), idDriver);
        String msg = Json.toJson(dataCar);
        clienteCar.enviaMensagem(msg);
    }

}
