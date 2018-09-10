package src.weixin;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import src.model.Department;
import src.model.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by caoshibin on 2018/1/26.
 */
@Component
//@PropertySource({"weixin.properties"})
public class WeixinUtil {
    @Value("${TJcompanyid}")
    private String companyID_TJ;
    @Value("${TJsecret}")
    private String secret_TJ;
    @Value("${TJgetUserFromAPONUrl}")
    private String getUserFromAPONUrl_TJ;
    @Value("${DLcompanyid}")
    private String companyID_DL;
    @Value("${DLsecret}")
    private String secret_DL;
    @Value("${DLgetUserFromAPONUrl}")
    private String getUserFromAPONUrl_DL;
    @Value("${getTokenUrl}")
    private String getTokenUrl;
    @Value("${addBUUrl}")
    private String addBUUrl;
    @Value("${encode}")
    private String encode;
    @Value("${tokenKey}")
    private String tokenKey;
    @Value("${secretKey}")
    private String secretKey;
    @Value("${companyKey}")
    private String companyKey;
    @Value("${deleteBUUrl}")
    private String deleteBUUrl;
    @Value("${addUserUrl}")
    private String addUserUrl;
    @Value("${updateUserUrl}")
    private String updateUserUrl;
    @Value("${deleteUserUrl}")
    private String deleteUserUrl;
    @Value("${updateBUUrl}")
    private String updateBUUrl;
    @Value("${upload_wechat_url}")
    private String upload_wechat_url;
    @Value("${getBUUrl}")
    private String getBUUrl;
    @Value("${synvBUByCSVUrl}")
    private String synvBUByCSVUrl;
    @Value("${synvUserByCSVUrl}")
    private String synvUserByCSVUrl;
    @Value("${getJobUrl}")
    private String getJobUrl;
    @Value("${isProxy}")
    private boolean isProxy;
    @Value("${ProxyHttpUrl}")
    private String proxyHttpUrl;
    @Value("${ProxyHttpPort}")
    private int proxyHttpPort;
    @Value("${getUserByDepartmentUrl}")
    private String getUserByDepartmentUrl;

