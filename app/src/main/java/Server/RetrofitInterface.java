package Server;

import android.net.Uri;

import com.google.common.primitives.Bytes;

import java.util.HashMap;
import java.util.List;
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

    @GET("/getUser/{uid}") // Working!
    Call<User> getUser (@Path("uid") String uid);


    @GET("/checkUserName/{username}") // Working!
    Call<Boolean> executeCheckUserName (@Path("username") String name);

    @GET("/getUserAccountSettings/{uid}") // Working!
    Call<UserAccountSettings> getUserAccountSettings (@Path("uid") String uid);

//for both user and user setting
    @GET("/getUserSettings/{uid}") // Working!
    Call<UserSettings> getUserSettings (@Path("uid") String uid);

//    @PUT("/setUser/{email}")
//    Call<User> overwriteUser (@Path("email") String email, @Body HashMap<String, String> map);

    @PATCH("/patchUser/{uid}") // Working!
    Call<Void> executePatchUser (@Path("uid") String uid, @Body HashMap<String, Object> map);

    @PATCH("/patchUserAccountSettings/{uid}")
    Call<Void> patchUserAccountSettings (@Path("uid") String uid, @Body HashMap<String, Object> map);


    // ================================ share ==============================
    @PATCH("/uploadNewPost/{uid}")
    Call<Void> uploadNewPost (@Path("uid") String uid, @Body HashMap<String, Object> map);





    @PATCH("/addOrRemovePostLiked/{uid}/{postOwnerId}/{postId}") // need to finish
    Call<Boolean> addOrRemovePostLiked (@Path("uid") String uid, @Path("postOwnerId") String postOwnerId, @Path("postId") String postId);







    @GET("/getUserFeedPosts/{uid}")
    Call<RequestUserFeed> getUserFeedPosts (@Path("uid") String uid);

    @GET("/getProfileFeedPosts/{uid}")
    Call<RequestUserFeed> getProfileFeedPosts (@Path("uid") String uid);


    @PATCH("/uploadProfilePhoto/{uid}/{image_uri}")
    Call<Void> uploadProfilePhoto (@Path("uid") String uid, @Path("image_uri") Uri image_uri);
    @PATCH("/uploadProfilePhoto/{uid}/{image_string}") //need to Implement
    Call<Void> uploadProfilePhoto (@Path("uid") String uid, @Path("image_string") String image_string);

    @GET("/getIngredients/")
    Call<String[]> getIngredients();

    @DELETE("/deleteObjectFromRef/{ref}/{email}")
    Call<Void> executeDeleteObjectFromRef(@Path("ref") String ref, @Path("email") String email);

    @GET("/getBothUserAndHisSettings/{uid}")
    Call<UserSettings> getBothUserAndHisSettings (@Path("uid") String uid);

// ================================ Chat ==============================

    @POST("/createNewChatGroup/{uid}/{name}")
    Call<Void> createNewChatGroup (@Path("uid") String uid, @Path("name") String name);

    @GET("/getUserChatGroups/{uid}")
    Call<String[]> getUserChatGroups (@Path("uid") String uid);


    @GET("/getFollowingUsers/{uid}")
    Call<User[]> getFollowingUsers (@Path("uid") String uid);

    @GET("/getFollowingUsersAccountSettings/{uid}")
    Call<UserAccountSettings[]> getFollowingUsersAccountSettings (@Path("uid") String uid);

    @GET("/getFollowingUsersAndAccounts/{uid}")
    Call<RequestUsersAndAccounts> getFollowingUsersAndAccounts (@Path("uid") String uid);


    @GET("/getContactsUsers/{uid}")
    Call<User[]> getContactsUsers (@Path("uid") String uid);

    @GET("/getContactsSettings/{uid}")
    Call<UserAccountSettings[]> getContactsSettings (@Path("uid") String uid);

    @GET("/getContactsUsersAndSettings/{uid}")
    Call<RequestUsersAndAccounts> getContactsUsersAndSettings (@Path("uid") String uid);


    @GET("/getRequests/{uid}")
    Call<RequestsResponse> getRequests (@Path("uid") String uid);

}