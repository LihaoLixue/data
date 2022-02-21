package com.njzq.dataDemo.dataDemo.component;

import com.hundsun.jrescloud.rpc.annotation.CloudComponent;
import com.njzq.dataDemo.Constant;
import com.njzq.dataDemo.dataDemo.mapper.DataServiceMapper;
import com.njzq.dataDemo.dataDemo.util.Configuration;
import com.njzq.dataDemo.dataDemo.util.HttpRequestUtil;
import com.njzq.dataDemo.dataDemo.util.SendMessageIn;
import com.njzq.dataDemo.service.IDataService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.njzq.dataDemo.dataDemo.util.DingTalkService.*;
import static com.njzq.dataDemo.dataDemo.util.HttpRequestUtil.getGeneralUrlByHttps;

@CloudComponent
public class DataServiceImpl implements IDataService {
    private static final Logger logger = LoggerFactory.getLogger(DataServiceImpl.class);
    private static final String INSTANCEID = "00120211119110345506352379";
//    private static final String JOBKEY = "202010120008";
    private static final int INTERVAL = 60;//任务执行间隔

    @Autowired
    DataServiceMapper dataServiceMapper;

    @Override
    public Object parseJson(String json, String user_token) {
        String return_value="";
        Properties prop = Configuration.getConf("./conf.properties");
        logger.info("json is " + json);
        try {
            JSONObject jsonObject = JSONObject.fromObject(json);
            String type = jsonObject.getString(Constant.TYPE);//模型编码
            String name = jsonObject.getString(Constant.NAME);
            String crop_code = jsonObject.getString(Constant.CROP_CODE);
            String promoter = jsonObject.getString(Constant.PROMOTER);
            String unif_soci_cred_code = jsonObject.getString(Constant.UNIF_SOCI_CRED_CODE);
            String date = jsonObject.getString(Constant.DATE);
            String time = jsonObject.getString(Constant.TIME);
            JSONObject indexDict = jsonObject.getJSONObject(Constant.INDEX_DICT);
            String accessToken = getAccessToken();
            assignXX_only_dd(accessToken, "002968", "内评系统来了一个评级任务，请注意查看");
            if (Constant.CM02.equals(type)) {
                String job_key= prop.getProperty("C_M02_JOBKEY");
                //type为CM02
                String qualFact00 = indexDict.getString(Constant.QUALFACT00);
                //查询主体所属政府是否存在
                String result1 = dataServiceMapper.getX1(qualFact00);
                if (Constant.ONE.equals(result1)) {
                    logger.info("查询城投C_M02主体所属政府不存在.");
                    //未查到，只刷表
                    Iterator<String> indexDicts = indexDict.keys();
                    //刷主体指标明细表
                    while (indexDicts.hasNext()) {
                        String key = indexDicts.next();
                        String value = indexDict.getString(key);
                        Map<String, String> map1 = new HashMap<>();
                        map1.put(Constant.TYPE, type);
                        map1.put(Constant.CROP_CODE, crop_code);
                        map1.put(Constant.INDEX_DICT, key);
                        map1.put(Constant.VALUE, value);
                        map1.put(Constant.DATE, date);
                        map1.put(Constant.DATA_STATE, Constant.ONE);//主体数据状态刷1
                        dataServiceMapper.insertIndicatorDetail(map1);
                    }
                    //刷主体信息表
                    Map<String, String> map2 = new HashMap<>();
                    map2.put(Constant.TYPE, type);
                    map2.put(Constant.CROP_CODE, crop_code);
                    map2.put(Constant.NAME, name);
                    map2.put(Constant.UNIF_SOCI_CRED_CODE, unif_soci_cred_code);
                    map2.put(Constant.PROMOTER, promoter);
                    map2.put(Constant.JOB_ID, "");
                    map2.put(Constant.SJ,time);
                    map2.put(Constant.GOV,qualFact00);
                    map2.put(Constant.DATA_STATE, Constant.ONE);//主体数据状态刷1
                    dataServiceMapper.insertSubjectInfo(map2);
                    //TODO 调用钉钉接口，发送主体所属政府不存在的信息，提醒人工处理的情况
                    String str1 = type + "敞口中主体:" + crop_code + name + "无所属政府,请手动处理-ids";
//                    String accessToken = getAccessToken();
                    assignXX_only_dd(accessToken, "002968", str1);
                    return_value= Constant.NOTFOUND;
                } else {
                    logger.info("type is CM02 查询表一结果为" + result1);
                    //查询表二
                    String result2 = dataServiceMapper.getX2(qualFact00, date);
                    indexDict.put(Constant.QUALFACT00, result2);//填充QualFact00字段--政府分
                    //刷表
                    Iterator<String> indexDicts = indexDict.keys();
                    //刷主体指标明细表
                    while (indexDicts.hasNext()) {
                        String key = indexDicts.next();
                        String value = indexDict.getString(key);
                        Map<String, String> map1 = new HashMap<>();
                        map1.put(Constant.TYPE, type);
                        map1.put(Constant.CROP_CODE, crop_code);
                        map1.put(Constant.INDEX_DICT, key);
                        map1.put(Constant.VALUE, value);
                        map1.put(Constant.DATE, date);
                        map1.put(Constant.DATA_STATE, Constant.ZERO);//主体数据状态刷0
                        dataServiceMapper.insertIndicatorDetail(map1);
                    }
                    //刷主体信息表
                    Map<String, String> map2 = new HashMap<>();
                    map2.put(Constant.TYPE, type);
                    map2.put(Constant.CROP_CODE, crop_code);
                    map2.put(Constant.NAME, name);
                    map2.put(Constant.UNIF_SOCI_CRED_CODE, unif_soci_cred_code);
                    map2.put(Constant.PROMOTER, promoter);
                    map2.put(Constant.JOB_ID, "");
                    map2.put(Constant.SJ,time);
                    map2.put(Constant.GOV,qualFact00);
                    map2.put(Constant.DATA_STATE, Constant.ZERO);//主体数据状态刷0
                    dataServiceMapper.insertSubjectInfo(map2);
                    //执行后续逻辑
                    excuteTask(crop_code,job_key,user_token,prop);
                    return_value= Constant.FOUND;
                }
            } else if(Constant.CM03.equals(type)){
                String job_key= prop.getProperty("C_M03_JOBKEY");
                excuteJudgeAndJob(user_token, prop, type, name, crop_code, promoter, unif_soci_cred_code, date, indexDict,job_key);
                return_value= Constant.FOUND;
            }else if(Constant.CM04.equals(type)){
                String job_key= prop.getProperty("C_M04_JOBKEY");
                excuteJudgeAndJob(user_token, prop, type, name, crop_code, promoter, unif_soci_cred_code, date, indexDict,job_key);
                return_value= Constant.FOUND;
            }
            else if(Constant.CM21.equals(type)){
                String job_key= prop.getProperty("C_M21_JOBKEY");
                excuteJudgeAndJob(user_token, prop, type, name, crop_code, promoter, unif_soci_cred_code, date, indexDict,job_key);
                return_value= Constant.FOUND;
            }//TODO 继续增加其他敞口类型的逻辑
            return return_value;
        } catch (Exception e) {
            logger.error("parse json error " + e);
            return Constant.NOTFOUND;
        }
    }