    private final Logger  logger= LoggerFactory.getLogger(WeixinUtil.class);
    public JSONObject getJOB(String jobid,String token){
        String url=getJobUrl.replace("JOBID",jobid).replace(tokenKey,token);
        return JSONObject.fromObject(httpRequest(url,"GET",null,true));
    }
    public JSONObject synvUserByMedia(String media_id,String token){
        Map<String,String> map=new HashMap<String,String>();
        map.put("media_id",media_id);
        System.out.println(JSONObject.fromObject(map));
        return JSONObject.fromObject(httpRequest(synvUserByCSVUrl.replace(tokenKey,token),"POST",JSONObject.fromObject(map).toString(),true));
    }
    public JSONObject synvBUByMedia(String media_id,String token){
        Map<String,String> map=new HashMap<String,String>();
        map.put("media_id",media_id);
        System.out.println(JSONObject.fromObject(map));
        return JSONObject.fromObject(httpRequest(synvBUByCSVUrl.replace(tokenKey,token),"POST",JSONObject.fromObject(map).toString(),true));
    }
    public String getToken(String TJorDL){
        String getToken="";
        if(TJorDL.equals("DL")){
            getToken=getTokenUrl.replace(companyKey,companyID_DL).replace(secretKey,secret_DL);
        }
        if(TJorDL.equals("TJ")){
            getToken=getTokenUrl.replace(companyKey,companyID_TJ).replace(secretKey,secret_TJ);
        }
        JSONObject json=JSONObject.fromObject(httpRequest(getToken,"GET",null,true));
        String token =json.getString(tokenKey.toLowerCase());
        System.out.println("token:"+token);
        logger.info("token:"+token);
        return token;
    }
    public JSONObject addUser(String token , User user){
        String url=addUserUrl.replace(tokenKey,token);
        String params=JSONObject.fromObject(user).toString();
        return JSONObject.fromObject(httpRequest(url,"POST",params,true));
    }
    public JSONObject updateUser(String token , User user){
        return JSONObject.fromObject(httpRequest(updateUserUrl.replace(tokenKey,token),"POST",JSONObject.fromObject(user).toString(),true));
    }
    public JSONObject deleteUser(String token,String id){
        return JSONObject.fromObject(httpRequest(deleteUserUrl.replace(tokenKey,token).replace("USERID",id),"GET",null,true));
    }
    public JSONObject updateDepartment(String token,Department department){
        return JSONObject.fromObject(httpRequest(updateBUUrl.replace(tokenKey,token),"POST",JSONObject.fromObject(department).toString(),true));
    }
    public  JSONObject addDepartment(String token,Department department){
        String url=addBUUrl.replace(tokenKey,token);
        String params=JSONObject.fromObject(department).toString();
        return JSONObject.fromObject(httpRequest(url,"POST",params,true));
    }
    public JSONObject deleteDepartment(String token,String id){
       String url=deleteBUUrl.replace("ID",id).replace(tokenKey,token);
        return JSONObject.fromObject(httpRequest(url,"GET",null,true));
    }
    public JSONArray getUserFromAPON(String TJorDL){
        logger.info("get date from userinfo API");
        String result="";
        if(TJorDL.equals("DL")){
            result=httpRequest(getUserFromAPONUrl_DL,"GET",null,false);
        }
        if(TJorDL.equals("TJ")){
            result=httpRequest(getUserFromAPONUrl_TJ,"GET",null,false);
        }
        return  JSONArray.fromObject(result);
    }
    public  JSONObject getALLBU(String token){
        JSONObject json=JSONObject.fromObject(httpRequest(getBUUrl.replace(tokenKey,token),"GET",null,true));
        return  json;
    }
    /**
     * 发起https请求并获取结果
     *
     * @param request
     *            请求地址
     * @param RequestMethod
     *            请求方式（GET、POST）
     * @param params
     *            提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public String httpRequest(String request, String RequestMethod,
                                         String params,boolean isUsePtoxy) {
        String str = null;
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(request);
            HttpURLConnection connection=null;
            if(isUsePtoxy&&isProxy){
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHttpUrl, proxyHttpPort));
                connection = (HttpURLConnection) url.openConnection(proxy);
            }else{
                connection = (HttpURLConnection) url.openConnection();
            }
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(10000);
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setUseCaches(false);
            connection.setRequestMethod(RequestMethod);
            if (params != null) {
                OutputStream out = connection.getOutputStream();
                out.write(params.getBytes(encode));
                out.close();
            }
            // 流处理
            InputStream input = connection.getInputStream();
            InputStreamReader inputReader = new InputStreamReader(input,
                    "UTF-8");
            BufferedReader reader = new BufferedReader(inputReader);
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            // 关闭连接、释放资源
            reader.close();
            inputReader.close();
            input.close();
            input = null;
            connection.disconnect();
            str=buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }


    /**
     * 本地文件上传文件到微信服务器
     *
     * @param accessToken
     * @param type
     * @param file
     * @return
     */
    public  JSONObject uploadByFile(String accessToken, String type,
                                          File file,boolean isUsePtoxy) {
        JSONObject jsonObject = null;
        String last_wechat_url = upload_wechat_url.replace(tokenKey,
                accessToken).replace("TYPE", type);
        // 定义数据分割符
        String boundary = "----------sunlight";
        try {
            URL url = new URL(last_wechat_url);
            HttpURLConnection conn = null;
            if(isUsePtoxy&&isProxy){
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHttpUrl, proxyHttpPort));
                conn = (HttpURLConnection) url.openConnection(proxy);
            }else{
                conn = (HttpURLConnection) url.openConnection();
            }

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);

            OutputStream out = new DataOutputStream(conn.getOutputStream());
            byte[] end_data = ("\r\n--" + boundary + "--\r\n").getBytes();// 定义最后数据分隔线
            StringBuilder sb = new StringBuilder();
            sb.append("--");
            sb.append(boundary);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"media\";filename=\""
                    + file.getName() + "\"\r\n");
            System.out.println(file.getName());
            sb.append("Content-Type:application/octet-stream\r\n\r\n");

            byte[] data = sb.toString().getBytes();
            out.write(data);
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[1024 * 8];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            out.write("\r\n".getBytes()); // 多个文件时，二个文件之间加入这个
            in.close();
            out.write(end_data);
            out.flush();
            out.close();

            // 定义BufferedReader输入流来读取URL的响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line = null;
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            // 使用json解析
            jsonObject = JSONObject.fromObject(buffer.toString());
            System.out.println(jsonObject);

        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }
        return jsonObject;
    }
    public JSONObject getUserByDepartment(String department_id,String token){
        String url=getUserByDepartmentUrl.replace("DEPARTMENT_ID",department_id).replace(tokenKey,token);
        return JSONObject.fromObject(httpRequest(url,"GET",null,true));
    }

    public String getGetTokenUrl() {
        return getTokenUrl;
    }

    public void setGetTokenUrl(String getTokenUrl) {
        this.getTokenUrl = getTokenUrl;
    }

    public String getAddBUUrl() {
        return addBUUrl;
    }

    public void setAddBUUrl(String addBUUrl) {
        this.addBUUrl = addBUUrl;
    }

    public String getDeleteBUUrl() {
        return deleteBUUrl;
    }

    public void setDeleteBUUrl(String deleteBUUrl) {
        this.deleteBUUrl = deleteBUUrl;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getCompanyKey() {
        return companyKey;
    }

    public String getCompanyID_TJ() {
        return companyID_TJ;
    }

    public void setCompanyID_TJ(String companyID_TJ) {
        this.companyID_TJ = companyID_TJ;
    }

    public String getSecret_TJ() {
        return secret_TJ;
    }

    public void setSecret_TJ(String secret_TJ) {
        this.secret_TJ = secret_TJ;
    }

    public String getGetUserFromAPONUrl_TJ() {
        return getUserFromAPONUrl_TJ;
    }

    public void setGetUserFromAPONUrl_TJ(String getUserFromAPONUrl_TJ) {
        this.getUserFromAPONUrl_TJ = getUserFromAPONUrl_TJ;
    }

    public String getCompanyID_DL() {
        return companyID_DL;
    }

    public void setCompanyID_DL(String companyID_DL) {
        this.companyID_DL = companyID_DL;
    }

    public String getSecret_DL() {
        return secret_DL;
    }

    public void setSecret_DL(String secret_DL) {
        this.secret_DL = secret_DL;
    }

    public String getGetUserFromAPONUrl_DL() {
        return getUserFromAPONUrl_DL;
    }

    public void setGetUserFromAPONUrl_DL(String getUserFromAPONUrl_DL) {
        this.getUserFromAPONUrl_DL = getUserFromAPONUrl_DL;
    }

    public String getAddUserUrl() {
        return addUserUrl;
    }

    public void setAddUserUrl(String addUserUrl) {
        this.addUserUrl = addUserUrl;
    }

    public String getUpdateUserUrl() {
        return updateUserUrl;
    }

    public void setUpdateUserUrl(String updateUserUrl) {
        this.updateUserUrl = updateUserUrl;
    }

    public String getDeleteUserUrl() {
        return deleteUserUrl;
    }

    public void setDeleteUserUrl(String deleteUserUrl) {
        this.deleteUserUrl = deleteUserUrl;
    }

    public String getUpdateBUUrl() {
        return updateBUUrl;
    }

    public void setUpdateBUUrl(String updateBUUrl) {
        this.updateBUUrl = updateBUUrl;
    }

    public String getUpload_wechat_url() {
        return upload_wechat_url;
    }

    public void setUpload_wechat_url(String upload_wechat_url) {
        this.upload_wechat_url = upload_wechat_url;
    }

    public String getGetBUUrl() {
        return getBUUrl;
    }

    public void setGetBUUrl(String getBUUrl) {
        this.getBUUrl = getBUUrl;
    }

    public String getSynvBUByCSVUrl() {
        return synvBUByCSVUrl;
    }

    public void setSynvBUByCSVUrl(String synvBUByCSVUrl) {
        this.synvBUByCSVUrl = synvBUByCSVUrl;
    }

    public String getSynvUserByCSVUrl() {
        return synvUserByCSVUrl;
    }

    public void setSynvUserByCSVUrl(String synvUserByCSVUrl) {
        this.synvUserByCSVUrl = synvUserByCSVUrl;
    }

    public String getGetJobUrl() {
        return getJobUrl;
    }

    public void setGetJobUrl(String getJobUrl) {
        this.getJobUrl = getJobUrl;
    }

    public boolean isProxy() {
        return isProxy;
    }

    public void setProxy(boolean proxy) {
        isProxy = proxy;
    }

    public String getProxyHttpUrl() {
        return proxyHttpUrl;
    }

    public void setProxyHttpUrl(String proxyHttpUrl) {
        this.proxyHttpUrl = proxyHttpUrl;
    }

    public int getProxyHttpPort() {
        return proxyHttpPort;
    }

    public void setProxyHttpPort(int proxyHttpPort) {
        this.proxyHttpPort = proxyHttpPort;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setCompanyKey(String companyKey) {
        this.companyKey = companyKey;
    }
}
