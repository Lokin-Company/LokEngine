package LokEngine.Network.TCP.Handlers.Default;

import LokEngine.Network.TCP.Handlers.SimpleTCPServerHandler;

import java.util.HashMap;

import static LokEngine.Network.TCP.Handlers.Default.DefaultTCPHandlersHeads.*;

public class DefaultTCPServerHandler extends SimpleTCPServerHandler {

    public static HashMap<String, TCPServerMethod> methods = new HashMap<>();
    public static HashMap<String, String> publicData = new HashMap<>();

    @Override
    public void connected(int userID) {
        this.userID = userID;
    }

    private String getPublicData(String name){
        return publicData.containsKey(name) ? publicData.get(name) : errorHeadName;
    }

    private String runMethod(String name, String[] args){
        if (methods.containsKey(name)){
            return methods.get(name).execute(args);
        }
        return errorHeadName;
    }

    @Override
    public String acceptMessage(String message) {
        String[] lines = message.split(System.getProperty("line.separator"));
        String returnMessage = errorHeadName;
        String head = lines[0];

        if (head.equals(publicDataHeadName)){
            returnMessage = getPublicData(lines[1]);
        }else if (head.equals(runServerMethodHeadName)){
            returnMessage = runMethod(lines[1], lines.length >= 3 ? lines[2].split(";") : new String[0]);
        }

        return returnMessage;
    }

    @Override
    public void disconnected() {

    }
}