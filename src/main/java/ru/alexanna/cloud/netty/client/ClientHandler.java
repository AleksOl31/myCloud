package ru.alexanna.cloud.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.model.CloudMessage;
import ru.alexanna.cloud.model.FileMessage;
import ru.alexanna.cloud.model.ListFilesMessage;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private Path currentDir;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        currentDir = Paths.get(System.getProperty("user.home"));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CloudMessage cloudMessage) throws Exception {
        log.info("received: {}", cloudMessage);
        switch (cloudMessage.getType()) {
            case FILE:
                processFileMessage((FileMessage) cloudMessage);
                break;
            case LIST:
                processListMessage((ListFilesMessage) cloudMessage);
                sendFileMessage(channelHandlerContext);
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        log.error(cause.getLocalizedMessage());
        cause.printStackTrace();
    }

    private void sendFileMessage(ChannelHandlerContext ctx) {
        Path filePath = currentDir.resolve("man_i40.pdf");
        log.debug(filePath.toString());
        try {
            FileMessage fileMessage = new FileMessage(filePath);
            ctx.writeAndFlush(fileMessage);
            log.debug("Sent file {}, size {}", fileMessage.getFileName(), fileMessage.getBytes().length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processListMessage(ListFilesMessage listFilesMessage) {

    }

    private void processFileMessage(FileMessage fileMessage) {

    }
}
