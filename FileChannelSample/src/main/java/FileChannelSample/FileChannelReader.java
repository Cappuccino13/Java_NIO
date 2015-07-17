package FileChannelSample;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class FileChannelReader {
	
	 /**
     * 使用FileChannel读取文件，并打印到控制台
     * 
     * @param allocate 每次读取多少个字节
     * @throws IOException
     */
    public static void readByChannelTest(String file, int allocate) throws IOException{
    	long start = System.currentTimeMillis();
    	FileInputStream fis = new FileInputStream(file);
    	
    	//1.从FileInputStream对象获取文件通道FileChannel
    	FileChannel channel = fis.getChannel();
    	long size = channel.size();
    	
    	//2.从通道读取文件内容
    	byte[] bytes = new byte[allocate];
    	ByteBuffer byteBuffer = ByteBuffer.allocate(allocate);
    	
    	//channel.read(ByteBuffer)方法就类似于inputStream.read(byte)
    	//每次read都将读取allocate个字节到ByteBuffer
    	int len;
    	while((len = channel.read(byteBuffer)) != -1){
    		//注意，先调用flip方法反转Buffer，再从Buffer读取数据
    		byteBuffer.flip();
    		
    		//有几种方式可以操作ByteBuffer
    		//1.可以将当前Buffer包含的字节数组全部读取出来
    		bytes = byteBuffer.array();
//    		System.out.print(new String(bytes));
    		
    		//2.类似于inputStream的read(byte[], offset, len)方法读取
//    		byteBuffer.get(bytes, 0, len);
//    		System.out.print(new String(bytes, 0, len));
    		
    		//3.也可以通过遍历Buffer读取每个字节数据
    		//一个字节一个字节地打印在控制台，但这种更慢且更耗时
//    		while(byteBuffer.hasRemaining()){
//    			System.out.print((char)byteBuffer.get());
//    		}
    		
    		//最后注意调用clear()方法，将Buffer的位置回归到0
    		byteBuffer.clear();    		
    	}
    	
    	//关闭文件和通道流
    	channel.close();
    	fis.close();
    	
    	long end = System.currentTimeMillis();
    	System.out.println(String.format("\n===>文件大小：%s字节", size));
    	System.out.println(String.format("===>读取并打印文件耗时：%s毫秒", end - start));
    	
    }
    
    /**
     * 仍然是根据FileChannel操作ByteBuffer，从ByteBuffer读取内容
     * 通道读取文件，速度比内存映射慢很多，甚至比普通缓冲流要慢
     * 
     * @param allocate
     * @throws IOException
     */
    public static void readByChannelTest2(String file, int allocate) throws IOException{
    	long start = System.currentTimeMillis();
    	FileInputStream fis = new FileInputStream(file);
    	
    	//1.从FileInputStream对象获取文件通道FileChannel
    	FileChannel channel = fis.getChannel();
    	long size = channel.size();
    	
    	//每次读取allocate个字节，计算要循环读取多少次
    	long cycle = size / allocate;
    	//看是否能整数倍读完
    	int mode = (int) (size % allocate);
    	
    	//循环读取
    	byte[] bytes;
    	ByteBuffer byteBuffer = ByteBuffer.allocate(allocate);
    	for(long i = 0; i < cycle; i++){
    		if(channel.read(byteBuffer) != -1){
    			byteBuffer.flip();
    			bytes = byteBuffer.array();
//    			System.out.print(new String(bytes));
    			byteBuffer.clear();
    		}
    	}
    	
    	//读取最后mode个字节
    	if(mode > 0){
    		byteBuffer = ByteBuffer.allocate(mode);
    		if(channel.read(byteBuffer) != -1){
    			byteBuffer.flip();
    			bytes = byteBuffer.array();
//    			System.out.print(new String(bytes));
    			byteBuffer.clear();
    		}
    	}
    	
    	//关闭文件和通道流
    	channel.close();
    	fis.close();
    	
    	long end = System.currentTimeMillis();
    	System.out.println(String.format("\n===>文件大小：%s字节", size));
    	System.out.println(String.format("===>读取并打印文件耗时：%s毫秒", end - start)); 	
    }
    
    /**
     * 通过FileChannel.map()拿到MappedByteBuffer
     * 使用内存文件映射，速度会快很多
     * 
     * @param allocate
     * @throws IOException
     */
    public static void readByChannelTest3(String file, int allocate) throws IOException{
    	long start = System.currentTimeMillis();
    	
    	RandomAccessFile fis = new RandomAccessFile(new File(file), "rw");
    	FileChannel channel = fis.getChannel();
    	long size = channel.size();
    	
    	//构建一个只读的MappedByteBuffer
    	MappedByteBuffer mappedByteBuffer = channel.map(MapMode.READ_ONLY, 0, size);
    	
    	//如果文件不大，可以选择一次性读取到数组
//    	byte[] all = new byte[(int)size];
//    	mappedByteBuffer.get(all, 0, (int)size);
    	//打印文件内容
//    	System.out.println(new String(all));
    	
    	//如果文件内容很大，可以循环读取，计算应该读取多少次
    	byte[] bytes = new byte[allocate];
    	long cycle = size / allocate;
    	int mode = (int)(size % allocate);
    	for(int i = 0; i < cycle; i++){
    		//每次读取allocate个字节
    		mappedByteBuffer.get(bytes);
    		
    		//打印文件内容，关闭打印速度会很快
//    		System.out.print(new String(bytes));
    	}
    	if(mode > 0){
    		bytes = new byte[mode];
    		mappedByteBuffer.get(bytes);
    		
    		//打印文件内容，关闭打印速度会很快
//    		System.out.print(new String(bytes));
    	}
    	
    	//关闭文件和通道流
    	channel.close();
    	fis.close();
    	
    	long end = System.currentTimeMillis();
    	System.out.println(String.format("\n===>文件大小：%s字节", size));
    	System.out.println(String.format("===>读取并打印文件耗时：%s毫秒", end - start));  	
    }
    
    /**
     * 普通Java IO 缓冲流读取
     * 
     * @throws IOException
     */
    public static void readByBufferedStream(String file, int allocate) throws IOException{
    	long start = System.currentTimeMillis();
    	BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
    	long size = bis.available();
    	
    	int len = 0;
    	byte[] eachBytes = new byte[allocate];
    	while((len = bis.read(eachBytes)) != -1){
//    		System.out.print(new String(eachBytes, 0, len));
    	}
    	
    	bis.close();
    	
    	long end = System.currentTimeMillis();
    	System.out.println(String.format("\n===>文件大小：%s字节", size));
    	System.out.println(String.format("===>读取并打印文件耗时：%s毫秒", end - start));	
    }
}
