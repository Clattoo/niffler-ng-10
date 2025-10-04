package guru.qa.niffler.api;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.UUID;

public interface SpendApi {
  @POST("internal/spends/add")
  Call<SpendJson> addSpend(@Body SpendJson spend);

  @PATCH("internal/spends/edit")
  Call<SpendJson> editSpend(@Body SpendJson spend);

  @GET("internal/spends/{id}")
  Call<SpendJson> getSpendById(@Path("id") String id);

  @GET("internal/spends/all")
  Call<List<SpendJson>> getAllSpends(@Query("username") String username);

  @DELETE("internal/spends/remove/{id}")
  Call<Void> deleteSpend(@Path("id") String id);

  @POST("internal/categories/add")
  Call<CategoryJson> createCategory(@Body CategoryJson category);

  @PATCH("internal/categories/update")
  Call<CategoryJson> updateCategory(@Body CategoryJson category);

  @GET("internal/categories/all")
  Call<List<CategoryJson>> getCategories(
          @Query("username") String username
  );
}
