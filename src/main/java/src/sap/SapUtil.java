package src.sap;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import src.cron.Task;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by caoshibin on 2018/1/26.
 */
@Component
//@PropertySource({"sap.properties"})
public class SapUtil {
    @Value("${sap.jco.client.ashost}")
    private String ashost;
    @Value("${sap.jco.client.sysnr}")
    private String sysnr;
    @Value("${TJ.sap.jco.client.client}")
    private String sap_tj_client;
    @Value("${DL.sap.jco.client.client}")
    private String sap_dl_client;
    @Value("${TJ.sap.jco.client.user}")
    private String sap_tj_user;
    @Value("${DL.sap.jco.client.user}")
    private String sap_dl_user;
    @Value("${TJ.sap.jco.client.passwd}")
    private String sap_tj_passwd;
    @Value("${DL.sap.jco.client.passwd}")
    private String sap_dl_passwd;
    @Value("${sap.jco.client.lang}")
    private String lang;
    @Value("${sap.abap_as}")
    private String ABAP_AS;
    private Properties sap_tj_connect_Properties = new Properties();
    private Properties sap_dl_connect_Properties = new Properties();
    private JCoDestination tj_jCoDestination;
    private JCoDestination dl_jCoDestination;
    private final static Logger logger = LoggerFactory.getLogger(Task.class);
    private void createDataFile(String name, String suffix, Properties properties) {
        File cfg = new File(name + "." + suffix);
        try {
            FileOutputStream fos = new FileOutputStream(cfg, false);
            properties.store(fos, "for tests only !");
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create the destination file " + cfg.getName(), e);
        }
    }

    public JCoDestination getTJJCoDestination() throws JCoException {
        logger.info("get TJ JCoDestination");
        if (tj_jCoDestination == null) {
            createDataFile(ABAP_AS, "jcoDestination", sap_tj_connect_Properties);
            tj_jCoDestination = JCoDestinationManager.getDestination(ABAP_AS);
//            tj_jCoDestination = connectWithoutPool("TJ");
        }
        return tj_jCoDestination;
    }
    public JCoDestination getDLJCoDestination() throws JCoException {
        logger.info("get DL JCoDestination");
        if (dl_jCoDestination == null) {
            createDataFile(ABAP_AS, "jcoDestination", sap_dl_connect_Properties);
            dl_jCoDestination = JCoDestinationManager.getDestination(ABAP_AS);
//            dl_jCoDestination = connectWithoutPool("DL");
        }
        return dl_jCoDestination;
    }

    @PostConstruct
    private void init() {
        logger.info("Sap init.");
        if(sap_tj_connect_Properties==null){
            sap_tj_connect_Properties=new Properties();
        }
        sap_tj_connect_Properties.setProperty(DestinationDataProvider.JCO_ASHOST, "10.167.8.155");
        sap_tj_connect_Properties.setProperty(DestinationDataProvider.JCO_ASHOST, ashost);
        sap_tj_connect_Properties.setProperty(DestinationDataProvider.JCO_SYSNR, sysnr);
        sap_tj_connect_Properties.setProperty(DestinationDataProvider.JCO_CLIENT, sap_tj_client);
        sap_tj_connect_Properties.setProperty(DestinationDataProvider.JCO_USER, sap_tj_user);
        sap_tj_connect_Properties.setProperty(DestinationDataProvider.JCO_PASSWD, sap_tj_passwd);
        sap_tj_connect_Properties.setProperty(DestinationDataProvider.JCO_LANG, lang);
        if(sap_dl_connect_Properties==null){
            sap_dl_connect_Properties=new Properties();
        }
        sap_dl_connect_Properties.setProperty(DestinationDataProvider.JCO_ASHOST, "10.167.8.155");
        sap_dl_connect_Properties.setProperty(DestinationDataProvider.JCO_ASHOST, ashost);
        sap_dl_connect_Properties.setProperty(DestinationDataProvider.JCO_SYSNR, sysnr);
        sap_dl_connect_Properties.setProperty(DestinationDataProvider.JCO_CLIENT, sap_dl_client);
        sap_dl_connect_Properties.setProperty(DestinationDataProvider.JCO_USER, sap_dl_user);
        sap_dl_connect_Properties.setProperty(DestinationDataProvider.JCO_PASSWD, sap_dl_passwd);
        sap_dl_connect_Properties.setProperty(DestinationDataProvider.JCO_LANG, lang);
//        createDataFile(ABAP_AS, "jcoDestination", connectProperties);
    }

    private JCoDestination connectWithoutPool(String keyWord) throws JCoException {
        JCoDestination destination=null;
        if(keyWord==null)
            return  null;
        if(keyWord.equals("TJ")){
            createDataFile(ABAP_AS, "jcoDestination", sap_tj_connect_Properties);
            destination = JCoDestinationManager.getDestination(ABAP_AS);
        }
        if(keyWord.equals("DL")){
            createDataFile(ABAP_AS, "jcoDestination", sap_dl_connect_Properties);
            destination = JCoDestinationManager.getDestination(ABAP_AS);
        }
        logger.info("Attributes:");
        logger.info(destination.getAttributes().toString());
        return destination;
    }
    public JCoFunction getFcuntion(JCoDestination destination,String functionName) throws JCoException {
        return destination.getRepository().getFunction(functionName);
    }
    public List<String> getTableNames(JCoFunction function){
        List<String> tableNames=new ArrayList<String>();
        if(function.getTableParameterList().getMetaData().getFieldCount()>0){
            for(int index =0 ; index <function.getTableParameterList().getMetaData().getFieldCount();index++){
                System.out.println("tableName:"+function.getTableParameterList().getMetaData().getName(index));
                tableNames.add(function.getTableParameterList().getMetaData().getName(index).trim());

            }
        }
        return tableNames;
    }
    public JCoTable getTable(JCoFunction function,String tableName){
       return function.getTableParameterList().getTable(tableName);
    }
    public List<String> getColumns(JCoTable table){
        List<String> columnNames=new ArrayList<String>();
        for(int index=0;index<table.getMetaData().getFieldCount();index++){
            System.out.println( "column:"+table.getMetaData().getName(index));
            columnNames.add(table.getMetaData().getName(index));
        }
        return columnNames;
    }
}
