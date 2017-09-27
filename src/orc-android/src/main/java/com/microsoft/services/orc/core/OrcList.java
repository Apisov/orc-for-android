package com.microsoft.services.orc.core;

import android.net.Uri;

import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.services.orc.http.HttpVerb;
import com.microsoft.services.orc.http.OrcResponse;
import com.microsoft.services.orc.http.OrcURL;
import com.microsoft.services.orc.http.Request;

import java.util.ArrayList;
import java.util.Collection;

public class OrcList<E> extends ArrayList<E> {

    private Class<E> clazz;
    private String nextLink;
    private String deltaLink;
    private DependencyResolver resolver;
    private BaseOrcContainer baseOrcContainer;

    public OrcList(Collection<? extends E> collection,
                   Class<E> clazz, String nextLink,
                   String deltaLink, DependencyResolver resolver,
                   BaseOrcContainer baseOrcContainer) {
        super(collection);
        this.clazz = clazz;
        this.nextLink = nextLink;
        this.deltaLink = deltaLink;
        this.resolver = resolver;
        this.baseOrcContainer = baseOrcContainer;
    }

    public boolean hasNext() {
        return nextLink != null;
    }

    public boolean hasDelta() {
        return deltaLink != null;
    }

    public String getDeltaToken() {
        if (deltaLink != null) {
            Uri uri = Uri.parse(deltaLink);
            String deltatoken = "$deltatoken";
            for (String parameter : uri.getQueryParameterNames()) {
                if (parameter.equalsIgnoreCase("$deltatoken")) {
                    deltatoken = parameter;
                }
            }
            return uri.getQueryParameter(deltatoken);
        }
        return null;
    }

    public String getSkipToken() {
        if (nextLink != null) {
            Uri uri = Uri.parse(deltaLink);
            String skipToken = "$skipToken";
            for (String parameter : uri.getQueryParameterNames()) {
                if (parameter.equalsIgnoreCase("$skiptoken")) {
                    skipToken = parameter;
                }
            }
            return uri.getQueryParameter(skipToken);
        }
        return null;
    }

    public ListenableFuture<OrcList<E>> followNextLink() {
        Request request = resolver.createRequest();

        request.setVerb(HttpVerb.GET);
        OrcURL url = resolver.getOrcURL();
        url.setBaseUrl(nextLink);
        request.setUrl(url);

        ListenableFuture<OrcResponse> future = baseOrcContainer.oDataExecute(request);
        ListenableFuture<String> stringFuture = Helpers.transformToStringListenableFuture(future);

        return Helpers.transformToEntityListListenableFuture(stringFuture, this.clazz, resolver, baseOrcContainer);
    }

    public ListenableFuture<OrcList<E>> followDeltaLink() {
        Request request = resolver.createRequest();

        request.setVerb(HttpVerb.GET);
        OrcURL url = resolver.getOrcURL();
        url.setBaseUrl(deltaLink);
        request.setUrl(url);

        ListenableFuture<OrcResponse> future = baseOrcContainer.oDataExecute(request);
        ListenableFuture<String> stringFuture = Helpers.transformToStringListenableFuture(future);

        return Helpers.transformToEntityListListenableFuture(stringFuture, this.clazz, resolver, baseOrcContainer);
    }
}
