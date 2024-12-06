package businessLogic;

import orm.MockUserDao;

public class LoginController {
	MockUserDao dao = new MockUserDao();

	public boolean login(String username, String password) {
		return dao.validationUsername(username, password);
	}
}