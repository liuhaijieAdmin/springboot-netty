package netty.knowledge.heartbeat;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

// 服务端的初始化器
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 配置如果5s内未触发读事件，就会触发读闲置事件
        pipeline.addLast("IdleStateHandler", new IdleStateHandler(5,0,0,TimeUnit.SECONDS));
        pipeline.addLast("Encoder",new StringEncoder(CharsetUtil.UTF_8));
        pipeline.addLast("Decoder",new StringDecoder(CharsetUtil.UTF_8));
        // 装载自定义的服务端心跳处理器
        pipeline.addLast("HeartbeatHandler",new HeartbeatServerHandler());
    }
}
