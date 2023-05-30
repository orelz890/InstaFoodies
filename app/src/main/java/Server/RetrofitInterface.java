package Server;

import java.util.HashMap;
import java.util.Map;

import models.User;
import models.UserAccountSettings;
import models.UserSettings;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;


/**
 * Retrofit turns your HTTP API into a Java interface.
 */
public interface RetrofitInterface {

    @POST("/login") // Working!
    Call<User> executeLogin(@Body HashMap<String, String> map);
//    @POST("/login") // Working!
//    Call<User> executeLogin(@Header("X-Forwarded-For") String ipAddress, @Header("If-Modified-Since") String ifModifiedSince, @Body HashMap<String, String> map);

//    @POST("/signup") // Working!
//    Call<User> executeSignup (@Body HashMap<String, String> map);

    @POST("/signup") // Working!
    Call<User> executeSignup (@Body HashMap<String, Object> map);

    @GET("/getUser/{email}") // Working!
    Call<User> executeGetUser (@Path("email") String email);


    @GET("/checkUserName/{username}") // Working!
    Call<Boolean> executeCheckUserName (@Path("username") String name);

    @GET("/getUserAccountSettings/{email}") // Working!
    Call<UserAccountSettings> getUserAccountSettings (@Path("email") String email);


    @GET("/getUserSettings/{uid}") // Working!
    Call<UserSettings> getUserSettings (@Path("uid") String uid);

//    @PUT("/setUser/{email}")
//    Call<User> overwriteUser (@Path("email") String email, @Body HashMap<String, String> map);

    @PATCH("/patchUser") // Working!
    Call<Void> executePatchUser (@Body HashMap<String, Object> map);

    @PATCH("/patchUserAccountSettings") // Working!
    Call<Void> executePatchUserAccountSettings (@Body HashMap<String, Object> map);

    @DELETE("/deleteObjectFromRef/{ref}/{email}")
    Call<Void> executeDeleteObjectFromRef(@Path("ref") String ref, @Path("email") String email);

// ================================ Chat ==============================

    @POST("/createNewChatGroup/{uid}/{name}") // Working!
    Call<Void> createNewChatGroup (@Path("uid") String uid, @Path("name") String name);

    @GET("/getUserChatGroups/{uid}") // Working!
    Call<String[]> getUserChatGroups (@Path("uid") String uid);



//    @POST("/login") // Working!
//    Call<User_old_version> executeLogin(@Body Map<String, String> map);
////    @POST("/login") // Working!
////    Call<User> executeLogin(@Header("X-Forwarded-For") String ipAddress, @Header("If-Modified-Since") String ifModifiedSince, @Body HashMap<String, String> map);
//
////    @POST("/signup") // Working!
////    Call<User> executeSignup (@Body HashMap<String, String> map);
//
//    @POST("/signup") // Working!
//    Call<User_old_version> executeSignup (@Header("X-Forwarded-For") String ipAddress, @Body HashMap<String, Object> map);
//
//    @GET("/getUser/{email}") // Working!
//    Call<User_old_version> executeGetUser (@Header("X-Forwarded-For") String ipAddress, @Path("email") String email);
//
////    @PUT("/setUser/{email}")
////    Call<User> overwriteUser (@Path("email") String email, @Body HashMap<String, String> map);
//
//    @PATCH("/patchUser") // Working!
//    Call<Void> executePatchUser (@Header("X-Forwarded-For") String ipAddress, @Body HashMap<String, Object> map);
//
//    @DELETE("/deleteObjectFromRef/{ref}/{email}")
//    Call<Void> executeDeleteObjectFromRef(@Header("X-Forwarded-For") String ipAddress, @Path("ref") String ref, @Path("email") String email);

}