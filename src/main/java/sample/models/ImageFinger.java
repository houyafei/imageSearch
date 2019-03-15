package sample.models;

import java.sql.Date;

public class ImageFinger {
    private int imageId;
    private String imageName;
    private String imageTags;
    private String imageFinger;
    private String imageAbsolutePath;
    /**
     * format: yyyy-MM-dd HH-mm-ss
     */
    private Date time;
    private int distance;

    public ImageFinger() {
    }

    public ImageFinger(String imageName,
                       String imageTags,
                       String imageFinger,
                       String imageAbsolutePath,
                       Date time,
                       int distance) {
        this.imageName = imageName;
        this.imageTags = imageTags;
        this.imageFinger = imageFinger;
        this.imageAbsolutePath = imageAbsolutePath;
        this.time = time;
        this.distance = distance;
    }

    public ImageFinger(int imageId,
                       String imageName,
                       String imageTags,
                       String imageFinger,
                       String imageAbsolutePath,
                       Date time) {
        this.imageId = imageId;
        this.imageName = imageName;
        this.imageTags = imageTags;
        this.imageFinger = imageFinger;
        this.imageAbsolutePath = imageAbsolutePath;
        this.time = time;
    }

    public ImageFinger(int imageId,
                       String imageName,
                       String imageTags,
                       String imageFinger,
                       String imageAbsolutePath,
                       Date time,
                       int distance) {
        this.imageId = imageId;
        this.imageName = imageName;
        this.imageTags = imageTags;
        this.imageFinger = imageFinger;
        this.imageAbsolutePath = imageAbsolutePath;
        this.time = time;
        this.distance = distance;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageTags() {
        return imageTags;
    }

    public void setImageTags(String imageTags) {
        this.imageTags = imageTags;
    }

    public String getImageFinger() {
        return imageFinger;
    }

    public void setImageFinger(String imageFinger) {
        this.imageFinger = imageFinger;
    }

    public String getImageAbsolutePath() {
        return imageAbsolutePath;
    }

    public void setImageAbsolutePath(String imageAbsolutePath) {
        this.imageAbsolutePath = imageAbsolutePath;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "ImageFinger{" +
                "imageId=" + imageId +
                ", imageName='" + imageName + '\'' +
                ", imageTags='" + imageTags + '\'' +
                ", imageFinger='" + imageFinger + '\'' +
                ", imageAbsolutePath='" + imageAbsolutePath + '\'' +
                ", time='" + time + '\'' +
                ", distance=" + distance +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return this.imageFinger.equals(((ImageFinger) obj).imageFinger);
    }
}
