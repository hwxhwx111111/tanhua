package com.itheima.tanhua;


import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class TestFastDFS {

    //从调度服务器获取，一个目标存储服务器，上传
    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private FdfsWebServer webServer;// 获取存储服务器的请求URL

    @Test
    public void testFileUpdate() throws FileNotFoundException {
        //1. 指定文件
        File file = new File("D:\\1.jpg");
        //2. 文件上传
        StorePath path = client.uploadFile(new FileInputStream(file),
                file.length(), "jpg", null);

        String fullPath = path.getFullPath();
        System.out.println(fullPath);
        //3. 拼接访问路径
        String url = webServer.getWebServerUrl() + fullPath;
        System.out.println(url);  //a/b/abc.jp

    }
}