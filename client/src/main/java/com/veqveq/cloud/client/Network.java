package com.veqveq.cloud.client;

import com.veqveq.cloud.common.BaseMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

public class Network {
    private static Socket socket;
    private static ObjectDecoderInputStream in;
    private static ObjectEncoderOutputStream out;

    public static void start() {
        try {
            socket = new Socket("localhost", 8789);
            in = new ObjectDecoderInputStream(socket.getInputStream());
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("Не удается создать сокет!");
        }
    }

    public static void stop() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMsg(BaseMessage msg) {
        try {
            out.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BaseMessage readMsg() throws IOException, ClassNotFoundException {
        BaseMessage msg = (BaseMessage) in.readObject();
        return msg;
    }
}
