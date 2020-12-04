package core;

import context.Log;
import manager.DBManager;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientConnection {
    private final Socket socket;
    private final BufferedReader br;
    private final PrintWriter pw;
    private final String host;
    private final int port;
    private final String tmpMsgTableName;
    private final DBManager dbm;

    ClientConnection(Socket socket){
        this.socket = socket;
        this.tmpMsgTableName = "tmp_"+System.currentTimeMillis();
        this.host = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
        this.dbm = DBManager.getInstance();
        try {
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            Log.error(e.getMessage());
            throw new RuntimeException();
        }

        dbm.createTmpMsgTable(this);
    }

    public String readLine(){
        try {
            return br.readLine();
        } catch (IOException e) {
            Log.error(e.getMessage());
            dbm.deleteTmpMsgTable(this);
            throw new RuntimeException();
        }
    }

    public void println(String line){
        pw.println(line);
        pw.flush();
    }

    public void close(){
        try {
            if(br != null){
                br.close();
            }
            if(pw != null){
                pw.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException e) {
            Log.error(e.getMessage());
        }
    }


    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public PrintWriter getWriter(){
        return pw;
    }

    public String getName(){
        return host + ":" + port;
    }

    public String getTmpMsgTableName() {
        return tmpMsgTableName;
    }



}
