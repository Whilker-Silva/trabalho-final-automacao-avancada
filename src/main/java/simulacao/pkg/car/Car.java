package simulacao.pkg.car;

import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.objects.SumoPosition2D;
import de.tudresden.sumo.objects.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;
import simulacao.EnvSimulator;
import simulacao.pkg.company.Route;

public class Car extends Vehicle implements Runnable {

    private static int qtdCars = 0;

    private SumoTraciConnection sumo;
    private String idCar;
    private DataCar carData;
    private Route route;

    public Car(String idDriver) {
        // Define idCar
        qtdCars += 1;
        idCar = "car" + qtdCars;

        this.sumo = EnvSimulator.getSumo();
        carData = new DataCar(idCar, idDriver);
    }

    @Override
    public void run() {

        try {

            iniciaRota();
            while (!route.acabou()) {
                Thread.sleep(100);
                atualizaSensores();
            }

        }

        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            System.out.printf("%s finalizada\n", carData.getIdRoute());
        }

    }

    private void iniciaRota() {
        try {
            sumo.do_job_set(de.tudresden.sumo.cmd.Route.add(route.getIdRoute(), route.getEdges()));

            sumo.do_job_set(Vehicle.addFull(idCar, // vehID
                    carData.getIdRoute(), // routeID
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
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void atualizaSensores() {
        try {

            synchronized (sumo) {

                SumoStringList carList = (SumoStringList) sumo.do_job_get(getIDList());

                if (carList.contains(idCar)) {
                    carData.setTimestamp();
                    double speed = (double) sumo.do_job_get(getSpeed(idCar));
                    double distancia = (double) sumo.do_job_get(getDistance(idCar));
                    double consumo = (double) sumo.do_job_get(getFuelConsumption(idCar));
                    double co2 = (double) sumo.do_job_get(getCO2Emission(idCar));
                    SumoPosition2D posicao2D = (SumoPosition2D) sumo.do_job_get(getPosition(idCar));
                    carData.setSpeed(speed);
                    carData.setDistancia(distancia);
                    carData.setFuelConsumption(consumo);
                    carData.setCo2Emission(co2);
                    carData.setLongitude(posicao2D.x);
                    carData.setLatitude(posicao2D.x);
                }

                else {
                    route.finish();
                }
            }

        }

        catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(10000);
            } catch (Exception ex) {
                // TODO: handle exception
            }
        }

    }

    public void setRoute(Route route) {
        carData.setRoute(route);
        this.route = route;
    }

    public String getIdCar() {
        return idCar;
    }

}
