package com.njzq.dataDemo.service;

import com.hundsun.jrescloud.rpc.annotation.CloudFunction;
import com.hundsun.jrescloud.rpc.annotation.CloudFunctionParam;
import com.hundsun.jrescloud.rpc.annotation.CloudService;

@CloudService
public interface IDataService {
    @CloudFunction(functionId = "3109001")
    Object parseJson(@CloudFunctionParam("json") String json, @CloudFunctionParam("user_token") String userToken);

}
