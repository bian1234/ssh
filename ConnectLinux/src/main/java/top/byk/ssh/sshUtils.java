package top.byk.ssh;

import net.neoremind.sshxcute.core.*;
import net.neoremind.sshxcute.exception.TaskExecFailException;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;
import net.neoremind.sshxcute.task.impl.ExecShellScript;

/**
 * ssh工具类
 * 
 * @author lu
 *
 */
public class sshUtils {

	static Logger logger = Logger.getLogger();


	// 远程主机的ip地址
	private String ip;
	// 远程主机登录用户名
	private String username;
	// 远程主机的登录密码
	private String password;

	private SSHExec SSH = null;
	private MySSHExec mySSH = null;

	public sshUtils(final String ip, final String username, final String password) {
		this.ip = ip;
		this.username = username;
		this.password = password;
		// 此处设置每条命令执行的时间间隔（1000L为1秒）
		SysConfigOption.INTEVAL_TIME_BETWEEN_TASKS = 2000L;
	}

	/**
	 * 启动链接
	 */
	public Boolean Start() {
		try {
			ConnBean cb = new ConnBean(ip, username, password);
			SSH = SSHExec.getInstance(cb);
			return SSH.connect();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}



	 /**
	   * @TODO:  判断是否连接
	   * @author ykbian@qq.com
	   * @date:2019/3/19 9:11
	   * @param
	   * @return
	   */
	 public boolean isConnent(String username,String host){
		 return mySSH.isConnect(username,host);
	 }




	/**
	 * 断开链接
	 */
	public Boolean Stop() {
		Boolean res = false;
		if (SSH != null) {
			try {
				res = SSH.disconnect();
				SSH = null;
			} catch (Exception ex) {

			}
		}
		return res;
	}

	/**
	 * 执行命令
	 * 
	 * @param strComm 执行的命令
	 * @return
	 */
	@SuppressWarnings("finally")
	public Result sellBySSH(String strComm) {
		Result res = null;
		try {
			CustomTask echo = new ExecCommand(strComm);
			// SSH.connect();
			res = SSH.exec(echo);
		} catch (TaskExecFailException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			return res;
		}
	}

	/**
	 * shellPath 代表脚本执行路径
	 * 
	 * @param shellPath
	 * @return
	 */
	@SuppressWarnings("finally")
	public Result exeScript(String shellPath) {
		Result res = null;
		// CustomTask echo = new ExecShellScript(shellPath);
		try {
			CustomTask echo = new ExecShellScript(shellPath);
			res = SSH.exec(echo);
			;
		} catch (TaskExecFailException e) {
			e.printStackTrace();
			// return null;
		} finally {
			return res;
		}
	}

	/**
	 * 带参执行脚本
	 * 
	 * @param shellPath
	 * @param args
	 * @return
	 */
	@SuppressWarnings("finally")
	public Result exeScript(String shellPath, String args) {

		Result res = null;
		try {
			CustomTask task = new ExecShellScript(shellPath, args);
			return SSH.exec(task);
		} catch (TaskExecFailException e) {
			e.printStackTrace();
			res = null;
		} finally {
			return res;
		}
	}

	
	/**
     * 上传文件
     * @param localFile
     * @param remotePath
     */
    public boolean putFile(String localFile, String remotePath) {
        
        try {
            SSH.uploadSingleDataToServer(localFile, remotePath);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
   
        }
    }
	
    /**
     * 上传目录
     * @param localPath
     * @param remotePath
     */
    public boolean putDir(String localPath, String remotePath) {
        
        try {
            SSH.uploadAllDataToServer(localPath, remotePath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
           
        }
    }
	
	
	
	/**
	 * 执行单个命令并返回执行结果
	 * 
	 * @param strComm 要执行Linux的命令
	 * @return
	 */
	@SuppressWarnings("finally")
	public Result shellBySingle(String strComm) {
		SSHExec ssh = null;
		Result res = null;
		try {
			ConnBean cb = new ConnBean(ip, username, password);
			ssh = SSHExec.getInstance(cb);
			CustomTask echo = new ExecCommand(strComm);
			ssh.connect();
			res = ssh.exec(echo);
		} catch (TaskExecFailException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			ssh.disconnect();
			return res;
		}
	}





	public static void main(String[] args) {
		String ip = "39.96.93.185";
		String user = "bian1";
		String password = "bianALIYUN123";
		sshUtils sshUtils = new sshUtils(ip,user,password);
		if (sshUtils.Start()){
			long start = System.currentTimeMillis();
//			Boolean result = sshUtils.putFile("C:\\ftpserver\\data\\data.zip","/SSHtest");
//			System.out.println((result == true)?"成功":"失败");
//			String str = "curl ftp://192.168.43.56/a.out –u holystarpc-11:bian -o /SSHtest/a.out";  //需要执行的命令
			String str = "su root";
			Result  result = sshUtils.sellBySSH(str);
			if (result.isSuccess){
				System.out.println("result.sysout>>>"+result.sysout+"===="+result.rc);
				System.out.println("执行时间："+(System.currentTimeMillis()-start));
			}else {
				System.out.println("============失败===========");
				System.out.println(result.error_msg);
			}
		}
	}
}
