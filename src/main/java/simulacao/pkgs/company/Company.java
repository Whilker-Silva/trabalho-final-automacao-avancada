package simulacao.pkgs.company;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import simulacao.pkgs.banco.BotPayment;
import simulacao.pkgs.car.Car;
import simulacao.pkgs.car.DataCar;
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
    private final HashMap<String, Route> rotasExecutando;
    private final ArrayList<Route> rotasExecutadas;

    private final Object lockData = new Object();
    private final Object lockRotas = new Object();
    private final Object lockcar = new Object();

    private Workbook workbook;
    private Sheet sheet;
    private int rowNum;

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
        rotasExecutando = new HashMap<>();
        rotasExecutadas = new ArrayList<>();

        this.importarRotas(2);

        // criando planilha para coleta de dados
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Dados_carros");
        Row headerRow = sheet.createRow(0);

        headerRow.createCell(0).setCellValue("Timestamp");
        headerRow.createCell(1).setCellValue("ID Car");
        headerRow.createCell(2).setCellValue("ID Car");
        headerRow.createCell(3).setCellValue("ID Route");
        headerRow.createCell(4).setCellValue("Speed");
        headerRow.createCell(5).setCellValue("Distance");
        headerRow.createCell(6).setCellValue("Fuel Consumption");
        headerRow.createCell(7).setCellValue("Fuel Type");
        headerRow.createCell(8).setCellValue("CO2 emission");
        headerRow.createCell(9).setCellValue("Longitude");
        headerRow.createCell(10).setCellValue("Latitude");

        rowNum = 1;
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
                }

                dataCar = filaDedados.poll();

                if (dataCar != null) {
                    processarMsg(dataCar);
                }

            }

            catch (Exception e) {
                System.err.println("Erro ao processar transação: " + e.getMessage());
            }
        }

        // Salva em arquivo
        try (FileOutputStream outputStream = new FileOutputStream("Dados_carros.xlsx")) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("PLANILHA SALVA COM SUCESSO");
    }

    private void processarMsg(DataCar dados) {

        if (dados.getPagamento()) {
            botPayment.solicitarTransferencia(dados.getIdDriver(), 3.25, senha);
        }

        if (dados.rotaFinalizada()) {
            String idRoute = dados.getIdRoute();
            Route aux = removeRotasExecutando(idRoute);
            addRotasExecutadas(aux);
        }

        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(dados.getTimestamp());
        row.createCell(1).setCellValue(dados.getIdCar());
        row.createCell(2).setCellValue(dados.getIdDriver());
        row.createCell(3).setCellValue(dados.getIdRoute());
        row.createCell(4).setCellValue(dados.getSpeed());
        row.createCell(5).setCellValue(dados.getDistancia());
        row.createCell(6).setCellValue(dados.getFuelConsumption());
        row.createCell(7).setCellValue(dados.getCombustivel());
        row.createCell(8).setCellValue(dados.getCo2Emission());
        row.createCell(9).setCellValue(dados.getLongitude());
        row.createCell(10).setCellValue(dados.getLatitude());

        for (int i = 0; i <= 10; i++) {
            sheet.autoSizeColumn(i);
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
            rotasExecutando.put(route.getIdRoute(), route);
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

    private Route removeRotasExecutando(String idRoute) {
        synchronized (lockRotas) {
            return rotasExecutando.remove(idRoute);
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

    public synchronized String getRoute() {
        Route rota = removeRotasExecutar();
        addRotasExecutando(rota);

        try {
            return Crypto.criptografar(Json.toJson(rota));
        } catch (Exception e) {
            return null;
        }
    }

    private void adicionarDados(DataCar dataCar) {
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
                adicionarDados(dataCar);
            }

            catch (Exception e) {
                System.err.println("Erro ao processar mensagem: " + e.getMessage());
                throw e;
            }
        }

    }

}
