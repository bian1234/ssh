package top.byk.ssh;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class SSH2Util {

    private Connection conn;
    private String ipAddr;
    private String charset = Charset.defaultCharset().toString();
    private String userName;
    private String password;

    public SSH2Util(String ipAddr, String userName, String password,
                    String charset) {
        this.ipAddr = ipAddr;
        this.userName = userName;
        this.password = password;
        if (charset != null) {
            this.charset = charset;
        }
    }

    /**
     * 登录远程Linux主机
     *
     * @return
     * @throws IOException
     */
    public boolean login() throws IOException {
        conn = new Connection(ipAddr);
        conn.connect(); // 连接
        return conn.authenticateWithPassword(userName, password); // 认证
    }

    /**
     * 执行Shell脚本或命令
     *
     * @param cmds
     *            命令行序列
     * @return
     */
    public String exec(String cmds) {
        InputStream in = null;
        String result = "";
        try {
            if (this.login()) {
                Session session = conn.openSession(); // 打开一个会话
                session.execCommand(cmds);
                in = session.getStdout();
                result = this.processStdout(in, this.charset);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            conn.close();
        }
        return result;
    }

    /**
     * 解析流获取字符串信息
     *
     * @param in
     *            输入流对象
     * @param charset
     *            字符集
     * @return
     */
    public String processStdout(InputStream in, String charset) {
        byte[] buf = new byte[1024];
        StringBuffer sb = new StringBuffer();
        try {
            while (in.read(buf) != -1) {
                sb.append(new String(buf, charset));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    public static void main(String[] args) {

            SSH2Util rms = new SSH2Util("39.96.93.185", "bian1", "bianALIYUN123", null);

            String result = rms.exec("su root");
            System.out.println(result);
//            result = rms.exec("sh backupJob.sh liutongping");
//            System.out.println(result);

    }
}
