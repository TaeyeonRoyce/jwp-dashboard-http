package nextstep.web;

import java.util.UUID;
import nextstep.jwp.application.UserService;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.SessionManager;
import org.apache.coyote.http11.mvc.AbstractController;
import org.apache.coyote.http11.mvc.view.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final UserService userService = new UserService();

    @Override
    public ResponseEntity handleGetRequest(final HttpRequest request, final HttpResponse response) {
        final Session session = SessionManager.findSession(request.getCookie("JSESSIONID"));
        if (session != null) {
            final User user = (User) session.getAttribute("user");
            log.info("already login: {}", user);
            return ResponseEntity.redirectTo("/index.html");
        }

        return ResponseEntity.forwardTo("/login.html");
    }

    @Override
    public ResponseEntity handlePostRequest(final HttpRequest request, final HttpResponse response) {
        final String account = request.getPayloadValue("account");
        final String password = request.getPayloadValue("password");
        if (userService.validateLogin(account, password)) {
            return successLogin(response, account);
        }

        return ResponseEntity.forwardTo(HttpStatus.UNAUTHORIZED, "/401.html");
    }

    private ResponseEntity successLogin(final HttpResponse response, final String account) {
        final User user = userService.getUserByAccount(account);
        log.info("login success: {}", user);

        final String uuid = UUID.randomUUID().toString();
        response.setCookie("JSESSIONID", uuid);
        final Session session = new Session(uuid);
        session.setAttribute("user", user);
        SessionManager.add(session);
        return ResponseEntity.redirectTo("/index.html");
    }
}
