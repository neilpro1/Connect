package sd2223.trab1.servers.java;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.ArrayList;
import sd2223.clients.trab1.rest.RestUsersClient;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.User;
import sd2223.trab1.api.discovery.Discovery;
import sd2223.trab1.api.java.Messages;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.api.java.Result.ErrorCode;

import java.net.URI;

public class JavaFeeds implements Messages {

	private static Logger Log = Logger.getLogger(JavaFeeds.class.getName());
	private String USERS = ":users";
	
	private Map<String, Account> accounts;

	Discovery discovery = Discovery.getInstance();

	private final String SIGN = "@";

	public JavaFeeds() {
		this.accounts = new HashMap<>();
	}

	@Override
	public Result<Long> postMessage(String user, String pwd, Message msg) {
		Log.info("post message: " + user + "; " + pwd + "; " + msg.toString());

		if (!user.split("@")[1].equals(msg.getDomain()))
			return Result.error(ErrorCode.BAD_REQUEST);

		var foundUser = getUser(user, pwd);

		if (foundUser.isOK()) {

			var account = this.accounts.get(user);
			if (account == null) {
				Account myAccount = new Account(user);
				myAccount.post(msg);
				this.accounts.put(user, myAccount);
			} else
				this.accounts.get(user).post(msg);

			var myFollowers = this.accounts.get(user).getMyFollowers();

			if (myFollowers != null)
				for (Account acc : myFollowers) {
					if (acc.getUser().split(SIGN)[1].equals(msg.getDomain()))
						this.accounts.get(acc.getUser()).post(msg);
					else
						return Result.error(ErrorCode.NOT_IMPLEMENTED);
				}

			return Result.ok(msg.getId());

		}
		return Result.error(foundUser.error());

	}

	@Override
	public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {
		var foundUser = getUser(user, pwd);

		if (foundUser.isOK()) {

			var account = this.accounts.get(user);
			if (account == null)
				return Result.error(ErrorCode.NOT_FOUND);
			else {
				this.accounts.get(user).removePost(user.split(SIGN)[0], mid);

				var myFollowers = account.getMyFollowers();
				for (Account acc : myFollowers) {
					this.accounts.get(acc.getUser()).removePost(user.split(SIGN)[0], mid);
				}

				return Result.ok();
			}
		}

		return Result.error(foundUser.error());
	}

	@Override
	public Result<Message> getMessage(String user, long mid) {
		Log.info("getMessage: " + user + "; " + mid);
		var foundUser = existUser(user);
		if (!foundUser)
			return Result.error(ErrorCode.NOT_FOUND);

		Account account = this.accounts.get(user);
		if (account != null) {
			var messages = account.getMyPosts();
			if (messages != null) {
				for (Message message : messages) {
					if (message.getId() == mid)
						return Result.ok(message);
				}
			} else
				Result.error(ErrorCode.NOT_FOUND);
		}

		return Result.error(ErrorCode.NOT_FOUND);
	}

	@Override
	public Result<List<Message>> getMessages(String user, long time) {
		Log.info("getMessages: " + user + "; " + time);
		var foundUser = existUser(user);
		var account = this.accounts.get(user);

		if (!foundUser)
			return Result.error(ErrorCode.NOT_FOUND);

		if (account != null) {
			var myPosts = account.getMyPosts(time);
			return Result.ok(myPosts);
		}

		return Result.ok(new ArrayList<>());

	}

	@Override
	public Result<Void> subUser(String user, String userSub, String pwd) {
		var user1 = getUser(user, pwd);

		var user2 = existUser(userSub);

		if (!user1.isOK())
			return Result.error(user1.error());
		if (!user2)
			return Result.error(ErrorCode.NOT_FOUND);

		Account accountUser = this.accounts.get(user);
		Account accountUserSub = this.accounts.get(userSub);

		if (accountUser == null) {
			accountUser = new Account(user);
			accountUser.subscribe(userSub);
			this.accounts.put(user, accountUser);
		} else {
			this.accounts.get(user).subscribe(userSub);
		}

		if (accountUserSub == null) {
			accountUserSub = new Account(user);
			accountUserSub.addFollowers(user);
			this.accounts.put(userSub, accountUserSub);
		} else
			this.accounts.get(userSub).addFollowers(user);

		return Result.ok();

	}

	@Override
	public Result<Void> unsubscribeUser(String user, String userSub, String pwd) {
		var users = getUser(user, pwd);

		if (users.isOK()) {
			Account accountUser = this.accounts.get(user);
			Account accountUserSub = this.accounts.get(userSub);

			if (accountUserSub == null)
				return Result.error(ErrorCode.NOT_FOUND);

			accountUser.unsubscribe(userSub);
			accountUserSub.removeFollower(user);

			return Result.ok();
		}

		return Result.error(users.error());
	}

	@Override
	public Result<List<String>> listSubs(String user) {
		var foundUser = existUser(user);
		var account = this.accounts.get(user);

		if (!foundUser)
			return Result.error(ErrorCode.NOT_FOUND);

		if (foundUser) {
			if (account != null) {
				List<String> mySubscribe = new ArrayList<>();
				var mySub = account.getMySubscribes();
				if (mySub != null)
					for (Account acc : mySub) {
						mySubscribe.add(acc.getUser());
					}
				return Result.ok(mySubscribe);
			} else
				return Result.ok(new ArrayList<String>());
		}

		return Result.error(ErrorCode.NOT_FOUND);
	}

	public Result<User> getUser(String user, String pwd) {
		URI[] uri = discovery.knownUrisOf(user.split(SIGN)[1] + USERS, 1);
		RestUsersClient rest = new RestUsersClient(uri[0]);
		return rest.getUser(user.split(SIGN)[0], pwd);
	}

	public boolean existUser(String user) {
		URI[] uri = discovery.knownUrisOf(user.split(SIGN)[1] + USERS, 1);
		RestUsersClient rest = new RestUsersClient(uri[0]);
		return rest.searchUsers(user).value().size() != 0;
	}

}
