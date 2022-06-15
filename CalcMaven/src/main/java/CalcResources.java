import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path(value = "/calc")
@Produces(MediaType.APPLICATION_JSON) //I don't fully understand this line, ask for help!
public class CalcResources {
  private final List<MathProblem> auditLog = new ArrayList<>();

  //"easy" endpoint to ensure the endpoints are able to be accessed properly. Always returns true.
  @GET
  @Path("easy")
  public boolean easy() {
    return true;
  }

  @GET
  @Path("add")
  public int add(@QueryParam("value1") int value1, @QueryParam("value2") int value2){
    auditLog.add(new MathProblem("add", value1, value2, (value1 + value2)));
    return value1 + value2;
  }

  @GET
  @Path("subtract")
  public int subtract(@QueryParam("value1") int value1, @QueryParam("value2") int value2){
    auditLog.add(new MathProblem("subtract", value1, value2, (value1 - value2)));
    return value1 - value2;
  }

  @GET
  @Path("divide")
  public double divide(@QueryParam("value1") int value1, @QueryParam("value2") int value2)
      throws Exception {
    if(value2 == 0){
      throw new Exception("Diving by zero");
    }
    auditLog.add(new MathProblem("divide", value1, value2, ((double)value1 / value2)));
    return (double)value1 / value2;
  }

  @GET
  @Path("multiply")
  public int multiply(@QueryParam("value1") int value1, @QueryParam("value2") int value2) {
    auditLog.add(new MathProblem("multiply", value1, value2, (value1 * value2)));
    return value1 * value2;
  }

  @GET
  @Path("audit")
  public List<MathProblem> audit(){
    return auditLog;
  }

}
