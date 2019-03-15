package sample.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import sample.db.SQLiteJDBC;
import sample.models.ImageFinger;
import sample.utils.PHashFingerPrinter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class SearchImagesService extends Service<List<String>> {

    private InputStream inputStream;

    private PHashFingerPrinter phash = new PHashFingerPrinter();

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public SearchImagesService(InputStream inputStream) {
        super();
        this.inputStream = inputStream;
    }

    private int count = 0;

    @Override
    protected Task<List<String>> createTask() {
        return new Task<List<String>>() {
            @Override
            protected List<String> call() throws Exception {
                if (inputStream == null) {
                    return null;
                }
                String srcPrintFinger = phash.getHash(inputStream);

                List<ImageFinger> listImage = SQLiteJDBC.queryList();
                int progressMax = listImage.size() * 2;
                updateProgress(0, progressMax);
                count = 0;
                listImage.forEach(ele -> {
                    ele.setDistance(phash.distance(srcPrintFinger, ele.getImageFinger()));
                    updateProgress(count++, progressMax);
                });
                listImage.sort((o1, o2) -> {
                    int temp = o1.getDistance() - o2.getDistance();
                    return Integer.compare(temp, 0);

                });
                List<String> result = new ArrayList<>(listImage.size());
                listImage.forEach(ele -> {
                    if (ele.getDistance() < 15) {
                        result.add(ele.getImageAbsolutePath());
                    }
                    updateProgress(count++, progressMax);
                });

                return result;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                System.out.println("this file printer:" + SearchImagesService.this.getValue());
            }

            @Override
            protected void updateProgress(double workDone, double max) {
                super.updateProgress(workDone, max);
                updateMessage(String.format("处理进度：%2f%%", (workDone / max) * 100));
            }
        };
    }


}

