import java.io.*;
import java.net.*;

public class TcpServer {
    public static void main(String[] argv) throws Exception {
        try (ServerSocket server = new ServerSocket(8001);
             FileOutputStream fos = new FileOutputStream("server_recv.txt");
             FileInputStream fis = new FileInputStream("server_send.txt")) {
            System.out.println("クライアントからの接続を待ちます。");
            Socket socket = server.accept();
            System.out.println("クライアント接続。");

            int ch;
            InputStream input = socket.getInputStream();        //サーバーから受け取った内容をserver_recv.txtに出力
            while ((ch = input.read()) != 0) {      //クライアントは終了のまーくとして0を返すから
                fos.write(ch);
            }
            OutputStream output = socket.getOutputStream();
            while ((ch = fis.read()) != -1) {
                output.write(ch);
            }
            socket.close();
            System.out.println("通信を終了しました");
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
