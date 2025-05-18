package simulacao;

import simulacao.Driver.Driver;

public class Main {
    public static void main(String[] args) {               

        Driver motorista1 = new Driver("driver 1", "senha 1");
        Driver motorista2 = new Driver("driver 2", "senha 2");

        motorista1.abastacer("driver 2", 100);        
        motorista2.abastacer("driver 1", 1000);        
        motorista2.abastacer("driver 1", 200);

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
