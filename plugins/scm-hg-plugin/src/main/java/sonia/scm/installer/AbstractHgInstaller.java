/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */

package sonia.scm.installer;

//~--- non-JDK imports --------------------------------------------------------

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.io.Command;
import sonia.scm.io.CommandResult;
import sonia.scm.io.SimpleCommand;
import sonia.scm.repository.HgConfig;
import sonia.scm.repository.HgRepositoryHandler;
import sonia.scm.util.IOUtil;
import sonia.scm.util.SystemUtil;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Sebastian Sdorra
 */
public abstract class AbstractHgInstaller implements HgInstaller
{

  /** Field description */
  public static final String DIRECTORY_REPOSITORY = "repositories";

  /** the logger for AbstractHgInstaller */
  private static final Logger logger = LoggerFactory
      .getLogger(AbstractHgInstaller.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param baseDirectory
   */
  public AbstractHgInstaller(File baseDirectory)
  {
    this.baseDirectory = baseDirectory;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param config
   *
   * @throws IOException
   */
  @Override
  public void install(HgConfig config) throws IOException
  {
    File repoDirectory = new File(baseDirectory, DIRECTORY_REPOSITORY.concat(
        File.separator).concat(HgRepositoryHandler.TYPE_NAME));

    IOUtil.mkdirs(repoDirectory);
    config.setRepositoryDirectory(repoDirectory);
  }

  /**
   * TODO check for windows
   *
   *
   *
   * @param path
   * @param cmd
   *
   * @return
   */
  protected String search(String[] path, String cmd)
  {
    String cmdPath = null;

    try
    {
      Command command = new SimpleCommand(cmd, "--version");
      CommandResult result = command.execute();

      if (result.isSuccessfull())
      {
        cmdPath = cmd;
      }
    }
    catch (IOException ex)
    {}

    if (cmdPath == null)
    {
      for (String pathPart : path)
      {
        List<String> extensions = getExecutableSearchExtensions();
        File file = findFileByExtension(pathPart, cmd, extensions);
        if (file != null)
        {
          cmdPath = file.getAbsolutePath();
          break;
        }
      }
    }

    if (cmdPath != null)
    {
      if (logger.isInfoEnabled())
      {
        logger.info("found {} at {}", cmd, cmdPath);
      }
    }
    else if (logger.isWarnEnabled())
    {
      logger.warn("could not find {}", cmd);
    }

    return cmdPath;
  }

  /**
   * Returns a list of file extensions to use when searching for executables.
   * The list is in priority order, with the highest priority first.
   */
  protected List<String> getExecutableSearchExtensions()
  {
    List<String> extensions;
    if (SystemUtil.isWindows())
    {
      extensions = Arrays.asList(".exe");
    }
    else
    {
      extensions = Arrays.asList("");
    }
    return extensions;
  }

  private File findFileByExtension(String parentPath, String cmd,
      List<String> potentialExtensions)
  {
    File file = null;
    for (String potentialExtension : potentialExtensions)
    {
      String fileName = cmd.concat(potentialExtension);
      File potentialFile = new File(parentPath, fileName);
      if (potentialFile.exists())
      {
        file = potentialFile;
        break;
      }
    }
    return file;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  protected File baseDirectory;
}
