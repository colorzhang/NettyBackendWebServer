package test.backend.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.Callable;

import test.backend.Provider;
import test.backend.Values;

public class DefaultHandler extends SimpleChannelInboundHandler<Values> {

	private Future<? extends Object> future;

	private final EventExecutorGroup executor;

	public DefaultHandler(EventExecutorGroup executor) {
		this.executor = executor;
	}

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx,
			final Values values) throws Exception {
		Callable<? extends Object> callable = new Provider(values);

		future = executor.submit(callable);

		future.addListener(new GenericFutureListener<Future<Object>>() {
			@Override
			public void operationComplete(Future<Object> future)
					throws Exception {
				if (future.isSuccess()) {
					ctx.writeAndFlush(future.get());
				} else {
					ctx.fireExceptionCaught(future.cause());
				}
			}
		});
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		if (future != null && !future.isDone()) {
			future.cancel(true);
		}
	}

}
