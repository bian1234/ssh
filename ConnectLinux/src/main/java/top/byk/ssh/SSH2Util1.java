package top.byk.ssh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;


public class SSH2Util1 {


    private static final int TIMEOUT = 2000;

    private static final int RECONNECT_TIMES = 20;

    private String userName;

    private String host;

    private String pass;

    private int port;

    private int sleepTime;

    private int waitTime;

    public static int getTIMEOUT() {
        return TIMEOUT;
    }

    public static int getReconnectTimes() {
        return RECONNECT_TIMES;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public Session openConnection() throws Exception {

        Connection connection = connect("39.96.93.185", 22, TIMEOUT);
        if (!connection.authenticateWithPassword("root", "bianALIYUN123")) {
            throw new Exception("用户名密码错误");
        }
        System.out.println("登录成功");
        Session session = connection.openSession();
        session.requestPTY("");
        session.startShell();
        return session;
    }

    private Connection connect(String address, int port, long timeOut) {

        Connection conn = new Connection(address, port);
        int connectTimes = 1;
        long waitTime = System.currentTimeMillis() + timeOut;
        do {
            try {
                conn.connect();
                break;
            } catch (IOException e) {
                System.out.println("ssh连接到主机时出错");
                e.printStackTrace();
                connectTimes++;
            }
        } while (System.currentTimeMillis() < waitTime
                && RECONNECT_TIMES <= connectTimes);
        return conn;
    }

    public InputStream executeCommand(String command, Session session)
            throws Exception {

        if (command.equals("")) {
            System.out.println("执行空指令");
            return null;
        }
        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(session.getStdin(),
                    "UTF-8"));
            out.println(command);
            out.flush();
            return handleBufferStr(session);
        } finally {
            if (null != out) {
                out.close();
            }
        }
    }

    /**
     * 处理接收的缓存数据
     */
    protected InputStream handleBufferStr(Session session) throws Exception {
        ByteArrayOutputStream sb = new ByteArrayOutputStream();
        ByteArrayOutputStream ersb = new ByteArrayOutputStream();
        InputStream in = null;
        try {
            if (sleepTime == 1000) {
                return null;
            }
            Thread.sleep(5000);
            in = receiveMsg(sb, ersb, session);
            return in;
        } finally {
            if (null != in) {
                in.close();
            }
        }
    }

    /**
     * 接收shell返回信息
     *
     * @param stdNormal
     * @param stdError
     * @return
     * @throws Exception
     */
    protected InputStream receiveMsg(ByteArrayOutputStream stdNormal,
                                     ByteArrayOutputStream stdError, Session session) throws Exception {
        InputStream stdout = null;
        try {
            stdout = session.getStdout();
            // InputStream stderr = session.getStderr();
            int conditions = session.waitForCondition(
                    ChannelCondition.STDOUT_DATA | ChannelCondition.STDERR_DATA
                            | ChannelCondition.EOF, waitTime);

            if ((conditions & ChannelCondition.TIMEOUT) != 0) {
                System.out.println("获取session数据超时");
                throw new IOException("获取打印数据超时");
            }
            if ((conditions & ChannelCondition.EOF) != 0) {
                System.out.println("无数据可读");
            }

            while (stdout.available() > 0) {
                return stdout;
                /*
                 * byte[] buffer = new byte[8192]; int len =
                 * stdout.read(buffer); if (len > 0) { // this check is somewhat
                 * paranoid if (log.isDebugEnable()) { //
                 * log.debug("Receive msg :" + buffer); }
                 * stdNormal.write(buffer, 0, len); }
                 */
            }

            /*
             * while (stderr.available() > 0) { byte[] buffer = new byte[8192];
             * int len = stderr.read(buffer); if (len > 0) {// this check is
             * somewhat paranoid if (log.isDebugEnable()) { //
             * log.debug("Receive error msg :" + buffer); }
             * stdError.write(buffer, 0, len); } }
             */
        } finally {
            if (null != stdout) {
                stdout.close();
            }
        }
        return null;
    }


    public String inputStream2String(InputStream inputStream) throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = inputStream.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }

    public static void main(String[] args) {
        SSH2Util1 SSH2Util1 = new SSH2Util1();
        try {
            String str1 = "date";
            Session session = SSH2Util1.openConnection();
            InputStream  inputStream = SSH2Util1.executeCommand(str1,session);
            String String = SSH2Util1.inputStream2String(inputStream);
            System.out.println(String);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}


