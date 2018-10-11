# ontology-shadowchain-java

* [1.Overview](#1.Overview)
* [2.Setting up the development environment](#2.Setting up the development environment)
* [3.Usage](#3.Usage)

## 1.Overview
We can use it to monitor the status update of the main network to update the shadow chain status.

## 2.Setting up the development environment

There are a few technical requirements before we start. Please install the following:
* Python3.7

## 3.Usage

1. get OntologySdk instance

```
String ip = "http://127.0.0.1";
String restUrl = ip + ":" + "20334";
String rpcUrl = ip + ":" + "20336";
String wsUrl = ip + ":" + "20335";
OntSdk wm = OntSdk.getInstance();
wm.setRpc(rpcUrl);
wm.setRestful(restUrl);
wm.setWesocket(wsUrl, lock);
wm.setDefaultConnect(wm.getWebSocket());
wm.openWalletFile("OntAssetDemo.json");
```

2. get ShadowChainServer instance

```
ShadowChainServer server = new ShadowChainServer(sdk, lock);
```

3. start server

```
server.startServer();
```

