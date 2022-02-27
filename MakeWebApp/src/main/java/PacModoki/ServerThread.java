package PacModoki;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.text.*;

public class ServerThread implements Runnable {
    private static final String DOCUMENT_ROOT = "C:\\Apache24\\htdocs";
    private static final String ERROR_DOCUMENT = "C:\\webserver\\error_document";
    private static final String SERVER_NAME = "localhost:8001";
    private Socket socket;

//    private static String readLine(InputStream input) throws Exception {
//        int ch;
//        String ret = "";
//        while ((ch = input.read()) != -1) {
//            if (ch == '\r') {
//
//            } else if (ch == '\n') {
//                break;
//            } else {
//                ret += (char)ch;
//            }
//        }
//        if (ch == -1) {
//            return null;
//        } else {
//            return ret;
//        }
//    }
//
//    private static void writeLine(OutputStream output, String str) throws Exception {
//        for (char ch : str.toCharArray()) {
//            output.write((int)ch);
//        }
//        output.write((int)'\r');
//        output.write((int)'\n');
//    }
//
//    private static String getDateStringUtc() {
//        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
//        df.setTimeZone(cal.getTimeZone());
//        return df.format(cal.getTime()) + "GMT";
//    }
//
//    private static final HashMap<String, String> contentTypeMap =
//        new HashMap<String, String>() {{
//            put("html", "text/html");
//            put("htm", "text/html");
//            put("txt", "text/plain");
//            put("css", "text/css");
//            put("png", "image/png");
//            put("jpg", "jpeg/image/jpeg");
//            put("jpeg", "image/jpeg");
//            put("gif", "image/gif");
//        }
//    };
//
//    private static String getContentType(String ext) {
//        String ret = contentTypeMap.get(ext.toLowerCase());
//        if (ret == null) {
//            return "application/octet-stream";
//        } else {
//            return ret;
//        }
//    }

    @Override
    public void run() {
        OutputStream output = null;
        try {
            InputStream input = socket.getInputStream();

            String line;
            String path = null;
            String ext = null;
            String host = null;
            while ((line = Util.readLine(input)) != null) {
                if (line.equals(""))
                    break;
                if (line.startsWith("GET")) {
                    path = MyURLDecoder.decode(line.split(" ")[1], "UTF-8");
                    String[] tmp = path.split("\\.");
                    ext = tmp[tmp.length - 1];
                } else if (line.startsWith("Host:")) {
                    host = line.substring("Host: ".length());
                }
            }
            if (path == null)
                return;

            if (path.endsWith("/")) {
                path += "index.html";
                ext = "html";
            }
            output = new BufferedOutputStream(socket.getOutputStream());

            FileSystem fs = FileSystems.getDefault();
            Path pathObj = fs.getPath(DOCUMENT_ROOT + path);
            Path realPath;
            try {
                realPath = pathObj.toRealPath();
            } catch (NoSuchFileException ex) {
                SendResponse.sendNotFoundResponse(output, ERROR_DOCUMENT);
                return;
            }
            if (!realPath.startsWith(DOCUMENT_ROOT)) {
                SendResponse.sendNotFoundResponse(output, ERROR_DOCUMENT);
                return;
            } else if (Files.isDirectory(realPath)) {
                String location = "http://"
                        + ((host != null) ? host : SERVER_NAME)
                        + path + "/";
                SendResponse.sendMovePermanentlyResponse(output, location);
                return;
            }
            try (InputStream fis
                         = new BufferedInputStream(Files.newInputStream(realPath))) {
                SendResponse.sendOkResponse(output, fis, ext);
            } catch (FileNotFoundException ex) {
                SendResponse.sendNotFoundResponse(output, ERROR_DOCUMENT);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                socket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    ServerThread(Socket socket) {
        this.socket = socket;
    }

}
