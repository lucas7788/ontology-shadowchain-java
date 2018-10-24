package demo;

import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.server.ShadowChainServer;

import java.io.IOException;

public class Demo3 {
    public static void main(String[] args) throws InterruptedException, ConnectorException, IOException {
        ShadowChainServer server = new ShadowChainServer(ShadowChainServer.NodeUrl1);
        server.startServer(10283);
    }
}
