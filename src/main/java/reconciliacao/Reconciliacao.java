package reconciliacao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;

public class Reconciliacao extends Thread {

    private final double[] medidasIdeiais = { 0, 20, 45, 25, 20, 10, 10, 20 };

    private Workbook workbookCarro;
    private Sheet dadosCarro;

    private Workbook workbookRec;
    private Sheet dadosRec;

    public Reconciliacao(String excelPath) {

        setName("Reconciliacao de dados");

        workbookRec = new XSSFWorkbook();
        dadosRec = workbookRec.createSheet("Reconconciliacao");
        criaTabelaRec();

        importaDadosCarro(excelPath);
    }

    @Override
    public void run() {

        filtrarDadosBruto();
        plotarDistribucao();
        calcularMédia();
        calularDesvioPadrao();
        calularBias();
        calcularPrecisao();
        calcularIncerteza();
        reconciliacaoDeDados();
        calcularVelocidades();
        salvarPlanilha();

        System.out.println("Reconciliacao de dados realizada com sucesso!");
    }

    private void importaDadosCarro(String excelPath) {
        try {
            FileInputStream fis = new FileInputStream(new File(excelPath));
            workbookCarro = new XSSFWorkbook(fis);
            dadosCarro = workbookCarro.getSheetAt(0); // primeira aba
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void criaTabelaRec() {
        Row headerRowRec = dadosRec.createRow(0);
        headerRowRec.createCell(0).setCellValue("Medidor");

        for (int i = 1; i <= 10; i++) {
            headerRowRec.createCell(i).setCellValue("Amostra " + i);
        }

        headerRowRec.createCell(12).setCellValue("Média");
        headerRowRec.createCell(13).setCellValue("Desvio padrão");
        headerRowRec.createCell(14).setCellValue("Bias");
        headerRowRec.createCell(15).setCellValue("Precisao");
        headerRowRec.createCell(16).setCellValue("Incerteza");
        headerRowRec.createCell(18).setCellValue("Tempos reconciliados");

        headerRowRec = dadosRec.createRow(10);
        headerRowRec.createCell(0).setCellValue("Trecho");
        headerRowRec.createCell(1).setCellValue("Distancia");
        headerRowRec.createCell(2).setCellValue("Velocidade");

        for (int i = 1; i <= 8; i++) {
            Row rowRec = dadosRec.createRow(i);
            rowRec.createCell(0).setCellValue("Medidor " + i);
        }

        for (int i = 11; i <= 17; i++) {
            Row rowRec = dadosRec.createRow(i);
            rowRec.createCell(0).setCellValue("Trecho " + (i - 10));
        }
    }

    private void filtrarDadosBruto() {

        int currentPos = 1;
        int currentSample = 1;

        double t_1 = 0;
        double t = 0;

        double currentLat;
        double currentLon;

        Row rowCarro;
        Row rowRec;

        double[] distancias = new double[8];

        for (int i = 1; i <= dadosCarro.getLastRowNum(); i++) {

            // Pega na planilha de dados brutos do carro a coordenada atual
            rowCarro = dadosCarro.getRow(i);
            currentLat = rowCarro.getCell(9).getNumericCellValue();
            currentLon = rowCarro.getCell(10).getNumericCellValue();

            // Calcula a distancia até o proximo ponto de medida
            double distancia = distancia(currentLat, currentLon, PontosDeMedidas.values()[currentPos - 1]);

            if (distancia < 20) {

                // Pega a linha correspondente ao ponto de medição
                rowRec = dadosRec.getRow(currentPos);
                if (rowRec == null) {
                    rowRec = dadosRec.createRow(currentPos);
                }

                // Se for o ponto inicial da amostra atual
                if (currentPos == 1) {
                    t_1 = rowCarro.getCell(0).getNumericCellValue() / 1000;

                }

                // pega o timestamp do instante que o carro chegou no ponto de medida
                t = rowCarro.getCell(0).getNumericCellValue() / 1000;
                // calcula intervalo de tempo entre a medida atual e anterior
                t -= t_1;
                // Atuliza tempo da medida anterior
                t_1 = rowCarro.getCell(0).getNumericCellValue() / 1000;

                distancias[currentPos - 1] = rowCarro.getCell(5).getNumericCellValue();

                // escreve valor da leitura na planilha
                rowRec.createCell(currentSample).setCellValue(t);

                // Verifica se acabou a amostra atual e incrementa currentPos
                if (currentPos == 8) {
                    currentSample++;
                    currentPos = 1;
                } else {
                    currentPos++;
                }
            }
        }

        for (int i = 2; i <= 8; i++) {
            double trecho = distancias[i - 1] - distancias[i - 2];

            rowRec = dadosRec.getRow(i + 9);
            rowRec.createCell(1).setCellValue(trecho * 1000); // converte de km para metros
        }

        // Fecha arquivo Dados_Carro
        try {
            workbookCarro.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void plotarDistribucao() {

        for (int medida = 1; medida <= 8; medida++) {

            Row row = dadosRec.getRow(medida);

            double[] dados = new double[10];
            for (int i = 0; i < 10; i++) {
                dados[i] = row.getCell(i + 1).getNumericCellValue();
            }

            // Criar dataset do histograma
            HistogramDataset dataset = new HistogramDataset();
            dataset.addSeries(medida, dados, 20);

            // Criar histograma
            JFreeChart histograma = ChartFactory.createHistogram(
                    "Distribuição das medidas no ponto " + medida,
                    "Valor",
                    "Frequência",
                    dataset);

            // Exportar como imagem
            try {
                String pathName = "data/reconciliacao de dados/distribuicao/Distribuicao" + medida + ".png";
                ChartUtils.saveChartAsPNG(new File(pathName), histograma, 800, 600);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void calcularMédia() {

        for (int medida = 1; medida <= 8; medida++) {

            Row row = dadosRec.getRow(medida);
            double soma = 0;

            for (int amostra = 1; amostra <= 10; amostra++) {
                soma += row.getCell(amostra).getNumericCellValue();
            }

            row.createCell(12).setCellValue(soma / 10);
        }

    }

    private void calularDesvioPadrao() {

        for (int medida = 1; medida <= 8; medida++) {

            Row row = dadosRec.getRow(medida);
            double media = row.getCell(12).getNumericCellValue();

            double somatoria = 0;
            for (int amostra = 1; amostra <= 10; amostra++) {
                double valor = row.getCell(amostra).getNumericCellValue();
                somatoria += Math.pow(valor - media, 2);
            }

            double desvio = Math.sqrt(somatoria / (10 - 1));
            row.createCell(13).setCellValue(desvio);
        }

    }

    private void calularBias() {

        for (int medida = 1; medida <= 8; medida++) {

            Row row = dadosRec.getRow(medida);
            double media = row.getCell(12).getNumericCellValue();

            double bias = medidasIdeiais[medida - 1] - media;
            row.createCell(14).setCellValue(bias);
        }
    }

    private void calcularPrecisao() {
        for (int medida = 1; medida <= 8; medida++) {

            Row row = dadosRec.getRow(medida);
            double desvio = row.getCell(13).getNumericCellValue();

            double precisao = 2 * desvio;
            row.createCell(15).setCellValue(precisao);
        }
    }

    private void calcularIncerteza() {
        for (int medida = 1; medida <= 8; medida++) {

            Row row = dadosRec.getRow(medida);
            double bias = row.getCell(14).getNumericCellValue();
            double precisao = row.getCell(15).getNumericCellValue();

            bias = Math.pow(bias, 2);
            precisao = Math.pow(precisao, 2);

            double incerteza = Math.sqrt(bias + precisao);
            row.createCell(16).setCellValue(incerteza);
        }
    }

    private void reconciliacaoDeDados() {

        double[] medidas = new double[8];
        double[] variancia = new double[8];

        for (int medida = 1; medida <= 8; medida++) {

            Row row = dadosRec.getRow(medida);

            medidas[medida - 1] = row.getCell(12).getNumericCellValue();
            variancia[medida - 1] = Math.pow(row.getCell(13).getNumericCellValue(), 2);
        }

        double[][] yArray = new double[8][1];
        for (int i = 0; i < 8; i++) {
            yArray[i][0] = medidas[i];
        }
        RealMatrix Y = MatrixUtils.createRealMatrix(yArray);

        RealMatrix V = MatrixUtils.createRealDiagonalMatrix(variancia);

        RealMatrix A = MatrixUtils.createRealMatrix(new double[][] {
                { 1, 1, 1, 1, 1, 1, 1, 1 }
        });

        RealMatrix B = MatrixUtils.createRealMatrix(new double[][] {
                { 150 }
        });

        // Transposta de A
        RealMatrix AT = A.transpose();

        // Produto A * V
        RealMatrix AV = A.multiply(V);

        // Produto A * V * A^T
        RealMatrix AVAT = AV.multiply(AT);

        // Inversa de AVAT
        RealMatrix invAVAT = new LUDecomposition(AVAT).getSolver().getInverse();

        // Produto A * Y
        RealMatrix AY = A.multiply(Y);

        // Subtração: (A * y - b)
        RealMatrix diff = AY.subtract(B);

        // Produto final: V * A^T * inv(AVAT) * (A y - b)
        RealMatrix ajuste = V.multiply(AT).multiply(invAVAT).multiply(diff);

        // Reconciliação final: y_hat = y - ajuste
        RealMatrix yHat = Y.subtract(ajuste);

        // Mostrar resultado reconciliado
        for (int i = 0; i < 8; i++) {
            Row row = dadosRec.getRow(i + 1); // linha 1 a 8 (ignora cabeçalho)

            double valorReconciliado = yHat.getEntry(i, 0);
            row.createCell(18).setCellValue(valorReconciliado);
        }

    }

    private void calcularVelocidades() {

        for (int linha = 11; linha <= 17; linha++) {

            Row rowDistancia = dadosRec.getRow(linha);
            Row rowTempo = dadosRec.getRow(linha - 9);

            double distancia = rowDistancia.getCell(1).getNumericCellValue();
            double tempo = rowTempo.getCell(18).getNumericCellValue();
            double velocidade = distancia / tempo * 3.6; //Velocidade em Km/h

            rowDistancia.createCell(2).setCellValue(velocidade);
        }

    }

    private double distancia(double lat, double lon, PontosDeMedidas pontoDeMedida) {

        double RaioTerra = 6371000.0;

        double pointLat = pontoDeMedida.getLatitude();
        double pointLon = pontoDeMedida.getLongitude();

        // Converter de graus para radianos
        double latRad1 = Math.toRadians(lat);
        double latRad2 = Math.toRadians(pointLat);
        double deltaLat = Math.toRadians(pointLat - lat);
        double deltaLon = Math.toRadians(pointLon - lon);

        // Fórmula de Haversine
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(latRad1) * Math.cos(latRad2) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distancia = RaioTerra * c; // distância em metros

        return distancia;
    }

    private void salvarPlanilha() {

        try {
            FileOutputStream outputStream = new FileOutputStream("data/reconciliacao de dados/Reconciliacao.xlsx");
            workbookRec.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            workbookRec.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
