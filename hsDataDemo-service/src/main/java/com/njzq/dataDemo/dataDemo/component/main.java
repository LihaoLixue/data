package com.njzq.dataDemo.dataDemo.component;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * @author LH
 * @description:
 * @date 2022-02-23 18:05
 */
public class main {
    static String str = "{instance_id=00120220223180223759633863, params_info=, job_key=202111180069, job_name=, exec_state=12, start_time=1645610543759, end_time={}, error_no=-99, error_info=, ip=173.2.52.12, mac=e4-43-4b-df-af-50, job_type=Process, system_id=001, step_exe_monitor_info_list=[]}";

    public static void main(String[] args) {
        String params = "C_M02-2020-IB_njzq_11111111-11111111南京证券股份有限公司";
//        String methodUrl = "http://173.2.32.10:8089/Artemis/rest/crk/subjectcredit/OrgIndexDQResource";
        String result = null;
//        CloseableHttpClient httpClient = HttpClientBuilder.createe().build();


        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {

            List<NameValuePair> list = new LinkedList<>();
            String[] split = params.split("-");
            String type = split[0];
            String date = split[1];
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss");
            String time =  dateformat.format(System.currentTimeMillis());
            String company_no = "11780";
            String name = split[3];
            String crop_code = split[2];
            String methodUrl = "http://173.2.32.10:8089/Artemis/rest/crk/subjectcredit/OrgIndexDQResource?type="+type+"&date="+date+"&time="+time+"&company_no="+company_no+"&name="+name+"&crop_code="+crop_code;
            URIBuilder uri = new URIBuilder(methodUrl);
            HttpGet httpGet = new HttpGet(methodUrl);
            System.out.println(httpGet.toString());
            httpGet.setHeader(new BasicHeader("Accept", "application/json,text/plain, */*;charset=utf-8"));
            System.out.println(httpGet);
            //设置请求状态参数
//            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(3000).setSocketTimeout(3000).setConnectTimeout(3000).build();
//            httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}


