package pkg.car;

public class DataCar {

    //private long timestamp;
    private String idCar;
    private String idRoute;
    private String idDriver;
    private double speed;
    private double distance;
    private double fuelConsumption;
    private String fuelType;
    private double CO2Emssion;
    private double longitude;
    private double latitude;

    public DataCar(String idCar,
            String idRoute,
            String idDriver,
            double speed,
            double distance,
            double fuelConsumption,
            String fuelType,
            double CO2Emssion,
            double longitude,
            double latitude)

    {
        //this.timestamp = 0;
        this.idCar = idCar;
        this.idRoute = idRoute;
        this.idDriver = idDriver;
        this.speed = speed;
        this.distance = distance;
        this.fuelConsumption = fuelConsumption;
        this.fuelType = fuelType;
        this.CO2Emssion = CO2Emssion;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public long getTimestamp() {
        return 0;
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

    public double getSpeed() {
        return speed;
    }

    public double getDistance() {
        return distance;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public String getFuelType() {
        return fuelType;
    }

    public double getCO2Emssion() {
        return CO2Emssion;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

}
