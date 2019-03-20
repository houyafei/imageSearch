package sample.services;


import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import sample.db.SQLiteJDBC;
import sample.models.ImageFinger;
import sample.utils.ImageUtils;
import sample.utils.PHashFingerPrinter;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Date;

public class CalculateFingerPrinterService extends Service<String> {

    private InputStream inputStream;

    private File ImageFile;

    private PHashFingerPrinter phash = new PHashFingerPrinter();

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public CalculateFingerPrinterService(File filePath) {
        this.ImageFile = filePath;
        try {
            this.inputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
                String fingerPrinter = phash.getHash(inputStream);
                BufferedImage nailImage = phash.resize(ImageIO.read(inputStream), 140, 200);
                System.out.println("buffImage"+(nailImage==null));
//                nailImage.getData().getDataBuffer()
                ImageFinger imageEle = new ImageFinger(ImageFile.getName(), ImageFile.getName(), fingerPrinter,
                        ImageFile.getAbsolutePath(), new Date(System.currentTimeMillis()), 0, nailImage);
                SQLiteJDBC.insertData(imageEle);
                return fingerPrinter;
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
