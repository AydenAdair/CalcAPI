import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitClient {
  @GET("/calc/add")
  Call<Integer> testAdd(@Query("value1") int value1, @Query("value2") int value2);

  @GET("/calc/subtract")
  Call<Integer> testSub(@Query("value1") int value1, @Query("value2") int value2);

  @GET("/calc/divide")
  Call<Double> testDiv(@Query("value1") int value1, @Query("value2") int value2);

  @GET("/calc/multiply")
  Call<Integer> testMult(@Query("value1") int value1, @Query("value2") int value2);

  @GET("/calc/audit")
  Call<ArrayList> testAudit();

  @GET("/calc/easy")
  Call<Void> testEasy();

}