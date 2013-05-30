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


package sonia.scm.repository.spi;

//~--- non-JDK imports --------------------------------------------------------

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import org.junit.Test;

import sonia.scm.repository.ChangesetPagingResult;
import sonia.scm.repository.RepositoryException;

import static org.junit.Assert.*;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

/**
 *
 * @author Sebastian Sdorra
 */
public class GitIncomingCommandTest
  extends AbstractRemoteCommandTestBase
{

  /**
   * Method description
   *
   *
   * @throws GitAPIException
   * @throws IOException
   * @throws RepositoryException
   */
  @Test
  public void testGetIncomingChangesets()
    throws IOException, GitAPIException, RepositoryException
  {
    write(outgoing, outgoingDirectory, "a.txt", "content of a.txt");

    RevCommit c1 = commit(outgoing, "added a");

    write(outgoing, outgoingDirectory, "b.txt", "content of b.txt");

    RevCommit c2 = commit(outgoing, "added b");

    GitIncomingCommand cmd = createCommand();
    IncomingCommandRequest request = new IncomingCommandRequest();

    request.setRemoteRepository(outgoingRepository);

    ChangesetPagingResult cpr = cmd.getIncomingChangesets(request);

    assertNotNull(cpr);

    assertEquals(2, cpr.getTotal());
    assertCommitsEquals(c1, cpr.getChangesets().get(0));
    assertCommitsEquals(c2, cpr.getChangesets().get(1));
  }

  /**
   * Method description
   *
   *
   * @throws IOException
   * @throws RepositoryException
   */
  @Test
  public void testGetIncomingChangesetsWithEmptyRepository()
    throws IOException, RepositoryException
  {
    GitIncomingCommand cmd = createCommand();
    IncomingCommandRequest request = new IncomingCommandRequest();

    request.setRemoteRepository(outgoingRepository);

    ChangesetPagingResult cpr = cmd.getIncomingChangesets(request);

    assertNotNull(cpr);

    assertEquals(0, cpr.getTotal());
  }

  /**
   * Method description
   *
   *
   * @throws GitAPIException
   * @throws IOException
   * @throws RepositoryException
   */
  @Test
  public void testGetIncomingChangesetsWithUnrelatedRepository()
    throws IOException, RepositoryException, GitAPIException
  {
    write(outgoing, outgoingDirectory, "a.txt", "content of a.txt");

    commit(outgoing, "added a");

    write(incoming, incomingDirectory, "b.txt", "content of b.txt");

    commit(incoming, "added b");

    GitIncomingCommand cmd = createCommand();
    IncomingCommandRequest request = new IncomingCommandRequest();

    request.setRemoteRepository(outgoingRepository);

    ChangesetPagingResult cpr = cmd.getIncomingChangesets(request);

    assertNotNull(cpr);

    assertEquals(0, cpr.getTotal());
  }

  /**
   * Method description
   *
   *
   * @return
   */
  private GitIncomingCommand createCommand()
  {
    return new GitIncomingCommand(handler, new GitContext(incomingDirectory),
      incomgingRepository);
  }
}
