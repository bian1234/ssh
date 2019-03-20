package top.byk.ssh;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import com.jcraft.jsch.*;
import org.apache.commons.net.util.Charsets;
import sun.misc.IOUtils;
import top.byk.ftp.SFTPInfo;

public class JSCHUtil {
    private static JSCHUtil instance;

    public static JSCHUtil getInstance() {
        if (instance == null) {
            instance = new JSCHUtil();
        }
        return instance;
    }

    private JSCHUtil() {

    }

    private Session getSession(String host, int port, String ueseName)
            throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(ueseName, host, port);
        return session;
    }

    public Session connect(String host, int port, String ueseName,
                           String password) throws Exception {
        Session session = getSession(host, port, ueseName);
        session.setPassword(password);
        Properties config = new Properties();
        config.setProperty("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        return session;
    }

    public String execCmd(Session session, String command) throws Exception {
        if (session == null) {
            throw new RuntimeException("Session is null!");
        }
        ChannelExec exec = (ChannelExec) session.openChannel("exec");
        InputStream in = exec.getInputStream();
        byte[] b = new byte[1024];

        exec.setCommand(command);
        exec.connect();
        StringBuffer buffer = new StringBuffer();
        while (in.read(b) > 0) {
            buffer.append(new String(b));
        }
        exec.disconnect();
        return buffer.toString();
    }

    public static String execCmd2(Session session, String command) throws Exception {
        byte[] commandStr = command.getBytes();
        if (session == null) {
            throw new RuntimeException("Session is null!");
        }
        ChannelExec exec = (ChannelExec) session.openChannel("exec");
        InputStream in = exec.getInputStream();
        byte[] b = new byte[1024];

        exec.setCommand(commandStr);
        exec.connect();
        StringBuffer buffer = new StringBuffer();
        while (in.read(b) > 0) {
            buffer.append(new String(b));
        }
        exec.disconnect();
        return buffer.toString();
    }



    public void clear(Session session) {
        if (session != null && session.isConnected()) {
            session.disconnect();
            session = null;
        }
    }

    /**
     * 读取 InputStream 到 String字符串中
     */
    public static String readStream(InputStream in) {
        try {
            //<1>创建字节数组输出流，用来输出读取到的内容
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //<2>创建缓存大小
            byte[] buffer = new byte[1024]; // 1KB
            //每次读取到内容的长度
            int len = -1;
            //<3>开始读取输入流中的内容
            while ((len = in.read(buffer)) != -1) { //当等于-1说明没有数据可以读取了
                baos.write(buffer, 0, len);   //把读取到的内容写到输出流中
            }
            //<4> 把字节数组转换为字符串
            String content = baos.toString();
            //<5>关闭输入流和输出流
            in.close();
            baos.close();
            //<6>返回字符串结果
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return  e.getMessage();
        }
    }


    private String executeCommand(String host, String user, String password, ArrayList<String> cmds) {
        JSch jsch = new JSch();
        final String result = "";

        //	Channel channel = null;
        Session session = null;
        ChannelShell channelShell = null;
        try {
            session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setServerAliveCountMax(0);

            UserInfo ui = new MyUserInfo(password) {
                @Override
                public void showMessage(String message) {
                    System.out.println(message);
                }

                @Override
                public boolean promptYesNo(String message) {
                    return true;
                }


                @Override
                public boolean promptPassphrase(String s) {
                    System.out.println("这里的密码是："+ s);
                    return true;
                }
            };

            session.setUserInfo(ui);
            session.connect(30000); // making a connection with timeout.
            channelShell = (ChannelShell) session.openChannel("shell");
            channelShell.connect(3000);

            InputStream inputStream = channelShell.getInputStream();
            OutputStream outputStream = channelShell.getOutputStream();
            for (String cmd : cmds) {
                outputStream.write((cmd + " \n\r").getBytes());
                outputStream.flush();
                TimeUnit.SECONDS.sleep(1);
                // BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
//                System.out.println(cmd);
                byte[] tmp = new byte[1024];
                while (true) {
                    while (inputStream.available() > 0) {
                        int i = inputStream.read(tmp, 0, 1024);
                        if (i < 0){
                            break;
                        }
                        System.out.println(new String(tmp, 0, i));
                    }
                    TimeUnit.SECONDS.sleep(1);
                    if (channelShell.isClosed()) {
                        if (inputStream.available() > 0){
                            continue;
                        }
                        System.out.println("exit-status: " + channelShell.getExitStatus());
                        break;
                    }
                    break;
                    // inputStream.close();
                }
            }

        } catch (JSchException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (channelShell != null){
                channelShell.disconnect();
            }
            if (session != null){
                session.disconnect();
            }

        }
        return result;
    }


    /**
     * 创建文件夹
     *
     * @param sftp
     * @param dir
     *            文件夹名称
     */
    public static void mkdir(ChannelSftp sftp, String dir) {
        try {
            sftp.mkdir(dir);
            System.out.println("创建文件夹成功！");
        } catch (SftpException e) {
            System.out.println("创建文件夹失败！");
            e.printStackTrace();
        }
    }

     /**
       * @TODO:  ftp上传文件
       * @author ykbian@qq.com
       * @date:2019/3/20 15:26
       * @param   用户名，IP，端口，上传的目标目录，上传的文件信息
       * @return
       */

    public  static String uploadFile(String userName,String password,String ip,int port,String dir,File file) {
        JSch jsch = new JSch();
        Session session  = null;
        ChannelSftp sftp = null;
        String result = "";
        try {
             session = jsch.getSession(userName, ip, port);
             session.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            session.setConfig(sshConfig);
            session.connect();
             sftp = (ChannelSftp)session.openChannel("sftp");
        }catch (JSchException e){
            e.printStackTrace();
            result = "上传失败";
        }

       if (session.isConnected()){
           System.out.println("====当前连接成功====");
       }
        try {
            mkdir(sftp,dir);
            sftp.cd(dir);
            if (file != null) {
                sftp.put(new FileInputStream(file), file.getName());
                result = "上传成功！";
            } else {
                result = "文件为空！不能上传！";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传失败");
            result = "上传失败！";
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
//        String ip = "39.96.93.185";
//        int port = 22;
//        String user = "root";
//        String ps = "bianALIYUN123";

//        ArrayList<String>  stringList02 = new ArrayList<String>();
//
//        /**
//         *  普通 登录 》检查用户信息 》切换到普通用户》检查用户信息》切回root》检查用户信息
//         *
//         *  每次登录时以root身份登录，需要普通用户操作时切换到普通用户，需要root用户权限时exit
//         *
//         * */
//        stringList02.add("whoami");
//        stringList02.add("su root");
//        stringList02.add("bianALIYUN123");
//        stringList02.add("whoami");
//        String result = JSCHUtil.getInstance().executeCommand(ip,user,ps,stringList02);
//        System.out.println(result);
//        File file = new File("C:\\ftpserver\\data\\SQLyog-12.0.9-0.x64.exe");
//        String result = JSCHUtil.uploadFile(user,ps,ip,port,"/SSHtest/",file);

        String ip = "192.168.43.59";
        int port = 21;
        String user = "holystarpc-11";
        String ps = "bian";

        String str = "curl ftp://192.168.43.59/bigfile.zip -u holystarpc-11:bian -o /mnt/internal_storage/bigfile.zip";  //需要执行的命令



    }
}
