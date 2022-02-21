package com.njzq.dataDemo;

public interface Constant {
    String TYPE = "type";
    String NAME = "name";
    String CROP_CODE = "crop_code";
    String PROMOTER = "promoter";
    String UNIF_SOCI_CRED_CODE = "unif_soci_cred_code";
    String DATE = "date";
    String TIME = "time";
    String INDEX_DICT = "index_dict";
    String VALUE = "value";
    String CM02 = "C_M02";
    String CM03="C_M03";
    String CM04="C_M04";
    String CM21="C_M21";
    String QUALFACT00 = "QualFact00";
    String ONE = "1";
    String ZERO = "0";
    String NOTFOUND = "1";
    String FOUND = "0";
    String DATA_STATE = "data_state";
    String SJ = "sj";
    String JOB_STATE = "job_state";
    String JOB_ID = "job_id";
    String GOV ="gov";

    String SYSTEMID = "systemId";
    String COMPANYID = "companyId";
    String USERID = "userId";
    String JOBKEY = "jobKey";
    String ISAUTO = "isAuto";


    /**
     * 任务提交状态
     */
    String TASK_SUCCESS = "0";
    /**
     * 流程id
     */
    String INSTANCEID = "instanceId";
    /**
     * 流程执行状态
     */
    //待执行
    String WAIT_JOB_EXEC = "-1";
    //启动中
    String STARTING = "0";
    //执行中
    String RUNNING = "1";
    //执行成功
    String EXEC_SUCCESS = "2";
    //异常结束
    String EXPECT_END = "3";
    //超时
    String WAIT_OUT = "4";
    //执行失败
    String EXEC_FAILE = "5";
    //启动失败
    String START_FAILE = "6";
    //强制终止
    String FORCE_TERM = "7";
    //暂停中
    String PAUSING = "8";
    //已暂停
    String PAUSED = "9";
    //恢复中
    String RECOVERY = "10";
    //警告
    String ALERT = "11";
    //等待中
    String WATING_JOB = "12";


}
