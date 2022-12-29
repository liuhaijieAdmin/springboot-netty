package netty.knowledge.frameDecoder.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

// 通过定长帧解码器解决粘包、半包问题的演示类
public class FixedLengthFrameDecoderDemo {
    public static void main(String[] args) {
        // 通过Netty提供的测试通道来代替服务端、客户端
        EmbeddedChannel channel = new EmbeddedChannel(
                // 添加一个定长帧解码器（每条数据以8字节为单位拆包）
                new FixedLengthFrameDecoder(8),
                new LoggingHandler(LogLevel.DEBUG)
        );

        // 调用三次发送数据的方法（等价于向服务端发送三次数据）
        sendData(channel,"ABCDEGF",8);
        sendData(channel,"XYZ",8);
        sendData(channel,"12345678",8);
    }

    private static void sendData(EmbeddedChannel channel, String data, int len){
        //  获取发送数据的字节长度
        byte[] bytes = data.getBytes();
        int dataLength = bytes.length;

        // 根据固定长度补齐要发送的数据
        String alignString = "";
        if (dataLength < len){
            int alignLength = len - bytes.length;
            for (int i = 1; i <= alignLength; i++) {
                alignString = alignString + "*";
            }
        }

        // 拼接上补齐字符，得到最终要发送的消息数据
        String msg = data + alignString;
        byte[] msgBytes = msg.getBytes();

        // 构建缓冲区，通过channel发送数据
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(8);
        buffer.writeBytes(msgBytes);
        channel.writeInbound(buffer);
    }
}
