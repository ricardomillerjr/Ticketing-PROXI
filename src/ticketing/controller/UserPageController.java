/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ticketing.controller;

import com.github.daytron.simpledialogfx.data.DialogResponse;
import com.github.daytron.simpledialogfx.data.DialogStyle;
import com.github.daytron.simpledialogfx.data.HeaderColorStyle;
import com.github.daytron.simpledialogfx.dialog.Dialog;
import com.github.daytron.simpledialogfx.dialog.DialogType;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.awt.HeadlessException;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ticketing.ConnectionManager;
import ticketing.pacd_user;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import org.apache.commons.collections.FastHashMap;
import org.controlsfx.control.Notifications;
import ticketing.CounterManager;
import ticketing.Counterr;

/**
 * FXML Controller class
 *
 * @author millerr
 */
public class UserPageController implements Initializable {

    private double xOffset = 0;
    private double yOffset = 0;
    private final pacd_user puser = new pacd_user();

    @FXML
    public AnchorPane inner_archpane;
    @FXML
    private FontAwesomeIconView powerbtn_ico;
    @FXML
    private Label lblpacduser;
    @FXML
    private Label lbldate;
    @FXML
    private AnchorPane root_pane;
    @FXML
    private Label lhioname;
    ObservableList<ModelTable> oblist = FXCollections.observableArrayList();
    @FXML
    private TableView<ModelTable> table;
    @FXML
    private TableColumn<ModelTable, String> ticketno;
    @FXML
    private TableColumn<ModelTable, String> type;
    @FXML
    private TableColumn<ModelTable, String> fdate;
    @FXML
    private Label lblsoaddress;

    @FXML
    private void special_OnClick(ActionEvent event) throws JRException {
        load_dd("LMP", puser.getUserid(), "SPECIAL LANE");
    }

    @FXML
    private void universal_onClick(ActionEvent event) throws JRException {
        load_dd("M2", puser.getUserid(), "UNIVERSAL LANE");
    }

    @FXML
    private void express_OnClick(ActionEvent event) throws JRException {
        load_dd("PACD", puser.getUserid(), "EXPRESS LANE");
    }

    @FXML
    private void empsec_OnClick(ActionEvent event) throws IOException, JRException {
        load_dd("ER2", puser.getUserid(), "EMPLOYED SECTOR");
    }

    @FXML
    private void call_spvrtOnClick(ActionEvent event) throws IOException, SQLException {
        String fullname = puser.getFirstname() + " " + puser.getMiddlename() + " " + puser.getLastname();
        Dialog dialog = new Dialog(
                DialogType.INPUT_TEXT,
                DialogStyle.UNDECORATED,
                "title",
                "Issues And Concern in (PACD)\n " + fullname,
                HeaderColorStyle.LINEAR_FADE_RIGHT_BLUEPURPLE,
                "Type Here",
                new SQLException().getNextException());

        dialog.showAndWait();
        if (dialog.getResponse() == DialogResponse.SEND) {
            CallableStatement callableStatement = ConnectionManager.getInstance().getConnection().prepareCall("{call call_supervisor(?,?,?,?,?,?)}", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            callableStatement.setString(1, fullname);
            callableStatement.setString(2, "PACD");
            callableStatement.setString(3, "0");
            callableStatement.setString(4, "PACD");
            callableStatement.setInt(5, 0);
            callableStatement.setString(6, dialog.getTextEntry());
            if (callableStatement.executeUpdate() == 1) {
                Image img = new Image("/ticketing/img/like-flat-128x128.png");
                Notifications notificationBuilder = Notifications.create();
                notificationBuilder.title("Call Supervisor");
                notificationBuilder.text("Submited");
                notificationBuilder.graphic(new ImageView(img));
                notificationBuilder.hideAfter(Duration.seconds(2.0));
                notificationBuilder.position(Pos.BOTTOM_RIGHT);
                notificationBuilder.hideCloseButton();
                notificationBuilder.show();
            }
        }

    }

    @FXML
    private void on_ClickPower(MouseEvent event) {
        if (event.getClickCount() == 1) {
            Stage stage = (Stage) powerbtn_ico.getScene().getWindow();
            stage.close();
            System.exit(0);
        }
    }

    @FXML
    private void onClick_Cashier(MouseEvent event) throws IOException {
        if (event.getClickCount() == 1) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ticketing/fxml/cash_lanes.fxml"));
            AnchorPane pane = loader.load();
            Cash_lanesController cash_lanesController = loader.getController();
            cash_lanesController.setUser(puser.getUserid(), puser.getFirstname(), puser.getMiddlename(), puser.getLastname(), puser.getLane(), lblsoaddress.getText());
            root_pane.getChildren().setAll(pane);
        }
    }

    public void getP(String userid, String FirstName, String Middalename, String LastName) {
        validate_table(userid);
        puser.setUserid(userid);
        puser.setFirstname(FirstName);
        puser.setMiddlename(Middalename);
        puser.setLastname(LastName);
        lblpacduser.setText(FirstName + " " + Middalename + " " + LastName);
    }

