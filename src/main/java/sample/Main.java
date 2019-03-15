package sample;

import javafx.application.Application;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import java.util.ArrayList;
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

    @Override
    public void start(final Stage stage) throws Exception {
        searchImage(stage);

    }


    public FlowPane addFlowPane() {
        FlowPane flow = new FlowPane();
        flow.setPadding(new Insets(5, 0, 5, 0));
        flow.setVgap(4);
        flow.setHgap(4);
//        flow.setPrefWrapLength(170); // 预设FlowPane的宽度，使其能够显示两列

        ImageView pages[] = new ImageView[5];
        for (int i = 0; i < 5; i++) {
            pages[i] = new ImageView(
                    new Image(getClass().getResourceAsStream("/images/" + imageNames[i])));
            pages[i].setFitWidth(100);
            pages[i].setFitHeight(100);
            int finalI = i;
            pages[i].setOnMouseClicked(event -> System.out.println(finalI));
            flow.getChildren().add(pages[i]);
        }

        return flow;
    }

    private void searchImage(Stage primaryStage) {
        primaryStage.setTitle("ImageSearch");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 800);
        root.setBackground(new Background(new BackgroundImage(new Image("/images/back.jpg"), null, null, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        VBox vBox = new VBox();
        MenuBar menuBar = setMenuBar(primaryStage);
        vBox.getChildren().add(menuBar);

        hBox.setPrefWidth(700);
        hBox.getChildren().add(progressLabel);
        hBox.setAlignment(Pos.CENTER);
        hBox.setVisible(false);

        vBox.getChildren().add(hBox);
        root.setTop(vBox);

        addSearchPane(root, primaryStage);
        if (srcImage != null) {
            addGridView(root, primaryStage);
        }


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addGridView(BorderPane root, Stage primaryStage) {
        GridView<Image> myGrid = new GridView<>(list);
        myGrid.setCellFactory(gridView -> {
            ImageGridCell imageGridCell = new ImageGridCell();
            imageGridCell.setOnMouseClicked(event -> System.out.println(imageGridCell.getIndex()));
            return imageGridCell;
        });
        myGrid.setCellHeight(140);
        myGrid.setCellWidth(140);
        myGrid.setHorizontalCellSpacing(5);
        myGrid.setVerticalCellSpacing(5);
        myGrid.getItems().addListener((ListChangeListener<Image>) c -> {
        });
        root.setCenter(myGrid);

    }

    private void addSearchPane(BorderPane root, Stage primaryStage) {
        VBox vBox = new VBox(20);
        vBox.setAlignment(Pos.CENTER);
        ImageView imageView = new ImageView("/images/search-1.png");
        imageView.setFitHeight(150);
        imageView.setFitWidth(150);
        ImageView srcImageView = new ImageView();
        srcImageView.setFitWidth(150);
        srcImageView.setFitHeight(150);
        Text text = new Text("Begin Search My Image");
        text.setFont(Font.font(18));
        imageView.setOnMouseClicked(event -> {
            final FileChooser fileChooser = new FileChooser();
            configureFileChooser(fileChooser);
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                System.out.println(file.getPath());
                try {
                    srcImage = ImageIO.read(file);
                    srcImageView.setImage(ImageUtils.bufferImage2Image(srcImage));

                    hBox.setVisible(true);
                    SearchImagesService searchImagesService = new SearchImagesService(new FileInputStream(file));
                    searchImagesService.start();
                    progressLabel.textProperty().bind(searchImagesService.messageProperty());
                    searchImagesService.setOnSucceeded(result -> {
                        List<String> listImages = searchImagesService.getValue();
                        new Thread(() -> {
                            List<Image> listImage = new ArrayList<>(listImages.size());
                            listImages.forEach(ele -> {
                                try {
                                    Image tempImage = ImageUtils.bufferImage2Image(ImageIO.read(new File(ele)));
                                    listImage.add(tempImage);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            Platform.runLater(() -> {
                                list.remove(0, list.size());
                                list.addAll(listImage);
                                hBox.setVisible(false);
                            });
                        }
                        ).start();
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImage(new Image("/images/search.jpg"));
                VBox vBox2 = new VBox(5, srcImageView, imageView);
                vBox2.setPadding(new Insets(40, 10, 10, 10));
                root.setLeft(vBox2);
                root.setCenter(null);
                addGridView(root, primaryStage);
            }
        });
        vBox.getChildren().addAll(imageView, text);
        root.setCenter(vBox);

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
                    try {
                        CalculateFingerPrinterService cpService = new CalculateFingerPrinterService(new FileInputStream(file));
                        cpService.start();
                        cpService.setOnSucceeded(result -> {
                            System.out.println("结果：--" + cpService.getValue());
                            String printerFinger = cpService.getValue();
                            ImageFinger imageEle = new ImageFinger(file.getName(), file.getName(), printerFinger, file.getAbsolutePath(), new Date(System.currentTimeMillis()), 0);
                            SQLiteJDBC.insertData(imageEle);
                        });

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

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


    private void OpenPicture(Stage stage) {
        stage.setTitle("File Chooser Sample");

        final FileChooser fileChooser = new FileChooser();

        final Button openButton = new Button("Open a Picture...");
        final Button openMultipleButton = new Button("Open Pictures...");

        openButton.setOnAction(
                (final ActionEvent e) -> {
                    configureFileChooser(fileChooser);
                    File file = fileChooser.showOpenDialog(stage);
                    if (file != null) {
                        openFile(file);
                    }
                });
        openMultipleButton.setOnAction(
                (final ActionEvent e) -> {
                    configureFileChooser(fileChooser);
                    List<File> list =
                            fileChooser.showOpenMultipleDialog(stage);
                    if (list != null) {
                        list.stream().forEach((file) -> {
                            openFile(file);
                        });
                    }
                });

        final GridPane inputGridPane = new GridPane();

        GridPane.setConstraints(openButton, 0, 0);
        GridPane.setConstraints(openMultipleButton, 1, 0);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(openButton, openMultipleButton);

        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));

        stage.setScene(new Scene(rootGroup));
        stage.show();
    }


    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
    }

    private void openFile(File file) {
        EventQueue.invokeLater(() -> {
            try {
                System.out.println(file.getAbsolutePath());
                desktop.open(file);
                Logger.getLogger("path:" + file.getAbsolutePath());
            } catch (IOException ex) {
                Logger.getLogger(Main.
                        class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        });
    }


    private void login(Stage primaryStage) {
        //createBtn(primaryStage);
        primaryStage.setTitle("ImageSearch Welcome");
        primaryStage.show();

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(grid, 800, 275);
        primaryStage.setScene(scene);
        Text sceneTitle = new Text("welcome");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        Label userName = new Label("UserName:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);
    }

    private void createBtn(Stage primaryStage) {
        Button btn = new Button();
        btn.setText("SayHello");
        btn.setOnAction(event -> System.out.println("Heeeeeeeeellllllllooooo"));
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
