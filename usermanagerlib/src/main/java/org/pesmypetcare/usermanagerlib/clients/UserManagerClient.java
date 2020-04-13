package org.pesmypetcare.usermanagerlib.clients;


import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.pesmypetcare.usermanagerlib.datacontainers.UserData;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class UserManagerClient {
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    //private static final String BASE_URL = "https://pes-my-pet-care.herokuapp.com/";
    private static final String BASE_URL = "https://image-branch-testing.herokuapp.com/";
    private static final String USERS_PATH = "users/";
    private static final String IMAGES_PATH = "storage/image/";
    private static final String PUT = "PUT";
    private static final String GET = "GET";
    private TaskManager taskManager;
    private Gson gson;

    public UserManagerClient() {
        taskManager = new TaskManager();
        gson = new Gson();
    }

    /**
     * Method called by the client to sign up a new user.
     * @param uid The user's unique identifier
     * @param data The user data object that contains the user's username, email and password
     * @return The response code
     * @throws ExecutionException When the retrieval of the pets fails
     * @throws InterruptedException When the retrieval is interrupted
     */
    public int createUser(String uid, UserData data) throws ExecutionException, InterruptedException, JSONException {
        taskManager = taskManager.resetTaskManager();
        JSONObject reqBody = new JSONObject();
        reqBody.put("uid", uid);
        reqBody.put("user", data.toJson());
        System.out.println(reqBody);
        taskManager.setTaskId("POST");
        taskManager.setReqBody(reqBody);
        StringBuilder response = taskManager.execute(BASE_URL + "signup", "").get();
        return Integer.parseInt(response.toString());
    }

    /**
     * Checks if a username is already in use.
     * @param username The username to check
     * @return True if the username is already in use
     * @throws ExecutionException When the retrieval of the pets fails
     * @throws InterruptedException When the retrieval is interrupted
     */
    public boolean usernameAlreadyExists(String username) throws ExecutionException, InterruptedException {
        taskManager = taskManager.resetTaskManager();
        taskManager.setTaskId(GET);
        StringBuilder json = taskManager.execute(BASE_URL + "usernames?username=" + username, "").get();
        if (json != null) {
            Type mapType = new TypeToken<HashMap<String, Boolean>>() { }.getType();
            Map<String, Boolean> response = gson.fromJson(json.toString(), mapType);
            return Objects.requireNonNull(response.get("exists"));
        }
        return true;
    }

    /**
     * Method called by the client to get a user.
     * @param accessToken The personal access token for the account
     * @param uid The user uid of which we want the information
     * @return Json that contains all the info of the user
     * @throws ExecutionException When the retrieval of the user fails
     * @throws InterruptedException When the retrieval is interrupted
     */
    public UserData getUser(String accessToken, String uid) throws ExecutionException, InterruptedException {
        taskManager = taskManager.resetTaskManager();
        taskManager.setTaskId(GET);
        StringBuilder json = taskManager.execute(BASE_URL + USERS_PATH + uid, accessToken).get();
        if (json != null) {
            Gson gson = new Gson();
            return gson.fromJson(json.toString(), UserData.class);
        }
        return null;
    }

    /**
     * Method called by the client to delete user completely.
     * @param accessToken The personal access token for the account
     * @param uid The user uid of which we want to delete
     * @return The response code
     * @throws ExecutionException When the retrieval of the pets fails
     * @throws InterruptedException When the retrieval is interrupted
     */
    public int deleteUser(String accessToken, String uid) throws ExecutionException, InterruptedException {
        taskManager = taskManager.resetTaskManager();
        taskManager.setTaskId("DELETE");
        StringBuilder response = taskManager.execute(BASE_URL + USERS_PATH + uid, accessToken).get();
        return Integer.parseInt(response.toString());
    }

    /**
     * Method called by the client to delete user from database.
     * @param accessToken The personal access token for the account
     * @param uid The user uid of which we want to delete
     * @return The response code
     * @throws ExecutionException When the retrieval of the pets fails
     * @throws InterruptedException When the retrieval is interrupted
     */
    public int deleteUserFromDatabase(String accessToken, String uid)
        throws ExecutionException, InterruptedException {
        taskManager = taskManager.resetTaskManager();
        taskManager.setTaskId("DELETE");
        StringBuilder response = taskManager.execute(BASE_URL + USERS_PATH + uid + "?db=true",
            accessToken).get();
        return Integer.parseInt(response.toString());
    }

    /**
     * Method called by the client to update the user's password.
     * @param accessToken The personal access token for the account
     * @param username The username of which we want to update
     * @param field The field to update
     * @param newValue The new field value
     * @return The response code
     * @throws ExecutionException When the retrieval of the pets fails
     * @throws InterruptedException When the retrieval is interrupted
     */
    public int updateField(String accessToken, String username, String field, String newValue)
        throws ExecutionException, InterruptedException {
        taskManager = taskManager.resetTaskManager();
        Map<String, String> reqData = new HashMap<>();
        reqData.put(field, newValue);
        taskManager.setTaskId(PUT);
        taskManager.setReqBody(new JSONObject(reqData));
        StringBuilder result = taskManager.execute(BASE_URL + USERS_PATH + username, accessToken).get();
        return Integer.parseInt(result.toString());
    }

    /**
     * Method called by the client to update the user's password.
     * @param accessToken The personal access token for the account
     * @param username The username of which we want to update
     * @param newPassword The new value of password
     */
    @Deprecated
    public void updatePassword(String accessToken, String username, String newPassword) {
        taskManager = taskManager.resetTaskManager();
        Map<String, String> reqData = new HashMap<>();
        reqData.put(PASSWORD, newPassword);
        taskManager.setTaskId(PUT);
        taskManager.setReqBody(new JSONObject(reqData));
        taskManager.execute(BASE_URL + USERS_PATH + username + "/update/password", accessToken);
    }

    /**
     *  Method called by the client to update the user's email.
     * @param accessToken The personal access token for the account
     * @param username The username of which we want to update
     * @param newEmail The new value of email
     */
    @Deprecated
    public void updateEmail(String accessToken, String username, String newEmail) {
        taskManager = taskManager.resetTaskManager();
        Map<String, String> reqData = new HashMap<>();
        reqData.put(EMAIL, newEmail);
        taskManager.setTaskId(PUT);
        taskManager.setReqBody(new JSONObject(reqData));
        taskManager.execute(BASE_URL + USERS_PATH + username + "/update/email", accessToken);
    }

    /**
     * Saves the image given as the profile image.
     * @param accessToken The personal access token for the account
     * @param uid The user's uid
     * @param image The image to save
     * @return The response code
     * @throws ExecutionException When the retrieval of the pets fails
     * @throws InterruptedException When the retrieval is interrupted
     */
    public int saveProfileImage(String accessToken, String uid, byte[] image)
        throws ExecutionException, InterruptedException {
        taskManager = taskManager.resetTaskManager();
        Map<String, Object> reqData = new HashMap<>();
        reqData.put("uid", uid);
        reqData.put("imgName", "profile-image.png");
        reqData.put("img", image);
        taskManager.setTaskId(PUT);
        taskManager.setReqBody(new JSONObject(reqData));
        StringBuilder response = taskManager.execute(BASE_URL + IMAGES_PATH, accessToken).get();
        return Integer.parseInt(response.toString());
    }

    /**
     * Downloads the profile image of the specified user.
     * @param accessToken The personal access token for the account
     * @param uid The user's uid
     * @return The profile image as a byte array
     * @throws ExecutionException When the retrieval of the user fails
     * @throws InterruptedException When the retrieval is interrupted
     */
    public byte[] downloadProfileImage(String accessToken,
                                       String uid) throws ExecutionException, InterruptedException {
        taskManager = taskManager.resetTaskManager();
        taskManager.setTaskId(GET);
        StringBuilder json = taskManager.execute(BASE_URL + IMAGES_PATH + uid + "?name=profile-image.png",
            accessToken).get();
        if (json != null) {
            return Base64.decode(json.toString(), Base64.DEFAULT);
        }
        return new byte[0];
    }
}
