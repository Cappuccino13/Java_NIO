package FileChannelSample;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class FileChannelWriter {
	// data chunk be written per time
	private static final int DATA_CHUNK = 128 * 1024 * 1024;
	// total data size is 2G 
	private static final long LEN = 2 * 1024 * 1024 * 1024L;
	
	/**
	 * 使用FileChannel写文件
	 * 
	 * @throws IOException
	 */
	public static void writeByFileChannel() throws IOException{
		File file = new File("FileChannelWriterTest.txt");
		if(file.exists()){
			file.delete();
		}
		
		FileOutputStream fos = new FileOutputStream(file);
		//得到文件通道
		FileChannel channel = fos.getChannel();
		
		//指定大小为1024的缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		
		//要写入文件的字符串
		String greeting = "Hello Java NIO!";
		
		//把以上字符串逐字放入缓冲区
//		for(int i = 0; i < greeting.length(); i++){
//			buffer.putChar(greeting.charAt(i));
//		}
		
		//把以上字符一次性放入缓冲区
		buffer.put(greeting.getBytes());
				
		//反转buffer
		buffer.flip();
		
		//缓冲区数据写入文件中
		channel.write(buffer);
		
		//关闭文件和通道流
		channel.close();
		fos.close();	
		System.out.println("Write file " + file.getName() + " Successfully!");
	}
	
	/**
	 * 普通Java IO 缓冲流写入
	 * 
	 * @throws IOException
	 */
	public static void writeByBufferedStream() throws IOException{
		File file = new File("FileChannelWriterTest2.txt");
		if(file.exists()){
			file.delete();
		}
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
		String greeting = "Hello Java NIO！";
		int len = greeting.length();
		byte[] bytes = new byte[len];
		bytes = greeting.getBytes();
		bos.write(bytes);
		bos.close();	
		System.out.println("Write file " + file.getName() + " Successfully!");
	}
	
	/**
	 * 使用FileChannel写2G大文件
	 * 
	 * @throws IOException
	 */
	public static void writeBiFileByFileChannel() throws IOException{
    	long start = System.currentTimeMillis();
    	
    	File fcFile = new File("fc.dat");
    	if(fcFile.exists()){
    		fcFile.delete();
    	}
    	RandomAccessFile raf = new RandomAccessFile(fcFile, "rw");
    	FileChannel fileChannel = raf.getChannel();
    	
    	byte[] data = null;
    	long len = LEN;
    	ByteBuffer byteBuffer = ByteBuffer.allocate(DATA_CHUNK);
    	int dataChunk = DATA_CHUNK / (1024 * 1024);
    	while(len >= DATA_CHUNK){
    		System.out.println("write a data chunk:" + dataChunk + "MB");
    		
    		byteBuffer.clear();
    		data = new byte[DATA_CHUNK];
    		for(int i = 0; i < DATA_CHUNK; i++){
    			byteBuffer.put(data[i]);
    		}
    		data = null;
    		
    		byteBuffer.flip();
    		fileChannel.write(byteBuffer);
    		fileChannel.force(true);
    		
    		len -= DATA_CHUNK;  		
    	}
    	
    	if(len > 0){
    		System.out.println("write rest data chunk:" + len + "B");
    		byteBuffer = ByteBuffer.allocateDirect((int)len);
    		data = new byte[(int)len];
    		for(int i = 0; i < len; i++){
    			byteBuffer.put(data[i]);
    		}
    		
    		byteBuffer.flip();
    		fileChannel.write(byteBuffer);
    		fileChannel.force(true);
    		data = null;
    	}
    	
    	//关闭文件和通道流
    	fileChannel.close();
    	raf.close();	
    	
    	long end = System.currentTimeMillis();
    	System.out.println(String.format("===>写2G文件 " + fcFile.getName() + " 耗时：%s毫秒", end - start));	
    }
    
	
	/**
	 * 通过FileChannel.map()拿到MappedByteBuffer
	 * 使用内存文件映射，写2G大文件，速度会快很多
	 * 
	 * @throws IOException
	 */
    public static void writeBigFileByMappedByteBuffer() throws IOException{
    	long start = System.currentTimeMillis();
    	
    	File mbFile = new File("mb.dat");
    	if(mbFile.exists()){
    		mbFile.delete();
    	}
    	
    	RandomAccessFile raf = new RandomAccessFile(mbFile, "rw");
    	FileChannel fileChannel = raf.getChannel();
    	
    	int pos = 0;
    	MappedByteBuffer mbb = null;
    	byte[] data = null;
    	long len = LEN;
    	int dataChunk = DATA_CHUNK / (1024 * 1024);
    	while(len >= DATA_CHUNK){
    		System.out.println("write a data chunk:" + dataChunk + "MB");
    		
    		mbb = fileChannel.map(MapMode.READ_WRITE, pos, DATA_CHUNK);
    		data = new byte[DATA_CHUNK];
    		mbb.put(data);
    		
    		data = null;
    		
    		len -= DATA_CHUNK;
    		pos += DATA_CHUNK;
    	}
    	
    	if(len > 0){
    		System.out.println("write rest data chunk:" + len + "B");
    		
    		mbb = fileChannel.map(MapMode.READ_WRITE, pos, len);
    		data = new byte[(int)len];
    		mbb.put(data);
    		data = null;
    	}
    	
    	//关闭文件和通道流
    	fileChannel.close();
    	raf.close();
    	
    	long end = System.currentTimeMillis();
    	System.out.println(String.format("===>写2G文件 " + mbFile.getName() + " 耗时：%s毫秒", end - start));	   	
    }    
}
