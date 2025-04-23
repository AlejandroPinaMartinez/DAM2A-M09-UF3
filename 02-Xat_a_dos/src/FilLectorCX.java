import java.io.*;

public class FilLectorCX extends Thread {
    private ObjectInputStream in;

    public FilLectorCX(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String missatge = (String) in.readObject();
                System.out.println("Rebut: " + missatge);
                
                if (missatge.equals("sortir")) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("El servidor ha tancat la connexió.");
        }
    }
}