package sample.models;

import javafx.scene.image.Image;

import java.util.List;

public class SearchServiceResult {

    private List<String> listPath;
    private List<Image> listImage;

    public SearchServiceResult(List<String> listPath, List<Image> listImage) {
        this.listPath = listPath;
        this.listImage = listImage;
    }

    public List<String> getListPath() {
        return listPath;
    }

    public List<Image> getListImage() {
        return listImage;
    }
}