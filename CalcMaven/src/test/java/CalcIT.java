import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URL;
import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
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

    String authToken = Credentials.basic("ketchup", "mustard");
    //create interceptor
    Interceptor headerAuthInterceptor = new Interceptor() {
      @Override
      public Response intercept(Chain chain) throws IOException {
        okhttp3.Request request = chain.request();
        Headers headers = request.headers().newBuilder().add("Authorization", authToken).build();
        request = request.newBuilder().headers(headers).build();
        return chain.proceed(request);
      }
    };

    URL baseUrl = new URL("http://localhost:8080");

    Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(JacksonConverterFactory.create())
        .client(new OkHttpClient().newBuilder().addInterceptor(headerAuthInterceptor).build())
        .build();

    client = retrofit.create(RetrofitClient.class);

    long startTime = System.currentTimeMillis();
    double serverTimeout = 10000; //10 seconds

    while (true) {
      try {
        if (client.testEasy().execute().code() == 200) {
          break;
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
    assertEquals(400, client.testDiv(4,0).execute().code());
  }

  @Test
  public void testMult() throws IOException {
    assertEquals(20, client.testMult(10, 2).execute().body());
  }

  @Test
  public void testAudit() throws IOException {
    //permissions are attached through interceptor
    assertEquals(200, client.testAudit().execute().code());

  }

}