    private void excuteJudgeAndJob(String user_token, Properties prop, String type, String name, String crop_code, String promoter, String unif_soci_cred_code, String date, JSONObject indexDict,String job_key) {
        logger.info("type is not CM02");
        //type不为CM02
        //刷表
        Iterator<String> indexDicts = indexDict.keys();
        //刷主体指标明细表
        while (indexDicts.hasNext()) {
            String key = indexDicts.next();
            String value = indexDict.getString(key);
            Map<String, String> map1 = new HashMap<>();
            map1.put(Constant.TYPE, type);
            map1.put(Constant.CROP_CODE, crop_code);
            map1.put(Constant.INDEX_DICT, key);
            map1.put(Constant.VALUE, value);
            map1.put(Constant.DATE, date);
            map1.put(Constant.DATA_STATE, Constant.ZERO);//主体数据状态刷0
            dataServiceMapper.insertIndicatorDetail(map1);
        }
        //刷主体信息表
        Map<String, String> map2 = new HashMap<>();
        map2.put(Constant.TYPE, type);
        map2.put(Constant.CROP_CODE, crop_code);
        map2.put(Constant.NAME, name);
        map2.put(Constant.UNIF_SOCI_CRED_CODE, unif_soci_cred_code);
        map2.put(Constant.PROMOTER, promoter);
        map2.put(Constant.JOB_ID, "");
        map2.put(Constant.DATA_STATE, Constant.ZERO);//主体数据状态刷0
        dataServiceMapper.insertSubjectInfo(map2);
        //执行后续逻辑
        excuteTask(crop_code,job_key,user_token,prop);
    }

