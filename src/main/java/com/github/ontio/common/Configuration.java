package com.github.ontio.common;

public class Configuration {
    public int N;
    public int C;
    public int K;
    public int L;
    public int BlockMsgDelay;
    public int HashMsgDelay;
    public int PeerHandshakeTimeout;
    public int MaxBlockChangeView;
    public Configuration(int N,int C,int K,int L,int BlockMsgDelay, int HashMsgDelay,int PeerHandshakeTimeout, int MaxBlockChangeView){
        this.N = N;
        this.C = C;
        this.K = K;
        this.L = L;
        this.BlockMsgDelay = BlockMsgDelay;
        this.HashMsgDelay = HashMsgDelay;
        this.PeerHandshakeTimeout = PeerHandshakeTimeout;
        this.MaxBlockChangeView = MaxBlockChangeView;
    }
}
