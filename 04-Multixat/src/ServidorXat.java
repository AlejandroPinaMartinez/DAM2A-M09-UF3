import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class ServidorXat {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";
    
    private Hashtable<String, GestorClients> clients;
    private boolean sortir;
    private ServerSocket serverSocket;
    
    public ServidorXat() {
        clients = new Hashtable<>();
        sortir = false;
    }
    
    public void servidorAEscoltar() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
    
    public void pararServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al tancar el serverSocket: " + e.getMessage());
        }
    }
    
    public void finalitzarXat() {
        enviarMissatgeGrup(MSG_SORTIR);
        System.out.println("Tancant tots els clients.");
        System.out.println("DEBUG: multicast sortir");
        clients.clear();
        sortir = true;
        pararServidor();
    }
    
    public void afegirClient(GestorClients gestorClient) {
        clients.put(gestorClient.getNom(), gestorClient);
        enviarMissatgeGrup("Entra: " + gestorClient.getNom());
        System.out.println(gestorClient.getNom() + " connectat.");
        System.out.println("DEBUG: multicast Entra: " + gestorClient.getNom());
    }
    
    public void eliminarClient(String nom) {
        if (nom != null && clients.containsKey(nom)) {
            clients.remove(nom);
            System.out.println(nom + " desconnectat.");
        }
    }
    
    public void enviarMissatgeGrup(String missatge) {
        for (GestorClients client : clients.values()) {
            try {
                client.enviarMissatge("", Missatge.getMissatgeGrup(missatge));
            } catch (Exception e) {
                System.err.println("Error enviant missatge de grup a " + client.getNom() + ": " + e.getMessage());
            }
        }
    }
    
    public void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
        if (clients.containsKey(destinatari)) {
            try {
                clients.get(destinatari).enviarMissatge(remitent, Missatge.getMissatgePersonal(destinatari, missatge));
                System.out.println("Missatge personal per (" + destinatari + ") de (" + remitent + "): " + missatge);
            } catch (Exception e) {
                System.err.println("Error enviant missatge personal a " + destinatari + ": " + e.getMessage());
            }
        } else {
            System.err.println("El client " + destinatari + " no existeix.");
        }
    }
    
    public static void main(String[] args) {
        ServidorXat servidorXat = new ServidorXat();
        servidorXat.servidorAEscoltar();
        
        while (!servidorXat.sortir) {
            try {
                Socket clientSocket = servidorXat.serverSocket.accept();
                System.out.println("Client connectat: " + clientSocket.getInetAddress());
                
                GestorClients gestorClient = new GestorClients(clientSocket, servidorXat);
                new Thread(gestorClient).start();
                
            } catch (IOException e) {
                if (!servidorXat.sortir) {
                    System.err.println("Error acceptant client: " + e.getMessage());
                }
            }
        }
        
        servidorXat.pararServidor();
    }
}