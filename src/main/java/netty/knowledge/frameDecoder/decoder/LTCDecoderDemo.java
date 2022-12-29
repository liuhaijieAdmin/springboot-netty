package netty.knowledge.frameDecoder.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

// 通过LTC帧解码器解决粘包、半包问题的演示类
public class LTCDecoderDemo {
public static void main(String[] args) {
    // 通过Netty提供的测试通道来代替服务端、客户端
    EmbeddedChannel channel = new EmbeddedChannel(
            // 添加一个行帧解码器（在超出1024后还未检测到换行符，就会停止读取）
            new LengthFieldBasedFrameDecoder(1024,0,4,0,0),
            new LoggingHandler(LogLevel.DEBUG)
    );

    // 调用三次发送数据的方法（等价于向服务端发送三次数据）
    sendData(channel,"Hi, ZhuZi.");
}

    private static void sendData(EmbeddedChannel channel, String data){
        // 获取要发送的数据字节以及长度
        byte[] dataBytes = data.getBytes();
        int dataLength = dataBytes.length;

        // 先将数据长度写入到缓冲区、再将正文数据写入到缓冲区
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeInt(dataLength);
        buffer.writeBytes(dataBytes);

        // 发送最终组装好的数据
        channel.writeInbound(buffer);
    }
}
