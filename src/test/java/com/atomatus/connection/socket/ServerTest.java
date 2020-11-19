package com.atomatus.connection.socket;

import com.atomatus.util.macvendors.Vendor;
import com.atomatus.connection.socket.event.*;
import junit.framework.TestCase;

import java.io.IOException;

public class ServerTest extends TestCase {

    public void testSocketObjectIO() {

        try {
            final Server s = new Server();
            s.setServerObjectAdapter(new ServerObjectAdapter() {
                @Override
                public void onInputObjectAction(InputObjectEvent evt) {
                    try {
                        Vendor v = evt.readObject();
                        assertEquals(v.getCompany(), "Client Inc");
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        fail(e.getMessage());
                    }
                }

                @Override
                public void onOutputObjectAction(OutputObjectEvent evt) {
                    try {
                        Vendor v = new Vendor();
                        v.setCompany("Server Inc");
                        evt.writeObject(v);
                    } catch (IOException e) {
                        e.printStackTrace();
                        fail(e.getMessage());
                    }
                }
            });

            s.open();

            try(Client c = new Client(s.getPort())) {
                Vendor v = new Vendor();
                v.setCompany("Client Inc");
                c.writeObject(v);
                Vendor resp = c.readObject();
                assertEquals(resp.getCompany(), "Server Inc");
            }

            s.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testSocketBytesIO() {

        try {
            final Server s = new Server();
            s.setServerDataAdapter(new ServerDataAdapter() {
                @Override
                public void onInputDataAction(InputDataEvent evt) {
                    try {
                        String fromClient = evt.readString();
                        assertEquals(fromClient, "client send data!");
                    } catch (IOException e) {
                        e.printStackTrace();
                        fail(e.getMessage());
                    }
                }

                @Override
                public void onOutputDataAction(OutputDataEvent evt) {
                    try {
                        evt.write("server answer client!");
                    } catch (IOException e) {
                        e.printStackTrace();
                        fail(e.getMessage());
                    }
                }
            });

            s.open();

            try(Client c = new Client(s.getPort())) {
                c.write("client send data!");
                String fromServer = c.readString();
                assertEquals(fromServer, "server answer client!");
            }

            s.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}