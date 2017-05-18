package com.mock.wificlient;

import java.nio.charset.Charset;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WifiClient {
	
	private static Channel channel;
	
	private static final Logger logger = LoggerFactory.getLogger(WifiClient.class);
	
	public static void connect(String host,Integer port) {
		
		NioEventLoopGroup group = new NioEventLoopGroup(1);
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						protected void initChannel(SocketChannel ch)
								throws Exception {
							ch.pipeline().addLast("decoder",new LengthFieldBasedFrameDecoder(200, 0, 2, 0, 0));
							ch.pipeline().addLast(new ReceiveMessageHandler());
						}
					})
					.option(ChannelOption.TCP_NODELAY, true);
			
			channel = bootstrap.connect(host, port).sync().channel();
			logger.info("connect to server!");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		connect("0.0.0.0", 11306);
		auth();
		sendProductInfo();
		//sendDeviceTime();
		//sendControl();
		//sendAbout();
		//sendStatInfo();
	}
	
	
	private static void auth() {
		
		ByteBuf data = ByteBufAllocator.DEFAULT.buffer();
		byte requestContent[] = "IAA_GZCYDQ_GZCH".getBytes();
		byte macContent[] = "123456".getBytes();
		data.writeShort(requestContent.length + macContent.length + 1);
		data.writeBytes(macContent);
		data.writeByte(0x0D);
		data.writeBytes(requestContent);
		channel.writeAndFlush(data);
	}
	
	
	@SuppressWarnings("unused")
	private static void sendAbout() {
		
		ByteBuf data = ByteBufAllocator.DEFAULT.buffer();
		byte macContent[] = "123456".getBytes(Charset.forName("UTF-8"));
		byte jsonContent[] = "{'pcbVer':'1.0','serial':'SN1236954','EquipmentVer':'2.0'}".getBytes(Charset.forName("UTF-8"));
		
		data.writeShort(macContent.length + 1 + jsonContent.length);
		data.writeBytes(macContent);
		data.writeByte(0x04);
		data.writeBytes(jsonContent);
		channel.writeAndFlush(jsonContent);
		channel.writeAndFlush(data);
	}
	
	
	@SuppressWarnings("unused")
	private static void sendControl() {
		
		ByteBuf data = ByteBufAllocator.DEFAULT.buffer();
		byte macContent[] = "123456".getBytes(Charset.forName("UTF-8"));
		
		data.writeShort(macContent.length + 68);
		data.writeBytes(macContent);
		
		data.writeByte(0x03);
		
		data.writeByte(12);
		
		//第一阶段
		data.writeByte(10);
		data.writeByte(9);
		data.writeByte(8);
		data.writeByte(7);
		data.writeByte(1);
		data.writeByte(0);
		data.writeShort(12);
		data.writeShort(8);
		
		//第二阶段
		data.writeByte(11);
		data.writeByte(10);
		data.writeByte(9);
		data.writeByte(9);
		data.writeByte(2);
		data.writeByte(1);
		data.writeShort(13);
		data.writeShort(9);
		
		//第三阶段
		data.writeByte(12);
		data.writeByte(11);
		data.writeByte(10);
		data.writeByte(10);
		data.writeByte(3);
		data.writeByte(2);
		data.writeShort(14);
		data.writeShort(10);
		
		
		//第四阶段
		data.writeByte(13);
		data.writeByte(12);
		data.writeByte(10);
		data.writeByte(8);
		data.writeByte(4);
		data.writeByte(5);
		data.writeShort(15);
		data.writeShort(7);
		
		//第五阶段
		data.writeByte(16);
		data.writeByte(13);
		data.writeByte(11);
		data.writeByte(9);
		data.writeByte(7);
		data.writeByte(9);
		data.writeShort(23);
		data.writeShort(19);
		
		data.writeByte(1);
		data.writeByte(10);
		data.writeByte(1);
		
		data.writeShort(100);
		data.writeShort(89);
		data.writeByte(56);
		
		data.writeShort(10);
		data.writeShort(60);
		data.writeShort(8);
		data.writeShort(58);
		
		channel.writeAndFlush(data);
	}
	
	@SuppressWarnings("unused")
	private static void sendDeviceTime() {
		
		ByteBuf data = ByteBufAllocator.DEFAULT.buffer();
		byte macContent[] = "123456".getBytes(Charset.forName("UTF-8"));
		
		data.writeShort(macContent.length + 8);
		data.writeBytes(macContent);
		data.writeByte(0x02);
		
		data.writeByte(16);
		data.writeByte(17);
		data.writeByte(5);
		data.writeByte(18);
		data.writeByte(22);
		data.writeByte(30);
		data.writeByte(30);
		channel.writeAndFlush(data);
	}
	
	private static void sendProductInfo() {
		
		ByteBuf data = ByteBufAllocator.DEFAULT.buffer();
		byte macContent[] = "123456".getBytes(Charset.forName("UTF-8"));
		byte jsonContent[] = "{'name':'wifi',addr:'中国'}".getBytes(Charset.forName("UTF-8"));
		
		data.writeShort(macContent.length + 1 + jsonContent.length);
		data.writeBytes(macContent);
		data.writeByte(0x01);
		data.writeBytes(jsonContent);
		channel.writeAndFlush(data);
	}
	
	
	
	@SuppressWarnings("unused")
	private static void sendStatInfo() {
		
		ByteBuf data = ByteBufAllocator.DEFAULT.buffer();
		
		byte macContent[] = "123456".getBytes();
		
		//写长度，（1控制头(D0) + 19数据  = 20）
		data.writeShort(macContent.length + 18);
		
		//MAC
		data.writeBytes(macContent);
		
		//状态数据(控制头)
		data.writeByte(0x06);
		
		//D9-D10精油余量
		data.writeShort(96);
		
		//D11-D12精油总量
		data.writeShort(102);
		
		//D13电量
		data.writeByte(56);
		
		//D14-D15气泵压力
		data.writeShort(89);
		
		//D16-D17环境气压
		data.writeShort(67);

		//D18-D21机器工作时间(单位：小时)
		data.writeBytes(toBytes(103, 4));
		
		//D22-D25气泵工作时间(单位：分钟)
		data.writeBytes(toBytes(-60, 4));
		
		for (int i = 0;i<data.readableBytes();i++) {
			System.out.print("0x" + Integer.toHexString(data.getByte(i)) + " ");
		}
		
		channel.writeAndFlush(data);
	}
	
	public static byte [] toBytes(long data,int byteLength) {
		byte [] byteArray = new byte [byteLength];
		for (int i = 0;i<byteLength;i++) {
			byteArray[i] = (byte)(data >> (8 *(byteLength - (i + 1))) & 0xFF);
		}
		return byteArray;
	}
}
