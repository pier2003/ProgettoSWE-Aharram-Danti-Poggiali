package orm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//DA UCCIDERE
public class MockUserDao implements UserDao{
	private List<String> usernames = new ArrayList<>(Arrays.asList("pippo","baudo"));
	private List<String> passwords = new ArrayList<>(Arrays.asList("pupu","pollo"));

	
	public boolean validationUsername(String username,String password) {
		if(usernames.contains(username) && passwords.contains(password)) {
			return true;
		}
		return false;
	}
}