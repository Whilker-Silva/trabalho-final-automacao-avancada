package reconciliacao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Reconciliacao extends Thread {

    private Workbook workbookCarro;
    private Sheet dadosCarro;

    private Workbook workbookRec;
    private Sheet dadosRec;
    private int currentPos;
    private int currentSample;
    private double t0;

    public Reconciliacao(String excelPath) {

        setName("Reconciliacao de dados");

        workbookRec = new XSSFWorkbook();
        dadosRec = workbookRec.createSheet("Dados_filtrados");
        criaTabelaRec();

        importaDadosCarro(excelPath);

        currentPos = 1;
        currentSample = 1;
    }

    @Override
    public void run() {

        for (int i = 1; i <= dadosCarro.getLastRowNum(); i++) {

            Row rowCarro = dadosCarro.getRow(i);

            double lat = rowCarro.getCell(9).getNumericCellValue();
            double lon = rowCarro.getCell(10).getNumericCellValue();

            if (distancia(lat, lon) < 20) {

                Row rowRec = dadosRec.getRow(currentPos);
                if (rowRec == null) {
                    rowRec = dadosRec.createRow(currentPos);
                }

                if (currentPos == 1) {
                    t0 = rowCarro.getCell(0).getNumericCellValue() / 1000;
                }

                double t = rowCarro.getCell(0).getNumericCellValue() / 1000 - t0;

                rowRec.createCell(currentSample).setCellValue(t);
                

                if (currentPos == 8) {
                    currentSample++;
                    currentPos = 1;
                }

                else {
                    currentPos++;
                }
            }
        }

        try {
            workbookCarro.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        salvarPlanilha();

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
        headerRowRec.createCell(0).setCellValue("Coordenadas");

        for (int i = 1; i <= 10; i++) {
            headerRowRec.createCell(i).setCellValue("Amostra " + i);
        }

        for (int i = 1; i <= 8; i++) {
            Row rowRec = dadosRec.createRow(i);
            rowRec.createCell(0).setCellValue("Coordenada " + i);
        }
    }

    private double distancia(double lat, double lon) {

        double RaioTerra = 6371000.0;

        double pointLat = PontosDeMedidas.values()[currentPos - 1].getLatitude();
        double pointLon = PontosDeMedidas.values()[currentPos - 1].getLongitude();

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
            FileOutputStream outputStream = new FileOutputStream("data/Dados_filtrados.xlsx");
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
