package netty.knowledge.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.util.internal.StringUtil;

public class ByteBufDemo {

    // 测试Netty-ByteBuf自动扩容机制
    private static void byteBufCapacityExpansion() {
        // 不指定默认容量大小为16
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(16);
        System.out.println("测试前的Buffer容量：" + buffer);
        // 使用StringBuffer来测试ByteBuf的自动扩容特性
        StringBuffer sb = new StringBuffer();
        // 往StringBuffer中插入17个字节的数据
        for (int i = 0; i < 17; i++) {
            sb.append("6");
        }
        // 将17个字节大小的数据写入缓冲区
        buffer.writeBytes(sb.toString().getBytes());
        printBuffer(buffer);
    }

    // 打印ByteBuf中数据的方法
    private static void printBuffer(ByteBuf buffer) {
        // 读取ByteBuffer已使用的字节数
        int byteSize = buffer.readableBytes();
        // 基于byteSize来计算显示的行数
        int rows = byteSize / 16 + (byteSize % 15 == 0 ? 0 : 1) + 4;
        // 创建一个StringBuilder用来显示输出
        StringBuilder sb = new StringBuilder(rows * 80 * 2);
        // 获取缓冲区的容量、读/写指针信息放入StringBuilder
        sb.append("ByteBuf缓冲区信息：{");
        sb.append("读取指针=").append(buffer.readerIndex()).append(", ");
        sb.append("写入指针=").append(buffer.writerIndex()).append(", ");
        sb.append("容量大小=").append(buffer.capacity()).append("}");

        // 利用Netty框架自带的格式化方法、Dump方法输出缓冲区数据
        sb.append(StringUtil.NEWLINE);
        ByteBufUtil.appendPrettyHexDump(sb, buffer);
        System.out.println(sb.toString());
    }

    // 查看创建的缓冲区是否使用了池化技术
    private static void byteBufferIsPooled(){
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(16);
        System.out.println(buffer.getClass());
    }

    // 测试ByteBuf的read、get、mark功能
    private static void bufferReader(){
        // 分配一个初始容量为10的缓冲区
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);

        // 向缓冲区中写入10个字符（占位十个字节）
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 10; i++) {
            sb.append(i);
        }
        buffer.writeBytes(sb.toString().getBytes());

        // 使用read方法读取前5个字节数据
        printBuffer(buffer);
        buffer.readBytes(5);
        printBuffer(buffer);

        // 再使用get方法读取后五个字节数据
        buffer.getByte(5);
        printBuffer(buffer);

        // 使用mark标记一下读取指针，然后再使用read方法读取数据
        buffer.markReaderIndex();
        buffer.readBytes(5);
        printBuffer(buffer);

        // 此时再通过reset方法，使读取指针恢复到前面的标记位置
        buffer.resetReaderIndex();
        printBuffer(buffer);
    }

    // 测试Netty-ByteBuf的slice零拷贝方法
    private static void sliceZeroCopy(){
        // 分配一个初始容量为10的缓冲区
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);

        // 写入0~9十个字节数据
        byte[] numData = {'0','1','2','3','4','5','6','7','8','9'};
        buffer.writeBytes(numData);
        printBuffer(buffer);

        // 从下标0开始，向后截取五个字节，拆分成一个新ByteBuf对象
        ByteBuf b1 = buffer.slice(0, 5);
        printBuffer(b1);
        // 从下标5开始，向后截取五个字节，拆分成一个新ByteBuf对象
        ByteBuf b2 = buffer.slice(5, 5);
        printBuffer(b2);

        // 证明切割出的两个ByteBuf对象，是共享第一个ByteBuf对象数据的
        // 这里修改截取后的b1对象，然后查看最初的buffer对象
        b1.setByte(0,'a');
        printBuffer(buffer);
    }

    // 测试Netty-ByteBuf的composite零拷贝方法
    private static void compositeZeroCopy(){
        // 创建两个小的ByteBuf缓冲区，并往两个缓冲区中插入数据
        ByteBuf b1 = ByteBufAllocator.DEFAULT.buffer(5);
        ByteBuf b2 = ByteBufAllocator.DEFAULT.buffer(5);
        byte[] data1 = {'a','b','c','d','e'};
        byte[] data2 = {'n','m','x','y','z'};
        b1.writeBytes(data1);
        b2.writeBytes(data2);

        // 创建一个合并缓冲区的CompositeByteBuf对象
        CompositeByteBuf buffer = ByteBufAllocator.DEFAULT.compositeBuffer();
        // 将前面两个小的缓冲区，合并成一个大的缓冲区，还可以用addComponent单个合并
        // （第一个参数必须为true，否则不会自动修改读写指针）
        buffer.addComponents(true,b1,b2);
        printBuffer(buffer);
    }

    public static void main(String[] args) {
        compositeZeroCopy();
    }

}