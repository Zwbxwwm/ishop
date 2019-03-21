package com.mmall.util;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class SftpUtils {

    //打印日志
    private static final Logger logger= LoggerFactory.getLogger(SftpUtils.class);

    private static ChannelSftp sftp = null;
    //从properties里面把服务器信息拿过来
    private static String ftpIp= PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser=PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass=PropertiesUtil.getProperty("ftp.pass");
    private static int ftpPort=22;
    //服务器整体地址
    private static String ftpImgPath=PropertiesUtil.getProperty("ftp.imgPath");

    public SftpUtils(String ip,String user,int port,String pwd){
        this.ip=ip;
        this.port=port;
        this.user=user;
        this.pwd=pwd;
    }

    public static boolean uploadFile(List<File> fileList){
        SftpUtils sftpUtils=new SftpUtils(ftpIp,ftpUser,ftpPort,ftpPass);
        logger.info("开始连接FTP服务器");
        boolean isSuccess=sftpUtils.uploadFile(ftpImgPath,fileList);
        return true;
    }

    private boolean uploadFile(String childPath,List<File> fileList){
        Session session=connectServer(this.ip,this.user,this.port,this.pwd);
        FileInputStream fis=null;
        if(session!=null){
            try {
                //创建参数
                Properties sshConfig = new Properties();
                //给参数对象赋值，这里解决
                sshConfig.put("StrictHostKeyChecking", "no");
                //这里设置参数给session主要解决把kerberos认证方式去掉
                session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
                //把参数对象给session对象注入
                session.setConfig(sshConfig);
                //打开session连接
                session.connect(500000);
                //使用session对象连接服务器
                Channel channel = session.openChannel("sftp");
                channel.connect();
                sftp = (ChannelSftp) channel;
                //使用ChannelSftp对象进行使用命令,使用linux的命令打开对应的文件夹
                sftp.cd(childPath);
                for(File fileItem : fileList){
                    try{
                        fis=new FileInputStream(fileItem);
                        sftp.put(fis,fileItem.getName());
                    }catch (IOException e){
                        logger.info("读取文件出现错误"+childPath,e);
                    }
                }
            }catch (JSchException e){
                logger.info("sftp连接错误",e);
            }catch (SftpException e){
                logger.info("Linux的cd命令出现错误",e);
            }

        }
        return true;

    }

    public Session connectServer(String ip,String user,int port,String password){
        Session session=null;
        try {
            JSch jSch = new JSch();
            session = jSch.getSession(user, ip, port);
            session.setPassword(password);
        }catch (JSchException e){
            logger.info("获取session异常");
        }
        return session;
    }



    //设置ip、端口、用户名、密码、实例；
    private String ip;
    private int port;
    private String user;
    private String pwd;


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }


}
