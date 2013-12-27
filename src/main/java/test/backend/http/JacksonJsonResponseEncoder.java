package test.backend.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import io.netty.handler.codec.http.HttpResponse;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.util.CharsetUtil;

public class JacksonJsonResponseEncoder extends ChannelOutboundHandlerAdapter {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	static {
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		if (msg instanceof HttpResponse) {
			super.write(ctx, msg, promise);
			return;
		}

		String res;
		try {
			res = objectMapper.writeValueAsString(msg);
		} catch (Exception ex) {
			ctx.fireExceptionCaught(ex);
			return;
		}

		// Build the response object
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
				Unpooled.copiedBuffer(res, CharsetUtil.UTF_8));

		response.headers().set(CONTENT_TYPE, "application/json");
		response.headers().set(CONTENT_LENGTH,
				response.content().readableBytes());

		// Write the response
		ctx.write(response, promise);
	}
}
