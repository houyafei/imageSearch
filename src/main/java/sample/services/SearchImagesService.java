package sample.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javafx.scene.image.Image;
import sample.db.SQLiteJDBC;
import sample.models.ImageFinger;
import sample.models.SearchServiceResult;

import sample.utils.ImageUtils;
import sample.utils.PHashFingerPrinter;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class SearchImagesService extends Service<SearchServiceResult> {

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
    protected Task<SearchServiceResult> createTask() {
        return new Task<SearchServiceResult>() {
            @Override
            protected SearchServiceResult call() throws Exception {
                if (inputStream == null) {
                    return null;
                }
                String srcPrintFinger = phash.getHash(inputStream);

                List<ImageFinger> listImageFingers = SQLiteJDBC.queryList();
                int progressMax = listImageFingers.size() * 2;
                updateProgress(0, progressMax);
                count = 0;
                listImageFingers.forEach(ele -> {
                    ele.setDistance(phash.hyDistance(srcPrintFinger, ele.getImageFinger()));
                    updateProgress(count++, progressMax);
                });
                listImageFingers.sort((o1, o2) -> {
                    int temp = o1.getDistance() - o2.getDistance();
                    return Integer.compare(temp, 0);

                });
                List<String> listPaths = new ArrayList<>(listImageFingers.size());
                List<Image> listImages = new ArrayList<>(listImageFingers.size());
                long start = System.currentTimeMillis();
                listImageFingers.forEach(ele -> {
                    listPaths.add(ele.getImageAbsolutePath());
                    BufferedImage tempImage = SQLiteJDBC.queryImage(ele.getImageId());
                    Image image = ImageUtils.bufferImage2Image(tempImage);
                    listImages.add(image);

                    updateProgress(count++, progressMax);
                });
                System.out.println("use time:" + (System.currentTimeMillis() - start));
                return new SearchServiceResult(listPaths, listImages);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
//                System.out.println("this file printer:" + SearchImagesService.this.getValue());
            }

            @Override
            protected void updateProgress(double workDone, double max) {
                super.updateProgress(workDone, max);
                updateMessage(String.format("已经搜索完成：%.2f%%", workDone / max * 100));
            }

            @Override
            protected void updateMessage(String message) {
                super.updateMessage(message);
            }

            @Override
            protected void running() {
                super.running();
            }
        };
    }
}



