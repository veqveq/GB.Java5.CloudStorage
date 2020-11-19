package com.veqveq.cloud.server;

import com.veqveq.cloud.common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class MessageHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof Command) {
                Command cmd = (Command) msg;
                ctx.fireChannelRead(cmd);
            } else if (msg instanceof FileRequest) {
                Path path = ((FileRequest) msg).getPath();
                FileMessage file = new FileMessage(path);
                ctx.writeAndFlush(file);
            } else if (msg instanceof FileMessage) {
                FileMessage file = (FileMessage) msg;
                Path path = file.getPath().resolve(file.getName());
                Files.write(path, file.getData(), StandardOpenOption.CREATE);
            } else if (msg instanceof DirRequest) {
                DirRequest dir = (DirRequest) msg;
                DirMessage dirMsg;
                if (dir.getDirectory() == null){
                    dirMsg = new DirMessage(Paths.get("server/src/main/java/root"));
                }else{
                    dirMsg = new DirMessage(Paths.get("server/src/main/java").resolve(dir.getDirectory()));
                }
                ctx.writeAndFlush(dirMsg);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
