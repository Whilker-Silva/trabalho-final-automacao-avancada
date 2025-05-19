package pkg.sumo;

import java.util.ArrayList;

import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.objects.SumoColor;
import de.tudresden.sumo.objects.SumoPosition2D;
import it.polito.appeal.traci.SumoTraciConnection;

public class Auto extends Thread {

	private String idAuto;
	private SumoColor colorAuto;
	private String driverID;
	private SumoTraciConnection sumo;

	private boolean on_off;
	private long acquisitionRate;
	private int fuelType; 			// 1-diesel, 2-gasoline, 3-ethanol, 4-hybrid
	private int fuelPreferential; 	// 1-diesel, 2-gasoline, 3-ethanol, 4-hybrid
	private double fuelPrice; 		// price in liters
	private int personCapacity;		// the total number of persons that can ride in this vehicle
	private int personNumber;		// the total number of persons which are riding in this vehicle

	private ArrayList<DrivingData> drivingRepport;
	
	public Auto(boolean _on_off, String _idAuto, SumoColor _colorAuto, String _driverID, SumoTraciConnection _sumo, long _acquisitionRate,
			int _fuelType, int _fuelPreferential, double _fuelPrice, int _personCapacity, int _personNumber) throws Exception {

		this.on_off = _on_off;
		this.idAuto = _idAuto;
		this.colorAuto = _colorAuto;
		this.driverID = _driverID;
		this.sumo = _sumo;
		this.acquisitionRate = _acquisitionRate;
		
		if((_fuelType < 0) || (_fuelType > 4)) {
			this.fuelType = 4;
		} else {
			this.fuelType = _fuelType;
		}
		
		if((_fuelPreferential < 0) || (_fuelPreferential > 4)) {
			this.fuelPreferential = 4;
		} else {
			this.fuelPreferential = _fuelPreferential;
		}

		this.fuelPrice = _fuelPrice;
		this.personCapacity = _personCapacity;
		this.personNumber = _personNumber;
		this.drivingRepport = new ArrayList<DrivingData>();
	}

