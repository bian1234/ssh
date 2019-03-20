package top.byk.ssh;



import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.neoremind.sshxcute.core.*;
import net.neoremind.sshxcute.exception.TaskExecFailException;
import net.neoremind.sshxcute.exception.UploadFileNotSuccessException;
import net.neoremind.sshxcute.task.CustomTask;

public class MySSHExec   {

    static Logger logger = Logger.getLogger();
    private Session session;
    private Channel channel;
    private ConnBean conn;
    private static MySSHExec ssh;
    private JSch jsch;
    protected Map<String, String> dataList = null;
    protected static String PARENT_DIR = "";

    private MySSHExec(ConnBean conn) {
        try {
            logger.putMsg(4, "SSHExec initializing ...");
            this.conn = conn;
            this.jsch = new JSch();
        } catch (Exception var3) {
            logger.putMsg(6, "Init SSHExec fails with the following exception: " + var3);
        }

    }

    public static MySSHExec getInstance(ConnBean conn) {
        if (ssh == null) {
            ssh = new MySSHExec(conn);
        }

        return ssh;
    }

    public Boolean connect() {

        try {
            this.session = this.jsch.getSession(this.conn.getUser(), this.conn.getHost(), SysConfigOption.SSH_PORT_NUMBER);
            UserInfo ui = new MyUserInfo(this.conn.getPassword());
            logger.putMsg(4, "Session initialized and associated with user credential " + this.conn.getPassword());
            this.session.setUserInfo(ui);
            logger.putMsg(4, "SSHExec initialized successfully");
            logger.putMsg(4, "SSHExec trying to connect " + this.conn.getUser() + "@" + this.conn.getHost());
            this.session.connect(3600000);
            logger.putMsg(4, "SSH connection established");
        } catch (Exception var2) {
            logger.putMsg(6, "Connect fails with the following exception: " + var2);
            return false;
        }

        return true;
    }


     /**
       * @TODO:  判断session是否为空
       * @author ykbian@qq.com
       * @date:2019/3/19 18:18
       * @param
       * @return
       */
     public Boolean isConnect(String user,String host) {
           Boolean b = true;
         try {
             this.session = this.jsch.getSession(user,host, SysConfigOption.SSH_PORT_NUMBER);
            if (this.session == null){
                b= false;
            }
         } catch (Exception var2) {
           var2.printStackTrace();
             b =  false;
         }
         return b;
     }

    public Boolean disconnect() {
        try {
            this.session.disconnect();
            this.session = null;
            logger.putMsg(4, "SSH connection shutdown");
        } catch (Exception var2) {
            logger.putMsg(6, "Disconnect fails with the following exception: " + var2);
            return false;
        }

        return true;
    }

}
