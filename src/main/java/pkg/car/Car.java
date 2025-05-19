package pkg.car;

import utils.Cliente;

public class Car implements Runnable{

    private static int qtdCars = 0;

    private ClientCar clienteCar;
    private int idCar;

    public  Car(){

        qtdCars =+ 1;
        idCar = qtdCars;

        clienteCar = new ClientCar(4001, "Car " + idCar);
    }

    @Override
    public void run() {
       
        
    }


    private class ClientCar extends Cliente {

        public ClientCar(int port, String name) {
            super(port, name);
        }

        public void enviaMensagem(String msg) {
            super.enviaMensagem(msg);
        }
    }    
    
}
