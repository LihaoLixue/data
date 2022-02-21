package com.njzq.dataDemo.dataDemo.util;

import java.security.MessageDigest;

public class MD5Util {
    public static String encodeByMD5(String originString) {
        if (originString != null) {
            try {
                // 创建具有指定算法名称的信息摘要
                MessageDigest md = MessageDigest.getInstance("MD5");
                // 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
                byte[] results = md.digest(originString.getBytes("UTF-8"));

                StringBuffer hexValue = new StringBuffer();
                for (int i = 0; i < results.length; i++) {
                    int val = ((int) results[i]) & 0xff;
                    if (val < 16) {
                        hexValue.append("0");
                    }
                    hexValue.append(Integer.toHexString(val));
                }
                return hexValue.toString();
                // 将得到的字节数组变成字符串返回
//                String resultString = byteArrayToHexString(results);
//                return resultString.toUpperCase();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
