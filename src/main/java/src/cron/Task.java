package src.cron;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import src.model.Department;
import src.model.User;
import src.sap.SapUtil;
import src.weixin.WeixinUtil;

import javax.annotation.Resource;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by caoshibin on 2018/1/26.
 */
@Component
//@PropertySource(value = "classpath:sap.properties",encoding = "utf-8")
public class Task {
    @Resource
    private SapUtil util;
    @Resource
    private WeixinUtil weixinUtil;
    @Value("${Dalian}")
    private String Dalian;
    @Value("${Tianjin}")
    private String Tianjin;
    @Value("${BUFunction}")
    private String buFunction;
    @Value("${userFunction}")
    private String userFunction;
    @Value("${DEFID}")
    private int DEFID;
    @Value("${csvSeparator}")
    private String csvSeparator;
    @Value("${csvSemicolon}")
    private String csvSemicolon;
    @Value("${excelPath}")
    private String excelPath;
    private String csvBUParams;
    private String csvUserParams;
    private String chineseMan;
    private String chineseWoman;
    private Map<Integer, Integer> indexs;
    private boolean flag = true;
    private final static Logger logger = LoggerFactory.getLogger(Task.class);
//    @Scheduled(cron = "${cron.syncDate}")
    public void cronJob() {
        if(flag){
            flag=false;
            deleteALL(Dalian);
//            weix
//            syncUsers(Tianjin);
        }

//        syncBUByMedia(Dalian);
//        syncUsers(Dalian);
//        syncBUByMedia(Tianjin);
//        syncUsers(Tianjin);
    }
//    @Scheduled(cron = "${cron.generateExcel}")
    public String generateExcel(){
            String result="SUCCESS";
//      try{
//          genetateExcel(buFunction,Dalian);
//          genetateExcel(userFunction,Dalian);
//      }catch (Exception e){
//          logger.error(e.toString());
//          result="FAILD";
//      }
//             genetateExcel(buFunction,Dalian);
//             genetateExcel(userFunction,Dalian);
            return result;

    }
    public void genetateExcel(String function,String TJorDL){
        List<List> results=getDateFromSap(function,TJorDL);
        DateFormat format=new SimpleDateFormat("YYYY-MM-dd_HH_mm_SS");
        logger.info("genetate Excel");
        dateToExcel((List<List<String>>)results.get(1),(List<String>)results.get(0),TJorDL+"_"+function+"_"+format.format(new Date()));
    }
    private void dateToExcel( List<List<String>> params,List<String> names,String name){
        logger.info("filename:"+name);
        logger.info("excelPath::"+excelPath);
        logger.info("params::"+params.size());
        logger.info("names::"+names.size());
        try{
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("data");
            wb.setSheetName(0, "data");
            Row headerRow = sheet.createRow(0);
            for(int j=0;j<names.size();j++){
                Cell cell = headerRow.createCell(j);
                cell.setCellValue(names.get(j));
                cell=null;
            }

            Row row =null;
            Cell cell = null;
            for(int index=0;index<params.size();index++){
                row = sheet.createRow(index+1);
                List<String> param=params.get(index);
                for(int index2=0;index2<param.size();index2++){
                    cell = row.createCell(index2);
                    cell.setCellValue(param.get(index2));
                }
            }

            File f = new File(excelPath);
            if (!f.exists()) {
                f.mkdirs();
            }

            String fileName = excelPath + File.separator + name + ".xlsx";

            FileOutputStream os; os = new FileOutputStream(fileName);
            logger.info("save file " + fileName);
            wb.write(os);
            os.close();
        }catch (Exception e){
            logger.info(e.toString());
            logger.error(e.toString());
        }
    }
    private List<List> getDateFromSap(String functionName,String TJorDL){
        if(TJorDL==null){
            return  null;
        }
        List<List> result=new ArrayList<List>();
        try {
            JCoDestination destination=null;
            if(TJorDL.equals("TJ")){
                destination = util.getTJJCoDestination();
            }
            if(TJorDL.equals("DL")){
                destination = util.getDLJCoDestination();
            }
            if(destination==null){
                return null;
            }
            logger.info(destination.toString());
            JCoFunction function = util.getFcuntion(destination, functionName);
            logger.info("function:" + function);
            function.execute(destination);
            JCoTable table = util.getTable(function, util.getTableNames(function).get(0));
//            logger.info("table"+table);
            List<String> names = util.getColumns(table);
            result.add(names);
            List<List<String>> resultList=new ArrayList<List<String>>();
            table.firstRow();
            for (int index = 0; index < table.getNumRows(); index++, table.nextRow()) {
                List<String> list=new ArrayList<String>();
                for(String key:names){
                    list.add(table.getString(key));
                }
                resultList.add(list);
            }
            result.add(resultList);
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return result;
    }
    public static List<String> readFile(String address, String encoding) {

        List<String> list = new ArrayList<String>();
        try {
            // String encoding="GBK";
            File file = new File(address);
            if (file.isFile() && file.exists()) { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file));// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    list.add(lineTxt);

                }
                read.close();
            } else {
                // file.createNewFile();
                System.out.println(file.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return list;

    }
    /**
     * 将 Userinfo API返回的json转为User对象
     * @param jsonObject Userinfo API返回的JSON
     * @param bumap 微信的BU信息
     * @return
     */
    private User syncUser(JSONObject jsonObject, Map<String, Integer> bumap) {
        User user = new User();
        user.setUserid(jsonObject.getString("staffCode"));
        user.setName(jsonObject.getString("printCNName"));
        user.setEnglish_name(jsonObject.getString("printENName").replace(",", ""));
        user.setMobile(jsonObject.getString("mobile").replace("+86", "").trim().replaceAll(" ", ""));
        user.setPosition(jsonObject.getString("title"));
        String directDepartment="";
        try{
            directDepartment=jsonObject.getString("directDepartment").trim().toLowerCase();
        }catch (Exception e){

        }
        if(directDepartment==null||directDepartment.equals("")||directDepartment.equals("null")){
            try{
                directDepartment=jsonObject.getString("department").trim().toLowerCase();
            }catch (Exception e){

            }
        }
        if(directDepartment==null||directDepartment.equals("")||directDepartment.equals("null")){
            try{
                directDepartment=jsonObject.getString("division").trim().toLowerCase();
            }catch (Exception e){
            }
        }
        if(directDepartment==null||directDepartment.equals("")||directDepartment.equals("null")){
            logger.error("This person dont have a departmnet.:"+jsonObject.toString());
            return null;
        }
        user.setDepartment(bumap.get(directDepartment));
        if (jsonObject.getString("titleGender") == null || jsonObject.getString("titleGender").trim().equals("")) {

        } else {
            if (jsonObject.getString("titleGender").equals("Mr")) {
                user.setGender("1");
            } else {
                user.setGender("2");
            }
        }
        if( jsonObject.getString("isEnable").equals("null")){
            user.setEnable(1);
        }else {
            user.setEnable(jsonObject.getInt("isEnable"));
        }

        user.setEmail(jsonObject.getString("email"));
        user.setTelephone(jsonObject.getString("telephone").replace("+86", "").trim().replaceAll(" ", ""));
//        System.out.println(JSONObject.fromObject(user));
        return user;
    }

    /**
     * 向微信同步User，逐条同步。
     */
    private void syncUsers(String keyWord) {
        List<User> users=getUserListFromAPON(keyWord);
        String token = weixinUtil.getToken(keyWord);
        for (int index = 0; index < users.size(); index++) {
            try {
                logger.warn((users.get(index).toString()))                                                                                                                                                                                                                                                                                                                                                             ;
                if (users.get(index).isEnable() == 1) {
                    JSONObject object=weixinUtil.addUser(token, users.get(index));
                    if(object.getInt("errcode")==60102){
                        object=weixinUtil.updateUser(token, users.get(index));
                    }
                    logger.warn("Add Or Update:"+object.toString());

                }
                if (users.get(index).isEnable() == 0) {
                    logger.warn("delete:"+weixinUtil.deleteUser(token, users.get(index).getUserid()).toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(users.get(index).toString());
                logger.error(e.getMessage());
//                logger.error();

            }
        }


    }
    private List<String> usersToCsvStr(List<User> users){
        List<String> list=new ArrayList<String>();
        logger.info(csvUserParams);
        list.add(csvUserParams);
        StringBuffer stringBuffer=new StringBuffer();
        for(int index=0;index<users.size();index++){
            User user=users.get(index);
            stringBuffer.append(user.getName().replace(csvSeparator,""));
            stringBuffer.append(csvSeparator);
            stringBuffer.append(user.getUserid());
            stringBuffer.append(csvSeparator);
            stringBuffer.append(user.getMobile().replace(csvSeparator,""));
            stringBuffer.append(csvSeparator);
            stringBuffer.append(user.getEmail());
            stringBuffer.append(csvSeparator);
            for(int a:user.getDepartment()){
                stringBuffer.append(a);
                stringBuffer.append(csvSemicolon);
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            stringBuffer.append(csvSeparator);
            stringBuffer.append(user.getPosition());
            stringBuffer.append(csvSeparator);
            //1=man 2=woman
            if(user.getGender().equals("1")){
                stringBuffer.append(chineseMan);
            }else{
                stringBuffer.append(chineseWoman);
            }
            stringBuffer.append(csvSeparator);
            if(user.isIsleader()){
                stringBuffer.append("1");
            }else{
                stringBuffer.append("0");
            }
            stringBuffer.append(csvSeparator);
            stringBuffer.append(user.getOrder());
            stringBuffer.append(csvSeparator);
            stringBuffer.append(user.getEnglish_name());
            stringBuffer.append(csvSeparator);
            stringBuffer.append(user.getTelephone());
            stringBuffer.append(csvSeparator);
            stringBuffer.append(user.isEnable());
            list.add(stringBuffer.toString());
            stringBuffer = new StringBuffer();
        }
        return  list;
    }
    /**
     * 向微信同步User信息，用CSV的方式增量同步。
     */
    private void syncUserByMedia(String keyWord){
        logger.info("syncUserByMedia start:");
//        List<User> users=getUserListFromAPON();
        List<String> list=readFile("csv\\user_example.csv","");
        csvUserParams=list.get(0);
        chineseMan=list.get(1);
        chineseWoman=list.get(2);
//        List<String> strList=usersToCsvStr(users);
        DateFormat format=new SimpleDateFormat("YYYY-MM-dd");
//        File file=save("csv//user"+format.format(new Date())+".csv",strList);
        File file=new File("csv//user2018-02-22.csv");
        String token = weixinUtil.getToken(keyWord);
        logger.warn("token:" + token);
        JSONObject object=weixinUtil.uploadByFile(token,"file",file,true);
        String media_id=object.getString("media_id");
        JSONObject object2=weixinUtil.synvUserByMedia(media_id,token);
        logger.warn(object2.toString());
        String jobid=object2.getString("jobid");
        JSONObject jobResult=weixinUtil.getJOB(jobid,token);
        logger.warn(jobResult.toString());
        logger.info("syncUserByMedia end:");
    }

    /**
     * 从Userinfo API获取User集合
     * @return
     */
    private List<User> getUserListFromAPON(String keyWord){
        List<User> userList=new ArrayList<User>();
        logger.warn("get userinfo from apon start");
        logger.warn(new Date().toString());
        JSONArray users = weixinUtil.getUserFromAPON(keyWord);
        logger.warn("get userinfo from apon end");
        logger.warn(new Date().toString());
        logger.warn("get bu from weixin");
        Map<String, Integer> bumap = new HashMap<String, Integer>();
        JSONObject buDate = weixinUtil.getALLBU(weixinUtil.getToken(keyWord));
        JSONArray buarray = buDate.getJSONArray("department");
        for (int index = 0; index < buarray.size(); index++) {
            JSONObject object = buarray.getJSONObject(index);
            bumap.put(object.getString("name").trim().toLowerCase(), object.getInt("id"));
        }
        for (int index = 0; index < users.size(); index++) {
            try {
                User user =  syncUser(users.getJSONObject(index),bumap);
                if (user==null){
                    continue;
                }
                userList.add(user);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(users.getJSONObject(index).toString());
                logger.error(e.getMessage());

            }
        }
        return  userList;
    }

    /**
     * 向微信同步BU信息，逐条同步
     */
    private  void syncBU(String keyWord){
        logger.warn("syncBU start:");
        List<Department> departmentList=getBUFromSap(keyWord);
        String token = weixinUtil.getToken(keyWord);
        logger.warn("token:" + token);
        for (Department department : departmentList) {
            logger.warn(department.toString());
            logger.warn(weixinUtil.addDepartment(token, department).toString());
        }
    }

    /**
     * 向微信同步BU信息，用CSV的方式全量更新。
     */
    private void syncBUByMedia(String TJorDL){
        logger.info( System.getProperty("file.encoding"));
        List<String> list=readFile("csv\\bu_example.csv","");
        csvBUParams=list.get(0);
        List<Department> departmentList=getBUFromSap(TJorDL);
        List<String> strList=departmentsToCsvStr(departmentList);
        DateFormat format=new SimpleDateFormat("YYYY-MM-dd");
        File file=save("csv//bu"+"_"+TJorDL+"_"+format.format(new Date())+"_"+".csv",strList);
        String token = weixinUtil.getToken(TJorDL);
        logger.warn("token:" + token);
        JSONObject object=weixinUtil.uploadByFile(token,"file",file,true);
        logger.warn("media:"+object.toString());
        String media_id=object.getString("media_id");
        JSONObject object2=weixinUtil.synvBUByMedia(media_id,token);
        logger.warn("job:"+object2.toString());
        String jobid=object2.getString("jobid");
        JSONObject jobResult=weixinUtil.getJOB(jobid,token);
        logger.warn(jobResult.toString());
        logger.warn("syncBUByMedia end:");
    }

    /**
     * 将department对象转化CSV样式的字符串
     * @param departmentList
     * @return
     */
    private List<String> departmentsToCsvStr(List<Department> departmentList){
        List<String> list=new ArrayList<String>();
        logger.info(csvBUParams);
        list.add(csvBUParams);
        StringBuffer stringBuffer=new StringBuffer();
        for(int index=0;index<departmentList.size();index++){
            stringBuffer.append(departmentList.get(index).getName().replace(csvSeparator,""));
            stringBuffer.append(csvSeparator);
            stringBuffer.append(departmentList.get(index).getId());
            stringBuffer.append(csvSeparator);
            stringBuffer.append(departmentList.get(index).getParentid());
            stringBuffer.append(csvSeparator);
            stringBuffer.append(departmentList.get(index).getOrder());
            list.add(stringBuffer.toString());
            stringBuffer = new StringBuffer();
        }
        return  list;
    }

    /**
     * 保存成文件
     * @param address
     * @param list
     * @return
     */
    public File save(String address,List<String> list) {
        logger.info("create csv :"+address);
        System.out.println(address);
        File file = new File(address);
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        PrintWriter writer;
        try {
            writer = new PrintWriter(new File(address));
            for(int index=0;index<list.size();index++){
                writer.print(list.get(index));
                writer.print("\r\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();// TODO: handle exception
            System.err.println("保存文件出错");
        }
        return  file;
    }

    /**
     * 从sap获得BU
     * @return
     */
    private List<Department> getBUFromSap(String TJorDL) {
        List<Department> departmentList=null;
        try {
            JCoDestination destination=null;
            if(TJorDL.equals("TJ")){
                destination = util.getTJJCoDestination();
            }
            if(TJorDL.equals("DL")){
                destination = util.getDLJCoDestination();
            }
            System.out.println(destination);
            JCoFunction function = util.getFcuntion(destination, buFunction);
            System.out.println("function:" + function);
            function.execute(destination);
            JCoTable table = util.getTable(function, util.getTableNames(function).get(0));
            List<String> names = util.getColumns(table);
            Map<String, Department> map = new HashMap<String, Department>();
            table.firstRow();
            for (int index = 0; index < table.getNumRows(); index++, table.nextRow()) {
                if (table.getString("BUNAME").trim().equals("") || (table.getString("HRBPCODE").trim().equals("") && table.getString("LCLEBPCODE").trim().equals(""))) {
                    continue;
                }
                Department department = new Department(table.getString("BUNAME").trim(), "", table.getString("PARENTBU").trim(), 0, table.getString("BUCODE"));
                department.setId(Long.valueOf(department.getId()).toString());
                map.put(department.getName(), department);
            }
            gradeBU(map);
//            for (String key : map.keySet()) {
//                setID(map.get(key));
//            }
            for (String key : map.keySet()) {
                Department department = map.get(key);
                if (map.containsKey(department.getParentName())) {
                    department.setParentid(map.get(department.getParentName()).getId());
                } else {
                    department.setParentid(DEFID + "");
                }
            }

            departmentList = new ArrayList<Department>(map.values());
            int VOLKSWAGEN=0;
            for(int index=0;index<departmentList.size();index++){
                if(departmentList.get(index).getId().equals("1")){
                    VOLKSWAGEN=index;
                    break;
                }
            }
            departmentList.remove(VOLKSWAGEN);
            Collections.sort(departmentList);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return departmentList;
    }

    /**
     * 分级部门
     * @param map
     */
    private void gradeBU(Map<String, Department> map) {
        for (String key : map.keySet()) {
            graderBU(map, map.get(key));
        }
    }

    /**
     * 分级部门
     * @param map
     * @param department
     * @return
     */
    private int graderBU(Map<String, Department> map, Department department) {
        if (department.getParentName() != null && !department.getParentName().equals("") && map.containsKey(department.getParentName())) {
            department.setOrder(graderBU(map, map.get(department.getParentName())) + 1);
            return department.getOrder();
        } else {
            department.setOrder(1);
            return 1;
        }
    }

    /**
     * 为department设置ID
     * @param department
     */
    private void setID(Department department) {
        if (indexs == null) {
            indexs = new HashMap<Integer, Integer>();
        }
        int order = department.getOrder();
        if (indexs.containsKey(order)) {
            department.setId(order + String.format("%03d", indexs.get(order) + 1));
            indexs.put(order, indexs.get(order) + 1);
        } else {
            department.setId(order + String.format("%03d", 1));
            indexs.put(order, 1);
        }
    }
    private void deleteALL(String TJorDL){
        String token=weixinUtil.getToken(TJorDL);
        JSONObject bus_object=weixinUtil.getALLBU(token);
        JSONArray bus=bus_object.getJSONArray("department");
        System.out.println(bus);
        int size=bus.size();
        for(int index=size-1;index>=1;index--){
            JSONObject bu=bus.getJSONObject(index);
            if("10".equals(bu.getString("id").trim())){
                continue;
            }
//            if()
            JSONArray users=weixinUtil.getUserByDepartment(bu.getString("id"),token).getJSONArray("userlist");
            int users_size=users.size();
            if(users_size==0){
                System.out.println(bu.toString());
//                System.out.println(weixinUtil.deleteDepartment(token,bu.getString("id")));
                continue;
            }
            for(int user_index=0;user_index<users_size;user_index++){
                System.out.println(user_index);
                JSONObject user=users.getJSONObject(user_index);
                System.out.println(user.getString("userid")+weixinUtil.deleteUser(token,user.getString("userid")));
            }
            System.out.println(bu.toString());
//            System.out.println(weixinUtil.deleteDepartment(token,bu.getString("id")));
        }
        System.out.println("end");
    }
}

