/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ticketing;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author melrose
 */
public class main_app extends Application {

    private double xOffset = 0;
    private double yOffset = 0;
    private static final Connection connection = ConnectionManager.getInstance().getConnection();

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ticketing/fxml/login.fxml"));
        root.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);// set "firstPage" route
        stage.show();
    }

    static void validate() {
        try (Statement statement = connection.createStatement()) {
            statement.addBatch("insert into tblog (select * from ttable where substr(fpdate,1,10)!=substr(now(),1,10))");
            statement.addBatch("delete from ttable where substr(fpdate,1,10)!=substr(now(),1,10)");
            int[] executeBatch = statement.executeBatch();
            for (int count = 0; count < executeBatch.length; count++) {
               System.out.println("[" + count + "] :---: Execute Query Batch [" + executeBatch[count] + "]");
            }
        } catch (SQLException sqlex) {
            System.out.println(sqlex.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        validate();
        launch(args);
    }

}
