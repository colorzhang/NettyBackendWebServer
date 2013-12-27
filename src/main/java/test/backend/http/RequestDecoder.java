package test.backend.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;

import java.util.Queue;

public class RequestDecoder extends SimpleChannelInboundHandler<HttpObject> {

	private final Queue<HttpRequest> requestQueue;

	public RequestDecoder(Queue<HttpRequest> requestQueue) {
		this.requestQueue = requestQueue;
	}

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
			requestQueue.add(request);
		}
		ctx.fireChannelRead(httpObject);
	}
}
