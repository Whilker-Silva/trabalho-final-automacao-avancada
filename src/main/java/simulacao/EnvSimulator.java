package simulacao;

import pkg.banco.AlphaBank;
import pkg.company.Company;
import pkg.driver.Driver;

public class EnvSimulator extends Thread {

	private static final int QTD_DRIVERS = 100;

	private Driver[] listaDrivers;

	public EnvSimulator() {
		setName("simulador");
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

			for (int i = 0; i < QTD_DRIVERS; i++) {
				listaDrivers[i].start();
			}

			for (int i = 0; i < QTD_DRIVERS; i++) {
				listaDrivers[i].join();
			}

			//System.out.println("ENCERRANDO COMPANY");
			Company.getInstance().shutdown();			
			AlphaBank.getInstancia().shutdown();
			// Encerra Alpha Bank

		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

}
