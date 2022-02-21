package com.njzq.dataDemo.dataDemo.controller;

import com.hundsun.jrescloud.rpc.annotation.CloudFunctionParam;
import com.njzq.dataDemo.dataDemo.util.Configuration;
import com.njzq.dataDemo.service.IDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Properties;

@RestController
@RequestMapping("/deqin")
public class DataDeqinController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataDeqinController.class);

    @Autowired
    private IDataService dataService;
    @CrossOrigin
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public Object test(@CloudFunctionParam("json") String json,@CloudFunctionParam("user_token") String userToken) {
        LOGGER.info("test++++++, {} and token is {}.",json,userToken );
        return dataService.parseJson(json,userToken);
    }
}
