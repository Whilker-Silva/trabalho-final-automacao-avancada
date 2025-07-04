package simulacao.pkgs.car;

import simulacao.pkgs.company.Company;
import simulacao.pkgs.company.Route;
import utils.Cliente;
import utils.Json;

public class DataCar implements Runnable {

    private String idCar;
    private String idDriver;
    private String combustivel;

    private int lastTimestamp;
    private int timestamp;

    private Route route;
    private double speed;
    private double lastDistancia;
    private double distancia;
    private double odometro;
    private double fuelConsumption;
    private double co2Emission;
    private double latitude;
    private double longitude;

    private boolean fimRota;

    private boolean solicitarPagamento;
    private transient Cliente clienteCar;

    /**
     * 
     * @param idCar
     * @param idDriver
     */
    public DataCar(String idCar, String idDriver) {

        this.idCar = idCar;
        this.idDriver = idDriver;
        this.combustivel = "Gasolina";

        clienteCar = new Cliente(4001, idCar);
        clienteCar.start();

        try {
            Thread.sleep(5);
        } catch (Exception e) {
            e.printStackTrace();
        }

        timestamp = 0;
        lastTimestamp = 0;
        lastDistancia = 0;
        fuelConsumption = 0;
        odometro = 0;

        solicitarPagamento = false;
        fimRota = false;

    }

    @Override
    public void run() {
        while (Company.getInstance().isAlive()) {
            try {

                while (timestamp == lastTimestamp && Company.getInstance().isAlive()) {
                    Thread.sleep(0, 100);
                }

                if (odometro - lastDistancia >= 1) {
                    solicitarPagamento = true;
                    lastDistancia = odometro;
                }

                else {
                    solicitarPagamento = false;
                }

                String msg = Json.toJson(this);
                clienteCar.enviaMensagem(msg);
                lastTimestamp = timestamp;

            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public String getIdCar() {
        return idCar;
    }

    public String getIdDriver() {
        return idDriver;
    }

    public String getCombustivel() {
        return combustivel;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public double getSpeed() {
        return speed;
    }

    public boolean getPagamento() {
        return solicitarPagamento;
    }

    public double getDistancia() {
        return distancia;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public double getCo2Emission() {
        return co2Emission;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getIdRoute() {
        return route.getIdRoute();
    }

    public void rotaAcabou() {
        fimRota = true;
        String msg = Json.toJson(this);
        clienteCar.enviaMensagem(msg);

        fimRota = false;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setDistancia(double distancia) {

        if (distancia == 0) {
            this.distancia = 0;
        }
        this.odometro += distancia - this.distancia;
        this.distancia = distancia;
    }

    public void setFuelConsumption(double fuelConsumption) {
        this.fuelConsumption += fuelConsumption;
    }

    public void setCo2Emission(double co2Emission) {
        this.co2Emission = co2Emission;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setTimestamp(int currentTime) {
        this.timestamp = currentTime;
    }

    public void closeSocket() {
        clienteCar.closeSocket();
    }

    public boolean rotaFinalizada() {
        return fimRota;
    }
}
