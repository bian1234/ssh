package top.byk.ssh;


import net.neoremind.sshxcute.core.ConnBean;
import net.neoremind.sshxcute.core.Result;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.exception.TaskExecFailException;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;
import net.neoremind.sshxcute.task.impl.ExecShellScript;

public class ConnectLinuxUtil {

    public static void main(String[] args) {
        // 新建一个 SSHExec 引用
        SSHExec ssh = null;
        final String ip = "39.96.93.185";
        final String rootUser = "root";
        final String testUser = "bian1";
        final String rootPW = "bianALIYUN123";
        final String testPW = "bianALIYUN123";
        // 下面所有的代码都放在 try-catch 块中
        try {
            // 实例化一个 ConnBean 对象，参数依次是 IP 地址、用户名和密码
            ConnBean cb = new ConnBean(ip, testUser, testPW);
            // 将刚刚实例化的 ConnBean 对象作为参数传递给 SSHExec 的单例方法得到一个 SSHExec 对象
            ssh = SSHExec.getInstance(cb);

            //执行的命令行任务
//            CustomTask ct2 = new ExecCommand("cd SSHtest;mkdir helloSSH");
            CustomTask ct2 = new ExecCommand("cd /;ls");
//            CustomTask ct2 = new ExecCommand("su root");
           // CustomTask ct3 = new ExecCommand("ll");
            ssh.connect();
            // 执行脚本并且返回一个 Result 对象
            Result res = ssh.exec(ct2);
           // Result res3 = ssh.exec(ct3);
            System.out.println("状态码: " + res.rc);

            if (res.isSuccess) {
                System.out.println("成功： " + res.sysout);
            } else {
                System.out.println("失败: " + res.error_msg);
            }
        } catch (TaskExecFailException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            ssh.disconnect();
        }
    }


}
