import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.nio.file.Paths;

public class httpserver {
    private static String response;
    private static int port;
    private static Map<String, String> mimeType;
    public static void main(String[] args) {
        port = 80;
        // A reference of the client socket
        Socket socket;
        //file type conversion
        mimeType = new HashMap<String, String>();
        mimeType.put("txt", "text/plain");
        mimeType.put("html", "text/html");


        try {
            // Setup the server socket
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Web Server is starting up, listening at port " + port);

            while (true) {
                // Make the server socket wait for the next client request
                socket = serverSocket.accept();
                // Local reader from the client
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Assign http request
                String clientRequest = reader.readLine();
                System.out.println(clientRequest);
                if (clientRequest.startsWith("GET")) {
                    response = handleGetRequest(clientRequest);
                    System.out.println(response);
//                } else if (clientRequest.startsWith("POST")) {
//                    response = handlePostRequest(clientRequest);
//                } else if(clientRequest.startsWith("PUT")){
//                    response = handlePutRequest(clientRequest);
//                } else if(clientRequest.startsWith("DELETE")){
//                    response = handleDeleteRequest(clientRequest);
//                } else {
//
//                }
//
//                OutputStream outputStream = clientSocket.getOutputStream();
//                outputStream.write(response.getBytes());
//                outputStream.flush();
//
//                System.out.println("Client Disconnected");
//                clientSocket.close();
//                reader.close();
                }
            }
        } catch (IOException ex) {
            //Handle the exception
            System.out.println(ex);
        }
    }

    public static String handleGetRequest(String request){
        String[] splitBySpace = request.split(" ");
        System.out.println(splitBySpace[1]);
        String[] splitByPeriod = splitBySpace[1].split("\\.");
        String mime = mimeType.get(splitByPeriod[0]);
        Path fileName = Paths.get("./" + splitBySpace[1]);
        System.out.println(fileName);
        String strResponse = "";

        try {
            if(!Files.exists(fileName)){
                byte[] bytes = Files.readAllBytes(Paths.get("cat404.jfif"));
                String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
                String html = "<html><body><h1>Error 404</h1>" +
                        "<p>File Found</p>" +
                        "<img src='data:image/jpeg;base64,"
                        + base64 + "'></body></html>";
                return  "HTTP/1.1 404 Not Found \nFile not found\n\n" + html;
            } else {
                strResponse = Files.readString(fileName);
            }
        } catch (IOException ex) {
            //Handle the exception
            System.out.println(ex);
        }


        return strResponse;
    }


}
