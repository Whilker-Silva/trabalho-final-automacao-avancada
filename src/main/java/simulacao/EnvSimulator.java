package simulacao;

import java.io.IOException;

import it.polito.appeal.traci.SumoTraciConnection;
import simulacao.pkg.banco.AlphaBank;
import simulacao.pkg.company.Company;
import simulacao.pkg.driver.Driver;

public class EnvSimulator extends Thread {

	private static final int QTD_DRIVERS = 10;

	private static SumoTraciConnection sumo;
	private String sumoBin;
	private String configFile;
	private Driver[] listaDrivers;


	public EnvSimulator() {
		setName("simulador");

		sumoBin = "sumo-gui";
		configFile = "map/map.sumo.cfg";

		sumo = new SumoTraciConnection(sumoBin, configFile);
		sumo.addOption("start", "1"); // auto-run on GUI show
		sumo.addOption("quit-on-end", "1"); // auto-close on end

		AlphaBank.getInstancia().start();
		Company.getInstance().start();

		listaDrivers = new Driver[QTD_DRIVERS];
		for (int i = 0; i < QTD_DRIVERS; i++) {
			String login = "driver" + (i + 1);
			String senha = "driver" + (i + 1);
			listaDrivers[i] = new Driver(login, senha);
			listaDrivers[i].setName(login);
		}
	}

	public void run() {		
		try {

			sumo.runServer(12345);

			for (int i = 0; i < QTD_DRIVERS; i++) {
				listaDrivers[i].start();
			}

			while (Driver.getCounter() > 0) {
				sumo.do_timestep();
				sleep(10);
			}

			System.out.println("\nRotas executadas com sucesso!\n");
			Company.getInstance().shutdown();
			AlphaBank.getInstancia().shutdown();

		}

		catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static SumoTraciConnection getSumo() {
		return sumo;
	}

}
