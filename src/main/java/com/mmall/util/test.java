package com.mmall.util;
import java.io.*;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Random;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import com.jcraft.jsch.*;
import com.jcraft.jsch.Session;
import org.springframework.web.multipart.MultipartFile;
public class test {

    private static ChannelSftp sftp = null;
    //ftp服务器ip地址
    private static final String FTP_ADDRESS = "111.231.251.88";
    //端口号
    private static final int FTP_PORT = 22;
    //用户名
    private static final String FTP_USERNAME = "root";
    //密码
    private static final String FTP_PASSWORD = "247426559.zwb";
    //图片路径
    public static final String FTP_BASEPATH = "/usr/local/inculde/img";
    //访问路径
    private static final String pathPrefix = "https://xingluote.cn/img/";

    /**
     * 上传图片到ftp服务器
     * @param file
     * @return
     * @throws IOException
     */
    public static String newFtpUpload(MultipartFile file) throws IOException {
        //获取到文件的文件名
        String fileName = file.getOriginalFilename();
        //根据文件名+UUID生产新的文件名
        String newFileName = UUID.randomUUID() + fileName;
        //从MultipartFile对象中获取流
        InputStream inputStream = file.getInputStream();
        //判断是否成功的boolean值
        boolean success = false;
        //返回值
        String path = "";
        //Session对象
        Session session = null;
        try {
            //创建JSch对象
            JSch jSch = new JSch();
            //调用JSch对象的getSession方法（参数是服务器的访问用户名,服务器访问路径,服务器的端口号）给session赋值
            session = jSch.getSession(FTP_USERNAME,FTP_ADDRESS,FTP_PORT);
            //给session对象设置密码参数也就是你的服务器访问的密码
            session.setPassword(FTP_PASSWORD);
            //创建参数
            Properties sshConfig = new Properties();
            //给参数对象赋值，这里解决
            sshConfig.put("StrictHostKeyChecking", "no");
            //这里设置参数给session主要解决把kerberos认证方式去掉，如果不写这一步走到这里你需要向控制台输入你的	    	    kerberos用户名和口令，如果你的项目环境没有涉及到kerberos应该是不用设置
            session.setConfig(
                    "PreferredAuthentications",
                    "publickey,keyboard-interactive,password");
            //把参数对象给session对象注入
            session.setConfig(sshConfig);
            //打开session连接
            session.connect(15000);
            //使用session对象连接服务器
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            //使用ChannelSftp对象进行使用命令
            //进入需要进入的路径
            sftp.cd(FTP_BASEPATH);
            //进行文件上传
            sftp.put(inputStream,newFileName);
            //设置上传成功
            success = true;
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            if (sftp != null) {
                if (sftp.isConnected()) {
                    sftp.disconnect();
                }
            }
            //关闭session
            if (session != null) {
                if (session.isConnected()) {
                    session.disconnect();
                }
            }
        }
        //判断是否成功
        if(success){
            //设置返回路径为访问路径（你的服务器访问路径+新文件名）
            path = pathPrefix + newFileName;
        }
        return path;
    }
}
