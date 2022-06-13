import io.confluent.rest.Application;
import io.confluent.rest.entities.ErrorMessage;
import java.io.InputStream;
import java.util.HashMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import org.eclipse.jetty.security.AbstractLoginService;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Password;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

public class CalcApp extends Application<CalcConfig>{

  private static final Logger log = LoggerFactory.getLogger(CalcApp.class);

  public CalcApp(CalcConfig config) {
    super(config);
  }

  public static void main(String[] args){
    try{
      HashMap<String, String> props = new HashMap<>();
      props.put("authentication.method", "BASIC");
      CalcApp app = new CalcApp(new CalcConfig(props));
      app.start();
      log.info("Server started and listening on port " + app.server.getURI());
      app.join();
    } catch (Exception e){
      log.error("Server failure: " + e.getMessage());
      System.exit(1);
      throw new RuntimeException(e);
    }
  }

  @Override
  //Configure the CalcResources' URLs
  public void setupResources(Configurable<?> configurable, CalcConfig calcConfig) {
    configurable.register(new CalcResources());

    ExceptionMapper<Exception> mapper = e -> {
      e.printStackTrace();

      int badRequestStatusCode = Status.BAD_REQUEST.getStatusCode();
      return Response.status(badRequestStatusCode)
          .entity(new ErrorMessage(badRequestStatusCode, e.getMessage()))
          .build();

    };

    configurable.register(mapper);
    configurable.register(new SwaggerResource());
  }

  @Override
  protected ConstraintMapping createGlobalAuthConstraint() {
    final Constraint constraint = new Constraint();
    constraint.setAuthenticate(true);
    constraint.setRoles(new String[]{"tester"});

    final ConstraintMapping mapping = new ConstraintMapping();
    mapping.setConstraint(constraint);
    mapping.setMethod("*");
    //whatever path this is set to will require the basic authorization
    mapping.setPathSpec("/calc/audit");
    return mapping;
  }

  @Override
  protected IdentityService createIdentityService() {
    return null;
  }

  @Override
  protected LoginService createLoginService() {
    return new BasicLoginService();
  }

  private static class BasicLoginService extends AbstractLoginService {

    protected String[] loadRoleInfo(UserPrincipal user){
      if (user.getName().equals("ketchup")) {
        return new String[]{"tester"};
      }
      return new String[0];
    }

    @Override
    protected UserPrincipal loadUserInfo(String name) {
      if (name.equals("ketchup")) {
        return new UserPrincipal(name, new Password("mustard"));
      }
      return null;
    }
  }


}
