import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class ClientMain {

    public static int SERVER_PORT = 54321;
    public static String SERVER_HOST_NAME = "localhost";
    public final static int BUFFER_SIZE = 2048;

    public static void main(String[] args) throws IOException {

        DatagramSocket clientSocket = new DatagramSocket();
        SocketAddress serverAddress = new InetSocketAddress(SERVER_HOST_NAME, SERVER_PORT);

        //mi preparo gli streams di lettura da tastiera
        BufferedReader tastiera = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Type 'stop' to terminate the communication\n");
        while(true){
            String keyboardMessage = tastiera.readLine();
            if(keyboardMessage.equalsIgnoreCase("stop")){
                clientSocket.close();
                break;
            }
            else{
                //operazione di scrittura verso il client
                byte[] sendArray = keyboardMessage.getBytes(StandardCharsets.UTF_8);
                DatagramPacket sendPacket = new DatagramPacket(sendArray, sendArray.length, serverAddress);
                clientSocket.send(sendPacket);

                //operazione di lettura dal server:
                byte[] receiveArray = new byte[BUFFER_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(receiveArray, receiveArray.length);
                clientSocket.receive(receivePacket);
                String echoed = new String(receivePacket.getData());
                System.out.println("Server: "+echoed.trim());
            }
        }

        clientSocket.close();
        System.out.println("termino l'esecuzione...");

    }
}
