package test.backend.http;

import test.backend.http.message.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;

public class RequestDecoder extends SimpleChannelInboundHandler<HttpObject> {
	
	private long orderNumber;

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject httpObject)
			throws Exception {

		DecoderResult result = httpObject.getDecoderResult();
		if (!result.isSuccess()) {
			throw new BadRequestException(result.cause());
		}

		if (httpObject instanceof HttpRequest) {
			HttpRequest request = (HttpRequest) httpObject;

			ReferenceCountUtil.retain(httpObject);
			ctx.fireChannelRead(new Request(request, orderNumber));
		}
		
		orderNumber += 1;
	}
}
