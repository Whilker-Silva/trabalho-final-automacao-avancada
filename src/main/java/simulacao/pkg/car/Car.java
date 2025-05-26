package simulacao.pkg.car;

import de.tudresden.sumo.cmd.Vehicle;
//mport de.tudresden.sumo.objects.SumoPosition2D;
import de.tudresden.sumo.objects.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;
import simulacao.EnvSimulator;
import simulacao.pkg.company.Route;
import de.tudresden.sumo.objects.SumoPosition2D;

public class Car extends Vehicle implements Runnable {

    private static int qtdCars = 0;
    private static final Object lock = new Object();

    private SumoTraciConnection sumo;
    private String idCar;
    private Route route;
    private DataCar carData;
    private boolean passoExutado;
    private boolean cadastrou;

    public Car(String idDriver) {

        synchronized (lock) {
            qtdCars += 1;
            idCar = "car" + qtdCars;
        }
        this.carData = new DataCar(idCar, idDriver);
        this.sumo = EnvSimulator.getSumo();

        Thread dataThread = new Thread(carData);
        dataThread.setName("data_" + idCar);
        dataThread.start();

        cadastrou = false;

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
            System.out.printf("%s finalizada\n", route.getIdRoute());
            cadastrou = false;
            route = null;
        }

    }

    private synchronized void executarPasso() {
        atualizaSensores();

        if (cadastrou && !route.acabou()) {
            EnvSimulator.passoExecutado();
            //System.out.println("passo " + idCar + " sensor atulizado");
        }

    }

    private synchronized void casdastrar() {
        try {

            SumoStringList carList = (SumoStringList) sumo.do_job_get(getIDList());
            while (carList.contains(idCar)) {
                carList = (SumoStringList) sumo.do_job_get(getIDList());
                Thread.sleep(10);
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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private synchronized void atualizaSensores() {
        try {

            SumoStringList carList = (SumoStringList) sumo.do_job_get(getIDList());            

            if (carList.contains(idCar)) {
                cadastrou = true;
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
                carData.setTimestamp();
            }

            else if (cadastrou) {
                route.finish();
                EnvSimulator.passoExecutado();  
                //System.out.println("passo " + idCar + " rota finalizada");              
            }

            else {
                EnvSimulator.passoExecutado();
                //System.out.println("passo " + idCar + " não cadastrado");
            }

            // System.out.print(a + " |");

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

    public boolean excutandoRota() {
        if (route != null) {
            return true;
        }
        return false;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public String getIdCar() {
        return idCar;
    }

}
