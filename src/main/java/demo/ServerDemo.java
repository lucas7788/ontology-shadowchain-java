package demo;

import com.github.ontio.server.ShadowChainServer;
import com.github.ontio.shadowexception.ShadowException;

public class ServerDemo {

    public static void main(String[] args) throws ShadowException, InterruptedException {
        ShadowChainServer server = ShadowChainServer.getInstance();
        server.readConfig("config.json");
        server.startServer();
    }
}
