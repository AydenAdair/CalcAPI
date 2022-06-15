import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import okhttp3.OkHttpClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;



@Test
public class CalcIT {

  private RetrofitClient client;

  @BeforeClass
  public void createThread() throws Exception {
    Thread thread;
    thread = new Thread(() -> {
      try {
        CalcApp.main(new String[]{});
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    thread.start();

    URL baseUrl = new URL("http://localhost:8080");

    Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(JacksonConverterFactory.create())
        .client(new OkHttpClient().newBuilder().build())
        .build();

    client = retrofit.create(RetrofitClient.class);

    long startTime = System.currentTimeMillis();
    double serverTimeout = 10000; //10 seconds

    while (true) {
      try {
        if (client.testEasy().execute().code() == 200) {
          break;
        }
        else{
          System.out.println(Objects.requireNonNull(client.testAdd(2, 2).execute().errorBody()).string());
        }
      } catch (Exception e) {
        //do nothing
      }
      if (System.currentTimeMillis() - serverTimeout > startTime) {
        throw new Exception("Server timed out");
      }
    }
  }

  @Test
  public void testAdd() throws IOException {
    assertEquals(3,client.testAdd(1, 2).execute().body());
  }

  @Test
  public void testSub() throws IOException {
    assertEquals(-1, client.testSub(2,3).execute().body());
  }

  @Test
  public void testDiv() throws IOException {
    assertEquals(4,client.testDiv(12, 3).execute().body());
    //test floating point return
    assertEquals(0.5, client.testDiv(2,4).execute().body());
    //test division by zero. Code should return error404
    //assertEquals(404, client.testDiv(4,0).execute().code());
  }

  @Test
  public void testMult() throws IOException {
    assertEquals(20, client.testMult(10, 2).execute().body());
  }

  @Test
  public void testAudit() throws IOException {
    //no permissions, should return 401 error "unauthorized"
    assertEquals(401, client.testAudit().execute().code());

  }

}