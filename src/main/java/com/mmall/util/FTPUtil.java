package com.mmall.util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE;

public class FTPUtil {
    private static final Logger logger= LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp= PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser=PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass=PropertiesUtil.getProperty("ftp.pass");


    public FTPUtil(String ip,int port,String user,String pwd){
        this.ip=ip;
        this.port=port;
        this.user=user;
        this.pwd=pwd;
    }
    public static boolean uploadFile(List<File> fileList){
        FTPUtil ftpUtil=new FTPUtil(ftpIp,22,ftpUser,ftpPass);
        logger.info("开始连接ftp服务器");
        boolean result=ftpUtil.uploadFile("/usr/local/inculde/img",fileList);
        logger.info("开始连接服务器，结束上传，上传结果：{}");
        return result;
    }

    private boolean uploadFile(String remotePath,List<File> fileList){
        boolean uploaded =true;
        FileInputStream fis=null;
        if(connectServer(this.getIp(),this.port,this.user,this.pwd)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(BINARY_FILE_TYPE);
                ftpClient.enterLocalActiveMode();
                for(File fileItem : fileList){
                    fis=new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
            } catch (IOException e) {
                logger.error("上传文件异常",e);
                e.printStackTrace();
                uploaded=false;
            }finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return uploaded;
    }

    public boolean connectServer(String ip,int port,String user,String pwd){
        boolean isSuccess=false;
        ftpClient=new FTPClient();
        try{
            ftpClient.connect(ip,port);
            logger.info("连接初始化");
            isSuccess=ftpClient.login(user,pwd);
        }catch (IOException e){
            logger.error("连接FTP服务器异常",e);
        }
        return isSuccess;
    }

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

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

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

}
