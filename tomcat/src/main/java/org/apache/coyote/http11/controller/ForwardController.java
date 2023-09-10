package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ForwardController extends AbstractController {

    private final String forwardPath;

    public ForwardController(final String forwardPath) {
        this.forwardPath = forwardPath;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.forwardTo(forwardPath);
    }
}
