package com.mock.wificlient;

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
		send();
	}
	
	private static void send() {
		
		ByteBuf data = ByteBufAllocator.DEFAULT.buffer();

		//写长度，（1控制头(D0) + 19数据  = 20）
		data.writeShort(20);
				
		//状态数据(控制头)
		data.writeByte(0x06);
		
		//D1-D2精油余量
		data.writeShort(96);
		
		//D3-D4精油总量
		data.writeShort(102);
		
		//D5电量
		data.writeByte(56);
		
		//D6-D7气泵压力
		data.writeShort(89);
		
		//D8-D9环境气压
		data.writeShort(67);
		
		//D10-D13(文档没有说明，用填充)
		data.writeZero(4);
		
		//D14-D16机器工作时间(单位：小时)
		data.writeBytes(toBytes(103, 3));
		
		//D17-D19气泵工作时间(单位：分钟)
		data.writeBytes(toBytes(60, 3));
		
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
