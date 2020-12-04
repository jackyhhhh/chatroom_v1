package context;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    private static PrintWriter writer;
    static {
        try {
            File file = new File("server_"+day_str()+".log");
            writer = new PrintWriter(new FileWriter(file, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String now(){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date now = new Date();
        return fmt.format(now);
    }

    public static String day_str(){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        Date now = new Date();
        return fmt.format(now);
    }

    public static void info(String msg){
        String line = "["+now()+"]-[INFO]-"+msg;
        System.out.println(line);
        writer.println(line);
        writer.flush();
    }

    public static void warn(String msg){
        String line = "["+now()+"]-[WARNING]-"+msg;
        System.out.println(line);
        writer.println(line);
        writer.flush();
    }

    public static void error(String msg){
        String line = "["+now()+"]-[ERROR]-"+msg;
        System.out.println(line);
        writer.println(line);
        writer.flush();
    }

    public static void error(Exception e){
        String line = "["+now()+"]-[ERROR]-"+e.getMessage();
        System.out.println(line);
        writer.println(line);
        writer.flush();
    }

}
