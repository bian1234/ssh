package top.byk.ssh;

import com.jcraft.jsch.UserInfo;

public class MyUserInfo  implements UserInfo {

    String passwd;
    public String getPassphrase() {
        return null;
    }

    public String getPassword() {
        return null;
    }

    public boolean promptPassword(String s) {
        return false;
    }

    public boolean promptPassphrase(String s) {
        return false;
    }


    public boolean promptYesNo(String s) {
        return false;
    }

    public void showMessage(String s) {

    }

    public MyUserInfo(String passwd){
        this.passwd= passwd;
    }
}
