package pkg.sumo;

public class DrivingData {

	/* SUMO's data */

	private String autoID;
	private String driverID;
	private long timeStamp; 			// System.currentTimeMillis()
	private double x_Position; 			// sumoPosition2D (x)
	private double y_Position; 			// sumoPosition2D (y)
	private String roadIDSUMO; 			// this.sumo.do_job_get(Vehicle.getRoadID(this.idAuto))
	private String routeIDSUMO; 		// this.sumo.do_job_get(Vehicle.getRouteID(this.idAuto))
	private double speed; 				// in m/s for the last time step
	private double odometer; 			// the distance in (m) that was already driven by this vehicle.
	private double fuelConsumption; 	// in mg/s for the last time step
	private double fuelPrice; 			// price in liters
	private int fuelType; 				// 1-diesel, 2-gasoline, 3-ethanol, 4-hybrid
	private double averageFuelConsumption;
	private int personCapacity;			// the total number of persons that can ride in this vehicle
	private int personNumber;			// the total number of persons which are riding in this vehicle
	private double co2Emission; 		// in mg/s for the last time step
	private double HCEmission; 			// in mg/s for the last time step

	/* SEVEN GO Smart Edge's data */

	// Chassis data (VIN)
	//private String vinRegistrationDate; // Registration date (microseconds)
	//private String vin; // Vehicle Identification Number (VIN)

	// Fuel Data
	//private double fuelLevel; // Fuel tank level (%)
	//private double consumption; // Average fuel consumption since last recharge (km/l)
	//private double total; // Total fuel consumed since last recharge (l)
	//private String fuelRegistrationDate; // Registration date (microseconds)

	// Engine Data
	//private double rpm; // Engine revolutions per minute (RPM)
	//private double co2; // Emitted Co2 level (g/s)
	//private double load; // Engine mechanical load (%)
	//private double fuel; // Instant fuel savings (km/l)
	//private double coolant; // Engine coolant temperature (�C)
	//private double oil; // Engine oil temperature (�C)
	//private String engineRegistrationDate; // Registration date (microseconds)

	// Battery and Ignition Data
	//private double batteryLevel; // Approximate battery voltage (V)
	//private String ignition; // Vehicle ignition status (7 = vehicle off and 8 = vehicle on)
	//private String batteryRegistrationDate; // Registration date (microseconds)

	// Localization Data
	//private double latL; // Latitude
	//private double lonL; // Longitude
	//private double heightL; // Vehicle height from sea level (m)
	//private double speedL; // Speed (km/h)
	//private double distL; // Distance from the last record sent (m)
	//private double accL; // Vehicle acceleration (m/s^2)
	//private String localizationRegistrationDate;// Registration date (microseconds)

	// Vehicle tracking data
	//private double latR; // Latitude
	//private double lonR; // Longitude
	//private double heightR; // Vehicle height from sea level (m)
	//private double speedR; // Speed (km/h)
	//private double distR; // Distance from the last record sent (m)
	//private double accR; // Vehicle acceleration (m/s^2)
	//private double headVeh; // Vehicle orientation in the 2D plane (deg)
	//private double orientation_valid; // 3D orientation is valid (0 = not valid and 1 = valid)
	//private double roll; // Angle of rotation around x (deg)
	//private double pitch; // Angle of rotation around y (deg)
	//private double heading; // Angle of rotation around z (deg)
	//private String trackingRegistrationDate; // Registration date (microseconds)

	// Vehicle Diagnostic Data
	//private String mil; // Warning light status (0 = off, 1 = on)
	//private String check; // Standard tests completed successfully (0 = failure, 1 = success)
	//private double n_dtc; // Number of active DTCs
	//private ArrayList<String> dtc_value; // List of active DTCs in total indicated by n_dtc
	//private String diagnosticRegistrationDate; // Registration date (microseconds)

	// Vehicle Event Data
	//private String eventCode; // Event code
	//private String eventValue; // Event value
	/*
	 * Event table: 0 High braking 1 High acceleration 2 Vehicle stopped 3 Vehicle
	 * in motion 4 Low Fuel 5 Low Battery 6 Refilled Fuel 7 Vehicle Off 8 Vehicle On
	 * 9 Changed Chassis 10 Frequent Acceleration 11 Frequent Brake 12 Frequent Stop
	 * 13 Driver Fatigue 14 Winding curve (Cornering) 15 Zig-zag (Swerving) 16
	 * Vehicle on hold
	 */
	//private double latE; // Latitude at which the event occurred
	//private double lonE; // Length at which the event occurred
	//private String eventRegistrationDate; // Registration date (microseconds)

