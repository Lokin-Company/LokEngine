package ru.lokincompany.lokengine.network.tcp.client;

import ru.lokincompany.lokengine.network.tcp.handlers.defaulthandles.DefaultTCPClientHandler;
import ru.lokincompany.lokengine.tools.Logger;

import java.io.*;
import java.net.Socket;

public class TCPClient {

    public Socket socket;
    public TCPClientHandler clientHandler;
    private BufferedReader fromServer;
    private BufferedWriter toServer;

    public TCPClient(String address, TCPClientHandler clientHandler, int port) throws IOException {
        socket = new Socket(address, port);
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        toServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.clientHandler = clientHandler;
        clientHandler.connected(fromServer, toServer, socket);
    }

    public TCPClient(Socket socket, TCPClientHandler clientHandler) throws IOException {
        this.socket = socket;
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        toServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.clientHandler = clientHandler;
        clientHandler.connected(fromServer, toServer, socket);
    }

    public TCPClient(Socket socket) throws IOException {
        this(socket, new DefaultTCPClientHandler());
    }

    public TCPClient(String address, int port) throws IOException {
        this(address, new DefaultTCPClientHandler(), port);
    }

    public void close() {
        try {
            fromServer.close();
            toServer.close();
            socket.close();
        } catch (IOException e) {
            Logger.warning("Fail close Client!", "LokEngine_TCPClient");
            Logger.printThrowable(e);
        }
    }

}
