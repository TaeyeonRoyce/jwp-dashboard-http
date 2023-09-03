package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.mvc.ControllerMapping;
import org.apache.coyote.http11.mvc.FrontController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.parser.HttpRequestMessageReader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.formatter.HttpResponseMessageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = HttpRequestMessageReader.readHttpRequest(inputStream);
            final HttpResponse httpResponse = new HttpResponse();

            FrontController frontController = new FrontController(new ControllerMapping()); //FIXME DI
            frontController.handleHttpRequest(httpRequest, httpResponse);

            HttpResponseMessageWriter.writeHttpResponse(httpResponse, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
