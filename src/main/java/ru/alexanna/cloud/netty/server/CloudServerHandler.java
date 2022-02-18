package ru.alexanna.cloud.netty.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.model.*;

@Slf4j
@ChannelHandler.Sharable
public class CloudServerHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private Path currentDir;
    private final String HOME_DIR = "data";

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.debug("New client joined...");
        currentDir = Paths.get(HOME_DIR);
        sendListFiles(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                CloudMessage cloudMessage) {
        switch (cloudMessage.getType()) {
            case FILE_REQUEST:
                processFileRequest((FileRequestMessage) cloudMessage, ctx);
                break;
            case FILE:
                processFileMessage((FileMessage) cloudMessage);
                sendListFiles(ctx);
                break;
            case PATH_CHANGE_REQUEST:
                processPathChangeRequestMessage((PathChangeRequestMessage) cloudMessage, ctx);

                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.debug("Client disconnected...");
    }

    private void sendListFiles(ChannelHandlerContext ctx) {
        CloudMessage message = null;
        try {
            message = new ListFilesMessage(currentDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ctx.writeAndFlush(message);
        log.debug("Message sent: {}", message);
    }

    private void processFileMessage(FileMessage cloudMessage) {
        try {
            log.debug("FileMessage received {}, size {}", cloudMessage.getFileName(), cloudMessage.getFileSize());
//            Files.write(currentDir.resolve(cloudMessage.getFileName()), cloudMessage.getFileSize());
            if (!Files.exists(currentDir.resolve(cloudMessage.getFileName()))) {
                Files.createFile(currentDir.resolve(cloudMessage.getFileName()));
                log.debug("FileMessage bytes wrote: {}", cloudMessage.getFileSize());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processFileRequest(FileRequestMessage cloudMessage, ChannelHandlerContext ctx) {
        Path path = currentDir.resolve(cloudMessage.getFileName());
        try {
            ctx.writeAndFlush(new FileMessage(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processPathChangeRequestMessage(PathChangeRequestMessage cloudMessage, ChannelHandlerContext ctx) {
        Path targetPath = currentDir.resolve(cloudMessage.getDirName()).normalize();
        if (Files.isDirectory(targetPath)) {
            if (!(currentDir.endsWith(HOME_DIR) && cloudMessage.getDirName().equals(".."))) {
                currentDir = targetPath;
                sendListFiles(ctx);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
