package netty.protocol.redis;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

// 基于Netty、RESP协议实现的Redis客户端
public class RedisClient {
    // 换行符的ASCII码
    static final byte[] LINE = {13, 10};

    public static void main(String[] args) {
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap client = new Bootstrap();

        try {
            client.group(worker);
            client.channel(NioSocketChannel.class);
            client.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel)
                                                        throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();

                    pipeline.addLast(new ChannelInboundHandlerAdapter(){

                        // 通道建立成功后调用：向Redis发送一条set命令
                        @Override
                        public void channelActive(ChannelHandlerContext ctx)
                                                            throws Exception {
                            String command = "set name ZhuZi";
                            ByteBuf buffer = respCommand(command);
                            ctx.channel().writeAndFlush(buffer);
                        }

                        // Redis响应数据时触发：打印Redis的响应结果
                        @Override
                        public void channelRead(ChannelHandlerContext ctx,
                                                Object msg) throws Exception {
                            // 接受Redis服务端执行指令后的结果
                            ByteBuf buffer = (ByteBuf) msg;
                            System.out.println(buffer.toString(CharsetUtil.UTF_8));
                        }
                    });
                }
            });

            // 根据IP、端口连接Redis服务端
            client.connect("192.168.12.129", 6379).sync();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static ByteBuf respCommand(String command){
        // 先对传入的命令以空格进行分割
        String[] commands = command.split(" ");
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();

        // 遵循RESP协议：先写入指令的个数
        buffer.writeBytes(("*" + commands.length).getBytes());
        buffer.writeBytes(LINE);

        // 接着分别写入每个指令的长度以及具体值
        for (String s : commands) {
            buffer.writeBytes(("$" + s.length()).getBytes());
            buffer.writeBytes(LINE);
            buffer.writeBytes(s.getBytes());
            buffer.writeBytes(LINE);
        }
        // 把转换成RESP格式的命令返回
        return buffer;
    }
}
