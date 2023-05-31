import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
//Koden är identisk med Client.java
public class Client2 {
    private Socket socket;
    private JTextArea textArea;
    private String name;
    private PrintWriter out;

    public Client2() {
        try {
            socket = new Socket("localhost", 12540);
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        name = JOptionPane.showInputDialog("Enter your chat alias");

        JFrame frame = new JFrame("Chat");
        textArea = new JTextArea();
        JTextField textField = new JTextField();
        JButton exitButton = new JButton("Exit");

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

        public Listener(Socket socket, JTextArea textArea) {
            this.socket = socket;
            this.textArea = textArea;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
        new Client2();
    }
}

