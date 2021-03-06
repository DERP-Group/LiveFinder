package com.derpgroup.livefinder.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.derpgroup.livefinder.model.accountlinking.AuthenticationException;
import com.derpgroup.livefinder.resource.AuthResource;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.MultipartBody;

public class TwitchClient {

  private static final Logger LOG = LoggerFactory.getLogger(TwitchClient.class);

  private static final String TOKEN_ENDPOINT = "/oauth2/token";
  private static final String USER_ENDPOINT = "/user";
  private static final String FOLLOWED_STREAMS_ENDPOINT = "/streams/followed";
  
  private String twitchApiRootUri;
  private String clientId;
  private String clientSecret;
  private String redirectUri;
  
  ObjectMapper mapper;
  
  public TwitchClient(String twitchApiRootUri, String clientId, String clientSecret, String redirectUri){
    this.twitchApiRootUri = twitchApiRootUri;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.redirectUri = redirectUri;
    
    mapper = new ObjectMapper();
  }

  //Is thread safety gonna be a problem here?
  public TwitchTokenResponse redeemCode(String code) throws AuthenticationException {
    LOG.info("Requesting access token for...\n "
        + "twitchApiRootUri: " + twitchApiRootUri + "\n"
        + "client_id: " + clientId + "\n"
        + "client_secret: " + clientSecret + "\n"
        + "redirect_uri: " + redirectUri + "\n"
        + "code: " + code);
    MultipartBody request = Unirest.post(twitchApiRootUri + TOKEN_ENDPOINT)
        .field("grant_type", "authorization_code")
        .field("client_id",clientId)
        .field("client_secret", clientSecret)
        .field("redirect_uri",redirectUri)
        .field("code",code);
    
    try {
      HttpResponse<String> response = request.asString();
      return mapper.readValue(response.getBody(), new TypeReference<TwitchTokenResponse>(){});
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new AuthenticationException(e.getMessage());
    }
  }
  
  public TwitchUserResponse getUser(String accessToken) throws AuthenticationException{
    GetRequest request = Unirest.get(twitchApiRootUri + USER_ENDPOINT).header("Authorization", "OAuth " + accessToken);
    
    try {
      HttpResponse<String> response = request.asString();
      return mapper.readValue(response.getBody(), new TypeReference<TwitchUserResponse>(){});
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new AuthenticationException(e.getMessage());
    }
  }

  public TwitchFollowedStreamsResponse getFollowedStreams(String accessToken) throws AuthenticationException {
    GetRequest request = Unirest.get(twitchApiRootUri + FOLLOWED_STREAMS_ENDPOINT).header("Authorization", "OAuth " + accessToken);
    
    try {
      HttpResponse<String> response = request.asString();
      return mapper.readValue(response.getBody(), new TypeReference<TwitchFollowedStreamsResponse>(){});
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new AuthenticationException(e.getMessage());
    }
  }

  public String getTwitchApiRootUri() {
    return twitchApiRootUri;
  }

  public void setTwitchApiRootUri(String twitchApiRootUri) {
    this.twitchApiRootUri = twitchApiRootUri;
  }
}
