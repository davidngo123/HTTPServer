import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.nio.file.Paths;


public class httpserver {

    static int port = 80;
    static OutputStream os;
    static Socket clientSocket;
    private static Map<String, String> mimeType;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server running on port: " + port);

        mimeType = new HashMap<String, String>();
        mimeType.put("txt", "text/plain");
        mimeType.put("html", "text/html");


        while (true) {
            try {
                clientSocket = serverSocket.accept();
                System.out.println("Client Connected");

                os = clientSocket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Assign http request
                String clientRequest = reader.readLine();

                String[] splitBySpace = clientRequest.split(" ");
                String[] splitByPeriod = splitBySpace[1].split("\\.");
                String mime = mimeType.get(splitByPeriod[0]);
                if(clientRequest.startsWith("GET")) {
                    handleGetRequest(splitBySpace[1], mime, false);
                } else if(clientRequest.startsWith("POST")){
                    handlePostRequest(splitBySpace[1], mime, reader);
                } else if(clientRequest.startsWith("DELETE")){
                    handleDeleteRequest(splitBySpace[1], mime);
                } else if(clientRequest.startsWith("HEAD")){
                    handleHeadRequest(splitBySpace[1], mime);
                } else if(clientRequest.startsWith("PUT")){
                    handlePutRequest(splitBySpace[1], mime, reader);
                } else {
                    byte[] bytes = Files.readAllBytes(Paths.get("./cat400.jfif"));
                    String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
                    String html = "<html><body><h1>Error 400</h1>" +
                            "<p>Bad Request/p>" +
                            "<img src='data:image/jpeg;base64,"
                            + base64 + "'></body></html>";
                    sendResponse(clientSocket,
                        "400 BAD REQUEST",
                            html.length(),
                            mimeType.get(mime),
                            false,
                            html.getBytes()
                    );
                }

            } catch(IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    public static void handleDeleteRequest(String filename, String type){

        Path fileName = Paths.get("./" + filename);

        try {
            if(!Files.exists(fileName)){
                byte[] bytes = Files.readAllBytes(Paths.get("./cat404.jfif"));
                String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
                String html = "<html><body><h1>Error 404</h1>" +
                        "<p>File Not Found</p>" +
                        "<img src='data:image/jpeg;base64,"
                        + base64 + "'></body></html>";

                sendResponse(clientSocket,
                        "404 NOT FOUND",
                        html.length(),
                        mimeType.get(type),
                        false,
                        html.getBytes()
                );
            } else {
                Files.delete(fileName);
                byte[] bytes = Files.readAllBytes(Paths.get("./200cat.jpg"));
                String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
                String content = "<html><body><h1>Success 200</h1>" +
                        "<p>File Found</p>" +
                        "<img src='data:image/jpeg;base64,"
                        + base64 + "'></body></html>";
                sendResponse(clientSocket,
                        "200 OK",
                        content.length(),
                        mimeType.get(type),
                        false,
                        content.getBytes()
                );
            }
        } catch (IOException ex) {
            //Handle the exception
            System.out.println(ex);
        }
    }


    public static void handleHeadRequest(String filename, String type) {
        handleGetRequest(filename, type, true);
    }


    public static void handlePutRequest(String filename, String type, BufferedReader reader){
        try {
            String line = "";
            String body = "";
            while((line = reader.readLine()) != null) {
                if(line.contains("Content-Length")) {
                    break;
                }
            }
            int cLength = Integer.valueOf(line.split(" ")[1]);
            System.out.println(cLength);
            reader.readLine();
            for (int i = 0, c = 0; i <= cLength + 1; i++) {
                c = reader.read();
                body += (char)c;
            }
            String catResponse = "";
            String status = "";
            File file = new File("./" + filename);
            if(file.createNewFile()) {
                catResponse = "./201cat.jpg";
                status = "201 Created";
            } else {
                catResponse = "./200cat.jpg";
                status = "200 OK";
            }
            Writer fileWriter = new FileWriter("./" + filename, false);
            fileWriter.write(body);
            fileWriter.flush();
            fileWriter.close();
            byte[] bytes = Files.readAllBytes(Paths.get(catResponse));
            String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
            String html = "<html><body><h1>" + status + "</h1>" +
                    "<img src='data:image/jpeg;base64,"
                    + base64 + "'></body></html>";
            sendResponse(clientSocket,
                    status,
                    html.length(),
                    mimeType.get(type),
                    false,
                    html.getBytes()
            );

        } catch (IOException ex) {
            //Handle the exception
            System.out.println(ex);
        }

    }
    public static void handlePostRequest(String filename, String type, BufferedReader reader){
        Path fileName = Paths.get("./" + filename);
        try {
            if(!Files.exists(fileName)) {
                byte[] bytes = Files.readAllBytes(Paths.get("./cat404.jfif"));
                String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
                String html = "<html><body><h1>Error 404</h1>" +
                        "<p>File Not Found</p>" +
                        "<img src='data:image/jpeg;base64,"
                        + base64 + "'></body></html>";

                sendResponse(clientSocket,
                        "404 NOT FOUND",
                        html.length(),
                        mimeType.get(type),
                        false,
                        html.getBytes()
                );
            } else {
                String line = "";
                String body = "";
                while((line = reader.readLine()) != null) {
                    if(line.contains("Content-Length")) {
                        break;
                    }
                }
                int cLength = Integer.valueOf(line.split(" ")[1]);
                System.out.println(cLength);
                reader.readLine();
                for (int i = 0, c = 0; i <= cLength + 1; i++) {
                    c = reader.read();
                    body += (char)c;
                }
                Writer fileWriter = new FileWriter("./" + filename, true);
                fileWriter.write(body);
                fileWriter.flush();
                fileWriter.close();

                byte[] bytes = Files.readAllBytes(Paths.get("./200cat.jpg"));
                String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
                String fileContent = Files.readString(fileName);
                String html = "<html><body><h1>Success 200</h1>" +
                        "<p>File Updated</p>" +
                        "<img src='data:image/jpeg;base64,"
                        + base64 + "'></body></html>";

                String content = fileContent+ "\r\n\r\n" + html;

                sendResponse(clientSocket,
                        "200 OK",
                        content.length(),
                        mimeType.get(type),
                        false,
                        content.getBytes()
                );
            }
        } catch (IOException ex) {
            //Handle the exception
            System.out.println(ex);
        }
    }
    public static void handleGetRequest(String filename, String type, boolean head){
        Path fileName = Paths.get("./" + filename);
        try {
            if(!Files.exists(fileName)) {
                byte[] bytes = Files.readAllBytes(Paths.get("./cat404.jfif"));
                String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
                String html = "<html><body><h1>Error 404</h1>" +
                        "<p>File Not Found</p>" +
                        "<img src='data:image/jpeg;base64,"
                        + base64 + "'></body></html>";

                sendResponse(clientSocket,
                        "404 NOT FOUND",
                        html.length(),
                        mimeType.get(type),
                        head,
                        html.getBytes()
                );
            } else {
                byte[] bytes = Files.readAllBytes(Paths.get("./200cat.jpg"));
                String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
                String fileContent = Files.readString(fileName);
                String html = "<html><body><h1>Success 200</h1>" +
                        "<p>File Found</p>" +
                        "<img src='data:image/jpeg;base64,"
                        + base64 + "'></body></html>";

                String content = fileContent+ "\r\n\r\n" + html;

                sendResponse(clientSocket,
                        "200 OK",
                        content.length(),
                        mimeType.get(type),
                        head,
                        content.getBytes()
                );
            }
        } catch (IOException ex) {
            //Handle the exception
            System.out.println(ex);
        }
    }


    private static void sendResponse(Socket client,
                                     String status,
                                     int contentLength,
                                     String contentType,
                                     boolean head,
                                     byte[] content) throws IOException {

        if(head) {
            contentLength = 0;
        }
        OutputStream output = client.getOutputStream();
        os.write(("HTTP/1.1 "+ status + "\r\n").getBytes());
        os.write(("Content-type: " + contentType).getBytes());
        os.write("\r\n".getBytes());
        os.write(("Content-length: " + contentLength).getBytes());
        os.write("\r\n\r\n".getBytes());
        if(!head) {
            os.write(content);
        }

        os.flush();
        os.close();
        client.close();
    }

    private static void parseBody(){

    }

}


