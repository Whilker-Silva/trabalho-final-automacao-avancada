package simulacao;

import org.python.modules.itertools.ifilter;

import pkg.company.Route;
import pkg.driver.Driver;

public class Principal {
    public static void main(String[] args) {

        /*
         * Company company = new Company("company", "company");
         * 
         * Driver motorista1 = new Driver("driver 1", "senha 1");
         * Driver motorista2 = new Driver("driver 2", "senha 2");
         * 
         * company.pagarMotorista(motorista1.getLogin(), 150);
         * company.pagarMotorista(motorista2.getLogin(), 300);
         */

        // EnvSimulator ev = new EnvSimulator();
        // ev.start();

        Driver driver1 = new Driver("driver1", "driver1");
        Driver driver2 = new Driver("driver2", "driver2");

        driver1.abastacer(driver2.getLogin(), 150);

    }
}
