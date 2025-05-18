package simulacao;

import simulacao.company.Company;
import simulacao.driver.Driver;

public class Main {
    public static void main(String[] args) {               

        Company company = new Company("company", "company");

        Driver motorista1 = new Driver("driver 1", "senha 1");
        Driver motorista2 = new Driver("driver 2", "senha 2");

        company.pagarMotorista(motorista1.getLogin(), 150);
        company.pagarMotorista(motorista2.getLogin(), 300);
    }

    public static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
