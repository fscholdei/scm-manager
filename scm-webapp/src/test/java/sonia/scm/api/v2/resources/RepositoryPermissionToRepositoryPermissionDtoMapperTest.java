package sonia.scm.api.v2.resources;

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import sonia.scm.repository.RepositoryPermission;
import sonia.scm.repository.Repository;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.Silent.class)
@SubjectAware(
  configuration = "classpath:sonia/scm/repository/shiro.ini"
)
public class RepositoryPermissionToRepositoryPermissionDtoMapperTest {

  @Rule
  public ShiroRule shiro = new ShiroRule();

  private final URI baseUri = URI.create("http://example.com/base/");

  @SuppressWarnings("unused") // Is injected
  private final ResourceLinks resourceLinks = ResourceLinksMock.createMock(baseUri);

  @InjectMocks
  RepositoryPermissionToRepositoryPermissionDtoMapperImpl mapper;

  @Test
  @SubjectAware(username = "trillian", password = "secret")
  public void shouldMapGroupPermissionCorrectly() {
    Repository repository = getDummyRepository();
    RepositoryPermission permission = new RepositoryPermission("42", "read,modify,delete", true);

    RepositoryPermissionDto repositoryPermissionDto = mapper.map(permission, repository);

    assertThat(repositoryPermissionDto.getLinks().getLinkBy("self").isPresent()).isTrue();
    assertThat(repositoryPermissionDto.getLinks().getLinkBy("self").get().getHref()).contains("@42");
  }

  @Test
  @SubjectAware(username = "trillian", password = "secret")
  public void shouldMapNonGroupPermissionCorrectly() {
    Repository repository = getDummyRepository();
    RepositoryPermission permission = new RepositoryPermission("42", "read,modify,delete", false);

    RepositoryPermissionDto repositoryPermissionDto = mapper.map(permission, repository);

    assertThat(repositoryPermissionDto.getLinks().getLinkBy("self").isPresent()).isTrue();
    assertThat(repositoryPermissionDto.getLinks().getLinkBy("self").get().getHref()).contains("42");
    assertThat(repositoryPermissionDto.getLinks().getLinkBy("self").get().getHref()).doesNotContain("@");
  }

  private Repository getDummyRepository() {
    return new Repository("repo", "git", "foo", "bar");
  }
}
