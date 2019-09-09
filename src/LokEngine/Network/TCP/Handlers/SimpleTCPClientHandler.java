package LokEngine.Network.TCP.Handlers;

import LokEngine.Network.TCP.Client.TCPClientHandler;
import LokEngine.Network.TCP.TCPTools;
import LokEngine.Tools.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SimpleTCPClientHandler implements TCPClientHandler {

    protected BufferedReader fromServer;
    protected BufferedWriter toServer;

    @Override
    public void connected(BufferedReader fromServer, BufferedWriter toServer) {
        this.fromServer = fromServer;
        this.toServer = toServer;
    }

    public String getMessage(){
        try {
            return TCPTools.getMessage(fromServer);
        } catch (IOException e) {
            Logger.warning("Fail get message from server", "LokEngine_SimpleTCPClientHandler");
            Logger.printException(e);
        }
        return null;
    }

    public void sendMessage(String message){
        try {
            TCPTools.sendMessage(message,toServer);
        } catch (IOException e) {
            Logger.warning("Fail send message to server", "LokEngine_SimpleTCPClientHandler");
            Logger.printException(e);
        }
    }

    @Override
    public void disconnected() {}
}
