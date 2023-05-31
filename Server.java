import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();

/* Vi deklarerar en ServerSocket som kommer att lyssna på inkommande anslutningar,
     och en lista med ClientHandlers för att hålla reda på alla anslutna klienter.*/

    public Server() throws IOException {
        serverSocket = new ServerSocket(12540);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket);
            clients.add(clientHandler);
            new Thread(clientHandler).start();
        }
    }
/*Server-konstruktorn skapar en ny ServerSocket som lyssnar på port 12540.
Sedan går den in i en oändlig loop där den accepterar inkommande anslutningar,
skapar en ny ClientHandler för varje anslutning,
lägger till den i listan över klienter, och startar en ny tråd för att hantera klienten.*/
    class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

/* Varje ClientHandler har en Socket för att kommunicera med klienten,
samt en BufferedReader och en PrintWriter för att läsa och skriva data till klienten.
 */
        public ClientHandler(Socket socket) throws IOException {
            this.clientSocket = socket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        }
        /* ClientHandler-konstruktorn tar emot en Socket som argument, sparar den,
        och skapar BufferedReader och PrintWriter objekt för att hantera datakommunikation.
         */
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    for (ClientHandler client : clients) {
                        Date date = new Date();
                        System.out.println("Server recieved message :" + date.toString());

                        client.out.println(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }
    /*Slutligen i vår main läser vi in inkommande meddelanden från klienten och skickar dem till alla anslutna klienter.*/

    public static void main(String[] args) {
        try {
            new Server();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
