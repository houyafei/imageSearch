package sample.utils;

public class ConstUtil {
    public static final String CREATE_TABLE = "" +
            "CREATE TABLE IF not exists image_finger" +
            "(" +
            "  imageId           INTEGER PRIMARY KEY  AUTOINCREMENT," +
            "  imageName         TEXT NOT NULL," +
            "  imageTags         TEXT," +
            "  imageFinger       TEXT NOT NULL," +
            "  imageAbsolutePath TEXT NOT NULL," +
            "  time              DATE NOT NULL," +
            "  nailImage         BLOB NOT NULL," +
            "  distance          INTEGER" +
            ");" +
            "CREATE INDEX IF NOT EXISTS finger_index" +
            "  on image_finger (imageFinger);" +
            "CREATE UNIQUE INDEX IF NOT EXISTS image_path_index" +
            "  on image_finger (imageAbsolutePath);";
    public static final String SQL_INSERT_IMAGE_FINGER = "" +
            "INSERT INTO image_finger (imageName, imageTags, imageFinger, imageAbsolutePath, time, nailImage, distance)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?);";
    public static final String SQL_QUERY_ALL = "" +
            "SELECT imageId,imageName, imageTags, imageFinger, imageAbsolutePath, time, distance FROM image_finger;";
    public static final String SQL_SELECT_NAIL_IMAGE_BY_ID = "" +
            "SELECT nailImage FROM image_finger WHERE imageId = ?";
    public static final String SQL_SELECT_IMAGE_FINGER_BY_IMAGE_PATH = "" +
            "SELECT FROM image_finger WHERE imageAbsolutePath = ?";
    public static final String SQL_UPDATE_BY_IMAGE_FINGER = "" +
            "UPDATE image_finger " +
            "SET imageName=?, imageTags=?, imageFinger=?, distance=?  WHERE imageAbsolutePath=?;";
    public static final String SQL_REPLACE_IMAGE_FINGER = "" +
            "REPLACE INTO image_finger (imageName, imageTags, imageFinger, imageAbsolutePath, time, nailImage, distance)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?);";

    public static final String KEY_IMAGEID = "imageId";
    public static final String KEY_IMAGENAME = "imageName";
    public static final String KEY_IMAGETAGS = "imageTags";
    public static final String KEY_IMAGEFINGER = "imageFinger";
    public static final String KEY_IMAGEABSOLUTEPATH = "imageAbsolutePath";
    public static final String KEY_TIME = "time";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_NAILIMAGE = "nailImage";

    public static final String IMAGE_REG = ".+(.JPEG|.jpeg|.JPG|.jpg|.png|.tif)$";
}
