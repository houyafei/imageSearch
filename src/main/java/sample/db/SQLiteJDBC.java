package sample.db;



import sample.models.ImageFinger;
import sample.utils.ConstUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteJDBC {
    static {
        createTable();
    }
//    public static void main(String args[]) {
//
//        List<ImageFinger> list = queryList();
//        for (int i = 0; i < 10; i++) {
//            System.out.println(list.get(i));
//        }
//    }

    public static void insertData(ImageFinger imageFinger) {
        Connection c = JDBCConnectionUtil.getConnection();
        if (c == null) {
            System.err.println("Connection is null");
            return;
        }
        PreparedStatement stmt;
        try {
            String sql = ConstUtil.SQL_REPLACE_IMAGE_FINGER;
            stmt = c.prepareStatement(sql);
            stmt.setString(1, imageFinger.getImageName());
            stmt.setString(2, imageFinger.getImageTags());
            stmt.setString(3, imageFinger.getImageFinger());
            stmt.setString(4, imageFinger.getImageAbsolutePath());
            stmt.setDate(5, imageFinger.getTime());
            stmt.setInt(6, imageFinger.getDistance());
            stmt.execute();
            JDBCConnectionUtil.releaseConnection(c, stmt, null);
            System.out.println("insert created successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

    }

    public static void batchInsert(ArrayList<ImageFinger> lists) {
        Connection c = JDBCConnectionUtil.getConnection();
        if (c == null || lists.isEmpty()) {
            System.err.println("Connection is null or list is empty");
            return;
        }
        PreparedStatement stmt;
        try {
            String sql = ConstUtil.SQL_REPLACE_IMAGE_FINGER;

            stmt = c.prepareStatement(sql);
            for (ImageFinger ele : lists) {
                stmt.setString(1, ele.getImageName());
                stmt.setString(2, ele.getImageTags());
                stmt.setString(3, ele.getImageFinger());
                stmt.setString(4, ele.getImageAbsolutePath());
                stmt.setDate(5, ele.getTime());
                stmt.setInt(6, ele.getDistance());
                stmt.addBatch();
            }
            int[] rows = stmt.executeBatch();
            JDBCConnectionUtil.releaseConnection(c, stmt, null);
            System.out.println("insert created successfully size(s): " + rows.length);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

    }

    public static ArrayList<ImageFinger> queryList() {
        ArrayList<ImageFinger> list = new ArrayList<>();
        Connection c = JDBCConnectionUtil.getConnection();
        if (c == null) {
            System.err.println("Connection is null or list is empty");
            return list;
        }
        PreparedStatement stmt;
        ResultSet resultSet;
        String sql = ConstUtil.SQL_QUERY_ALL;
        try {
            stmt = c.prepareStatement(sql);
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                ImageFinger temp = new ImageFinger();
                temp.setImageId(resultSet.getInt(ConstUtil.KEY_IMAGEID));
                temp.setImageName(resultSet.getString(ConstUtil.KEY_IMAGENAME));
                temp.setImageFinger(resultSet.getString(ConstUtil.KEY_IMAGEFINGER));
                temp.setImageTags(resultSet.getString(ConstUtil.KEY_IMAGETAGS));
                temp.setImageAbsolutePath(resultSet.getString(ConstUtil.KEY_IMAGEABSOLUTEPATH));
                temp.setDistance(resultSet.getInt(ConstUtil.KEY_DISTANCE));
                temp.setTime(resultSet.getDate(ConstUtil.KEY_TIME));
                list.add(temp);
            }
            JDBCConnectionUtil.releaseConnection(c, stmt, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static void createTable() {
        Connection c = JDBCConnectionUtil.getConnection();
        if (c == null) {
            System.err.println("Connection is null");
            return;
        }
        Statement stmt;
        try {
            String sql = ConstUtil.CREATE_TABLE;
            stmt = c.createStatement();
            stmt.executeUpdate(sql);
            JDBCConnectionUtil.releaseConnection(c, stmt, null);
            System.out.println("Table created successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

    }


}