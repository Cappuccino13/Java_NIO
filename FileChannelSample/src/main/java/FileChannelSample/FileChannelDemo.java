package FileChannelSample;

import java.io.IOException;

public class FileChannelDemo {
	public static void main( String[] args ) throws IOException{
		String file = "FileChannelReaderTest.txt";
//		String file = "Maven-definitive-guide_zh.pdf";
		//普通NIO读取
		FileChannelReader.readByChannelTest(file, 1024);
		//普通NIO读取
		FileChannelReader.readByChannelTest2(file, 1024);
		//使用内存映射文件来读取
		FileChannelReader.readByChannelTest3(file, 1024);
		//普通缓冲流读取
		FileChannelReader.readByBufferedStream(file, 1024);
		
		//普通NIO写文入
		FileChannelWriter.writeByFileChannel();
		//普通缓冲流写入
		FileChannelWriter.writeByBufferedStream();
		//普通NIO写2G大文件
		FileChannelWriter.writeBiFileByFileChannel();
		//使用内存映射文件写2G大文件
		FileChannelWriter.writeBigFileByMappedByteBuffer();
	}
}
