package breeze.app.tulip.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import brz.breeze.tool_utils.Blog;

public class AppMySQLTool {
    static {
        System.loadLibrary("app");
    }

    private static Statement statement;
    public native void initData();

    public AppMySQLTool() {
        initData();
    }

    public void connectMySQL(String host, String username, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(host, username, password);
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getData(String token){
        try {
            ResultSet resultSet = statement.executeQuery("select Token,Time from tulip_uid where Token = \""+token+"\"");
            if (resultSet.next()){
                return resultSet.getLong("Time");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    public boolean update(String token){
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.MONTH,instance.get(Calendar.MONTH)+1);
        try {
            statement.execute("insert into tulip_uid values(\""+token+"\","+instance.getTimeInMillis()+")");
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return  false;
    }

    public long setData(String token){
        if (update(token)){
            return getData(token);
        }
        return 0;
    }
}
