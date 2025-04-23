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
                System.out.println("Rebut: " + missatge);
                
                if (missatge.equals(MSG_SORTIR)) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("El client ha tancat la connexió.");
        }
    }
}