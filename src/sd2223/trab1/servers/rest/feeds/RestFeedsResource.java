package sd2223.trab1.servers.rest.feeds;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import sd2223.trab1.api.Message;
import sd2223.trab1.api.discovery.Discovery;
import sd2223.trab1.api.java.Messages;
import sd2223.trab1.api.rest.FeedsService;
import sd2223.trab1.servers.java.JavaFeeds;
import sd2223.trab1.servers.rest.users.RestUsersServer;
import jakarta.inject.Singleton;

@Singleton
public class RestFeedsResource extends RestResource implements FeedsService {

	final Messages messages;

	public RestFeedsResource() {
		messages = new JavaFeeds();
	}
	
	@Override
	public long postMessage(String user, String pwd, Message msg) {
		return super.fromJavaResult(messages.postMessage(user, pwd, msg));
	}

	@Override
	public void removeFromPersonalFeed(String user, long mid, String pwd) {
		super.fromJavaResult(messages.removeFromPersonalFeed(user, mid, pwd));
		
	}

	@Override
	public Message getMessage(String user, long mid) {
		return super.fromJavaResult(messages.getMessage(user, mid));
	}

	@Override
	public List<Message> getMessages(String user, long time) {
		return super.fromJavaResult(messages.getMessages(user, time));
	}

	@Override
	public void subUser(String user, String userSub, String pwd) {
		super.fromJavaResult(messages.subUser(user, userSub, pwd));
	}

	@Override
	public void unsubscribeUser(String user, String userSub, String pwd) {
		super.fromJavaResult(messages.unsubscribeUser(user, userSub, pwd));
	}

	@Override
	public List<String> listSubs(String user) {
		return super.fromJavaResult(messages.listSubs(user));
	}

	
		
}
