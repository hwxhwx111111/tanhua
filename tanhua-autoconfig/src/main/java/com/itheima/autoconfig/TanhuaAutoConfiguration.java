package com.itheima.autoconfig;



import com.itheima.autoconfig.properties.HuanXinProperties;
import com.itheima.autoconfig.properties.SmsProperties;
import com.itheima.autoconfig.template.HuanXinTemplate;
import com.itheima.autoconfig.properties.AipFaceProperties;
import com.itheima.autoconfig.properties.OssProperties;
import com.itheima.autoconfig.template.AipFaceTemplate;
import com.itheima.autoconfig.template.OssTemplate;
import com.itheima.autoconfig.template.SmsTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({
        SmsProperties.class,
        OssProperties.class,
        AipFaceProperties.class,
        HuanXinProperties.class
})
public class TanhuaAutoConfiguration {

    @Bean
    public SmsTemplate smsTemplate(SmsProperties properties) {
        return new SmsTemplate(properties);
    }

    @Bean
    public OssTemplate ossTemplate(OssProperties properties){
        return new OssTemplate(properties);
    }

    @Bean
    public AipFaceTemplate aipFaceTemplate(){
        return new AipFaceTemplate();
    }

    @Bean
    public HuanXinTemplate huanXinTemplate(HuanXinProperties properties) {
        return new HuanXinTemplate(properties);
    }
}