package org.pesmypetcare.communitymanager.managers;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.pesmypetcare.communitymanager.datacontainers.GroupData;
import org.pesmypetcare.communitymanager.datacontainers.ImageData;
import org.pesmypetcare.communitymanager.datacontainers.TagData;
import org.pesmypetcare.httptools.HttpClient;
import org.pesmypetcare.httptools.HttpParameter;
import org.pesmypetcare.httptools.HttpResponse;
import org.pesmypetcare.httptools.MyPetCareException;
import org.pesmypetcare.httptools.RequestMethod;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Santiago Del Rey
 */
public class GroupManagerClient {
    private static final String IMAGE_STORAGE_BASE_URL = "https://image-branch-testing.herokuapp.com/storage/image/";
    private static final String COMMUNITY_BASE_URL = "https://image-branch-testing.herokuapp.com/community";
    private final Gson gson;

    public GroupManagerClient() {
        gson = new Gson();
    }

    /**
     * Creates a new group.
     *
     * @param group The group data
     * @throws MyPetCareException When the request fails
     */
    public void createGroup(GroupData group) throws MyPetCareException {
        new HttpClient().request(RequestMethod.POST, COMMUNITY_BASE_URL, null, null, gson.toJson(group));
    }

    /**
     * Deletes a group.
     *
     * @param groupName The group name
     * @throws MyPetCareException When the request fails
     */
    public void deleteGroup(String groupName) throws MyPetCareException {
        HttpParameter[] params = new HttpParameter[1];
        params[0] = new HttpParameter("group", groupName);
        new HttpClient().request(RequestMethod.DELETE, COMMUNITY_BASE_URL, params, null, null);
    }

    /**
     * Retrieves a group information.
     *
     * @param groupName The group name
     * @return The group data
     * @throws MyPetCareException When the request fails
     */
    public GroupData getGroup(String groupName) throws MyPetCareException {
        HttpParameter[] params = new HttpParameter[1];
        params[0] = new HttpParameter("group", groupName);
        HttpResponse response = new HttpClient().request(RequestMethod.GET, COMMUNITY_BASE_URL, params, null, null);
        return gson.fromJson(response.asString(), GroupData.class);
    }

    /**
     * Retrieves all groups information.
     *
     * @return All groups data
     * @throws MyPetCareException When the request fails
     */
    public List<GroupData> getAllGroups() throws MyPetCareException {
        HttpResponse response = new HttpClient().request(RequestMethod.GET, COMMUNITY_BASE_URL, null, null, null);
        Type listType = TypeToken.getParameterized(List.class, GroupData.class).getType();
        return gson.fromJson(response.asString(), listType);
    }

    public Map<String, TagData> getAllTags() throws MyPetCareException {
        HttpResponse resp = new HttpClient().request(RequestMethod.GET, COMMUNITY_BASE_URL + "/tags", null, null, null);
        Type mapType = TypeToken.getParameterized(Map.class, String.class, TagData.class).getType();
        return gson.fromJson(resp.asString(), mapType);
    }

    public void updateField(String groupName, String field, String newValue) throws MyPetCareException {
        HttpParameter[] params = new HttpParameter[2];
        params[0] = new HttpParameter("group", groupName);
        params[1] = new HttpParameter("field", field);
        Map<String, String> map = new HashMap<>();
        map.put("value", newValue);
        new HttpClient().request(RequestMethod.PUT, COMMUNITY_BASE_URL, params, null, gson.toJson(map));
    }

    public void updateGroupTags(String groupName, List<String> deletedTags, List<String> newTags)
            throws MyPetCareException {
        HttpParameter[] params = new HttpParameter[1];
        params[0] = new HttpParameter("group", groupName);
        Map<String, List<String>> newValue = new HashMap<>();
        newValue.put("deleted", deletedTags);
        newValue.put("new", newTags);
        new HttpClient().request(RequestMethod.PUT, COMMUNITY_BASE_URL + "/tags", params, null, gson.toJson(newValue));
    }

    public void subscribe(String token, String groupName, String username) throws MyPetCareException {
        HttpParameter[] params = new HttpParameter[2];
        params[0] = new HttpParameter("group", groupName);
        params[1] = new HttpParameter("username", username);
        Map<String, String> headers = new HashMap<>();
        headers.put("token", token);
        new HttpClient().request(RequestMethod.POST, COMMUNITY_BASE_URL + "/subscribe", params, headers, null);
    }

    public void unsubscribe(String token, String groupName, String username) throws MyPetCareException {
        HttpParameter[] params = new HttpParameter[2];
        params[0] = new HttpParameter("group", groupName);
        params[1] = new HttpParameter("username", username);
        Map<String, String> headers = new HashMap<>();
        headers.put("token", token);
        new HttpClient().request(RequestMethod.DELETE, COMMUNITY_BASE_URL + "/unsubscribe", params, headers, null);
    }

    public void updateGroupIcon(String token, String groupName, byte[] image) throws MyPetCareException {
        ImageData imageData = new ImageData(groupName, image);
        imageData.setImgName(groupName + "-icon");
        Map<String, String> headers = new HashMap<>();
        headers.put("token", token);
        new HttpClient().request(RequestMethod.PUT, IMAGE_STORAGE_BASE_URL + HttpParameter.encode(groupName), null,
                headers, gson.toJson(imageData));
    }

    public byte[] getGroupIcon(String groupName) throws MyPetCareException {
        HttpParameter[] params = new HttpParameter[1];
        params[0] = new HttpParameter("name", groupName + "-icon");
        HttpResponse response = new HttpClient().request(RequestMethod.GET,
                IMAGE_STORAGE_BASE_URL + "groups/" + HttpParameter.encode(groupName), params, null, null);
        return Base64.decode(response.asString(), Base64.DEFAULT);
    }

    public List<String> getUserSubscriptions(String token, String username) throws MyPetCareException {
        //TODO: Move to usermanager
        HttpParameter[] params = new HttpParameter[1];
        params[0] = new HttpParameter("username", username);
        Map<String, String> headers = new HashMap<>();
        headers.put("token", token);
        HttpResponse res = new HttpClient().request(RequestMethod.GET,
                "https://image-branch-testing.herokuapp.com/users/subscriptions", params, headers, null);
        Type listType = TypeToken.getParameterized(List.class, String.class).getType();
        return gson.fromJson(res.asString(), listType);
    }
}
