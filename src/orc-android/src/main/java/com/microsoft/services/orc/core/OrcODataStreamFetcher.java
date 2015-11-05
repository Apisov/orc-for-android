package com.microsoft.services.orc.core;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.services.orc.http.HttpVerb;
import com.microsoft.services.orc.http.OrcResponse;
import com.microsoft.services.orc.http.OrcURL;
import com.microsoft.services.orc.http.Request;

import java.io.InputStream;

import static com.microsoft.services.orc.core.Helpers.transformToVoidListenableFuture;


public class OrcODataStreamFetcher {

    private String urlComponent;
    private OrcExecutable parent;
    private DependencyResolver dependencyResolver;

    public OrcODataStreamFetcher(String urlComponent, OrcExecutable parent, DependencyResolver dependencyResolver) {
        this.urlComponent = urlComponent;
        this.parent = parent;
        this.dependencyResolver = dependencyResolver;
    }

    /**
     * Gets streamed content.
     *
     * @return the streamed content
     */
    public ListenableFuture<InputStream> getStream() {

        Request request = dependencyResolver.createRequest();
        request.setVerb(HttpVerb.GET);
        request.addOption(Request.MUST_STREAM_RESPONSE_CONTENT, "true");
        OrcURL url = request.getUrl();
        url.appendPathComponent("$value");

        ListenableFuture<OrcResponse> future = oDataExecute(request);

        return Futures.transform(future, new AsyncFunction<OrcResponse, InputStream>() {
            @Override
            public ListenableFuture<InputStream> apply(OrcResponse response) throws Exception {
                SettableFuture<InputStream> result = SettableFuture.create();
                result.set(new ODataStream(response.openStreamedResponse(), response));
                return result;
            }
        });
    }

    /**
     * Put content.
     *
     * @param content the content
     * @return the listenable future
     */
    public ListenableFuture<Void> putContent(byte[] content) {

        Request request = getResolver().createRequest();
        request.setContent(content);
        request.setVerb(HttpVerb.PUT);
        OrcURL url = request.getUrl();
        url.appendPathComponent("$value");

        ListenableFuture<OrcResponse> future = oDataExecute(request);

        return transformToVoidListenableFuture(future);
    }

    /**
     * Put content.
     *
     * @param stream the stream
     * @param streamSize the stream size
     * @return the listenable future
     */
    public ListenableFuture<Void> putContent(InputStream stream, long streamSize) {
        Request request = getResolver().createRequest();
        request.setStreamedContent(stream, streamSize);
        request.setVerb(HttpVerb.PUT);
        OrcURL url = request.getUrl();
        url.appendPathComponent("$value");

        ListenableFuture<OrcResponse> future = oDataExecute(request);

        return transformToVoidListenableFuture(future);
    }


    protected ListenableFuture<OrcResponse> oDataExecute(Request request) {

        OrcURL orcURL = request.getUrl();
        orcURL.prependPathComponent(urlComponent);
        return parent.oDataExecute(request);
    }

    protected DependencyResolver getResolver() {
        return dependencyResolver;
    }
}
