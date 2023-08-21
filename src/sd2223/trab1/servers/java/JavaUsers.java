package sd2223.trab1.servers.java;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;
import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.api.java.Result.ErrorCode;
import sd2223.trab1.api.java.Users;

public class JavaUsers implements Users {
	private final Map<String, User> users = new HashMap<>();

	private static Logger Log = Logger.getLogger(JavaUsers.class.getName());

	@Override
	public Result<String> createUser(User user) {
		Log.info("createUser : " + user);

		// Check if user data is valid
		if (user.getName() == null || user.getPwd() == null || user.getDisplayName() == null
				|| user.getDomain() == null) {
			Log.info("User object invalid.");
			return Result.error(ErrorCode.BAD_REQUEST);
		}

		// Insert user, checking if name already exists
		if (users.putIfAbsent(user.getName(), user) != null) {
			Log.info("User already exists.");
			return Result.error(ErrorCode.CONFLICT);
		}
		return Result.ok(user.getName() + "@" + user.getDomain());
	}

	@Override
	public Result<User> getUser(String name, String pwd) {
		Log.info("getUser : user = " + name + "; pwd = " + pwd);

		// Check if user is valid
		if (name == null || pwd == null) {
			Log.info("Name or Password null.");
			return Result.error(ErrorCode.BAD_REQUEST);
		}

		User user = users.get(name);
		// Check if user exists
		if (user == null) {
			Log.info("User does not exist.");
			return Result.error(ErrorCode.NOT_FOUND);
		}

		// Check if the password is correct
		if (!user.getPwd().equals(pwd)) {
			Log.info("Password is incorrect.");
			return Result.error(ErrorCode.FORBIDDEN);
		}

		return Result.ok(user);
	}

	@Override
	public Result<User> updateUser(String name, String pwd, User user) {
		// Check if user is valid
		Log.info("updateUser : user = " + name + "; pwd = " + pwd);

		var oldUser = getUser(name, pwd);

		var otherName = user.getName();
		var otherPwd = user.getPwd();
		var otherDisplayName = user.getDisplayName();
		
		if (oldUser.isOK()) {
			
			
			var updateUser = users.get(name);
			
			if (!otherName.equals(updateUser.getName())) {
				Log.info("Name");
				return Result.error(ErrorCode.BAD_REQUEST);
			}
			
			
			if(otherName != null)
				updateUser.setName(otherName);
			if(otherPwd != null)
				updateUser.setPwd(otherPwd);
			if (otherDisplayName != null)
				updateUser.setDisplayName(otherDisplayName);
			
			return Result.ok(users.get(name));

		}

		return oldUser;
	}

	@Override
	public Result<User> deleteUser(String name, String pwd) {
		Log.info("deleteUser : user = " + name + "; pwd = " + pwd);

		var user = getUser(name, pwd);

		if (user.isOK())
			users.remove(name);

		return user;
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
		Log.info("searchUsers : pattern = " + pattern);

		
		List<User> matchingUsers = new ArrayList<>();
	    for (User user : users.values()) {
	        String name = user.getName();
	        String domain = user.getDomain();
	        if ((name + "@" + domain).toLowerCase().contains(pattern.toLowerCase())) {
	            matchingUsers.add(user);
	        }
	    }
	    
		if (matchingUsers.isEmpty()) {
			Log.info(String.format("Dont have the %s", pattern));
			return Result.ok(matchingUsers);
		} else
			return Result.ok(matchingUsers);
	}

	@Override
	public Result<Void> verifyPassword(String name, String pwd) {
		var res = getUser(name, pwd);
		if (res.isOK())
			return Result.ok();
		else
			return Result.error(res.error());
	}
}
