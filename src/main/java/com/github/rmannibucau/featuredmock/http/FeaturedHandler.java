package com.github.rmannibucau.featuredmock.http;

import com.github.rmannibucau.featuredmock.util.Extensions;
import com.github.rmannibucau.featuredmock.util.IOs;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

import java.io.InputStream;

@ChannelHandler.Sharable
class FeaturedHandler extends SimpleChannelInboundHandler<FullHttpRequest> implements Extensions {
    private final ContentTypeMapper[] mappers;

    public FeaturedHandler(final ContentTypeMapper[] mappers) {
        this.mappers = mappers;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception {
        if (!request.getDecoderResult().isSuccess()) {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        InputStream stream = null;
        String type = null;

        final String accept = request.headers().get(HttpHeaders.Names.ACCEPT);
        if (accept != null) {
            for (final String a : accept.split(",")) {
                if (mappers != null) {
                    for (final ContentTypeMapper mapper : mappers) {
                        if (mapper.handle(a)) {
                            final String extension = mapper.extension(a);
                            stream = findResource(request, extension);
                            if (stream == null && extension != null && !extension.startsWith(".")) {
                                stream = findResource(request, "." + extension);
                            }
                            if (stream != null) {
                                type = mapper.contentType(a);
                                break;
                            }
                        }
                    }
                }

                if (stream == null) {
                    for (final String ext : EXTENSIONS) {
                        if (a.contains(ext.substring(1))) {
                            stream = findResource(request, ext);
                            if (stream != null) {
                                type = "application/" + ext.substring(1); // remove dot
                            }
                            break;
                        }
                    }
                }

                if (stream != null) {
                    break;
                }
            }
        }

        if (stream == null) { // not found from content type so try all extensions
            for (final String ext : EXTENSIONS) {
                stream = findResource(request, ext);
                if (stream != null) {
                    type = "application/" + ext.substring(1); // remove dot
                    break;
                }
            }
        }

        if (stream == null) { // try without extension
            stream = findResource(request, "");
            if (stream != null) {
                type = "text/plain";
            }
        }

        if (stream == null) {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }

        final byte[] bytes = IOs.slurp(stream);
        final HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(bytes));
        HttpHeaders.setContentLength(response, bytes.length);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, type);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static InputStream findResource(final FullHttpRequest request, final String ext) {
        String path = request.getUri();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf('?'));
        }
        if (path.contains("#")) {
            path = path.substring(0, path.indexOf('#'));
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1) + ext;
        } else {
            path = path + ext;
        }

        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final InputStream is = loader.getResourceAsStream(request.getMethod().name() + "-" + path);
        if (is != null) {
            return is;
        }
        return loader.getResourceAsStream(path);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel().isActive()) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static void sendError(final ChannelHandlerContext ctx, final HttpResponseStatus status) {
        final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
