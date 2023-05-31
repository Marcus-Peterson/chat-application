import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

/*Vi deklarerar en Socket för att kommunicera med servern, en JTextArea för att visa meddelanden,
 en String för att hålla klientens namn, och en PrintWriter för att skicka meddelanden till servern.*/
public class Client {
    private Socket socket;
    private JTextArea textArea;
    private String name;
    private PrintWriter out;

    public Client() {
        /*Inom konstruktorn för Client, försöker vi att ansluta till servern på "localhost" på port 12540.
        Sedan skapar vi en PrintWriter som vi kan använda för att skicka meddelanden till servern.*/
        try {
            socket = new Socket("localhost", 12540);
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Frågar användaren att ange ett chat-alias genom att använda en dialogruta.
        name = JOptionPane.showInputDialog("Enter your chat alias");

        JFrame frame = new JFrame("Chat");
        textArea = new JTextArea();
        JTextField textField = new JTextField();
        JButton exitButton = new JButton("Exit");
        /*Vi skapar flera grafiska komponenter: en ram för hela chattapplikationen,
        en textArea för att visa meddelanden, ett textField för att skriva in nya meddelanden, och en knapp för att avsluta applikationen.*/
        frame.setLayout(new BorderLayout());

        frame.add(textArea, BorderLayout.CENTER);
        frame.add(textField, BorderLayout.SOUTH);
        frame.add(exitButton, BorderLayout.NORTH);

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = name + ": " + textField.getText();
                out.println(message);
                textField.setText("");
            }
        });
/*Vi lägger till en actionListener till textField. När användaren trycker på Enter-tangenten,
tar vi texten från textField, lägger till användarens namn framför den,
och skickar det som ett meddelande till servern. Sedan rensar vi textField.*/
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    socket.close();
                    System.exit(0);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        /*Vi skapar en ny tråd med en Listener som argument, och startar den.
        Listener kommer att lyssna på inkommande meddelanden från servern*/
        Thread listenerThread = new Thread(new Listener(socket, textArea));
        listenerThread.start();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setVisible(true);
    }

    class Listener implements Runnable {
        private Socket socket;
        private JTextArea textArea;
        private BufferedReader in;
/*Varje Listener har en Socket för att kommunicera med servern, en JTextArea för att visa meddelanden,
 och en BufferedReader för att läsa inkommande meddelanden från servern.*/
        public Listener(Socket socket, JTextArea textArea) {
            this.socket = socket;
            this.textArea = textArea;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /* Listener-konstruktorn tar en Socket och en JTextArea som argument, sparar dem,
         och försöker att skapa en BufferedReader som den kan använda för att läsa inkommande meddelanden från servern.*/
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    textArea.append(message + "\n");
                }
            } catch (IOException e) {
                System.err.println("Error in Listener thread: " + e);
            }
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}

/*
Ytterligare dokumentation:
ServerSocket och Socket klasserna: Dessa klasser tillhandahåller gränssnittet för att använda TCP i Java. En ServerSocket används för att lyssna på inkommande TCP-förbindelser, och en Socket representerar en enskild TCP-förbindelse mellan två maskiner.

I Server klassen skapas en ServerSocket för att acceptera inkommande anslutningar från klienter. För varje anslutning som tas emot skapas en ny Socket och en ny ClientHandler tråd för att hantera den anslutningen.

I Client klassen skapas en Socket för att ansluta till servern.

BufferedReader och PrintWriter klasserna: Dessa klasser används för att läsa och skriva data över en TCP-förbindelse. En BufferedReader används för att läsa data som har skickats över en Socket, och en PrintWriter används för att skicka data över en Socket.

I både Server och Client klasserna skapas en BufferedReader och en PrintWriter för varje Socket.



*/

