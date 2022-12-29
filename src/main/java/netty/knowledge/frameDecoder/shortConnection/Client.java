package netty.knowledge.frameDecoder.shortConnection;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

// 演示通过短连接解决粘包问题的客户端
public class Client {

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            sendData();
        }
    }

    private static void sendData(){
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap client = new Bootstrap();
        try {
            client.group(worker);
            client.channel(NioSocketChannel.class);
            client.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        // 在通道准备就绪后会触发的事件
                        @Override
                        public void channelActive(ChannelHandlerContext ctx)
                                throws Exception {
                            // 向服务端发送一个20字节的数据包，然后断开连接
                            ByteBuf buffer = ctx.alloc().buffer(1);
                            buffer.writeBytes(new byte[]
                                        {'0','1','2','3','4',
                                        '5','6','7','8','9',
                                        'A','B','C','D','E',
                                        'M','N','X','Y','Z'});
                            ctx.writeAndFlush(buffer);
                            ctx.channel().close();
                        }
                    });
                }
            });
            client.connect("127.0.0.1", 8888).sync();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
