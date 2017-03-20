package sunny.example.indoorlocation;

/**
 * Created by zhic on 16-12-13.
 */

public class MyApplication {
    // 程序退出标记
    private static boolean isProgramExit = false;

    public void setExit(boolean exit){
        isProgramExit = exit;
    }

    public boolean isExit(){
        return isProgramExit;
    }
}
