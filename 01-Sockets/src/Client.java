import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 7777; 
    
    private Socket socket;
    private PrintWriter out;

    public void conecta() {
        try {
            socket = new Socket(HOST, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connectat a servidor en " + HOST + ": " + PORT);
        } catch (IOException e) {
            System.err.println("Error al connectar amb el servidor: " + e.getMessage());
        }
    }

    public void envia() {
        try (Scanner scanner = new Scanner(System.in)) {
            String missatge;
            System.out.println("Prem Enter per tancar el servidor...");
            missatge = scanner.nextLine();
            while (missatge != null && !missatge.isEmpty()){
                out.println(missatge);
                System.out.println("Enviat al servidor: " + missatge);
                System.out.println("Prem Enter per tancar el servidor...");
                missatge = scanner.nextLine();
            }
        } catch (Exception e) {
            System.err.println("Error en la comunicació: " + e.getMessage());
        }
    }

    public void tanca() {
        try {
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("Client tancat");
        } catch (IOException e) {
            System.err.println("Error al tancar la conexió: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.conecta();
        client.envia();
        client.tanca();
    }
}

