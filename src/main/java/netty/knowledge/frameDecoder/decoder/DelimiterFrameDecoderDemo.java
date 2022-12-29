package netty.knowledge.frameDecoder.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

// 通过分隔符解码器解决粘包、半包问题的演示类
public class DelimiterFrameDecoderDemo {
    public static void main(String[] args) {
        // 自定义一个分隔符（记得要用ByteBuf对象来包装）
        ByteBuf delimiter = ByteBufAllocator.DEFAULT.buffer(1);
        delimiter.writeByte('*');

        // 通过Netty提供的测试通道来代替服务端、客户端
        EmbeddedChannel channel = new EmbeddedChannel(
                // 添加一个分隔符帧解码器（传入自定义的分隔符）
                new DelimiterBasedFrameDecoder(1024,delimiter),
                new LoggingHandler(LogLevel.DEBUG)
        );

        // 调用三次发送数据的方法（等价于向服务端发送三次数据）
        sendData(channel,"ABCDEGF");
        sendData(channel,"XYZ");
        sendData(channel,"12345678");
    }

    private static void sendData(EmbeddedChannel channel, String data){
        // 在要发送的数据结尾，拼接上一个*号（因为前面自定义的分隔符为*号）
        String msg = data + "*";
        //  获取发送数据的字节长度
        byte[] msgBytes = msg.getBytes();

        // 构建缓冲区，通过channel发送数据
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(8);
        buffer.writeBytes(msgBytes);
        channel.writeInbound(buffer);
    }
}
