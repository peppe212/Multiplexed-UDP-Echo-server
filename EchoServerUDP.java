import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

public class EchoServerUDP {

    private final static int BUFFER_SIZE = 2048;
    private final static int PORT = 54321;
    private final static String HOST_NAME = "localhost";

    private class Info{
        ByteBuffer request;
        ByteBuffer response;
        SocketAddress sa;

        public Info(){
            request = ByteBuffer.allocate(BUFFER_SIZE);
            response = null;
            sa = null;
        }
    }

    public void begin() throws IOException {
        SocketAddress serverAddress = new InetSocketAddress(HOST_NAME,PORT);
        DatagramChannel channel = DatagramChannel.open();
        channel.socket().bind(serverAddress);
        channel.configureBlocking(false);

        Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ,new Info());
        System.out.println("Server pronto sulla porta "+PORT);

        while(true) {
            if(selector.select() == 0)
                continue;
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if(!key.isValid())
                    continue;
                if (key.isReadable()) {
                    read(key);
                    key.interestOps(SelectionKey.OP_WRITE);
                } else if (key.isWritable()) {
                    write(key);
                    key.interestOps(SelectionKey.OP_READ);
                }
            }
        }
    }

    private void read(SelectionKey key) throws IOException{
        DatagramChannel channel = (DatagramChannel) key.channel();
        Info info = (Info) key.attachment();
        //mi preparo a leggere il messaggio di echo del client
        while((info.sa = channel.receive(info.request)) == null){
            //nop
        }
        info.request.flip();
        byte[] byteArray = info.request.array();
        String fromClient = new String(byteArray);
        System.out.println("Client "+ info.sa.toString()+ ": "+fromClient.trim());
        info.response = info.request;
    }

    private void write(SelectionKey key) throws IOException{
        DatagramChannel channel = (DatagramChannel) key.channel();
        Info info = (Info) key.attachment();
        //mi preparo a inviare il messaggio del client, come una echo
        while((channel.send(info.response,info.sa)) == 0){
            //nop
        }
        info.response.clear();
    }
}
