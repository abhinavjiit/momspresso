package com.kellton.api.initials;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

public class MeetupApi extends DefaultApi10a {
	@Override
	public String getAccessTokenEndpoint() {
		return "https://api.meetup.com/oauth/access/";
	}
	@Override
	public String getAuthorizationUrl(Token arg0) {
		return "http://www.meetup.com/authenticate/?oauth_token=";
	}
	@Override
	public String getRequestTokenEndpoint() {
		return "https://api.meetup.com/oauth/request/";
	}
}
