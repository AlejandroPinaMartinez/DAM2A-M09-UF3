import java.io.*;
import java.net.*;

public class Servidor {
    private static final int PORT = 7777;
    private static final String HOST = "localhost";
    private ServerSocket srvSocket;
    private Socket clientSocket;

    public Servidor() {
        try {
            srvSocket = new ServerSocket(PORT);
            System.out.println("Servidor en marxa a " + HOST + ": " + PORT);
        } catch (IOException e) {
            System.out.println("Error en crear el servidor: " + e.getMessage());
        }
    }

    public void connecta() {
        try {
            System.out.println("Esperant connexions a " + HOST + ": " + PORT);
            clientSocket = srvSocket.accept();
            System.out.println("Client connectat: /127.0.0.1");
        } catch (IOException e) {
            System.out.println("Error en acceptar connexió: " + e.getMessage());
        }
    }

    public void repDades() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String missatge;
            while ((missatge = reader.readLine()) != null) {
                System.out.println("Rebut: " + missatge);
            }
        } catch (IOException e) {
            System.out.println("Error en rebre dades: " + e.getMessage());
        }
    }

    public void tanca() {
        try {
            if (clientSocket != null) clientSocket.close();
            if (srvSocket != null) srvSocket.close();
            System.out.println("Servidor tancat.");
        } catch (IOException e) {
            System.out.println("Error en tancar el servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.connecta();
        servidor.repDades();
        servidor.tanca();
    }

}