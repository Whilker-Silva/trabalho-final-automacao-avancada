package simulacao.pkgs.car;

import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.objects.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;
import simulacao.EnvSimulator;
import simulacao.pkgs.company.Route;
import de.tudresden.sumo.objects.SumoColor;
import de.tudresden.sumo.objects.SumoPosition2D;

public class Car extends Vehicle implements Runnable {

    private static int qtdCars = 0;
    private static final Object lock = new Object();

    private SumoTraciConnection sumo;
    private String idCar;
    private Route route;
    private boolean passoExutado;
    private boolean cadastrou;
    private DataCar carData;
    private double fuelTank;
    private boolean abastecendo;

    private final Object lockAbastece;

    public Car(String idDriver, Object lockAbastece) {

        this.lockAbastece = lockAbastece;

        synchronized (lock) {
            qtdCars += 1;
            idCar = "car" + qtdCars;
        }
        this.sumo = EnvSimulator.getSumo();

        fuelTank = 10;

        cadastrou = false;
        abastecendo = false;

    }

    @Override
    public void run() {
        try {

            casdastrar();

            passoExutado = false;
            while (!route.acabou()) {

                if (EnvSimulator.getExecutarPasso()) {
                    if (!passoExutado) {
                        executarPasso();
                        EnvSimulator.passoExecutado();
                        passoExutado = true;
                    }
                }

                else {
                    passoExutado = false;
                }

                Thread.sleep(0, 1);

            }

        }

        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            synchronized (lockAbastece) {
                System.out.printf("%s finalizada\n", route.getIdRoute());
                carData.rotaAcabou();
                cadastrou = false;
                route = null;
                lockAbastece.notifyAll();
            }
        }

    }

    private synchronized void executarPasso() {

        SumoStringList carList;
        try {
            carList = (SumoStringList) sumo.do_job_get(getIDList());

            if (carList.contains(idCar)) {
                atualizaSensores();

                if (this.fuelTank < 3) {
                    if (!abastecendo) {
                        sumo.do_job_set(setSpeed(idCar, 0));
                        abastecendo = true;

                        synchronized (lockAbastece) {
                            lockAbastece.notifyAll();
                        }
                    }
                } else if (carData.getSpeed() == 0) {

                    synchronized (lockAbastece) {
                        this.abastecendo = false;
                        sumo.do_job_set(setSpeed(idCar, -1));
                        lockAbastece.notifyAll();
                    }
                }
            }

            else if (cadastrou) {
                route.finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void casdastrar() {
        try {

            SumoStringList carList = (SumoStringList) sumo.do_job_get(getIDList());
            while (carList.contains(idCar)) {
                carList = (SumoStringList) sumo.do_job_get(getIDList());
                Thread.sleep(2);
            }

            sumo.do_job_set(de.tudresden.sumo.cmd.Route.add(route.getIdRoute(), route.getEdges()));

            sumo.do_job_set(Vehicle.addFull(idCar, // vehID
                    route.getIdRoute(), // routeID
                    "DEFAULT_VEHTYPE", // typeID
                    "now", // depart
                    "0", // departLane
                    "0", // departPos
                    "0", // departSpeed
                    "current", // arrivalLane
                    "max", // arrivalPos
                    "current", // arrivalSpeed
                    "", // fromTaz
                    "", // toTaz
                    "", // line
                    1, // personCapacity
                    1) // personNumber
            );

            SumoColor red = new SumoColor(38, 235, 12, 255); // RGBA: vermelho
            sumo.do_job_set(Vehicle.setColor(idCar, red));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private synchronized void atualizaSensores() {
        try {

            cadastrou = true;
            double speed = (double) sumo.do_job_get(getSpeed(idCar));
            double distancia = (double) sumo.do_job_get(getDistance(idCar));
            double consumo = (double) sumo.do_job_get(getFuelConsumption(idCar));
            double co2 = (double) sumo.do_job_get(getCO2Emission(idCar));
            SumoPosition2D posicao2D = (SumoPosition2D) sumo.do_job_get(getPosition(idCar));
            carData.setSpeed(speed);
            carData.setDistancia(distancia / 1000); // converte para quilometros
            carData.setFuelConsumption(consumo / 150000); // Denisida da gosolina - aproxamente 750g/L
            carData.setCo2Emission(co2);
            carData.setLongitude(posicao2D.x);
            carData.setLatitude(posicao2D.x);
            carData.setTimestamp();
        }

        catch (

        Exception e) {
            e.printStackTrace();
        }

    }

    public boolean excutandoRota() {
        if (route != null) {
            return true;
        }
        return false;
    }

    public void setRoute(Route route) {
        this.route = route;
        carData.setRoute(route);
    }

    public String getIdCar() {
        return idCar;
    }

    public void setCarData(DataCar carData) {
        this.carData = carData;
    }

    public boolean getAbastecendo() {
        return abastecendo;
    }

    public void abatecer(double litros) {
        fuelTank += litros;
        System.out.println("Fim abstecimento " + idCar);
    }

    public double getFuelTank() {
        return fuelTank;
    }

}
