import java.io.*;

public class FilServidorXat extends Thread {
    private ObjectInputStream in;
    private static final String MSG_SORTIR = "sortir";

    public FilServidorXat(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String missatge = (String) in.readObject();
                if (missatge.equals(MSG_SORTIR)) {
                    break;
                }
                System.out.println("Rebut: " + missatge);
            }
        } catch (IOException | ClassNotFoundException e) {
            // Evitar imprimir mensajes no deseados
        }
    }
}
