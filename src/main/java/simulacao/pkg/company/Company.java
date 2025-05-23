package simulacao.pkg.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import simulacao.pkg.banco.BotPayment;
import simulacao.pkg.car.Car;
import simulacao.pkg.car.DataCar;
import utils.Crypto;
import utils.Json;
import utils.Server;

/**
 * Classe Company que atua como Cliente do AlphaBank e Server para Car
 * Implementa funcionalidades de comunicação com o banco e com os carros
 */
public class Company extends Thread {

    private static Company instancia;

    private final String login;
    private final String senha;

    private final ServerCompany serverCompany;
    private BotPayment botPayment;

    private final HashMap<String, Car> carros;
    private final Queue<DataCar> filaDedados;

    private final Queue<Route> rotasExecutar;
    private final ArrayList<Route> rotasExecutando;
    private final ArrayList<Route> rotasExecutadas;

    private final Object lockData = new Object();
    private final Object lockRotas = new Object();
    private final Object lockcar = new Object();

    /**
     * Método que criar um instancia única para a classe {@code Company}
     * 
     * @param login - String
     * @param senha - String
     * @return
     */
    public static synchronized Company getInstance() {
        if (instancia == null) {
            instancia = new Company("company", "company");
        }
        return instancia;
    }

    /**
     * Construtor privado da classe Company
     * 
     * @param login - Login da conta no AlphaBank
     * @param senha - Senha da conta no AlphaBank
     */
    private Company(String login, String senha) {
        setName("company");
        this.login = login;
        this.senha = senha;

        // Inicializa o Serve para comunicação com os Cars
        serverCompany = new ServerCompany(4001, login);
        try {
            if (serverCompany.begin()) {
                serverCompany.start();
            }

        } catch (Exception e) {
            System.err.println("Erro no servidor AlphaBank: " + e.getMessage());
        }

        // Inicializa o BotPayment para realizar pagamentos aos Drivers
        try {
            botPayment = new BotPayment(login, senha, 10000000);
            botPayment.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Inicializa HashMap para instancia de Cars
        carros = new HashMap<>();

        // Ininicailiza fila de dados a processar
        filaDedados = new LinkedList<>();

        // Inicializa lista de rotas (A executar, execuntado e executadas)
        rotasExecutar = new LinkedList<>();
        rotasExecutando = new ArrayList<>();
        rotasExecutadas = new ArrayList<>();

        this.importarRotas(200);
    }

    @Override
    public void run() {
        while (serverCompany.isAlive()) {
            try {
                DataCar dataCar = null;

                synchronized (lockData) {
                    while (filaDedados.isEmpty()) {
                        if (!serverCompany.isAlive()) {
                            break;
                        }
                        lockData.wait();
                    }

                    dataCar = filaDedados.poll();
                }

                if (dataCar != null) {
                    // TODO processar dados enviados pelos CARS
                    payDriver(dataCar.getIdDriver(), 3.85);                   
                }

            }

            catch (Exception e) {
                System.err.println("Erro ao processar transação: " + e.getMessage());
            }
        }
    }

    /**
     * Metodo que desliga o servidor do banco e encerra a Thread
     */
    public void shutdown() {
        synchronized (lockData) {
            try {
                botPayment.closeSocket();
                serverCompany.stopServer();
                while (serverCompany.isAlive()) {
                    sleep(100);
                }
                lockData.notifyAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metódo privado para importada rotas da base de dados
     * 
     * @param qtd
     */
    private void importarRotas(int qtd) {

        try {
            for (int i = 1; i <= qtd; i++) {
                addRotasExecutar(new Route(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addRotasExecutar(Route route) {
        synchronized (lockRotas) {
            rotasExecutar.add(route);
        }
    }

    private void addRotasExecutando(Route route) {
        synchronized (lockRotas) {
            rotasExecutando.addLast(route);
        }
    }

    private void addRotasExecutadas(Route route) {
        synchronized (lockRotas) {
            rotasExecutadas.addLast(route);
        }
    }

    private Route removeRotasExecutar() {
        synchronized (lockRotas) {
            return rotasExecutar.poll();
        }
    }

    private Route removeRotasExecutando(int index) {
        synchronized (lockRotas) {
            return rotasExecutando.get(index);
        }
    }

    public void addCar(Car car) {
        synchronized (lockcar) {
            carros.put(car.getIdCar(), car);
        }
    }

    public String getLogin() {
        return login;
    }

    public synchronized Route getRoute() {
        Route rota = removeRotasExecutar();
        addRotasExecutando(rota);
        return rota;
    }

    private synchronized void payDriver(String destino, double valor) {
        botPayment.solicitarTransferencia(destino, valor, senha);
    }

    private void adicionarTransacao(DataCar dataCar) {
        synchronized (lockData) {
            filaDedados.add(dataCar);
            lockData.notify();
        }
    }

    /**
     * 
     */
    private class ServerCompany extends Server {

        public ServerCompany(int port, String name) {
            super(port, name);
        }

        @Override
        protected void processarMensagem(String msg) throws Exception {
            try {
                String msgDescriptografada = Crypto.descriptografar(msg);
                DataCar dataCar = Json.fromJson(msgDescriptografada, DataCar.class);
                adicionarTransacao(dataCar);
            }

            catch (Exception e) {
                System.err.println("Erro ao processar mensagem: " + e.getMessage());
                throw e;
            }
        }

    }

}