    private void excuteTask(String crop_code,String job_key,String user_token,Properties prop) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            String taskResult = dataServiceMapper.getX3();
            if (Constant.TASK_SUCCESS.equals(taskResult)) {
                //结束流程调度
                scheduledExecutorService.shutdownNow();
                //执行后续逻辑，获取任务id
                String instanceId = getJobId(job_key,user_token,prop);
                //根据crop_code 刷新 job_id
                dataServiceMapper.updateJobId(crop_code, instanceId);
                //循环查询任务状态情况
                executeState(instanceId,user_token,prop);
            }
        }, 0, INTERVAL, TimeUnit.SECONDS);
    }

    private String getJobId(String job_key,String user_token,Properties prop) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.SYSTEMID, prop.getProperty("SYSTEMID"));
        params.put(Constant.COMPANYID, prop.getProperty("COMPANYID"));
        params.put(Constant.USERID, prop.getProperty("USERID"));
        params.put(Constant.JOBKEY, job_key);
        params.put(Constant.ISAUTO, prop.getProperty("ISAUTO"));
        JSONObject jb = JSONObject.fromObject(params);
        logger.info("getJobId param:" + jb.toString());

        String queryUrl = prop.getProperty("SUBMIT_JOB","http://173.2.51.42:8088/scheduler/scheduler/1.0/executeJobReturnInstanceId");
        String result = "";
        try {
            result = HttpRequestUtil.post(queryUrl, jb.toString(),user_token);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info("getJobId result:" + result);
        JSONObject resultJson = JSONObject.fromObject(result);
        JSONArray datas = resultJson.getJSONArray("data");
        JSONObject data = datas.getJSONObject(0);
        return data.getString("result");
    }

    private void executeState(String instanceId,String user_token,Properties prop) {

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constant.INSTANCEID, instanceId);
                JSONObject jb = JSONObject.fromObject(params);
                logger.info("executeState param:" + jb.toString());
                String queryUrl = prop.getProperty("CHECK_JOB_STATE","http://173.2.51.42:8088/scheduler/scheduler/1.0/getExeMonitorInfoByInstanceId");
                String result = "";
                try {
                    result = HttpRequestUtil.post(queryUrl, jb.toString(),user_token);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
//                logger.info("executeState result:" + result);
                JSONObject resultJson = JSONObject.fromObject(result);
                JSONArray datas = resultJson.getJSONArray("data");
                JSONObject data = datas.getJSONObject(0);
                String exec_state = data.getString("exec_state");
                String end_time = data.getString("end_time");
                dataServiceMapper.updateJobState(instanceId, exec_state);
                if (Constant.EXEC_SUCCESS.equals(exec_state)) {
                    scheduledExecutorService.shutdownNow();
                    String x4 = dataServiceMapper.getX4(instanceId);
                    getGeneralUrlByHttps(x4);
                } else if (Constant.EXPECT_END.equals(exec_state) ||
                        Constant.WAIT_OUT.equals(exec_state) ||
                        Constant.EXEC_FAILE.equals(exec_state) ||
                        Constant.START_FAILE.equals(exec_state) ||
                        Constant.EXEC_FAILE.equals(exec_state)
                ) {
                    scheduledExecutorService.shutdownNow();
                    executeStop(instanceId,user_token,prop);  //停止并发送信息
                    logger.error("出现异常，发送钉钉");
                    //TODO 调用强制停止接口
                }
            } catch (Throwable t) {
                t.printStackTrace();
                logger.error("executeState scheduleAtFixedRate error: " + t.toString());
            }

        }, 0, 20, TimeUnit.SECONDS);

    }

    //强制停止接口
    private void executeStop(String instanceId,String user_token,Properties prop) {

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.INSTANCEID, instanceId);
        JSONObject jb = JSONObject.fromObject(params);
        logger.info("executeState param:" + jb.toString());
        String queryUrl = prop.getProperty("FORCE_KILL","http://173.2.51.42:8088/scheduler/scheduler/1.0/instanceStop");
        String result = "";
        try {
            result = HttpRequestUtil.post(queryUrl, jb.toString(),user_token);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.error("任务强制成功,任务id为：{}",instanceId);
    }
}
