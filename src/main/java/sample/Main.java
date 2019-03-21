package sample;

import javafx.application.Application;


import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.GridView;
import org.controlsfx.control.cell.ImageGridCell;
import sample.db.SQLiteJDBC;
import sample.models.ImageFinger;
import sample.models.SearchServiceResult;
import sample.services.CalculateFingerPrinterService;
import sample.services.SaveImageService;
import sample.services.SearchImagesService;
import sample.utils.ImageUtils;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.util.List;


import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    private final Desktop desktop = Desktop.getDesktop();

    private BufferedImage srcImage = null;

    private ObservableList<Image> list = FXCollections.observableArrayList();


    final ScrollPane sp = new ScrollPane();
    final Image[] images = new Image[5];
    final ImageView[] pics = new ImageView[5];
    final FlowPane vb = new FlowPane();
    final Label fileName = new Label();
    final String[] imageNames = new String[]{"back.jpg", "search.jpg",
            "back.jpg", "search.jpg", "back.jpg"};

    private Label progressLabel = new Label("图片处理中");
    private ProgressBar progressBar = new ProgressBar(-1);
    private HBox hBox = new HBox();


    private StackPane stackPane = null;

    private VBox showBigImageVBosx = new VBox();
    private ImageView showBigImage = new ImageView();
    private Pagination pagination = new Pagination();

    private SearchServiceResult serviceResult = null;

    @Override
    public void start(final Stage stage) throws Exception {
        searchImage(stage);

    }


    private void searchImage(Stage primaryStage) {
        primaryStage.setTitle("ImageSearch");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 850, 800);
        root.setBackground(new Background(new BackgroundImage(new Image("/images/back.jpg"), null, null, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        VBox vBox = new VBox();
        MenuBar menuBar = setMenuBar(primaryStage);
        vBox.getChildren().add(menuBar);

        hBox.setPrefWidth(700);
        progressBar.setPrefWidth(400);
        hBox.getChildren().addAll(progressBar);
        hBox.getChildren().add(progressLabel);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().removeAll(progressBar);
        hBox.setVisible(false);


        vBox.getChildren().add(hBox);
        root.setTop(vBox);

        stackPane = new StackPane();
        root.setCenter(stackPane);
        stackPane.getChildren().add(addGridView());
        addSearchPane(root, primaryStage);


        pagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
        pagination.setPageFactory(value -> {
            VBox vBox1 = new VBox();
            vBox1.setAlignment(Pos.CENTER);
            vBox1.setBackground(new Background(new BackgroundImage(new Image("/images/back.jpg"), null, null, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

            ImageView imageView = new ImageView();
            imageView.setFitHeight(500);
            imageView.setFitHeight(700);
            try {
                if (serviceResult != null) {
                    imageView.setImage(ImageUtils.bufferImage2Image(ImageIO.read(new File(serviceResult.getListPath().get(value)))));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            vBox1.getChildren().add(imageView);
            return vBox1;
        });
        pagination.setVisible(false);
        stackPane.getChildren().add(pagination);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridView<Image> addGridView() {
        GridView<Image> myGrid = new GridView<>(list);
        myGrid.setCellFactory(gridView -> {
            ImageGridCell imageGridCell = new ImageGridCell();
            imageGridCell.setOnMouseClicked(event -> {
                pagination.setVisible(true);
            });
            return imageGridCell;
        });
        myGrid.setCellHeight(200);
        myGrid.setCellWidth(140);
        myGrid.setHorizontalCellSpacing(5);
        myGrid.setVerticalCellSpacing(5);
        myGrid.getItems().addListener((ListChangeListener<Image>) c -> {
        });
        return myGrid;
    }

    private void addSearchPane(BorderPane root, Stage primaryStage) {
        VBox vBox = new VBox(20);
        vBox.setPrefWidth(180);
        vBox.setAlignment(Pos.TOP_CENTER);
        ImageView imageView = new ImageView("/images/search-1.png");
        imageView.setFitHeight(150);
        imageView.setFitWidth(150);
        Button searchBtn = new Button("Search Now");
        searchBtn.setPrefWidth(150);
        vBox.getChildren().add(imageView);
        vBox.getChildren().add(searchBtn);
        vBox.getChildren().add(new Separator());
        root.setLeft(vBox);

        searchBtn.setOnAction(searchEventEventHandler(primaryStage, imageView));

    }

    private EventHandler<ActionEvent> searchEventEventHandler(Stage primaryStage, ImageView imageView) {
        return event -> {
            final FileChooser fileChooser = new FileChooser();
            configureFileChooser(fileChooser);
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    pagination.setVisible(false);
                    srcImage = ImageIO.read(file);
                    imageView.setImage(ImageUtils.bufferImage2Image(srcImage));
                    list.remove(0, list.size());
                    hBox.setVisible(true);
                    hBox.getChildren().add(progressBar);
                    SearchImagesService searchImagesService = new SearchImagesService(new FileInputStream(file));
                    searchImagesService.start();
                    progressBar.progressProperty().bind(searchImagesService.progressProperty());
                    progressLabel.textProperty().bind(searchImagesService.messageProperty());
                    searchImagesService.setOnSucceeded(result -> {
                        serviceResult = searchImagesService.getValue();

                        Platform.runLater(() -> {
                            list.addAll(serviceResult.getListImage());
                            hBox.getChildren().removeAll(progressBar);
                            hBox.setVisible(false);
                            pagination.setPageCount(serviceResult.getListImage().size());
                        });
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private MenuBar setMenuBar(Stage primaryStage) {
        final FileChooser fileChooser = new FileChooser();
        MenuBar menuBar = new MenuBar();

        Menu menu = new Menu("整理图片");
        MenuItem fileMenu = new MenuItem("选择图片文件");
        MenuItem directMenu = new MenuItem("选择图片文件夹");
        MenuItem exitMenu = new MenuItem("退出");
        exitMenu.setOnAction(event -> System.exit(0));
        fileMenu.setOnAction(event -> {
            configureFileChooser(fileChooser);
            List<File> list = fileChooser.showOpenMultipleDialog(primaryStage);
            if (list != null) {
                list.forEach((file) -> {
                    CalculateFingerPrinterService cpService = new CalculateFingerPrinterService(file);
                    cpService.start();
                    cpService.setOnSucceeded(result -> {
                        System.out.println("结果：--" + cpService.getValue());
                    });
                });


            }
        });
        directMenu.setOnAction(event -> {
            final DirectoryChooser directoryChooser = new DirectoryChooser();
            final File selectedDirectory =
                    directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                hBox.setVisible(true);
                SaveImageService saveImagesService = new SaveImageService(selectedDirectory.getAbsolutePath());
                saveImagesService.start();
                progressLabel.textProperty().bind(saveImagesService.messageProperty());

                saveImagesService.setOnSucceeded(e -> {
                    System.out.println(saveImagesService.getMessage());
                    hBox.setVisible(false);
                });
            }
        });
        menu.getItems().add(fileMenu);
        menu.getItems().add(directMenu);
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(exitMenu);


        menuBar.getMenus().add(menu);

        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        return menuBar;
    }


    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
