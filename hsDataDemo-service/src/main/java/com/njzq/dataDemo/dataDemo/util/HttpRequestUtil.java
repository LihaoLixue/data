package com.njzq.dataDemo.dataDemo.util;

//import com.para.osc.framework.common.util.MD5Util;
//import com.para.osc.framework.common.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class HttpRequestUtil {
	public static final String CODE = "code";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String AUTHORIZATION_CODE = "authorization_code";
	public static final String APP_SECRET_KEY = "*******"; // 密钥，向IAM申请


	static class miTM implements javax.net.ssl.TrustManager,
			javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}



	/**
	 * 根据请求的URL是https还是http请求数据
	 *
	 * @param sendUrl
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static String getResult(String sendUrl, String param)
			throws Exception {
		if (sendUrl.startsWith("https")) {
			return getResultByHttps(sendUrl, param);
		}
		return getResultByHttp(sendUrl, param);
	}

	private static String getResultByHttps(String sendUrl, String param)
			throws NoSuchAlgorithmException, KeyManagementException,
			IOException {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		// javax.net.ssl.SSLContext sc =
		// javax.net.ssl.SSLContext.getInstance("SSL");
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
				.getInstance("SSLv3");

		sc.init(null, trustAllCerts, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc
				.getSocketFactory());

		HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		};

		HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);

		OutputStream out = null;
		BufferedReader reader = null;
		String result = "";
		URL url = null;
		HttpsURLConnection conn = null;
		try {
			url = new URL(sendUrl);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Cookie","token=");
			// 必须设置false，否则会自动redirect到重定向后的地址
			conn.setInstanceFollowRedirects(false);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(15000);
			conn.connect();
			out = conn.getOutputStream();
			out.write(param.getBytes());
			InputStream input = conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			String line = "";
			StringBuffer sb = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			result = sb.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
			if (out != null) {
				out.close();
			}
			if (reader != null) {
				reader.close();
			}
		}
		return result;
	}

	private static String getResultByHttp(String sendUrl, String param)
			throws NoSuchAlgorithmException, KeyManagementException,
			IOException {

		HttpURLConnection conn = null;
		OutputStream out = null;
		BufferedReader reader = null;
		String result = "";
		try {

			URL url = new URL(sendUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(150000);
			conn.connect();
			out = conn.getOutputStream();
			out.write(param.getBytes());
			out.flush();
			out.close();
			InputStream input = conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			String line;
			StringBuffer sb = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
			out.close();
			conn.disconnect();
		}

		return result;
	}

	public static String getSign(Map<String, String> params,String secret)
    {
        String sign="";
        StringBuilder sb = new StringBuilder();
        //排序
        Set<String> keyset=params.keySet();
        TreeSet<String> sortSet=new TreeSet<String>();
        sortSet.addAll(keyset);
        Iterator<String> it=sortSet.iterator();
        //加密字符串
        while(it.hasNext())
        {
            String key=it.next();
            String value=params.get(key);
            sb.append(key).append(value);
        }
        sb.append("appkey").append(secret);
        try {
//           sign=  MD5Util.md5s(sb.toString()).toUpperCase();
			sign=  MD5Util.encodeByMD5(sb.toString()).toUpperCase();
        } catch (Exception e) {
        }
        return sign;
    }

	/**
	 * 组装获取用户api参数，含签名
	 * @param client_ID
	 * @param client_secret
	 * @param token
	 * @return
	 */
	public static String getUserParam(String client_ID, String client_secret,String token) {
		String nonce_str = radomString();
		String appkey=HttpRequestUtil.APP_SECRET_KEY;
		long timestamp= System.currentTimeMillis();

		Map<String, String> params = new HashMap<String, String>();

		params.put("client_id", client_ID);
		params.put("client_secret", client_secret);
		params.put("nonce_str", nonce_str);
		params.put("oauth_timestamp", String.valueOf(timestamp));
		if(token.contains("access_token=")){
			 int strStartIndex = token.indexOf("access_token=");
		     int strEndIndex = token.indexOf("&expires");
		     String access_token = token.substring(strStartIndex, strEndIndex).substring("access_token=".length());
		 	params.put("access_token", access_token);

		}else{
			params.put("access_token", token);
		}
		String sign = getSign(params, appkey+client_secret);
		StringBuffer tokenParam = new StringBuffer();
		for (String key : params.keySet()) {
			if(tokenParam.length()==0){
				tokenParam.append(key).append("=").append(params.get(key));
			}else{
				tokenParam.append("&").append(key).append("=").append(params.get(key));
			}

		 }
		tokenParam.append("&sign=").append(sign);
		return tokenParam.toString();
	}

	/**
	 * 组装检查心跳API参数含签名
	 * @param client_ID
	 * @param client_secret
	 * @return
	 */
	public static String getIAMServiceParam(String client_ID, String client_secret) {
		String nonce_str = radomString();
		String appkey=HttpRequestUtil.APP_SECRET_KEY;
		long timestamp= System.currentTimeMillis();

		Map<String, String> params = new HashMap<String, String>();

		params.put("client_id", client_ID);
		params.put("client_secret", client_secret);
		params.put("nonce_str", nonce_str);
		params.put("oauth_timestamp", String.valueOf(timestamp));
		String sign = getSign(params, appkey+client_secret);
		StringBuffer tokenParam = new StringBuffer();
		for (String key : params.keySet()) {
			if(tokenParam.length()==0){
				tokenParam.append(key).append("=").append(params.get(key));
			}else{
				tokenParam.append("&").append(key).append("=").append(params.get(key));
			}

		 }
		tokenParam.append("&sign=").append(sign);
		return tokenParam.toString();
	}

	private static String radomString(){
		  String result="";
		        for(int i=0;i<10;i++){
		            int intVal=(int)(Math.random()*26+97);
		            result=result+(char)intVal;
		        }
		        return result;
		 }

	/**
	 * 组装获取token api参数 含签名
	 * @param client_ID
	 * @param client_secret
	 * @param redirect_uri
	 * @param code
	 * @return
	 */
	public static String getAccessTokenParam(String client_ID, String client_secret, String redirect_uri, String code) {
		String nonce_str = radomString();
		String appkey=HttpRequestUtil.APP_SECRET_KEY;
		long timestamp= System.currentTimeMillis();

		Map<String, String> params = new HashMap<String, String>();

		params.put("client_id", client_ID);
		params.put("client_secret", client_secret);
		params.put("nonce_str", nonce_str);
		params.put("oauth_timestamp", String.valueOf(timestamp));
		params.put("code", code);
		params.put("redirect_uri",redirect_uri);
		params.put("grant_type", "authorization_code");
		String sign = getSign(params, appkey+client_secret);
		StringBuffer tokenParam = new StringBuffer();
		for (String key : params.keySet()) {
			if(tokenParam.length()==0){
				tokenParam.append(key).append("=").append(params.get(key));
			}else{
				tokenParam.append("&").append(key).append("=").append(params.get(key));
			}

		 }
		tokenParam.append("&sign=").append(sign);
		return tokenParam.toString();
	}



	  public static Map<String, String>  getCommonAuthParamClient(String client_ID, String client_secret, String nonce_str, String timestamp) {

			Map<String, String> params = new HashMap<String, String>();
			params.put("client_id", client_ID);
			params.put("client_secret", client_secret);
			params.put("nonce_str", nonce_str);
			params.put("timestamp",timestamp);
			return params;
		}


		public static String postGeneralUrl(String generalUrl, String contentType,
				String params, String encoding,String hander,String user_token) throws Exception {
			URL url = new URL(generalUrl);
			// 打开和URL之间的连接
			HttpURLConnection connection = null;
			String result = "";
			OutputStream out = null;
			BufferedReader in = null;
			try {
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				// 设置通用的请求属性
				connection.setRequestProperty("Content-Type", contentType);
				connection.setRequestProperty("Connection", "Keep-Alive");
				connection.setRequestProperty("Cookie","token="+user_token);
				connection.setUseCaches(false);
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setConnectTimeout(5000);
				if (hander != null) {
					if(hander.equals("1")){
						connection.setRequestProperty("SOAPAction", "");
					}else{
						connection.setRequestProperty("Authorization", hander);
					}

				}

				// 得到请求的输出流对象
			    out = connection.getOutputStream();
				out.write(params.getBytes(encoding));
				out.flush();
				out.close();

				// 建立实际的连接
				connection.connect();
				in = new BufferedReader(new InputStreamReader(connection
						.getInputStream(), encoding));
				result = "";
				String getLine;
				while ((getLine = in.readLine()) != null) {
					result += getLine;
				}
				in.close();
			} catch (Exception e) {
				return e.getMessage();
			}finally {
				if (connection != null) {
					connection.disconnect();
				}
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			}
			return result;
		}
		public static void getGeneralUrlByHttps(String params){
			String methodUrl ="http://173.2.32.10:8089/Artemis/rest/crk/subjectcredit/OrgIndexDQResource";
					//?type=C_M02&date=2020&time=2021-12-12 14:28:20&company_no=11780&name=万科企业股份有限公司&crop_code=1213131331
			HttpURLConnection connection = null;
			BufferedReader reader = null;
			String line = null;
			String[] split = params.split("-");
			if(split.length!=4){
				return;
			}
			String type = split[0];
			String date = split[1];
			String time=String.valueOf(System.currentTimeMillis());
			String company_no = "11780";
			String name = split[3];
			String crop_code = split[2];
			try {
				URL url = new URL(methodUrl + "?type="+type+"&date="+date+"&time="+time+"&company_no="+company_no+"&name="+name+"&crop_code="+crop_code);
				connection = (HttpURLConnection) url.openConnection();// 根据URL生成HttpURLConnection
				connection.setRequestMethod("GET");// 默认GET请求
				connection.connect();// 建立TCP连接
				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));// 发送http请求
					StringBuilder result = new StringBuilder();
					// 循环读取流
					while ((line = reader.readLine()) != null) {
						result.append(line).append(System.getProperty("line.separator"));// "\n"
					}
					System.out.println(result.toString());
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				connection.disconnect();
			}
		}

		public static String postGeneralUrlByHttps(String generalUrl, String contentType,
				String params, String encoding,String hander,String user_token) throws Exception {
			URL url = new URL(generalUrl);
			javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
			javax.net.ssl.TrustManager tm = new miTM();
			trustAllCerts[0] = tm;
			// javax.net.ssl.SSLContext sc =
			// javax.net.ssl.SSLContext.getInstance("SSL");
			javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
					.getInstance("SSLv3");

			sc.init(null, trustAllCerts, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sc
					.getSocketFactory());

			HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			};

			HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);

			OutputStream out = null;
			BufferedReader reader = null;
			String result = "";
			HttpsURLConnection conn = null;
			try {
				conn = (HttpsURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-type",contentType);
				// 必须设置false，否则会自动redirect到重定向后的地址
				conn.setInstanceFollowRedirects(false);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setRequestProperty("Charset", encoding);
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Cookie","token="+user_token);
				conn.setConnectTimeout(5000);
				if (hander != null) {
					if(hander.equals("1")){
						conn.setRequestProperty("SOAPAction", "");
					}else{
						conn.setRequestProperty("Authorization", hander);
					}

				}
				conn.connect();
				out =  conn.getOutputStream();
				out.write(params.getBytes("UTF-8"));
				InputStream input = conn.getInputStream();
				reader = new BufferedReader(new InputStreamReader(input, encoding));
				String line = "";
				StringBuffer sb = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				result = sb.toString();
			} catch (Exception e) {
				return e.getMessage();
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
				if (out != null) {
					out.close();
				}
				if (reader != null) {
					reader.close();
				}
			}
			return result;
		}

		public static String post(String requestUrl, String params,String user_token)throws Exception {
			String url = requestUrl;
			if(url.startsWith("https")){
				return HttpRequestUtil.postGeneralUrlByHttps(url, "application/json", params, "UTF-8",null,user_token);

			}
			return HttpRequestUtil.postGeneralUrl(url, "application/json", params, "UTF-8",null,user_token);
		}

	public static void main(String[] args){
		//key,由IAM提供，应用方保存，该 值与单点登录得key不一致
		String key="EEF3D161ADTRF900FEF15331AB8428ER3DB71F5845C036533555F99YU8";
		//client_id, client_secret由IAM提供，应用方保存
		String client_id="6ca40SUju3";
		String client_secret="362ead09-238b-4a6a-a853-b88b24739cee";
		String nonce_str = radomString();
		String timestamp= String.valueOf(System.currentTimeMillis());

		//1. 生成参与签名得参数
		Map<String, String>  params = HttpRequestUtil.getCommonAuthParamClient(client_id, client_secret, nonce_str, timestamp);
		//2. 生成签名
		String sign = HttpRequestUtil.getSign(params, key+client_secret);
		//3. 组装调用接口得参数
		params.put("sign", sign);
		params.put("pageSize", "1000");//最大不超过1000
		params.put("pageIndex", "1");
		JSONObject jb = JSONObject.fromObject(params);
		System.out.println("param:"+jb.toString());
		//4. 调用接口
		String queryAccUrl ="http://paraview.paraesc.com/iamquery/service/api/v1/accountSync/queryIncrePagingAccountInfo";
		String reuslt = "";
		try {
//			reuslt = HttpRequestUtil.post(queryAccUrl, jb.toString());
		} catch (Exception e) {
		}
		System.out.println("result:"+reuslt);

		//获取账号信息后需要回调IAM接口
		//1. 解析获取后得数据
		JSONObject jb_ret = JSONObject.fromObject(reuslt);
		String acc_list = jb_ret.getString("result");
//		if(StringUtils.isEmpty(acc_list)){
//			return;
//		}
		if (acc_list == null || acc_list.length() == 0) {
			return;
		}
		JSONArray json_arr = JSONArray.fromObject(acc_list);
		JSONArray ja_call_arr = new JSONArray();
		for(int i=0; i<json_arr.size(); i++){
		    JSONObject obj = (JSONObject) json_arr.get(i);
		    String requestLogId = obj.getString("requestLogId");
//		    if(StringUtils.isEmpty(requestLogId)){
//		    	continue;
//		    }
			if (requestLogId == null || requestLogId.length() == 0) {
				continue;
			}
		    JSONObject jb_call = new JSONObject();
		    jb_call.put("requestLogId", requestLogId);
		    ja_call_arr.element(jb_call);

		}
		//2. 组装签名参数
		String timestamp_call= String.valueOf(System.currentTimeMillis());
		Map<String, String>  params_call = HttpRequestUtil.getCommonAuthParamClient(client_id, client_secret, nonce_str, timestamp_call);

		//3. 生成签名
		String sign_call = HttpRequestUtil.getSign(params_call, key+client_secret);
		params_call.put("sign", sign_call);
		params_call.put("requestlog_str", ja_call_arr.toString());
		JSONObject jb_call = JSONObject.fromObject(params_call);
		System.out.println("param:"+jb_call.toString());
		String callAccUrl ="http://paraview.paraesc.com/iamquery/service/api/v1/accountSync/updateIamAccountStatus";
		String reuslt_call = "";
		//4. 调用接口
		try {
//			reuslt_call = HttpRequestUtil.post(callAccUrl, jb_call.toString());
		} catch (Exception e) {
		}
		System.out.println("reuslt_call:"+reuslt_call);


	}

}
