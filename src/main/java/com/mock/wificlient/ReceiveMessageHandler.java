package com.mock.wificlient;

import java.nio.charset.Charset;

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
			short length = data.readShort();
			byte control = data.readByte();
			if (control == 0x0D) {
				String message = data.toString(Charset.forName("UTF-8"));
				logger.info("auth result : {}",message);
			}else {
				logger.info("Get response from server. length : {},control:{},data:{}",length,control,data.readByte());
			}
		}finally {
			ReferenceCountUtil.release(msg);
		}
	}
}

