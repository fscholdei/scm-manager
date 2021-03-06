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

package sonia.scm.repository.client.spi;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.GitChangesetConverter;
import sonia.scm.repository.GitTestHelper;
import sonia.scm.repository.client.api.RepositoryClientException;

import java.io.IOException;

public class GitMergeCommand implements MergeCommand {

  private final Git git;

  GitMergeCommand(Git git) {
    this.git = git;
  }

  @Override
  public Changeset merge(MergeRequest request) throws IOException {
    try (GitChangesetConverter converter = GitTestHelper.createConverterFactory().create(git.getRepository())) {
      ObjectId resolved = git.getRepository().resolve(request.getBranch());
      org.eclipse.jgit.api.MergeCommand mergeCommand = git.merge()
        .include(request.getBranch(), resolved)
        .setMessage(request.getMessage());

      switch (request.getFfMode()) {
        case FF:
          mergeCommand.setFastForward(org.eclipse.jgit.api.MergeCommand.FastForwardMode.FF);
          break;
        case NO_FF:
          mergeCommand.setFastForward(org.eclipse.jgit.api.MergeCommand.FastForwardMode.NO_FF);
          break;
        case FF_ONLY:
          mergeCommand.setFastForward(org.eclipse.jgit.api.MergeCommand.FastForwardMode.FF_ONLY);
          break;
        default:
          throw new IllegalStateException("Unknown FF mode: " + request.getFfMode());
      }

      MergeResult mergeResult = mergeCommand
        .call();

      try (RevWalk revWalk = new RevWalk(git.getRepository())) {
        RevCommit commit = revWalk.parseCommit(mergeResult.getNewHead());
        return converter.createChangeset(commit);
      }
    } catch (GitAPIException ex) {
      throw new RepositoryClientException("could not commit changes to repository", ex);
    }
  }
}
