package nextstep.web;

import nextstep.jwp.application.RegisterService;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final RegisterService registerService = new RegisterService();

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) {
        response.forwardTo("/register.html");
    }

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) {
        final String account = request.getPayloadValue("account");
        final String password = request.getPayloadValue("password");
        final String email = request.getPayloadValue("email");

        registerService.register(account, password, email);

        response.redirectTo("/index.html");
    }
}
