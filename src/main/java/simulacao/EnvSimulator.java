package simulacao;

import java.io.IOException;

import it.polito.appeal.traci.SumoTraciConnection;
import simulacao.pkgs.banco.AlphaBank;
import simulacao.pkgs.company.Company;
import simulacao.pkgs.driver.Driver;
import simulacao.pkgs.fuelStation.FuelStation;

public class EnvSimulator extends Thread {

	private int qtdDrivres;
	private static final int stepTime = 50;

	private static Object lock = new Object();
	private static SumoTraciConnection sumo;
	private Driver[] listaDrivers;
	private static int carReady;
	private static boolean executarPasso;
	private static boolean rotaUnica;

	public EnvSimulator(int qtdDrivres, int qtdRotas, boolean transito, boolean rotaUnica) {
		setName("simulador");

		this.qtdDrivres = qtdDrivres;
		EnvSimulator.rotaUnica = rotaUnica;

		String sumoBin = "sumo-gui";
		String configFile = transito? "map/map.sumo.cfg" :"map/map.sumoSemTransito.cfg";

		sumo = new SumoTraciConnection(sumoBin, configFile);
		sumo.addOption("start", "1"); // auto-run on GUI show
		sumo.addOption("quit-on-end", "1"); // auto-close on end
		sumo.addOption("step-length", "0.5");

		AlphaBank.getInstancia().start();
		Company.getInstance(qtdRotas).start();
		FuelStation.getInstance().start();
		

		listaDrivers = new Driver[qtdDrivres];
		for (int i = 0; i < qtdDrivres; i++) {
			String login = "driver" + (i + 1);
			String senha = "driver" + (i + 1);
			listaDrivers[i] = new Driver(login, senha);
			listaDrivers[i].setName(login);
		}
	}

	public void run() {
		try {

			sumo.runServer(12345);

			for (int i = 0; i < qtdDrivres; i++) {
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

	public static boolean getRotaUnica(){
		return rotaUnica;
	}

}
