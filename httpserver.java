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

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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
                    System.out.println(clientRequest);
                } else if(clientRequest.startsWith("DELETE")){
                    handleDeleteRequest(splitBySpace[1], mime);
                } else if(clientRequest.startsWith("OPTION")){
                    handleOptionsRequest(splitBySpace[1], mime);
                } else if(clientRequest.startsWith("HEAD")){
                    handleHeadRequest(splitBySpace[1], mime);
                } else if(clientRequest.startsWith("PUT")){
                    handlePutRequest(splitBySpace[1], mime);
                } else {

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
                System.out.println(Paths.get("./cat404.jfif"));
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

    public static void handleOptionsRequest(String filename, String type){

    }

    public static void handlePutRequest(String filename, String type){

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

}


