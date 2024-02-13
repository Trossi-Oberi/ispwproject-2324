package logic.utils;

import logic.controllers.CNotification;
import logic.controllers.ClientListener;
import logic.model.Message;
import logic.server.Server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SecureObjectInputStream extends ObjectInputStream {

    public SecureObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException {

        List<String> approvedClasses = new ArrayList<>();

        //aggiungo le classi che sono autorizzate a fare la deserializzazione
        approvedClasses.add(Server.class.getName());
        approvedClasses.add(CNotification.class.getName());
        approvedClasses.add(ClientListener.class.getName());
        approvedClasses.add(Message.class.getName());
        approvedClasses.add(MessageTypes.class.getName());
        approvedClasses.add(Enum.class.getName());
        approvedClasses.add(UserTypes.class.getName());


        if (!approvedClasses.contains(osc.getName())) {
            throw new InvalidClassException("Unauthorized deserialization", osc.getName());
        }

        return super.resolveClass(osc);
    }
}