	// Route Data
	//private String a; // Registration date (microseconds)
	//private double b; // Total distance traveled in the trip (since the last vehicle on event) (m)
	//private double c; // Total trip time (microseconds)
	//private double d; // Distance traveled in economy mode (m)
	//private double e; // Distance traveled in extra economy mode (m)
	//private double f; // Distance traveled in non-economy mode (m)
	//private double g; // Distance traveled at idle (m)
	//private double h; // Distance traveled in power zone (m)
	//private double i; // Distance traveled in engine danger zone (m)
	//private double j; // Distance traveled at constant speed (m)
	//private double k; // Distance traveled above the defined limit speed (m)
	//private double l; // Distance traveled at defined limit speed (m)
	//private double m; // Distance traveled in acceleration/deceleration within the defined limit speed
						// range (m)
	//private double n; // Number of times the engine brake was used
	//private double o; // Number of times the service brake was used
	//private double p; // Number of high acceleration events
	//private double q; // Number of high deceleration events
	//private double r; // Number of prolonged stop events with running engine (greater than 1 minute)
	//private double s; // Total time in prolonged stop with engine running (microseconds)
	//private double t; // Number of gear changes outside the recommended range
	//private double u; // Number of frequent acceleration events
	//private double v; // Number of frequent brake events
	//private double w; // Total fuel consumed in economy mode (L)
	//private double x; // Total fuel consumed in extra economy mode (L)
	//private double y; // Total fuel consumed in non-economy mode (L)
	//private double z; // Total fuel consumed at idle (L)
	//private double a1; // Total fuel consumed in power zone (L)
	//private double b1; // Total fuel consumed in engine danger zone (L)
	//private double c1; // Total fuel consumed at constant speed (L)
	//private double d1; // Total fuel consumed above speed limit (L)
	//private double e1; // Total fuel consumed at set limit speed (L)
	//private double f1; // Total fuel consumed in acceleration/deceleration within the defined limit
						// speed range (L)
	//private double g1; // Driver score for vehicle speed(%)
	//private double h1; // Driver score for vehicle inertia (%)
	//private double i1; // Driver's score for the use of brakes (%)
	//private double j1; // Driver score for vehicle RPM (%)
	//private double k1; // Driver Score for Fuel Usage (%)

	public DrivingData(

			String _autoID, String _driverID, long _timeStamp, double _x_Position, double _y_Position,
			String _roadIDSUMO, String _routeIDSUMO, double _speed, double _odometer, double _fuelConsumption,
			double _averageFuelConsumption, int _fuelType, double _fuelPrice, double _co2Emission, double _HCEmission, int _personCapacity, int _personNumber) {

		this.autoID = _autoID;
		this.driverID = _autoID;
		this.timeStamp = _timeStamp;
		this.x_Position = _x_Position;
		this.y_Position = _y_Position;
		this.roadIDSUMO = _roadIDSUMO;
		this.routeIDSUMO = _routeIDSUMO;
		this.speed = _speed;
		this.odometer = _odometer;
		this.fuelConsumption = _fuelConsumption;
		this.averageFuelConsumption = _averageFuelConsumption;
		this.fuelType = _fuelType;
		this.fuelPrice = _fuelPrice;
		this.co2Emission = _co2Emission;
		this.HCEmission = _HCEmission;
		this.personCapacity = _personCapacity;
		this.personNumber = _personNumber;

	}

	public double getSpeed() {
		return this.speed;
	}

	public double getOdometer() {
		return this.odometer;
	}

	public double getFuelConsumption() {
		return this.fuelConsumption;
	}

	public double getCo2Emission() {
		return this.co2Emission;
	}

	public double getX_Position() {
		return this.x_Position;
	}

	public double getY_Position() {
		return this.y_Position;
	}

	public long getTimeStamp() {
		return this.timeStamp;
	}

	public String getRoadIDSUMO() {
		return this.roadIDSUMO;
	}

	public String getRouteIDSUMO() {
		return this.routeIDSUMO;
	}

	public String getAutoID() {
		return this.autoID;
	}

	public String getDriverID() {
		return this.driverID;
	}

	public double getHCEmission() {
		return this.HCEmission;
	}

	public double getFuelPrice() {
		return this.fuelPrice;
	}

	public int getFuelType() {
		return this.fuelType;
	}

	public int getPersonCapacity() {
		return this.personCapacity;
	}

	public int getPersonNumber() {
		return this.personNumber;
	}

	public double getAverageFuelConsumption() {
		return this.averageFuelConsumption;
	}

	public void setAverageFuelConsumption(double _averageFuelConsumption) {
		this.averageFuelConsumption = _averageFuelConsumption;
	}
}