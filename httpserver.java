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
                System.out.println(splitBySpace[1]);
                String[] splitByPeriod = splitBySpace[1].split("\\.");
                String mime = mimeType.get(splitByPeriod[0]);
                Path fileName = Paths.get("./" + splitBySpace[1]);
                System.out.println(fileName);
                if(clientRequest.startsWith("GET")) {
                    handleGetRequest(splitBySpace[1], mime);
                }
            } catch(IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    public static void handleGetRequest(String filename, String type){
        Path fileName = Paths.get("./" + filename);
        System.out.println(fileName);

        try {
            if(!Files.exists(fileName)){
                sendResponse(clientSocket,
                        "404 NOT FOUND",
                        0,
                        mimeType.get(type),
                        "".getBytes()
                );
            } else {
                String fileContent = Files.readString(fileName);
                String content = fileContent;

                sendResponse(clientSocket,
                        "200 OK",
                        ((fileContent).length()),
                        mimeType.get(type),
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
                                     byte[] content) throws IOException {


        OutputStream output = client.getOutputStream();
        os.write(("HTTP/1.1 "+ status + "\r\n").getBytes());
        os.write(("Content-type: " + contentType).getBytes());
        os.write("\r\n".getBytes());
        os.write(("Content-length: " + contentLength).getBytes());
        os.write("\r\n\r\n".getBytes());
        os.write(content);
        os.flush();
        os.close();
        client.close();
    }

}


