package reconciliacao;

public enum PontosDeMedidas {

    P1(-22.96971415, -43.1903903),
    P2(-22.972158183631414, -43.19176070707961),
    P3(-22.9738636683789, -43.189132877746346),
    P4(-22.971693959967507, -43.18735347192985),
    P5(-22.972823383462572, -43.1859779414221),
    P6(-22.971885437717994, -43.18447098883925),
    P7(-22.971011259553094, -43.183464708759026),
    P8(-22.96906639, -43.18063);

    private final double latitude;
    private final double longitude;

    PontosDeMedidas(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}
