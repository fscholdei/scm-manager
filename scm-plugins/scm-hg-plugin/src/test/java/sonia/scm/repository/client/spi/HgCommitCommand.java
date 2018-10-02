/**
 * Copyright (c) 2014, Sebastian Sdorra
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
package sonia.scm.repository.client.spi;

import com.aragost.javahg.Repository;
import com.google.common.collect.Lists;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Person;

import java.io.IOException;

/**
 * Mercurial implementation of the {@link CommitCommand}.
 * 
 * @author Sebastian Sdorra
 */
public class HgCommitCommand implements CommitCommand
{
  
  private final Repository repository;

  HgCommitCommand(Repository repository)
  {
    this.repository = repository;
  }

  @Override
  public Changeset commit(CommitRequest request) throws IOException
  {
    com.aragost.javahg.Changeset c = com.aragost.javahg.commands.CommitCommand
      .on(repository)
      .user(request.getAuthor().toString())
      .message(request.getMessage())
      .execute();
    
    Changeset changeset = new Changeset(
      c.getNode(), 
      c.getTimestamp().getDate().getTime(), 
      Person.toPerson(c.getUser()),
      c.getMessage()
    );
    
    changeset.setBranches(Lists.newArrayList(c.getBranch()));
    changeset.setTags(c.tags());
    return changeset;
  }
  
}
