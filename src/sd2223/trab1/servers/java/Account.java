package sd2223.trab1.servers.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import sd2223.trab1.api.Message;

public class Account {
	private static Logger Log = Logger.getLogger(Account.class.getName());

	private Map<String, Account> myFollowers;
	private Map<String, Account> mySubscribes;
	private List<Message> myAccessPosts;
	private String user;
	
	public Account(String user) {
		this.user = user;
		this.myFollowers = new HashMap<>();
		this.mySubscribes = new HashMap<>();
		this.myAccessPosts = new ArrayList<>();
	}
	
	public String getUser() {
		return this.user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public void addFollowers(String user) {
		this.myFollowers.put(user, new Account(user));
	}
	
	public void subscribe(String user) {
		this.mySubscribes.put(user, new Account(user));
	}
	
	public void post(Message message) {
		this.myAccessPosts.add(message);
	}
	
	public List<Message> getMyPersonalPost() {
		List<Message> listMessage = new ArrayList<>();
		for(Message message:this.myAccessPosts) {
			if(user.equals(message.getUser()))
				listMessage.add(message);
		}
		return listMessage;
	}
	
	public List<Account> getMyFollowers() {
		List<Account> myFollowers = new ArrayList<>();
		for(Account acc:this.myFollowers.values()) {
			myFollowers.add(acc);
		}
		return myFollowers;
	}
	
	public void removeFollower(String user) {
		this.myFollowers.remove(user);
	}
	
	public List<Account> getMySubscribes() {
		List<Account> mySubscribe = new ArrayList<>();
		for(Account acc:this.mySubscribes.values()) {
			mySubscribe.add(acc);
		}
		return mySubscribe;
	}
	
	public void unsubscribe(String user) {
		this.mySubscribes.remove(user);
	}
	
	public List<Message> getMyPosts() {
		return this.myAccessPosts;
	}
	
	public List<Message> getMyPosts(long time) {
		List<Message> list = new ArrayList<>();
		for(Message message:getMyPosts()) {
			if(message.getCreationTime() > time)
				list.add(message);
		}
		return list;
	}
	
	public void removePost(String name, long mid) {
		for(int i = 0; i < this.myAccessPosts.size(); i++) {
			if(this.myAccessPosts.get(i).getUser().equals(name) 
					&& this.myAccessPosts.get(i).getId() == mid) {
				Log.info("I FOUND MESSAGE TO REMOVE: " + myAccessPosts.get(i).toString());
				this.myAccessPosts.remove(i);
				i = this.myAccessPosts.size();
			}else {
				Log.info("I NOT FOUND : " + myAccessPosts.get(i).toString());
			}
		}
	}
}
