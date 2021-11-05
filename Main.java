import java.io.IOException;

/**
 *
 *@author Giuseppe Muschetta
 */
public class Main {

    public static void main(String[] args) {

        EchoServerUDP server = new EchoServerUDP();
        try {

            //START
            server.begin();

        }catch(IOException io){
            io.printStackTrace();
        }
    }
}
