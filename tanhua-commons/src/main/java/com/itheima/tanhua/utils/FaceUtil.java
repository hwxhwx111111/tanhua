package com.itheima.tanhua.utils;

import cn.hutool.core.convert.Convert;
import com.baidu.aip.face.AipFace;
import org.json.JSONObject;

import java.util.HashMap;

public class FaceUtil {
    private FaceUtil(){
    }

    //设置APPID/AK/SK
    public static final String APP_ID = "27551102";
    public static final String API_KEY = "8dtVqR9o3eCefqmiL3FWpUjN";
    public static final String SECRET_KEY = "gQZb1aCVLTLWuvGXPUmUwGvXEqNGviYk";


    //提供人类识别的检测方法  返回值   err_code    0,是
    public static Integer checkFace(String imagePath){
        // 初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        HashMap<String, String> options = new HashMap<>();
        options.put("face_field","age");
        options.put("max_face_num","2");
        options.put("face_type","LIVE");
        options.put("liveness_control","LOW");

        // 调用接口
       // String image = "取决于image_type参数，传入BASE64字符串或URL字符串或FACE_TOKEN字符串";
        String imageType = "URL";

        // 人脸检测
        JSONObject res = client.detect(imagePath, imageType, options);
        System.out.println(res.toString(2));
        return Convert.toInt(res.get("error_code"));
    }



}