    public void validate_table(String userid) {
        try {
            oblist.clear();
            CallableStatement callableStatement = ConnectionManager.getInstance().getConnection().prepareCall("{call count_ticket(?)}",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            callableStatement.setString(1, userid);
            ResultSet resultSet = callableStatement.executeQuery();
            while (resultSet.next()) {
                oblist.add(new ModelTable(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3)));
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        ticketno.setCellValueFactory(new PropertyValueFactory<>("TicketNumber"));
        type.setCellValueFactory(new PropertyValueFactory<>("Type"));
        fdate.setCellValueFactory(new PropertyValueFactory<>("date"));
        table.setItems(oblist);
    }

    protected String getDateNow() {
        SimpleDateFormat SimpleDateFormmatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
        c.setTime(sqlDate);
        c.add(Calendar.DAY_OF_MONTH, 3);
        Date sqlDateCurrent = c.getTime();
        return SimpleDateFormmatter.format(sqlDateCurrent);
    }

    protected String getCurrentTime() {
        SimpleDateFormat SimpleTimeFormatter = new SimpleDateFormat("hh:mm:ss a");
        Time sqlTime = new Time(new java.util.Date().getTime());
        return SimpleTimeFormatter.format(sqlTime);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lbldate.setText(getDateNow());
        try {
            Statement statement = ConnectionManager.getInstance().getConnection().createStatement();
            ResultSet rs = statement.executeQuery("SELECT so_lib.so_name,so_address FROM office_profile INNER JOIN so_lib ON office_profile.so_code = so_lib.so_code AND office_profile.lhio_code = so_lib.procode");
            if (rs.next()) {
                lhioname.setText(rs.getString(1));
                lblsoaddress.setText(rs.getString(2));
                puser.setLane(lhioname.getText());
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @SuppressWarnings({"unchecked", "unchecked", "deprecation"})
    protected void load_dd(String ftable, String puserid, String lane_name) throws JRException {
        try {
            Counterr bean = CounterManager.getNumber(ftable);
            if (bean == null) {
                System.err.println("No Rows Found");
            } else {
                try {
                    FastHashMap parameters = new FastHashMap();
                    parameters.put("logo", this.getClass().getClassLoader().getResource("ticketing/img/logo.png"));
                    parameters.put("queue_number", bean.getCounter());
                    parameters.put("lane_descrip", bean.getDescription());
                    parameters.put("lhioname", lhioname.getText());
                    parameters.put("dateNow", bean.getDate());
                    parameters.put("puserid", puser.getUserid());
                    parameters.put("soaddress", lblsoaddress.getText());

                    System.out.println(bean.getDate());
                    DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();
                    JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.xpath.executer.factory",
                            "net.sf.jasperreports.engine.util.xml.JaxenXPathExecuterFactory");
                    JasperPrint print = JasperFillManager.fillReport("report/ticketrcp5.jasper", parameters, new JREmptyDataSource());
                    if (JasperPrintManager.printReport(print, false)) {
                        CallableStatement callableStatement = ConnectionManager.getInstance().getConnection().prepareCall("{call create_ticket_no(?,?,?)}",
                                ResultSet.TYPE_SCROLL_INSENSITIVE,
                                ResultSet.CONCUR_READ_ONLY);
                        callableStatement.setString(1, bean.getCounter());
                        callableStatement.setString(2, bean.getType());
                        callableStatement.setString(3, puserid);
                        if (callableStatement.executeUpdate() == 1) {
                            Image img = new Image(getClass().getResource("/ticketing/img/poton.png").openStream());
                            Notifications notificationBuilder = Notifications.create()
                                    .title("Printer.....")
                                    .text("Get Your Ticket").darkStyle()
                                    .graphic(new ImageView(img))
                                    .hideAfter(Duration.seconds(1.0))
                                    .position(Pos.CENTER)
                                    .hideCloseButton();
                            notificationBuilder.show();
                        }
                        validate_table(puserid);
                    }
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                    Logger.getLogger(UserPageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException | HeadlessException | IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    protected String Now() {
        SimpleDateFormat SimpleDateFormmatter = new SimpleDateFormat("hh:mm:ss a");
        java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
        return SimpleDateFormmatter.format(sqlDate);
    }

    @FXML
    private void InquiryOnClick(ActionEvent event) {
    }

    @FXML
    private void onClicnkHome(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ticketing/fxml/login.fxml"));
            Parent root = loader.load();
            LoginController appController = loader.getController();
            Scene scene = new Scene(root);
            Stage stage2 = new Stage();
            root.setOnMousePressed((MouseEvent event1) -> {
                xOffset = event1.getSceneX();
                yOffset = event1.getSceneY();
            });
            root.setOnMouseDragged((MouseEvent event1) -> {
                stage2.setX(event1.getScreenX() - xOffset);
                stage2.setY(event1.getScreenY() - yOffset);
            });

            stage2.initStyle(StageStyle.TRANSPARENT);
            stage2.setScene(scene);
            stage2.centerOnScreen();
            stage2.show();

            Stage stage = new Stage();
            stage = (Stage) ((de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView) event.getSource()).getScene().getWindow();
            stage.close();

            if (stage2.isShowing()) {
                Notifications notificationBuilder = Notifications.create()
                        .title("(PACD) User:")
                        .text("Bye").darkStyle()
                        .graphic(new ImageView(new Image("ticketing/img/logo2.png")))
                        .hideAfter(Duration.seconds(2.0))
                        .position(Pos.TOP_LEFT)
                        .hideCloseButton();
                notificationBuilder.show();
            }
        } catch (IOException ex) {
            Logger.getLogger(UserPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
