package com.bastiarts.blockz;

/**
 * @author BastiArts
 */

import com.bastiarts.blockz.endpoint.DrawEndpoint;
import com.bastiarts.blockz.endpoint.GameEndpoint;
import org.glassfish.tyrus.server.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        runServer();
    }

    public static void runServer() {
        Set<Class<?>> endpoints = new HashSet<>();
        endpoints.add(GameEndpoint.class);
        endpoints.add(DrawEndpoint.class);
        Server server = new Server("0.0.0.0", 8025, "/websockets", endpoints);


        try {
            server.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            System.out.print("Please press a key to stop the server.");
            reader.readLine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            server.stop();
        }
    }
}
