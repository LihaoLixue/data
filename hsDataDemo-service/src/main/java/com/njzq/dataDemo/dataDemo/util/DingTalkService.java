package com.njzq.dataDemo.dataDemo.util;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * @author LH
 * @description:
 * @date 2021-11-22 14:58
 */
@Slf4j
public class DingTalkService {
//    static Logger logger = Logger.getLogger(DingTalkService.class);

    //文本消息
    private static final String TEXT = "text";
    private static final String LINK = "link";
    private static final String MARKDOWN = "markdown";
    private static final String ACTION_CARD = "actionCard";
    private static final String FEED_CARD = "feedCard";

    public static String getAccessToken() throws Exception {
        String AppKey = "dingjht5yto6txlhej7h";
        String AppSecret = "onn5bPZ95U_149s6ebyv4hWzyhnECFSPjZGAfI-6LiAJPUsMzCEuCtHdIT4iykpC";
//        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken?appkey=" + AppKey + "?appsecret=" + AppSecret);
//        OapiGettokenRequest request = new OapiGettokenRequest();
//
//        request.setAppkey(AppKey);
//        request.setAppsecret(AppSecret);
//        request.setHttpMethod("GET");
//        OapiGettokenResponse response = client.execute(request);
//        String accessToken = response.getAccessToken();
        Properties prop = System.getProperties();
        // 设置http访问要使用的代理服务器的地址
        prop.setProperty("http.proxyHost", "10.254.255.55");
        prop.setProperty("http.proxyPort", "3128");
        // 对https也开启代理
        System.setProperty("https.proxyHost", "10.254.255.55");
        System.setProperty("https.proxyPort", "3128");
        //设置请求访问的地址
        URL url = new URL("https://oapi.dingtalk.com/gettoken?appkey=" + AppKey + "&appsecret=" + AppSecret);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setConnectTimeout(60000);
        conn.setReadTimeout(60000);
        // 设置文件类型:
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        // 设置接收类型否则返回415错误
        //conn.setRequestProperty("accept","*/*")此处为暴力方法设置接受所有类型，以此来防范返回415;
        conn.setRequestProperty("accept", "application/json");
        conn.connect();
        int HttpResult = conn.getResponseCode();
        String accessToken = null;
        JSONObject jsonObject = null;
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "utf-8"));
            String line = null;
            StringBuffer sb = new StringBuffer("");
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
                jsonObject = JSONObject.parseObject(line);
            }
//            System.out.println(sb);
            br.close();

            accessToken = (String) jsonObject.get("access_token");
            //System.out.println(""+sb.toString());

        } else {
            System.out.println(conn.getResponseMessage());
        }


        return accessToken;
    }
    public static void assignXX_only_dd(String accessToken, String user_id, String cotent) {
//        String accessToken = getAccessToken();
        Properties prop = System.getProperties();
        // 设置http访问要使用的代理服务器的地址
        prop.setProperty("http.proxyHost", "10.254.255.55");
        prop.setProperty("http.proxyPort", "3128");
        // 对https也开启代理
        System.setProperty("https.proxyHost", "10.254.255.55");
        System.setProperty("https.proxyPort", "3128");
        //设置请求访问的地址
        HttpURLConnection conn = null;
        try {
            URL url = new URL("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2?access_token=" + accessToken);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            // 设置文件类型:
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            // 设置接收类型否则返回415错误
            //conn.setRequestProperty("accept","*/*")此处为暴力方法设置接受所有类型，以此来防范返回415;
            conn.setRequestProperty("accept", "application/json");
            conn.connect();
            /**
             *
             *    msg.setMsgtype("markdown");
             * //            msg.setMarkdown(new OapiMessageCorpconversationAsyncsendV2Request.Markdown());
             * //            msg.getMarkdown().setText(message);
             * //            msg.getMarkdown().setTitle("【南京证券-信用风险监控】");
             * */
            JSONObject jsonParam = new JSONObject();
            JSONObject jsonParam1 = new JSONObject();
            JSONObject jsonParam3 = new JSONObject();

//        jsonParam1.put("title", "【南京证券-信用风险监控】");
//        jsonParam1.put("single_title", "查看详情");
//        jsonParam1.put("single_url", "");
//        jsonParam1.put("markdown", cotent);
//        jsonParam3.put("msgtype", "action_card");
//        jsonParam3.put("action_card", jsonParam1);
            jsonParam1.put("title", "【南京证券-信用风险监控】");
            jsonParam1.put("text", cotent);
            jsonParam3.put("markdown", jsonParam1);
            jsonParam3.put("msgtype", "markdown");


            jsonParam.put("agent_id", 1390330698L);
            jsonParam.put("userid_list", user_id);
            jsonParam.put("msg", jsonParam3);

            String jsonStr = JSONObject.toJSONString(jsonParam);
//            logger.info(jsonStr);
            DataOutputStream printout;
            DataInputStream input;

            printout = new DataOutputStream(conn.getOutputStream());
            printout.write(jsonStr.getBytes());
            printout.flush();
            printout.close();

            int HttpResult = conn.getResponseCode();
            System.out.println(HttpResult);
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        conn.getInputStream(), "utf-8"));
                String line = null;
                StringBuffer sb = new StringBuffer("");
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
//                    logger.info(sb);
                }
                br.close();
            } else {
//                logger.info(conn.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
