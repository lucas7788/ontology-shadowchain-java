package demo;

import com.github.ontio.server.ShadowChainServer;
import com.github.ontio.shadowexception.ShadowException;

public class ServerDemo {

    public static void main(String[] args) throws ShadowException {
        ShadowChainServer server = new ShadowChainServer();
        server.readConfig("config.json");
        server.startServer();
    }
}
