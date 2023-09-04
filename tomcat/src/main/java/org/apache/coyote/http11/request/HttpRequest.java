package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.common.HttpHeaders;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final HttpHeaders httpRequestHeaders;
    private final Map<String, String> queryParams;
    private final Map<String, String> payload;
    private final HttpCookie cookie;

    public HttpRequest(
            final HttpRequestLine httpRequestLine,
            final HttpHeaders httpRequestHeaders,
            final HttpCookie cookie,
            final Map<String, String> payload
    ) {
        this.httpRequestLine = httpRequestLine;
        this.httpRequestHeaders = httpRequestHeaders;
        this.queryParams = httpRequestLine.getQueryParams();
        this.cookie = cookie;
        this.payload = payload;
    }

    public static HttpRequest of(
            final HttpRequestLine httpRequestLine,
            final HttpHeaders headers,
            final HttpCookie cookie
    ) {
        return new HttpRequest(
                httpRequestLine,
                headers,
                cookie,
                Map.of()
        );
    }

    public static HttpRequest of(
            final HttpRequestLine httpRequestLine,
            final HttpHeaders headers,
            final HttpCookie cookie,
            final Map<String, String> payload
    ) {
        return new HttpRequest(
                httpRequestLine,
                headers,
                cookie,
                payload
        );
    }

    public HttpRequestLine getHttpStartLine() {
        return httpRequestLine;
    }

    public String getHeader(final String header) {
        return httpRequestHeaders.get(header);
    }

    public String getParam(final String parameter) {
        return queryParams.get(parameter);
    }

    public String getPayloadValue(final String key) {
        return payload.get(key);
    }

    public String getCookie(final String cookieKey) {
        return cookie.getCookieValue(cookieKey);
    }
}
