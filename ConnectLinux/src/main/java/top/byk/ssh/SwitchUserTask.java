//package top.byk;
//
//
//import net.neoremind.sshxcute.task.CustomTask;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//
///**
//   * @TODO:   实现sshxcute切换用户信息
//  * @author ykbian@qq.com
//   * @date:2019/3/18 9:48
//   * @param
//   * @return
//   */
//
//public class SwitchUserTask extends CustomTask {
//
//     private static String tempCmdFile = "tmpCmd.sh";
//
//     protected String command = "";
//     private String[] args;
//
//     public SwitchUserTask(String user, String... args) {
//         this.args = args;
//         DateFormat df = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
//         tempCmdFile = FilenameUtils.getBaseName(tempCmdFile) + "-" + df.format(new Date()) + "." + FilenameUtils.getExtension(tempCmdFile);
//
//         StringBuffer sb = new StringBuffer();
//         for (int i = 0; i < args.length; i++) {
//             sb.append("echo \"" + args[i].replace("\"", "\\\"") + "\" >> " + tempCmdFile + ";");
//         }
//         this.command = "currentPath=`pwd`;rm " + tempCmdFile + ";" +
//                 sb.toString() +
//                 "chmod 777 " + tempCmdFile + ";" +
//                 "sudo -i -u " + user + " ${currentPath}/" + tempCmdFile + ";" +
//                 "cat " + tempCmdFile + ";" +
//                 "rm " + tempCmdFile + ";";
//     }
//
//     public Boolean checkStdOut(String stdout) {
//         Iterator<String> iter = err_sysout_keyword_list.iterator();
//         while (iter.hasNext()) {
//             if (stdout.contains(iter.next())) {
//                 return false;
//             }
//         }
//         return true;
//     }
//
//     public Boolean checkExitCode(int exitCode) {
//         if (exitCode == 0){
//             return true;
//         }
//         else{
//             return false;
//         }
//
//     }
//
//     public String getCommand() {
//         return command;
//     }
//
//     public List<String> getInputCommands(){
//         return Arrays.asList(args);
//     }
//
//     public String getInfo() {
//         return "Exec command " + getCommand();
//     }
//}
