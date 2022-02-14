package ru.alexanna.cloud.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.alexanna.cloud.model.CloudMessage;
import ru.alexanna.cloud.model.FileMessage;
import ru.alexanna.cloud.model.ListFilesMessage;

@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<CloudMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CloudMessage cloudMessage) throws Exception {
        log.info("received: {}", cloudMessage);
        switch (cloudMessage.getType()) {
            case FILE:
                processFileMessage((FileMessage) cloudMessage);
                break;
            case LIST:
                processListMessage((ListFilesMessage) cloudMessage);
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    private void processListMessage(ListFilesMessage listFilesMessage) {

    }

    private void processFileMessage(FileMessage fileMessage) {

    }
}
