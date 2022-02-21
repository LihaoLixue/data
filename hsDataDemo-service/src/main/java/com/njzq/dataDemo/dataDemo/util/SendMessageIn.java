package com.njzq.dataDemo.dataDemo.util;

import lombok.Data;

import javax.annotation.sql.DataSourceDefinition;
import java.util.List;

/**
 * @author LH
 * @description:
 * @date 2021-11-22 14:57
 */
@Data
public class SendMessageIn {

    //消息类型
    private String msgType;
    //webhook
    private String webhook;
    //密钥
    private String secret;
    //文本
    private String text;
    //指定对象
    private List<String> mobileList;
    //是否推送所有人
    private boolean isAtAll;
}
