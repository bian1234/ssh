package top.byk.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.net.MalformedURLException;
import java.net.SocketException;

/**
 * @author: ykbian
 * @date: 2019/3/19 21:56
 * @Description:
 */
public class FtpUtils {



    /**
     * @Author：ykbian
     * @Description:  连接ftp
     * @Date:  2019/3/19 22:53
     * @Param:
     * @return:
     */
    public static FTPClient getFTPClient(String ftpHost, String ftpUserName,
                                         String ftpPassword, int ftpPort) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(ftpHost, ftpPort);// 连接FTP服务器
            ftpClient.login(ftpUserName, ftpPassword);// 登陆FTP服务器
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                System.out.println("未连接到FTP，用户名或密码错误。");
                ftpClient.disconnect();
            } else {
                System.out.println("FTP连接成功。");
            }
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("FTP的IP地址可能错误，请正确配置。");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("FTP的端口错误,请正确配置。");
        }
        return ftpClient;
    }


    /**
     * 上传文件（可供Action/Controller层使用）
     *
     * @param hostname
     *            FTP服务器地址
     * @param port
     *            FTP服务器端口号
     * @param username
     *            FTP登录帐号
     * @param password
     *            FTP登录密码
     * @param pathname
     *            FTP服务器保存目录
     * @param fileName
     *            上传到FTP服务器后的文件名称
     * @param inputStream
     *            输入文件流
     * @return
     */
    public static boolean uploadFile(String hostname, int port, String username, String password, String pathname,
                                     String fileName, InputStream inputStream) {
        boolean flag = false;
        FTPClient ftpClient = new FTPClient();
//        FTPClient ftpClient = getFTPClient(hostname,username,password,port);
        ftpClient.setControlEncoding("UTF-8");
        try {
            // 连接FTP服务器
            ftpClient.connect(hostname, port);

            // 登录FTP服务器
            ftpClient.login(username, password);
            // 是否成功登录FTP服务器
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("ftp登录失败");
                return flag;
            }

            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.makeDirectory(pathname);
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            ftpClient.logout();
            System.out.println("上传完成");

            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * 上传文件（可对文件进行重命名）
     *
     * @param hostname
     *            FTP服务器地址
     * @param port
     *            FTP服务器端口号
     * @param username
     *            FTP登录帐号
     * @param password
     *            FTP登录密码
     * @param pathname
     *            FTP服务器保存目录
     * @param filename
     *            上传到FTP服务器后的文件名称
     * @param originfilename
     *            待上传文件的名称（绝对地址）
     * @return
     */
    public static boolean uploadFileFromProduction(String hostname, int port, String username, String password,
                                                   String pathname, String filename, String originfilename) {
        boolean flag = false;
        try {
            InputStream inputStream = new FileInputStream(new File(originfilename));
            flag = uploadFile(hostname, port, username, password, pathname, filename, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 上传文件（不可以进行文件的重命名操作）
     *
     * @param hostname
     *            FTP服务器地址
     * @param port
     *            FTP服务器端口号
     * @param username
     *            FTP登录帐号
     * @param password
     *            FTP登录密码
     * @param pathname
     *            FTP服务器保存目录
     * @param originfilename
     *            待上传文件的名称（绝对地址）
     * @return
     */
    public static boolean uploadFileFromProduction(String hostname, int port, String username, String password,
                                                   String pathname, String originfilename) {
        boolean flag = false;
        try {
            String fileName = new File(originfilename).getName();
            InputStream inputStream = new FileInputStream(new File(originfilename));
            flag = uploadFile(hostname, port, username, password, pathname, fileName, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除文件
     *
     * @param hostname
     *            FTP服务器地址
     * @param port
     *            FTP服务器端口号
     * @param username
     *            FTP登录帐号
     * @param password
     *            FTP登录密码
     * @param pathname
     *            FTP服务器保存目录
     * @param filename
     *            要删除的文件名称
     * @return
     */
    public static boolean deleteFile(String hostname, int port, String username, String password, String pathname,
                                     String filename) {
        boolean flag = false;
        FTPClient ftpClient = new FTPClient();
        try {
            // 连接FTP服务器
            ftpClient.connect(hostname, port);
            // 登录FTP服务器
            ftpClient.login(username, password);
            // 验证FTP服务器是否登录成功
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                return flag;
            }
            // 切换FTP目录
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.dele(filename);
            ftpClient.logout();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                } catch (IOException e) {

                }
            }
        }
        return flag;
    }

    /**
     * 下载文件
     *
     * @param hostname
     *            FTP服务器地址
     * @param port
     *            FTP服务器端口号
     * @param username
     *            FTP登录帐号
     * @param password
     *            FTP登录密码
     * @param pathname
     *            FTP服务器文件目录
     * @param filename
     *            文件名称
     * @param localpath
     *            下载后的文件路径
     * @return
     */
    public static boolean downloadFile(String hostname, int port, String username, String password, String pathname,
                                       String filename, String localpath) {
        boolean flag = false;
        FTPClient ftpClient = new FTPClient();
        try {
            // 连接FTP服务器
            ftpClient.connect(hostname, port);
            // 登录FTP服务器
            ftpClient.login(username, password);
            // 验证FTP服务器是否登录成功
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                return flag;
            }
            // 切换FTP目录
            ftpClient.changeWorkingDirectory(pathname);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                if (filename.equalsIgnoreCase(file.getName())) {
                    File localFile = new File(localpath + "/" + file.getName());
                    OutputStream os = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(file.getName(), os);
                    os.close();
                }
            }
            ftpClient.logout();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                } catch (IOException e) {

                }
            }
        }
        return flag;
    }


    /** * 删除文件 *
     * @param pathname FTP服务器保存目录 *
     * @param filename 要删除的文件名称 *
     * @return */
    public boolean deleteFile(String pathname, String filename){
        boolean flag = false;
        FTPClient ftpClient = new FTPClient();
        try {
            System.out.println("开始删除文件");
//            initFtpClient();
            //切换FTP目录
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.dele(filename);
            ftpClient.logout();
            flag = true;
            System.out.println("删除文件成功");
        } catch (Exception e) {
            System.out.println("删除文件失败");
            e.printStackTrace();
        } finally {
            if(ftpClient.isConnected()){
                try{
                    ftpClient.disconnect();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }
    public static void main(String[] args) {
        String hostname = "192.168.43.56";
        int port = 21;
        String username = "holystarpc-11";
        String password = "bian";
        String originfilename = "C:\\byk\\byk.zip";
        boolean res = uploadFileFromProduction(hostname, port, username, password, "/data", originfilename);
        System.out.println("文件上传"+((res == true) ?"成功":"失败") );


        String str = "";
//      String pathname = "E:/fileZilla";
//      String filename = "003.xlsx";
//      String localpath = "/Users/wangbt/work/accessory/OA";
//      downloadFile(hostname, port, username, password, pathname, filename, localpath);
//        boolean res11= deleteFile(hostname, port, username, password, "/data", "byk.zip");
//        System.out.println("文件删除"+((res11 == true) ?"成功":"失败") );

    }
}
