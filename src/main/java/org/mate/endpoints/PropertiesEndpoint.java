package org.mate.endpoints;

import org.mate.network.message.Message;
import org.mate.network.Endpoint;
import org.mate.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class PropertiesEndpoint implements Endpoint {

    @Override
    public Message handle(Message request) {

        if (request.getSubject().startsWith("/properties/get_mate_properties")) {
            return getProperties();
        } else {
            throw new IllegalArgumentException("Message request with subject: "
                    + request.getSubject() + " can't be handled by PropertiesEndpoint!");
        }
    }

    private Message getProperties() {

        Properties properties = new Properties();
        try {
            properties.load(new FileReader(new File("mate.properties")));
        } catch (IOException e) {
            Log.println("WARNING: Failed to load mate.properties file: " + e.getLocalizedMessage());
        }

        Log.println("Loaded " + properties.size() + " properties from mate.properties file.");


        Message.MessageBuilder mb = new Message.MessageBuilder("/properties");
        for (Map.Entry<Object, Object> propertyEntry : properties.entrySet()) {
            Log.println("Property: " + propertyEntry.getKey() + " = " + propertyEntry.getValue());
            mb.withParameter((String) propertyEntry.getKey(), (String) propertyEntry.getValue());
        }

        return mb.build();
    }

}
