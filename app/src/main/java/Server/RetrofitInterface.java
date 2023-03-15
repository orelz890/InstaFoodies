package Server;

import java.util.HashMap;

import Login.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Retrofit turns your HTTP API into a Java interface.
 */
public interface RetrofitInterface {

    @POST("/login")
    Call<User> executeLogin(@Body HashMap<String, String> map);

    @POST("/signup")
    Call<Void> executeSignup (@Body HashMap<String, String> map);

    @GET("/getUser/{email}")
    Call<User> executeGetUser (@Path("email") String email);

    @PUT("/updateUser/{email}")
    Call<User> overwriteUser (@Path("email") String email, @Body HashMap<String, String> map);

    @PATCH("/updateUser/{email}")
    Call<User> patchUser (@Path("email") String email, @Body HashMap<String, String> map);

    @DELETE("/deleteObjectFromRef/{email}")
    Call<Void> executeDeleteObjectFromRef(@Path("email") String email);






}