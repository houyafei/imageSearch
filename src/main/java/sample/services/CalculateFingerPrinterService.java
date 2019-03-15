package sample.services;


import javafx.concurrent.Service;
import javafx.concurrent.Task;
import sample.utils.PHashFingerPrinter;


import java.io.InputStream;

public class CalculateFingerPrinterService extends Service<String> {

    private InputStream inputStream;

    private  PHashFingerPrinter phash = new PHashFingerPrinter();

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public CalculateFingerPrinterService(InputStream inputStream) {
        super();
        this.inputStream = inputStream;
    }

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                if (inputStream == null) {
                    return null;
                }
                return phash.getHash(inputStream);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                System.out.println("this file printer:" + CalculateFingerPrinterService.this.getValue());
            }

            @Override
            protected void updateProgress(double workDone, double max) {
                super.updateProgress(workDone, max);
            }
        };
    }


}
