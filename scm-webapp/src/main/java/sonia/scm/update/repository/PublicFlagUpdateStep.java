package sonia.scm.update.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.SCMContext;
import sonia.scm.SCMContextProvider;
import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermission;
import sonia.scm.repository.xml.XmlRepositoryDAO;
import sonia.scm.user.User;
import sonia.scm.user.xml.XmlUserDAO;
import sonia.scm.version.Version;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import static sonia.scm.version.Version.parse;

@Extension
public class PublicFlagUpdateStep implements UpdateStep {

  private static final Logger LOG = LoggerFactory.getLogger(PublicFlagUpdateStep.class);

  private static final String V1_REPOSITORY_BACKUP_FILENAME = "repositories.xml.v1.backup";

  private final SCMContextProvider contextProvider;
  private final XmlUserDAO userDAO;
  private final XmlRepositoryDAO repositoryDAO;

  @Inject
  public PublicFlagUpdateStep(SCMContextProvider contextProvider, XmlUserDAO userDAO, XmlRepositoryDAO repositoryDAO) {
    this.contextProvider = contextProvider;
    this.userDAO = userDAO;
    this.repositoryDAO = repositoryDAO;
  }

  @Override
  public void doUpdate() throws JAXBException {
    createNewAnonymousUserIfNotExists();
    deleteOldAnonymousUserIfAvailable();

    JAXBContext jaxbContext = JAXBContext.newInstance(V1RepositoryHelper.V1RepositoryDatabase.class);
    LOG.info("Migrating public flags of repositories as RepositoryRolePermission 'READ' for user '_anonymous'");
    V1RepositoryHelper.readV1Database(jaxbContext, contextProvider, V1_REPOSITORY_BACKUP_FILENAME).ifPresent(
      this::addRepositoryReadPermissionForAnonymousUser
    );
  }

  @Override
  public Version getTargetVersion() {
    return parse("2.0.3");
  }

  @Override
  public String getAffectedDataType() {
    return "sonia.scm.repository.xml";
  }

  private void addRepositoryReadPermissionForAnonymousUser(V1RepositoryHelper.V1RepositoryDatabase v1RepositoryDatabase) {
    User v2AnonymousUser = userDAO.get(SCMContext.USER_ANONYMOUS);
    v1RepositoryDatabase.repositoryList.repositories
      .stream()
      .filter(V1Repository::isPublic)
      .forEach(v1Repository -> {
        Repository v2Repository = repositoryDAO.get(v1Repository.getId());
        LOG.info(String.format("Add RepositoryRole 'READ' to _anonymous user for repository: %s - %s/%s", v2Repository.getId(), v2Repository.getNamespace(), v2Repository.getName()));
        v2Repository.addPermission(new RepositoryPermission(v2AnonymousUser.getId(), "READ", false));
        repositoryDAO.modify(v2Repository);
      });
  }

  private void createNewAnonymousUserIfNotExists() {
    if (!userExists(SCMContext.USER_ANONYMOUS)) {
      LOG.info("Create new _anonymous user");
      userDAO.add(SCMContext.ANONYMOUS);
    }
  }

  private void deleteOldAnonymousUserIfAvailable() {
    String oldAnonymous = "anonymous";
    if (userExists(oldAnonymous)) {
      User anonymousUser = userDAO.get(oldAnonymous);
      LOG.info("Delete obsolete anonymous user");
      userDAO.delete(anonymousUser);
    }
  }

  private boolean userExists(String username) {
    return userDAO
      .getAll()
      .stream()
      .anyMatch(user -> user.getName().equals(username));
  }
}
