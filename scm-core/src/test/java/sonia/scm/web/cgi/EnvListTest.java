/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package sonia.scm.web.cgi;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class EnvListTest {

  @Test
  void shouldNotPrintAuthorizationValue() {
    EnvList env = new EnvList();
    env.set("HTTP_AUTHORIZATION", "Basic xxx");
    env.set("SOME_OTHER", "other");
    env.set("SCM_BEARER_TOKEN", "secret");

    String value = env.toString();

    assertThat(value)
      .contains("SOME_OTHER=other")
      .contains("HTTP_AUTHORIZATION=(is set)")
      .contains("SCM_BEARER_TOKEN=(is set)")
      .doesNotContain("HTTP_AUTHORIZATION=Basic xxx")
      .doesNotContain("SCM_BEARER_TOKEN=secret");
  }

  @Test
  void shouldReturnAsArray() {
    EnvList env = new EnvList();
    env.set("SPACESHIPT", "Heart of Gold");
    env.set("DOMAIN", "hitchhiker.com");

    assertThat(env.getEnvArray())
      .contains("SPACESHIPT=Heart of Gold")
      .contains("DOMAIN=hitchhiker.com");
  }
}
