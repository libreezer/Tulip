package breeze.app.tulip;

import breeze.app.tulip.database.AppDataBaseTool;
import brz.breeze.app_utils.BApplication;

public class APP extends BApplication {

    static {
        System.loadLibrary("app");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppDataBaseTool.init(this);
    }
}
