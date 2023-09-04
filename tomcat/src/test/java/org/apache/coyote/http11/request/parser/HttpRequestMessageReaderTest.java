package org.apache.coyote.http11.request.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import support.StubSocket;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("HttpMethod 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestMessageReaderTest {

    @Test
    void 요청스트림을_읽어_HttpRequest를_생성한다() throws IOException {
        // given
        final String httpRequestMessage = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cache-Control: max-age=0 ",
                "sec-ch-ua: \"Not)A;Brand\";v=\"24\", \"Chromium\";v=\"116\" ",
                "sec-ch-ua-mobile: ?0 ",
                "sec-ch-ua-platform: \"macOS\" ",
                "DNT: 1 ",
                "Upgrade-Insecure-Requests: 1 ",
                "",
                "");
        final StubSocket stubSocket = new StubSocket(httpRequestMessage);
        final InputStream inputStream = stubSocket.getInputStream();

        // when
        final HttpRequest httpRequest = HttpRequestMessageReader.readHttpRequest(inputStream);

        // then
        assertSoftly(softAssertions -> {
                    assertThat(httpRequest.getHttpStartLine().getHttpRequestMethod()).isEqualTo(HttpMethod.GET);
                    assertThat(httpRequest.getHttpStartLine().getRequestURI()).isEqualTo("/index.html");
                    assertThat(httpRequest.getHttpStartLine().getHttpVersion()).isEqualTo("HTTP/1.1");
                    assertThat(httpRequest.getHeader("Connection")).isEqualTo("keep-alive");
                    assertThat(httpRequest.getHeader("Host")).isEqualTo("localhost:8080");
                    assertThat(httpRequest.getHeader("Cache-Control")).isEqualTo("max-age=0");
                }
        );
        stubSocket.close();
    }

    @Test
    void 잘못된_HTTP_요청_메세지_시작라인인_경우_예외_발생() throws IOException {
        // given
        final String httpRequestMessage = String.join("\r\n",
                "GET /index.html wrongSize HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final StubSocket stubSocket = new StubSocket(httpRequestMessage);
        final InputStream inputStream = stubSocket.getInputStream();

        // when & then
        assertThatThrownBy(() -> HttpRequestMessageReader.readHttpRequest(inputStream))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("시작 라인의 토큰은 3개여야 합니다.");
        stubSocket.close();
    }

    @Test
    void 쿼리파라미터와_URI를_분리하여_저장한다() throws IOException {
        // given
        final String httpRequestMessage = String.join("\r\n",
                "GET /login.html?name=royce&password=p1234 HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final StubSocket stubSocket = new StubSocket(httpRequestMessage);
        final InputStream inputStream = stubSocket.getInputStream();

        // when
        final HttpRequest httpRequest = HttpRequestMessageReader.readHttpRequest(inputStream);

        // then
        assertSoftly(softAssertions -> {
            assertThat(httpRequest.getParam("name")).isEqualTo("royce");
            assertThat(httpRequest.getParam("password")).isEqualTo("p1234");
        });
        stubSocket.close();
    }

    @Test
    void POST요청시_body_데이터를_저장한다() throws IOException {
        // given
        final String requestBody = "name=royce&password=p1234";
        final String httpRequestMessage = String.join("\r\n",
                "POST /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Content-Length: " + requestBody.length() + " ",
                "",
                requestBody);
        final StubSocket stubSocket = new StubSocket(httpRequestMessage);

        // when
        final HttpRequest httpRequest = HttpRequestMessageReader.readHttpRequest(stubSocket.getInputStream());

        // then
        assertSoftly(softAssertions -> {
            assertThat(httpRequest.getPayloadValue("name")).isEqualTo("royce");
            assertThat(httpRequest.getPayloadValue("password")).isEqualTo("p1234");
        });
        stubSocket.close();
    }

    @Test
    void 요청시_Cookie를_저장한다() throws IOException {
        // given
        final String requestBody = "name=royce&password=p1234";
        final String httpRequestMessage = String.join("\r\n",
                "POST /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; newjeans_cookie=newjeans; JSESSIONID=randomUUID",
                "Content-Type: application/x-www-form-urlencoded ",
                "Content-Length: " + requestBody.length() + " ",
                "",
                requestBody);
        final StubSocket stubSocket = new StubSocket(httpRequestMessage);

        // when
        final HttpRequest httpRequest = HttpRequestMessageReader.readHttpRequest(stubSocket.getInputStream());

        // then
        assertSoftly(softly -> {
            softly.assertThat(httpRequest.getCookie("yummy_cookie")).isEqualTo("choco");
            softly.assertThat(httpRequest.getCookie("newjeans_cookie")).isEqualTo("newjeans");
            softly.assertThat(httpRequest.getCookie("JSESSIONID")).isEqualTo("randomUUID");
        });
        stubSocket.close();
    }
}
