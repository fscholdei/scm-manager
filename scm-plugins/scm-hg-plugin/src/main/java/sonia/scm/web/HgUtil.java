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

package sonia.scm.web;

import sonia.scm.SCMContext;
import sonia.scm.repository.HgConfig;
import sonia.scm.repository.HgPythonScript;
import sonia.scm.util.Util;

import java.io.File;

/**
 *
 * @author Sebastian Sdorra
 */
public final class HgUtil {

  public static final String REVISION_TIP = "tip";

  private HgUtil() {}

  public static String getPythonPath(HgConfig config) {
    String pythonPath = Util.EMPTY_STRING;

    if (config != null) {
      pythonPath = Util.nonNull(config.getPythonPath());
    }

    if (Util.isNotEmpty(pythonPath)) {
      pythonPath = pythonPath.concat(File.pathSeparator);
    }

    pythonPath = pythonPath.concat(
      HgPythonScript.getScriptDirectory(
        SCMContext.getContext()
      ).getAbsolutePath()
    );

    return pythonPath;
  }

  public static String getRevision(String revision) {
    return Util.isEmpty(revision) ? REVISION_TIP : revision;
  }

}
