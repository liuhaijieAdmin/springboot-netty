package netty.knowledge.heartbeat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

// 演示心跳机制的客户端（不会发送心跳包）
public class ClientB {
    public static void main(String[] args) {
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap client = new Bootstrap();
        try {
            client.group(worker);
            client.channel(NioSocketChannel.class);
            client.option(ChannelOption.SO_KEEPALIVE, true);
            client.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast("Encoder",new StringEncoder(CharsetUtil.UTF_8));
                    pipeline.addLast("Decoder",new StringDecoder(CharsetUtil.UTF_8));
                    pipeline.addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // 建立连接成功之后，先向服务端发送一条数据
                            ctx.channel().writeAndFlush("我是不会发心跳包的客户端-B！");
                        }
                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            System.out.println("因为没发送心跳包，俺被开除啦！");
                            // 当通道被关闭时，停止前面启动的线程池
                            worker.shutdownGracefully();
                        }
                    });
                }
            });
            client.connect("127.0.0.1", 8888).sync();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
