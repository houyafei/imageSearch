package sample.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import sample.db.SQLiteJDBC;
import sample.models.ImageFinger;
import sample.utils.ConstUtil;
import sample.utils.ImageUtils;
import sample.utils.PHashFingerPrinter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaveImageService extends Service<String> {


    private String directoryPath;

    private PHashFingerPrinter phash = new PHashFingerPrinter();

    private Pattern pattern = Pattern.compile(ConstUtil.IMAGE_REG);

    private int count = 0;

    public SaveImageService(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                findImage(directoryPath);
                return "";
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                System.out.println("this file printer:" + SaveImageService.this.getValue());
                updateMessage("");
            }

            private void findImage(String filePath) {
                File file = new File(filePath);
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null && files.length > 0) {
                        for (File listFile : files) {
                            findImage(listFile.getAbsolutePath());
                        }
                    }

                } else {
                    String subFile = filePath.toLowerCase();
                    Matcher matcher = pattern.matcher(subFile);
                    if (matcher.find()) {
                        try {

                            String finger = phash.getHash(new FileInputStream(filePath));
                            count += 1;
                            new Thread(() -> {
                                BufferedImage nailImage = null;
                                try {
                                    nailImage = phash.resize(ImageIO.read(new FileInputStream(filePath)), 140, 200);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ImageFinger imageEle = new ImageFinger(file.getName(), file.getName(), finger,
                                        file.getAbsolutePath(), new Date(System.currentTimeMillis()), 0, nailImage);
                                SQLiteJDBC.insertData(imageEle);
                            }).start();
                            updateMessage("已经整理图片：" + count + "张");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        };
    }

}
