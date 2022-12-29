package netty.knowledge.heartbeat;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

// 客户端的初始化器
public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 配置如果3s内未触发写事件，就会触发写闲置事件
        pipeline.addLast("IdleStateHandler", new IdleStateHandler(0,3,0,TimeUnit.SECONDS));
        pipeline.addLast("Encoder",new StringEncoder(CharsetUtil.UTF_8));
        pipeline.addLast("Decoder",new StringDecoder(CharsetUtil.UTF_8));
        // 装载自定义的客户端心跳处理器
        pipeline.addLast("HeartbeatHandler",new HeartbeatClientHandler());
    }
}
