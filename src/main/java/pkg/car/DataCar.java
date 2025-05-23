package pkg.car;

public class DataCar {

    // private long timestamp;
    private String idCar;
    private String idRoute;
    private String idDriver;

    /**
     * 
     * @param idCar
     * @param idRoute
     * @param idDriver
     */
    public DataCar(String idCar, String idRoute, String idDriver) {
        // this.timestamp = 0;
        this.idCar = idCar;
        this.idRoute = idRoute;
        this.idDriver = idDriver;
    }

    public String getIdCar() {
        return idCar;
    }

    public String getIdRoute() {
        return idRoute;
    }

    public String getIdDriver() {
        return idDriver;
    }
}