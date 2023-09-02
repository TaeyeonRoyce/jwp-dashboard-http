package org.apache.coyote.http11.web;

import java.util.Map;
import java.util.function.BiFunction;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestMethod;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseStatus;

public abstract class AbstractController implements Controller {

    private final Map<HttpRequestMethod, BiFunction<HttpRequest, HttpResponse, View>> supportedMethods = Map.of(
            HttpRequestMethod.GET, this::handleGetRequest,
            HttpRequestMethod.POST, this::handlePostRequest
    );


    @Override
    public View handleRequest(final HttpRequest request, final HttpResponse response) {
        final HttpRequestMethod httpRequestMethod = request.getHttpStartLine().getHttpRequestMethod();
        return supportedMethods.get(httpRequestMethod).apply(request, response);
    }

    protected View handleGetRequest(final HttpRequest request, final HttpResponse response) {
        return forwardTo("/404.html", response, HttpResponseStatus.METHOD_NOT_ALLOWED);
    }

    protected View handlePostRequest(final HttpRequest request, final HttpResponse response) {
        return forwardTo("/404.html", response, HttpResponseStatus.METHOD_NOT_ALLOWED);
    }

    protected View redirect(final String path, final HttpResponse response) {
        response.updateHttpResponseStatusLineByStatus(HttpResponseStatus.FOUND);
        response.setHeader("Location", path);
        return new View(path);
    }

    protected View forwardTo(final String path) {
        return new View(path);
    }

    protected View forwardTo(
            final String path,
            final HttpResponse response,
            final HttpResponseStatus httpResponseStatus
    ) {
        response.updateHttpResponseStatusLineByStatus(httpResponseStatus);
        return forwardTo(path);
    }
}
