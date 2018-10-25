/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ticketing.controller;

import com.github.daytron.simpledialogfx.data.DialogResponse;
import com.github.daytron.simpledialogfx.data.DialogStyle;
import com.github.daytron.simpledialogfx.dialog.Dialog;
import com.github.daytron.simpledialogfx.dialog.DialogType;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.textfield.TextFields;
import ticketing.ConnectionManager;
import ticketing.pacd_user;

public class LoginController implements Initializable {

	private double xOffset = 0;
	private double yOffset = 0;
	private final Connection connection = ConnectionManager.getInstance().getConnection();
	@FXML
	private JFXTextField username;
	@FXML
	private JFXPasswordField password;
	@FXML
	private JFXButton cancel;
	private String full_name = new String();

	@FXML
	public void login_OnClick(ActionEvent event) throws IOException {
		try {
			CallableStatement callableStatement = connection.prepareCall("{call emplookup(?)}",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			callableStatement.setString(1, username.getText());
			ResultSet resultSet = callableStatement.executeQuery();
			if (resultSet.next()) {
				if (resultSet.getString("pname").equals(getMD5(String.valueOf(password.getText())))) {
					pacd_user puser = new pacd_user();
					puser.setUserid(resultSet.getString("userid"));
					puser.setFirstname(resultSet.getString("fname"));
					puser.setMiddlename(resultSet.getString("mname"));
					puser.setLastname(resultSet.getString("lname"));

					full_name = puser.getLastname()
							+ "," + puser.getFirstname()
							+ " " + puser.getMiddlename();

					try {
						FXMLLoader loader = new FXMLLoader();
						loader.setLocation(getClass().getResource("/ticketing/fxml/UserPage.fxml"));
						Parent home_page_parent = loader.load();
						UserPageController userpage = loader.getController();
						userpage.getP(puser.getUserid(), puser.getFirstname(), puser.getMiddlename(), puser.getLastname());
						Scene home_page_scene = new Scene(home_page_parent);
						Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

						home_page_parent.setOnMousePressed((MouseEvent mouseEvent) -> {
							xOffset = mouseEvent.getSceneX();
							yOffset = mouseEvent.getSceneY();
						});
						home_page_parent.setOnMouseDragged((MouseEvent mouseEvent) -> {
							app_stage.setX(mouseEvent.getScreenX() - xOffset);
							app_stage.setY(mouseEvent.getScreenY() - yOffset);
						});
//						app_stage.hide(); //optional
						app_stage.setScene(home_page_scene);
						app_stage.setFullScreen(true);
						app_stage.show();
						if (app_stage.isShowing()) {
							Notifications notificationBuilder = Notifications.create()
									.title("(PACD) User:")
									.text(puser.getUserid() + "\n" + full_name)
									.graphic(new ImageView(new Image("ticketing/img/logo2.png")))
									.hideAfter(Duration.seconds(2.0))
									.position(Pos.TOP_LEFT)
									.hideCloseButton();
							notificationBuilder.show();

						}

					} catch (IOException ex) {
						Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
					}
				} else {//Here Additional Code For Error Handling. For password Field
				}
			} else {//Here Additional Code For Error Handling. For ResultSet Object
			}
		} catch (SQLException ex) {
			Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@FXML
	public void cancel_OnClick(ActionEvent event) {
		Stage stage = (Stage) cancel.getScene().getWindow();
		stage.close();
		System.exit(0);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT userid from acct order by userid DESC");
			List<String> results = new ArrayList<>();
			while (rs.next()) {
				results.add(rs.getString(1));
			}
			TextFields.bindAutoCompletion(username, results);
			NumberValidator numvalidator = new NumberValidator();
			RequiredFieldValidator validator = new RequiredFieldValidator();
			username.getValidators().add(numvalidator);
			password.getValidators().add(validator);
			numvalidator.setMessage("Numeric Only Not Letter.");
			validator.setMessage("Input Not Given.");
			username.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (!newValue) {
						username.validate();
						Image icn = new Image("ticketing/img/Status-security-low-icon.png");
						numvalidator.setIcon(new ImageView(icn));
					}
				}
			});
			password.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (!newValue) {
						password.validate();
						Image icn = new Image("ticketing/img/Status-security-low-icon.png");
						validator.setIcon(new ImageView(icn));
					}
				}
			});
		} catch (SQLException ex) {
			Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static String getMD5(String input) {
		String generatedPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(input.getBytes());
			byte[] bytes = md.digest();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;
	}

}
