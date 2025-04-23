import java.io.*;
import java.net.*;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String MSG_SORTIR = "sortir";
    private ServerSocket serverSocket;

    public void iniciarServidor() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
    }

    public void pararServidor() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("Servidor aturat.");
        }
    }

    public String getNom(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return (String) in.readObject();
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        
        try {
            servidor.iniciarServidor();
            Socket clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());
            
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            
            String nomClient = servidor.getNom(in);
            System.out.println("Nom rebut: " + nomClient);
            
            FilServidorXat fil = new FilServidorXat(in);
            System.out.println("Fil de xat creat.");
            System.out.println("Fil de " + nomClient + " iniciat");
            fil.start();
            
            BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
            String missatge;
            
            while (true) {
                System.out.print("Missatge ('sortir' per tancar): ");
                missatge = consola.readLine();
                out.writeObject(missatge);
                out.flush();
                if (missatge.equals(MSG_SORTIR)) {
                    System.out.println("Fil de xat finalitzat.");
                    System.out.println(missatge); 
                    break;
                }
                
            }
            fil.join();
            clientSocket.close();
            servidor.pararServidor();
            
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}