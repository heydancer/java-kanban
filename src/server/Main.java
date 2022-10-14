package server;

import manager.http.HTTPTaskServer;


import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        new KVServer().start();
        new HTTPTaskServer().start();
    }
}
