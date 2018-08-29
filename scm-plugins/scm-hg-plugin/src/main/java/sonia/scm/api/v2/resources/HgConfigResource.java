package sonia.scm.api.v2.resources;

import com.webcohesion.enunciate.metadata.rs.ResponseCode;
import com.webcohesion.enunciate.metadata.rs.StatusCodes;
import com.webcohesion.enunciate.metadata.rs.TypeHint;
import sonia.scm.config.ConfigurationPermissions;
import sonia.scm.repository.HgConfig;
import sonia.scm.repository.HgRepositoryHandler;
import sonia.scm.web.HgVndMediaType;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * RESTful Web Service Resource to manage the configuration of the hg plugin.
 */
@Path(HgConfigResource.HG_CONFIG_PATH_V2)
public class HgConfigResource {

  static final String HG_CONFIG_PATH_V2 = "v2/config/hg";

  private final HgConfigDtoToHgConfigMapper dtoToConfigMapper;
  private final HgConfigToHgConfigDtoMapper configToDtoMapper;
  private final HgRepositoryHandler repositoryHandler;
  private final Provider<HgConfigPackageResource> packagesResource;
  private final Provider<HgConfigAutoConfigurationResource> autoconfigResource;
  private final Provider<HgConfigInstallationsResource> installationsResource;

  @Inject
  public HgConfigResource(HgConfigDtoToHgConfigMapper dtoToConfigMapper, HgConfigToHgConfigDtoMapper configToDtoMapper,
                          HgRepositoryHandler repositoryHandler, Provider<HgConfigPackageResource> packagesResource,
                          Provider<HgConfigAutoConfigurationResource> autoconfigResource,
                          Provider<HgConfigInstallationsResource> installationsResource) {
    this.dtoToConfigMapper = dtoToConfigMapper;
    this.configToDtoMapper = configToDtoMapper;
    this.repositoryHandler = repositoryHandler;
    this.packagesResource = packagesResource;
    this.autoconfigResource = autoconfigResource;
    this.installationsResource = installationsResource;
  }

  /**
   * Returns the hg config.
   */
  @GET
  @Path("")
  @Produces(HgVndMediaType.CONFIG)
  @TypeHint(HgConfigDto.class)
  @StatusCodes({
    @ResponseCode(code = 200, condition = "success"),
    @ResponseCode(code = 401, condition = "not authenticated / invalid credentials"),
    @ResponseCode(code = 403, condition = "not authorized, the current user does not have the \"configuration:read:hg\" privilege"),
    @ResponseCode(code = 500, condition = "internal server error")
  })
  public Response get() {

    ConfigurationPermissions.read(HgConfig.PERMISSION).check();

    HgConfig config = repositoryHandler.getConfig();

    if (config == null) {
      config = new HgConfig();
      repositoryHandler.setConfig(config);
    }

    return Response.ok(configToDtoMapper.map(config)).build();
  }

  /**
   * Modifies the hg config.
   *
   * @param configDto new configuration object
   */
  @PUT
  @Path("")
  @Consumes(HgVndMediaType.CONFIG)
  @StatusCodes({
    @ResponseCode(code = 204, condition = "update success"),
    @ResponseCode(code = 401, condition = "not authenticated / invalid credentials"),
    @ResponseCode(code = 403, condition = "not authorized, the current user does not have the \"configuration:write:hg\" privilege"),
    @ResponseCode(code = 500, condition = "internal server error")
  })
  @TypeHint(TypeHint.NO_CONTENT.class)
  public Response update(HgConfigDto configDto) {

    HgConfig config = dtoToConfigMapper.map(configDto);

    ConfigurationPermissions.write(config).check();

    repositoryHandler.setConfig(config);
    repositoryHandler.storeConfig();

    return Response.noContent().build();
  }

  @Path("packages")
  public HgConfigPackageResource getPackagesResource() {
    return packagesResource.get();
  }

  @Path("auto-configuration")
  public HgConfigAutoConfigurationResource getAutoConfigurationResource() {
    return autoconfigResource.get();
  }

  @Path("installations")
  public HgConfigInstallationsResource getInstallationsResource() {
    return installationsResource.get();
  }
}