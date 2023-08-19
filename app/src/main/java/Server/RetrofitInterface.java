package Server;

import android.net.Uri;

import java.util.HashMap;
import java.util.List;

import models.Comment;
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

    @POST("/signup") // Working!
    Call<User> executeSignup (@Body HashMap<String, Object> map);

    @GET("/getUser/{uid}") // Working!
    Call<User> getUser (@Path("uid") String uid);


    @GET("/checkUserName/{username}") // Working!
    Call<Boolean> executeCheckUserName (@Path("username") String name);

    @GET("/getUserAccountSettings/{uid}") // Working!
    Call<UserAccountSettings> getUserAccountSettings (@Path("uid") String uid);

    @PATCH("/patchUser/{uid}") // Working!
    Call<Void> executePatchUser (@Path("uid") String uid, @Body HashMap<String, Object> map);

    @PATCH("/patchUserAccountSettings/{uid}")
    Call<Void> patchUserAccountSettings (@Path("uid") String uid, @Body HashMap<String, Object> map);


    // ================================ Search ==============================
    @GET("/getUsers/")
    Call<User[]> getUsers();

    @GET("/getUserByUserName/")
    Call<User> getUserByUserName(@Path("uid") String username);

    // ================================ share ==============================
    @PATCH("/uploadNewPost/{uid}")
    Call<Void> uploadNewPost (@Path("uid") String uid, @Body HashMap<String, Object> map);


    // Cart
    @GET("/getCartPosts/{uid}")
    Call<RequestPosts> getCartPosts (@Path("uid") String uid);

    @GET("/getLikedPosts/{uid}")
    Call<RequestPosts> getLikedPosts (@Path("uid") String uid);

    @PATCH("/deleteProfilePosts/{uid}")
    Call<Boolean> deleteProfilePosts (@Path("uid") String uid, @Body List<String> PostsId);



    // ================================ Follow and Unfollow action ==============================
    /*
    uidCurrent: the user that want to follow or unfollow
    uidToFolloeOrUnfollow: the user that the uidCurrent want to follow or unfollow
    followOrUnfollow: true == "follow" or false == "unfollow"
     */
    @PATCH("/followUnfollow/{uid}/{friendUid}/{followOrNot}")// need to Implement(14/8/2023)
    Call<Boolean> followUnfollow (@Path("uid") String uid, @Path("friendUid") String friendUid,
                               @Path("followOrNot") boolean followOrNot);


    // =========================== Posts =============================
    @PATCH("/addOrRemovePostLiked/{uid}/{postOwnerId}/{postId}")
    Call<Boolean> addOrRemovePostLiked (@Path("uid") String uid, @Path("postOwnerId") String postOwnerId, @Path("postId") String postId);

    @PATCH("/addOrRemoveCartPost/{uid}/{postOwnerId}/{postId}")
    Call<Boolean> addOrRemoveCartPost (@Path("uid") String uid, @Path("postOwnerId") String postOwnerId, @Path("postId") String postId);

    @GET("/getUserFeedPosts/{uid}")
    Call<RequestPosts> getUserFeedPosts (@Path("uid") String uid);




    @GET("/getUserAndHisFeedPosts/{uid}")
    Call<RequestUserFeed> getUserAndHisFeedPosts (@Path("uid") String uid);





    @GET("/getProfileFeedPosts/{uid}")
    Call<RequestPosts> getProfileFeedPosts (@Path("uid") String uid);

    @POST("/addOrRemoveLikeToPostComment/{postOwner}/{postId}/{uid}/{position}")
    Call<Boolean> addOrRemoveLikeToPostComment (@Path("postOwner") String postOwner,
                                             @Path("postId") String postId,
                                             @Path("uid") String uid,
                                             @Path("position") int position);


    // =============================== ML KIT =================================

    @PATCH("/reportIllegalPost/{uid}/{post_id}")
    Call<Void> reportIllegalPost (@Path("uid") String uid, @Path("post_id") String post_id);


    // ===============================
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


    // ====================== Comments ================================

    @POST("/addCommentToPost/{postOwnerId}/{postId}/{uid}/{comment}/{name}/{photo}/{commentId}")
    Call<Comment> addCommentToPost (@Path("postOwnerId") String postOwnerId,
                                 @Path("postId") String postId,
                                 @Path("uid") String uid,
                                 @Path("comment") String comment,
                                 @Path("name") String name,
                                 @Path("photo") String photo,
                                 @Path("commentId") String commentId);

    @GET("/getPostComments/{postOwnerId}/{postId}")
    Call<Comment[]> getPostComments (@Path("postOwnerId") String postOwnerId,
                                         @Path("postId") String postId);


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