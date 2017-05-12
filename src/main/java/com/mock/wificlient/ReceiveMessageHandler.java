package com.mock.wificlient;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiveMessageHandler extends ChannelInboundHandlerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(ReceiveMessageHandler.class);
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		try {
			ByteBuf data = (ByteBuf) msg;
			logger.info("receive message:{}",data.getByte(2));
		}finally {
			ReferenceCountUtil.release(msg);
		}
		
	}

}
