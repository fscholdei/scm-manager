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

package sonia.scm.repository;

import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.lib.CommitBuilder;
import org.eclipse.jgit.lib.GpgSignature;
import org.eclipse.jgit.lib.GpgSigner;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.transport.CredentialsProvider;
import sonia.scm.security.GPG;
import sonia.scm.security.PrivateKey;
import sonia.scm.security.PublicKey;

import java.util.Collections;
import java.util.Optional;

public final class GitTestHelper {

  private GitTestHelper() {
  }

  public static GitChangesetConverterFactory createConverterFactory() {
    return new GitChangesetConverterFactory(new NoopGPG());
  }

  public static class SimpleGpgSigner extends GpgSigner {

    public static byte[] getSignature() {
      return "SIGNATURE".getBytes();
    }

    @Override
    public void sign(CommitBuilder commitBuilder, String s, PersonIdent personIdent, CredentialsProvider
      credentialsProvider) throws CanceledException {
      commitBuilder.setGpgSignature(new GpgSignature(SimpleGpgSigner.getSignature()));
    }

    @Override
    public boolean canLocateSigningKey(String s, PersonIdent personIdent, CredentialsProvider credentialsProvider) throws CanceledException {
      return true;
    }

  }

  private static class NoopGPG implements GPG {

    @Override
    public String findPublicKeyId(byte[] signature) {
      return "secret-key";
    }

    @Override
    public Optional<PublicKey> findPublicKey(String id) {
      return Optional.empty();
    }

    @Override
    public Iterable<PublicKey> findPublicKeysByUsername(String username) {
      return Collections.emptySet();
    }

    @Override
    public PrivateKey getPrivateKey() {
      return null;
    }
  }
}
