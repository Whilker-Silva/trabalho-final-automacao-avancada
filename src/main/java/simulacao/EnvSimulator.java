package simulacao;

import java.io.IOException;

import it.polito.appeal.traci.SumoTraciConnection;
import simulacao.pkg.banco.AlphaBank;
import simulacao.pkg.company.Company;
import simulacao.pkg.driver.Driver;
import simulacao.pkg.fuelStation.FuelStation;

public class EnvSimulator extends Thread {

	private static final int QTD_DRIVERS = 100;
	private static final int stepTime = 50;

	private static Object lock = new Object();
	private static SumoTraciConnection sumo;
	private Driver[] listaDrivers;
	private static int carReady;
	private static boolean executarPasso;

	public EnvSimulator() {
		setName("simulador");

		String sumoBin = "sumo-gui";
		String configFile = "map/map.sumo.cfg";

		sumo = new SumoTraciConnection(sumoBin, configFile);
		sumo.addOption("start", "1"); // auto-run on GUI show
		sumo.addOption("quit-on-end", "1"); // auto-close on end
		sumo.addOption("step-length", "0.5");

		AlphaBank.getInstancia().start();
		Company.getInstance().start();
		FuelStation.getInstance().start();
		

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
				synchronized (lock) {
					sumo.do_timestep();
					sleep(stepTime);
					executarPasso = true;

					carReady = 0;
					int respostas = Driver.getCounter();

					long start = System.currentTimeMillis();
					long timeout = 100;
					while (carReady < respostas) {
						lock.wait();
						if (System.currentTimeMillis() - start > timeout) {
							carReady = respostas;
						}
					}

					// sleep(100);

					// System.out.println("");
					// System.out.println("");

					executarPasso = false;
				}

			}

			System.out.println("\nRotas executadas com sucesso!\n");

			Company.getInstance().shutdown();
			AlphaBank.getInstancia().shutdown();
			FuelStation.getInstance().shutdown();
			sumo.close();
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

	public static int getSteptime() {
		return stepTime;
	}

	public static void passoExecutado() {
		synchronized (lock) {
			carReady++;
			lock.notifyAll();
		}
	}

	public static boolean getExecutarPasso() {
		return executarPasso;
	}

}
