package test.backend.http;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static io.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.ServerCookieEncoder;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Queue;
import java.util.Set;

public class ResponseEncoder extends ChannelOutboundHandlerAdapter {

	private static final String SESSION_COOKIE_NAME = "JSESSIOINID";
	private static final SecureRandom random = new SecureRandom();
	private final Queue<HttpRequest> requestQueue;

	public ResponseEncoder(Queue<HttpRequest> requestQueue) {
		this.requestQueue = requestQueue;
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		if (!(msg instanceof HttpResponse)) {
			super.write(ctx, msg, promise);
			return;
		}

		HttpResponse response = (HttpResponse) msg;
		HttpRequest request = requestQueue.poll();

		String cookieString = request.headers().get(COOKIE);
		Boolean hasSessionId = false;
		if (cookieString != null) {
			Set<Cookie> cookies = CookieDecoder.decode(cookieString);
			if (!cookies.isEmpty()) {
				// Reset the cookies if necessary.
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals(SESSION_COOKIE_NAME)) {
						hasSessionId = true;
					}
					response.headers().add(SET_COOKIE,
							ServerCookieEncoder.encode(cookie));
				}
			}
		}
		if (!hasSessionId) {
			response.headers().add(
					SET_COOKIE,
					ServerCookieEncoder.encode(SESSION_COOKIE_NAME,
							nextSessionId()));
		}

		Boolean keepAlive = isKeepAlive(request);

		if (keepAlive) {
			// Add keep alive header as per:
			// -
			// http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
			response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		} else {
			response.headers().set(CONNECTION, HttpHeaders.Values.CLOSE);
		}

		ctx.write(msg, promise);
	}

	public String nextSessionId() {
		return new BigInteger(130, random).toString(32);
	}
}
