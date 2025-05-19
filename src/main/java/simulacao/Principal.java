package simulacao;

import pkg.company.Company;
import pkg.driver.Driver;
import pkg.sumo.*;

public class Principal {
    public static void main(String[] args) {               

        Company company = new Company("company", "company");

        Driver motorista1 = new Driver("driver 1", "senha 1");
        Driver motorista2 = new Driver("driver 2", "senha 2");

        company.pagarMotorista(motorista1.getLogin(), 150);
        company.pagarMotorista(motorista2.getLogin(), 300);

        EnvSimulator ev = new EnvSimulator();
        ev.start();


    }
   
}
