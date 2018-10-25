/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ticketing.controller;

import java.awt.HeadlessException;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
import ticketing.ConnectionManager;
import ticketing.CounterManager;
import ticketing.Counterr;
import ticketing.pacd_user;

/**
 * FXML Controller class
 *
 * @author millerr
 */
public class Cash_lanesController implements Initializable {

    private final pacd_user puser = new pacd_user();
   
    @FXML
    private AnchorPane main_root_anchorPane;
    @FXML
    private Label lhioname;
    @FXML
    private Label soaddress;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    public void priority_pyOnclick(ActionEvent event) throws JRException {
        load_dd("CASHIERP", puser.getUserid(), "PAYMENT PRIORITY");
    }

    @FXML
    public void pp_OnClick(ActionEvent event) throws JRException {
        load_dd("CASHIER", puser.getUserid(), "PAYMENT REGULAR");
    }

    @FXML
    public void OnClick_Back(MouseEvent event) throws IOException {
        if (event.getClickCount() == 1) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ticketing/fxml/UserPage.fxml"));
            Parent pane = loader.load();
            UserPageController userpage = loader.getController();
            userpage.getP(puser.getUserid(), puser.getFirstname(), puser.getMiddlename(), puser.getLastname());
            userpage.validate_table(puser.getUserid());
            main_root_anchorPane.getChildren().add(pane);
        }
    }

    public void setUser(String userid, String FirstName, String Middalename, String LastName, String lanename, String soaddress) {
        puser.setUserid(userid);
        puser.setFirstname(FirstName);
        puser.setMiddlename(Middalename);
        puser.setLastname(LastName);
        puser.setLane(lanename);
        lhioname.setText(lanename);
        this.soaddress.setText(soaddress);
    }

    @SuppressWarnings({"unchecked", "unchecked", "unchecked", "unchecked"})
    protected void load_dd(String ftable, String puserid, String lane_name) throws JRException {
        try {
            Counterr bean = CounterManager.getNumber(ftable);
            if (bean == null) {
                System.err.println("No Rows Found");
            } else {
                try {
                    FastHashMap parameters = new FastHashMap();
                    parameters.put("logo", getClass().getClassLoader().getResource("ticketing/img/logo.png"));
                    parameters.put("queue_number", bean.getCounter());
                    parameters.put("lane_descrip", bean.getDescription());
                    parameters.put("lhioname", lhioname.getText());
                    parameters.put("dateNow", bean.getDate());
                    parameters.put("puserid", puser.getUserid());
                    parameters.put("soaddress", soaddress.getText());
                    
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
                            Image img = new Image("/ticketing/img/poton.png");
                            Notifications notificationBuilder = Notifications.create()
                                    .title("Printing.....")
                                    .text("Get Your Ticket").darkStyle()
                                    .graphic(new ImageView(img))
                                    .hideAfter(Duration.seconds(1.0))
                                    .position(Pos.CENTER)
                                    .hideCloseButton();
                            notificationBuilder.show();
                        }

                    }
                } catch (SQLException | HeadlessException ex) {
                    System.err.println(ex.getLocalizedMessage());
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

}
