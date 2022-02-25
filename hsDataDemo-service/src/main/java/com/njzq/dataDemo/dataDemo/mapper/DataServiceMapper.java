package com.njzq.dataDemo.dataDemo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Mapper
public interface DataServiceMapper {

    String getX1(@Param("qualFact00") String qualFact00);

    String getX2(@Param("qualFact00") String qualFact00, @Param("date") String date);

    String getX3();

    void insertIndicatorDetail(Map<String, String> map);//主体指标明细表

    void insertSubjectInfo(Map<String, String> map);//主体信息表

    void updateJobId(@Param("crop_code") String crop_code, @Param("job_id") String job_id);//刷新任务id

    void updateJobState(@Param("instanceId") String crop_code, @Param("exec_state") String exec_state);//刷新任务状态

    String getX4(@Param("instanceId") String instanceId);



}
