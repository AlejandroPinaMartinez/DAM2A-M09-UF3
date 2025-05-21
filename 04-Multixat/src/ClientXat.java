import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientXat implements Runnable {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean sortir;
    
    public void connecta() {
        try {
            socket = new Socket(ServidorXat.HOST, ServidorXat.PORT);
            System.out.println("Client connectat a " + ServidorXat.HOST + ":" + ServidorXat.PORT);
            
            // Inicialitzar stream de sortida
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (UnknownHostException e) {
            System.err.println("Host desconegut: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error I/O: " + e.getMessage());
        }
    }
    
    public void enviarMissatge(String missatge) {
        try {
            if (oos != null) {
                System.out.println("Enviant missatge: " + missatge);
                oos.writeObject(missatge);
                oos.flush();
            } else {
                System.out.println("oos null. Sortint...");
                sortir = true;
            }
        } catch (IOException e) {
            System.err.println("Error enviant missatge: " + e.getMessage());
            sortir = true;
        }
    }
    
    public void tancarClient() {
        System.out.println("Tancant client...");
        try {
            if (ois != null) {
                ois.close();
                System.out.println("Flux d'entrada tancat.");
            }
            if (oos != null) {
                oos.close();
                System.out.println("Flux de sortida tancat.");
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error tancant client: " + e.getMessage());
        }
    }
    
    @Override
    public void run() {
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("DEBUG: Iniciant rebuda de missatges...");
            
            while (!sortir) {
                try {
                    String missatgeCru = (String) ois.readObject();
                    String codi = Missatge.getCodiMissatge(missatgeCru);
                    
                    if (codi == null) continue;
                    
                    String[] parts = Missatge.getPartsMissatge(missatgeCru);
                    if (parts == null) continue;
                    
                    switch (codi) {
                        case Missatge.CODI_SORTIR_TOTS:
                            sortir = true;
                            break;
                        case Missatge.CODI_MSG_PERSONAL:
                            if (parts.length >= 3) {
                                System.out.println("Missatge de (" + parts[1] + "): " + parts[2]);
                            }
                            break;
                        case Missatge.CODI_MSG_GRUP:
                            if (parts.length >= 2) {
                                System.out.println(parts[1]);
                            }
                            break;
                        default:
                            System.err.println("Error: codi desconegut " + codi);
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Error de classe: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("Error rebent missatge. Sortint...");
                    sortir = true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error inicialitzant flux d'entrada: " + e.getMessage());
        } 
    }
    
    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("1.- Conectar al servidor (primer pass obligatori)");
        System.out.println("2.- Enviar missatge personal");
        System.out.println("3.- Enviar missatge al grup");
        System.out.println("4.- (o línia en blanc)-> Sortir del client");
        System.out.println("5.- Finalitzar tothom");
        System.out.println("---------------------");
    }
    
    public String getLinea(Scanner scanner, String missatge, boolean obligatori) {
        String linia = "";
        boolean valid = false;
        
        while (!valid) {
            System.out.print(missatge);
            linia = scanner.nextLine().trim();
            
            if (linia.isEmpty() && obligatori) {
                System.out.println("Aquest camp és obligatori. Torna a intentar.");
            } else {
                valid = true;
            }
        }
        
        return linia;
    }
    
    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        client.connecta();
        
        Thread threadRebre = new Thread(client);
        threadRebre.start();
        
        client.ajuda();
        
        Scanner scanner = new Scanner(System.in);
        String linia;
        String missatgeCodificat = "";
        
        while (!client.sortir) {
            linia = scanner.nextLine().trim();
            
            if (linia.isEmpty() || linia.equals("4")) {
                missatgeCodificat = Missatge.getMissatgeSortirClient("Adéu");
                client.sortir = true;
            } else {
                switch (linia) {
                    case "1":
                        String nom = client.getLinea(scanner, "Introdueix el nom: ", true);
                        missatgeCodificat = Missatge.getMissatgeConectar(nom);
                        break;
                    case "2":
                        String destinatari = client.getLinea(scanner, "Destinatari:: ", true);
                        String missatge = client.getLinea(scanner, "Missatge a enviar: ", true);
                        missatgeCodificat = Missatge.getMissatgePersonal(destinatari, missatge);
                        break;
                    case "3":
                        missatge = client.getLinea(scanner, "Missatge a enviar: ", true);
                        missatgeCodificat = Missatge.getMissatgeGrup(missatge);
                        break;
                    case "5":
                        missatgeCodificat = Missatge.getMissatgeSortirTots("Adéu");
                        client.sortir = true;
                        break;
                    default:
                        System.out.println("Opció no vàlida");
                        client.ajuda();
                        continue;
                }
            }
            
            client.enviarMissatge(missatgeCodificat);
            if (!client.sortir) {
                client.ajuda();
            }
        }
        
        scanner.close();
        client.tancarClient();
        System.exit(0);
    }
}