/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.excelinsight.practical;

import static org.junit.jupiter.api.Assertions.*;

import java.net.*;
import java.net.http.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tools.jackson.databind.ObjectMapper;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PracticalHttpIntegrationTest {
  @LocalServerPort int port;
  @Autowired ObjectMapper json;
  private final HttpClient client = HttpClient.newHttpClient();
  private static final String TOKEN = UUID.randomUUID().toString() + UUID.randomUUID();

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @DynamicPropertySource
  static void property(DynamicPropertyRegistry registry) {
    registry.add("app.practical-token", () -> TOKEN);
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private String authorization() {
    return "Bearer " + TOKEN;
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private HttpResponse<String> post(String payload, String authorization) throws Exception {
    var request =
        HttpRequest.newBuilder(
                URI.create("http://127.0.0.1:" + port + "/api/practical/typed-table-profile"))
            .timeout(Duration.ofSeconds(15))
            .header("Content-Type", "application/json");
    if (authorization != null) request.header("Authorization", authorization);
    return client.send(
        request.POST(HttpRequest.BodyPublishers.ofString(payload)).build(),
        HttpResponse.BodyHandlers.ofString());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsMissingIdentity() throws Exception {
    assertEquals(
        401,
        post(
                "{\"columns\":[{\"id\":\"amount\",\"type\":\"NUMBER\"},{\"id\":\"date\",\"type\":\"DATE\"}],\"rows\":[{\"id\":\"R1\",\"values\":[\"10\",\"2026-10-23\"]},{\"id\":\"R2\",\"values\":[\"bad\",\"2026-02-30\"]},{\"id\":\"R3\",\"values\":[\"\",\"\"]}]}",
                null)
            .statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsInvalidIdentity() throws Exception {
    assertEquals(
        401,
        post(
                "{\"columns\":[{\"id\":\"amount\",\"type\":\"NUMBER\"},{\"id\":\"date\",\"type\":\"DATE\"}],\"rows\":[{\"id\":\"R1\",\"values\":[\"10\",\"2026-10-23\"]},{\"id\":\"R2\",\"values\":[\"bad\",\"2026-02-30\"]},{\"id\":\"R3\",\"values\":[\"\",\"\"]}]}",
                "Bearer invalid")
            .statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void returnsActualBusinessResult() throws Exception {
    var response =
        post(
            "{\"columns\":[{\"id\":\"amount\",\"type\":\"NUMBER\"},{\"id\":\"date\",\"type\":\"DATE\"}],\"rows\":[{\"id\":\"R1\",\"values\":[\"10\",\"2026-10-23\"]},{\"id\":\"R2\",\"values\":[\"bad\",\"2026-02-30\"]},{\"id\":\"R3\",\"values\":[\"\",\"\"]}]}",
            authorization());
    assertEquals(200, response.statusCode());
    var body = json.readValue(response.body(), Map.class);
    assertEquals(true, body.get("success"));
    assertFalse(((Map<?, ?>) body.get("data")).isEmpty());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsMissingBusinessFields() throws Exception {
    assertEquals(400, post("{}", authorization()).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsMalformedJson() throws Exception {
    assertEquals(400, post("{", authorization()).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsArrayRoot() throws Exception {
    assertEquals(400, post("[]", authorization()).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsUnknownBusinessField() throws Exception {
    var payload =
        json.readValue(
            "{\"columns\":[{\"id\":\"amount\",\"type\":\"NUMBER\"},{\"id\":\"date\",\"type\":\"DATE\"}],\"rows\":[{\"id\":\"R1\",\"values\":[\"10\",\"2026-10-23\"]},{\"id\":\"R2\",\"values\":[\"bad\",\"2026-02-30\"]},{\"id\":\"R3\",\"values\":[\"\",\"\"]}]}",
            Map.class);
    payload.put("__unknown", true);
    assertEquals(400, post(json.writeValueAsString(payload), authorization()).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsWrongAuthorizationScheme() throws Exception {
    assertEquals(
        401,
        post(
                "{\"columns\":[{\"id\":\"amount\",\"type\":\"NUMBER\"},{\"id\":\"date\",\"type\":\"DATE\"}],\"rows\":[{\"id\":\"R1\",\"values\":[\"10\",\"2026-10-23\"]},{\"id\":\"R2\",\"values\":[\"bad\",\"2026-02-30\"]},{\"id\":\"R3\",\"values\":[\"\",\"\"]}]}",
                "Basic " + TOKEN)
            .statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsDuplicateJsonFieldOverHttp() throws Exception {
    String source =
        "{\"columns\":[{\"id\":\"amount\",\"type\":\"NUMBER\"},{\"id\":\"date\",\"type\":\"DATE\"}],\"rows\":[{\"id\":\"R1\",\"values\":[\"10\",\"2026-10-23\"]},{\"id\":\"R2\",\"values\":[\"bad\",\"2026-02-30\"]},{\"id\":\"R3\",\"values\":[\"\",\"\"]}]}";
    var response =
        post(source.substring(0, source.length() - 1) + ",\"columns\":null}", authorization());
    assertEquals(400, response.statusCode());
    assertTrue(response.body().contains("JSON"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsTrailingJsonOverHttp() throws Exception {
    assertEquals(
        400,
        post(
                "{\"columns\":[{\"id\":\"amount\",\"type\":\"NUMBER\"},{\"id\":\"date\",\"type\":\"DATE\"}],\"rows\":[{\"id\":\"R1\",\"values\":[\"10\",\"2026-10-23\"]},{\"id\":\"R2\",\"values\":[\"bad\",\"2026-02-30\"]},{\"id\":\"R3\",\"values\":[\"\",\"\"]}]}"
                    + " {}",
                authorization())
            .statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsUnderflowInsteadOfTreatingAsZero() throws Exception {
    String source =
        "{\"columns\":[{\"id\":\"amount\",\"type\":\"NUMBER\"},{\"id\":\"date\",\"type\":\"DATE\"}],\"rows\":[{\"id\":\"R1\",\"values\":[\"10\",\"2026-10-23\"]},{\"id\":\"R2\",\"values\":[\"bad\",\"2026-02-30\"]},{\"id\":\"R3\",\"values\":[\"\",\"\"]}]}";
    var response =
        post(
            source.substring(0, source.length() - 1) + ",\"__scale\":1e-1000000}", authorization());
    assertEquals(400, response.statusCode());
    assertTrue(response.body().contains("数值精度"));
  }
}
