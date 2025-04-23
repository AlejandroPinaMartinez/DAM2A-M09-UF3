import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connecta() throws IOException {
        socket = new Socket("localhost", 9999);
        System.out.println("Client connectat a localhost:9999");
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("Flux d'entrada i sortida creat.");
    }

    public void enviarMissatge(String missatge) throws IOException {
        out.writeObject(missatge);
        out.flush();
        System.out.println("Enviant missatge: " + missatge);
    }

    public void tancarClient() throws IOException {
        if (out != null) out.close();
        if (in != null) in.close();
        if (socket != null) socket.close();
        System.out.println("Client tancat.");
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        Scanner scanner = new Scanner(System.in);
        
        try {
            client.connecta();
            
            FilLectorCX filLector = new FilLectorCX(client.in);
            filLector.start();
            System.out.println("Fil de lectura iniciat");
            
            System.out.print("Rebut: Escriu el teu nom: ");
            String nom = scanner.nextLine();
            client.enviarMissatge(nom);
            
            String missatge;
            while (true) {
                System.out.print("Missatge ('sortir' per tancar): ");
                missatge = scanner.nextLine();
                client.enviarMissatge(missatge);
                
                if (missatge.equals("sortir")) {
                    System.out.println("Tancant client...");
                    break;
                }
            }
            
            scanner.close();
            client.tancarClient();
            
        } catch (IOException e) {
            System.out.println("El servidor ha tancat la connexió.");
        }
    }
}