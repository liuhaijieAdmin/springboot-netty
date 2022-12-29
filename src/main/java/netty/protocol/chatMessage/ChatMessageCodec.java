package netty.protocol.chatMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import netty.im.message.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@ChannelHandler.Sharable
public class ChatMessageCodec extends MessageToMessageCodec<ByteBuf, Message> {

    // 消息出站时会经过的编码方法（将原生消息对象封装成自定义协议的消息格式）
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg,
                          List<Object> list) throws Exception {
        ByteBuf outMsg = ctx.alloc().buffer();
        // 前五个字节作为魔数
        byte[] magicNumber = new byte[]{'Z','h','u','Z','i'};
        outMsg.writeBytes(magicNumber);
        // 一个字节作为版本号
        outMsg.writeByte(1);
        // 一个字节表示序列化方式  0：JDK、1：Json、2：ProtoBuf.....
        outMsg.writeByte(0);
        // 一个字节用于表示消息类型
        outMsg.writeByte(msg.getMessageType());
        // 四个字节表示消息序号
        outMsg.writeInt(msg.getSequenceId());

        // 使用Java-Serializable的方式对消息对象进行序列化
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] msgBytes = bos.toByteArray();

        // 使用四个字节描述消息正文的长度
        outMsg.writeInt(msgBytes.length);
        // 将序列化后的消息对象作为消息正文
        outMsg.writeBytes(msgBytes);

        // 将封装好的数据传递给下一个处理器
        list.add(outMsg);
    }

    // 消息入站时会经过的解码方法（将自定义格式的消息转变为具体的消息对象）
    @Override
    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf inMsg, List<Object> list) throws Exception {
        // 读取前五个字节得到魔数
        byte[] magicNumber = new byte[5];
        inMsg.readBytes(magicNumber,0,5);
        // 再读取一个字节得到版本号
        byte version = inMsg.readByte();
        // 再读取一个字节得到序列化方式
        byte serializableType = inMsg.readByte();
        // 再读取一个字节得到消息类型
        byte messageType = inMsg.readByte();
        // 再读取四个字节得到消息序号
        int sequenceId = inMsg.readInt();
        // 再读取四个字节得到消息正文长度
        int messageLength = inMsg.readInt();

        // 再根据正文长度读取序列化后的字节正文数据
        byte[] msgBytes = new byte[messageLength];
        inMsg.readBytes(msgBytes,0,messageLength);

        // 对于读取到的消息正文进行反序列化，最终得到具体的消息对象
        ByteArrayInputStream bis = new ByteArrayInputStream(msgBytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Message message = (Message) ois.readObject();

        // 最终把反序列化得到的消息对象传递给后续的处理器
        list.add(message);
    }
}
