package com.playerrealms.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;

public class PacketEncoder extends MessageToByteEncoder<ByteBuf> {

	@Override
	protected void encode(ChannelHandlerContext ch, ByteBuf buf, ByteBuf out) throws Exception {
		
		out.writeInt(buf.readableBytes());
		out.writeBytes(buf);
		
	}

}