	@Override
	public void run() {
		while (this.on_off) {
			try {
				Auto.sleep(this.acquisitionRate);
				this.atualizaSensores();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void atualizaSensores() {

		try {

			if (!this.getSumo().isClosed()) {
				SumoPosition2D sumoPosition2D;
				sumoPosition2D = (SumoPosition2D) sumo.do_job_get(Vehicle.getPosition(this.idAuto));

				System.out.println("AutoID: " + this.getIdAuto());
				System.out.println("RoadID: " + (String) this.sumo.do_job_get(Vehicle.getRoadID(this.idAuto)));
				System.out.println("RouteID: " + (String) this.sumo.do_job_get(Vehicle.getRouteID(this.idAuto)));
				System.out.println("RouteIndex: " + this.sumo.do_job_get(Vehicle.getRouteIndex(this.idAuto)));
				
				DrivingData _repport = new DrivingData(

						this.idAuto, this.driverID, System.currentTimeMillis(), sumoPosition2D.x, sumoPosition2D.y,
						(String) this.sumo.do_job_get(Vehicle.getRoadID(this.idAuto)),
						(String) this.sumo.do_job_get(Vehicle.getRouteID(this.idAuto)),
						(double) sumo.do_job_get(Vehicle.getSpeed(this.idAuto)),
						(double) sumo.do_job_get(Vehicle.getDistance(this.idAuto)),

						(double) sumo.do_job_get(Vehicle.getFuelConsumption(this.idAuto)),
						// Vehicle's fuel consumption in ml/s during this time step,
						// to get the value for one step multiply with the step length; error value:
						// -2^30
						
						1/*averageFuelConsumption (calcular)*/,

						this.fuelType, this.fuelPrice,

						(double) sumo.do_job_get(Vehicle.getCO2Emission(this.idAuto)),
						// Vehicle's CO2 emissions in mg/s during this time step,
						// to get the value for one step multiply with the step length; error value:
						// -2^30

						(double) sumo.do_job_get(Vehicle.getHCEmission(this.idAuto)),
						// Vehicle's HC emissions in mg/s during this time step,
						// to get the value for one step multiply with the step length; error value:
						// -2^30
						
						this.personCapacity,
						// the total number of persons that can ride in this vehicle
						
						this.personNumber
						// the total number of persons which are riding in this vehicle

				);

				// Criar relatorio auditoria / alertas
				// velocidadePermitida = (double)
				// sumo.do_job_get(Vehicle.getAllowedSpeed(this.idSumoVehicle));

				this.drivingRepport.add(_repport);

				//System.out.println("Data: " + this.drivingRepport.size());
				System.out.println("idAuto = " + this.drivingRepport.get(this.drivingRepport.size() - 1).getAutoID());
				//System.out.println(
				//		"timestamp = " + this.drivingRepport.get(this.drivingRepport.size() - 1).getTimeStamp());
				//System.out.println("X=" + this.drivingRepport.get(this.drivingRepport.size() - 1).getX_Position() + ", "
				//		+ "Y=" + this.drivingRepport.get(this.drivingRepport.size() - 1).getY_Position());
				System.out.println("speed = " + this.drivingRepport.get(this.drivingRepport.size() - 1).getSpeed());
				System.out.println("odometer = " + this.drivingRepport.get(this.drivingRepport.size() - 1).getOdometer());
				System.out.println("Fuel Consumption = "
						+ this.drivingRepport.get(this.drivingRepport.size() - 1).getFuelConsumption());
				//System.out.println("Fuel Type = " + this.fuelType);
				//System.out.println("Fuel Price = " + this.fuelPrice);

				System.out.println(
						"CO2 Emission = " + this.drivingRepport.get(this.drivingRepport.size() - 1).getCo2Emission());

				//System.out.println();
				//System.out.println("************************");
				//System.out.println("testes: ");
				//System.out.println("getAngle = " + (double) sumo.do_job_get(Vehicle.getAngle(this.idAuto)));
				//System.out
				//		.println("getAllowedSpeed = " + (double) sumo.do_job_get(Vehicle.getAllowedSpeed(this.idAuto)));
				//System.out.println("getSpeed = " + (double) sumo.do_job_get(Vehicle.getSpeed(this.idAuto)));
				//System.out.println(
				//		"getSpeedDeviation = " + (double) sumo.do_job_get(Vehicle.getSpeedDeviation(this.idAuto)));
				//System.out.println("getMaxSpeedLat = " + (double) sumo.do_job_get(Vehicle.getMaxSpeedLat(this.idAuto)));
				//System.out.println("getSlope = " + (double) sumo.do_job_get(Vehicle.getSlope(this.idAuto))
				//		+ " the slope at the current position of the vehicle in degrees");
				//System.out.println(
				//		"getSpeedWithoutTraCI = " + (double) sumo.do_job_get(Vehicle.getSpeedWithoutTraCI(this.idAuto))
				//				+ " Returns the speed that the vehicle would drive if no speed-influencing\r\n"
				//				+ "command such as setSpeed or slowDown was given.");

				//sumo.do_job_set(Vehicle.setSpeed(this.idAuto, (1000 / 3.6)));
				//double auxspeed = (double) sumo.do_job_get(Vehicle.getSpeed(this.idAuto));
				//System.out.println("new speed = " + (auxspeed * 3.6));
				//System.out.println(
				//		"getSpeedDeviation = " + (double) sumo.do_job_get(Vehicle.getSpeedDeviation(this.idAuto)));
				
				
				sumo.do_job_set(Vehicle.setSpeedMode(this.idAuto, 0));
				sumo.do_job_set(Vehicle.setSpeed(this.idAuto, 10));
								
				System.out.println("getPersonNumber = " + sumo.do_job_get(Vehicle.getPersonNumber(this.idAuto)));
				//System.out.println("getPersonIDList = " + sumo.do_job_get(Vehicle.getPersonIDList(this.idAuto)));
				
				System.out.println("************************");

			} else {
				System.out.println("SUMO is closed...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isOn_off() {
		return this.on_off;
	}

	public void setOn_off(boolean _on_off) {
		this.on_off = _on_off;
	}

	public long getAcquisitionRate() {
		return this.acquisitionRate;
	}

	public void setAcquisitionRate(long _acquisitionRate) {
		this.acquisitionRate = _acquisitionRate;
	}

	public String getIdAuto() {
		return this.idAuto;
	}

	public SumoTraciConnection getSumo() {
		return this.sumo;
	}

	public int getFuelType() {
		return this.fuelType;
	}

	public void setFuelType(int _fuelType) {
		if((_fuelType < 0) || (_fuelType > 4)) {
			this.fuelType = 4;
		} else {
			this.fuelType = _fuelType;
		}
	}

	public double getFuelPrice() {
		return this.fuelPrice;
	}

	public void setFuelPrice(double _fuelPrice) {
		this.fuelPrice = _fuelPrice;
	}

	public SumoColor getColorAuto() {
		return this.colorAuto;
	}

	public int getFuelPreferential() {
		return this.fuelPreferential;
	}

	public void setFuelPreferential(int _fuelPreferential) {
		if((_fuelPreferential < 0) || (_fuelPreferential > 4)) {
			this.fuelPreferential = 4;
		} else {
			this.fuelPreferential = _fuelPreferential;
		}
	}

	public int getPersonCapacity() {
		return this.personCapacity;
	}

	public int getPersonNumber() {
		return this.personNumber;
	}
}