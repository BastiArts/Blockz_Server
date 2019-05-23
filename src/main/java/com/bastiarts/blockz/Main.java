package com.bastiarts.blockz;

/**
 * @author BastiArts
 */

import com.bastiarts.blockz.endpoint.GameEndpoint;
import org.glassfish.tyrus.server.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        runServer();
    }

    public static void runServer() {
        Server server = new Server("0.0.0.0", 8025, "/websockets", GameEndpoint.class);

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
