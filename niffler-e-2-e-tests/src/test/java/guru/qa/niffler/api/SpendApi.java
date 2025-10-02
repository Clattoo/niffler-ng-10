package guru.qa.niffler.api;

import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.http.*;

public interface SpendApi {
  @POST("internal/spends/add")
  Call<SpendJson> createSpend(@Body SpendJson spend);

  @PATCH("internal/spends/edit")
  Call<SpendJson> editSpend(@Body SpendJson spend);

  @GET("internal/spends/{id}")
  Call<SpendJson> getSpendById(@Body SpendJson spend);

  @GET("internal/spends/all")
  Call<SpendJson> getAllSpends(@Body SpendJson spend);

  @DELETE("internal/spends/remove")
  Call<SpendJson> deleteSpend(@Body SpendJson spend);

  @POST("internal/categories/add")
  Call<SpendJson> createCategories(@Body SpendJson spend);

  @PATCH("internal/categories/update")
  Call<SpendJson> updateCategories(@Body SpendJson spend);

  @GET("internal/categories/all")
  Call<SpendJson> getCategories(@Body SpendJson spend);
}
