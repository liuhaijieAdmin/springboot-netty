package netty.knowledge.frameDecoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

// 演示粘包、半包问题的通用初始化器
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
            // 数据就绪事件：当收到客户端数据时会读取通道内的数据
            @Override
            public void channelReadComplete(ChannelHandlerContext ctx)
                    throws Exception {
                // 在这里直接输出通道内的数据信息
                System.out.println(ctx.channel());
                super.channelReadComplete(ctx);
            }
        });
    }
}
