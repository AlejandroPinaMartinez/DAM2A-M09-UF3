import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestorClients implements Runnable {
    private Socket client;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ServidorXat servidorXat;
    private String nom;
    private boolean sortir;
    
    public GestorClients(Socket client, ServidorXat servidorXat) {
        this.client = client;
        this.servidorXat = servidorXat;
        this.sortir = false;
        
        try {
            this.oos = new ObjectOutputStream(client.getOutputStream());
            this.ois = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            System.err.println("Error inicialitzant streams per client: " + e.getMessage());
        }
    }
    
    public String getNom() {
        return nom;
    }
    
    @Override
    public void run() {
        try {
            String missatge;
            while (!sortir) {
                missatge = (String) ois.readObject();
                processaMissatge(missatge);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al fil del client: " + e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                System.err.println("Error tancant socket del client: " + e.getMessage());
            }
        }
    }
    
    public void enviarMissatge(String remitent, String missatge) throws IOException {
        if (oos != null) {
            oos.writeObject(missatge);
            oos.flush();
        }
    }
    
    public void processaMissatge(String missatgeCru) {
        String codi = Missatge.getCodiMissatge(missatgeCru);
        if (codi == null) return;
        
        String[] parts = Missatge.getPartsMissatge(missatgeCru);
        if (parts == null || parts.length < 2) return;
        
        switch (codi) {
            case Missatge.CODI_CONECTAR:
                this.nom = parts[1];
                servidorXat.afegirClient(this);
                break;
            case Missatge.CODI_SORTIR_CLIENT:
                sortir = true;
                servidorXat.eliminarClient(this.nom);
                break;
            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                servidorXat.finalitzarXat();
                break;
            case Missatge.CODI_MSG_PERSONAL:
                if (parts.length >= 3) {
                    String destinatari = parts[1];
                    String missatge = parts[2];
                    servidorXat.enviarMissatgePersonal(destinatari, this.nom, missatge);
                }
                break;
            case Missatge.CODI_MSG_GRUP:
                if (parts.length >= 2) {
                    String missatge = parts[1];
                    servidorXat.enviarMissatgeGrup("(" + this.nom + "): " + missatge);
                }
                break;
            default:
                System.err.println("Codi d'operació desconegut: " + codi);
        }
    }
}