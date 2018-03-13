/**
 * Created by Administrator on 2018/1/7.
 */

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class JavafxTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        StackPane pane = new StackPane();

        GridPane gridPane=new GridPane();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gridPane.add(new Cell(), i, j);
            }
        }

        pane.getChildren().add(gridPane);

        Text text = new Text(20, 10, "Game Over" +
                "");
        pane.getChildren().add(text);
        text.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.ITALIC, 50));
        text.xProperty().bind(pane.widthProperty().divide(2).subtract(10));
        text.yProperty().bind(pane.heightProperty().divide(2).subtract(10)
        );

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            if (text.getText().length() != 0) {
                text.setText("");
            } else {
                text.setText("Game Over");

            }
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();

        Scene scene = new Scene(pane);
        primaryStage.setTitle("test");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
    static class Cell extends Pane {
        public Cell() {
            setStyle("-fx-border-color: green;");
            setPrefSize(1000, 1000);
            javafx.scene.shape.Rectangle rectangle = new Rectangle(0,0,0,0);
            rectangle.setFill(Color.GREEN);
            rectangle.setStroke(Color.BLACK);
            rectangle.widthProperty().bind(widthProperty());
            rectangle.heightProperty().bind(heightProperty());
            getChildren().add(rectangle);
        }
    }
}
