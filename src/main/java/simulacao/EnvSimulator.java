package simulacao;

import java.io.IOException;

import de.tudresden.sumo.objects.SumoColor;
import it.polito.appeal.traci.SumoTraciConnection;
import pkg.sumo.Auto;
import pkg.sumo.Itinerary;
import pkg.sumo.TransportService;

public class EnvSimulator extends Thread{

    private SumoTraciConnection sumo;

    public EnvSimulator(){ 

    }

    public void run(){

		/* SUMO */
		String sumo_bin = "sumo-gui";		
		String config_file = "map/map.sumo.cfg";
		
		// Sumo connection
		this.sumo = new SumoTraciConnection(sumo_bin, config_file);
		sumo.addOption("start", "1"); // auto-run on GUI show
		sumo.addOption("quit-on-end", "1"); // auto-close on end

		try {
			sumo.runServer(12345);

			Itinerary i1 = new Itinerary("data/dados2.xml", "0");
			Itinerary i2 = new Itinerary("data/dados2.xml", "1");

			if (i1.isOn()) {

				// fuelType: 1-diesel, 2-gasoline, 3-ethanol, 4-hybrid
				int fuelType = 2;
				int fuelPreferential = 2;
				double fuelPrice = 3.40;
				int personCapacity = 1;
				int personNumber = 1;

				SumoColor green = new SumoColor(0, 255, 0, 126);
				Auto a1 = new Auto(true, "CAR1", green,"D1", sumo, 500, fuelType, fuelPreferential, fuelPrice, personCapacity, personNumber);
				SumoColor red = new SumoColor(255, 0, 0, 126);
				Auto a2 = new Auto(true, "CAR2", red,"D2", sumo, 500, fuelType, fuelPreferential, fuelPrice, personCapacity, personNumber);

				TransportService tS1 = new TransportService(true, "CAR1", i1, a1, sumo);
				TransportService tS2 = new TransportService(true, "CAR2", i2, a2, sumo);
				
				Thread.sleep(5000);
				tS1.start();
				tS2.start();
			}

		
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

    }

}
