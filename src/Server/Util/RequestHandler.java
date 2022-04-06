package Server.Util;

import java.net.Socket;

import Server.User.UserDAO;
import Server.User.UserDTO;

public class RequestHandler {
	public static int requestHandler(final Socket so, final String request) {
		String[] s = request.split(" ");

		try {
			if (s[0].equals("usersignup"))
				return UserDAO.userSignup(new UserDTO(s[1]), s[2]);
			else if (s[0].equals("userlogin"))
				return UserDAO.userLogin(new UserDTO(s[1]), s[2]);
			else if (s[0].equals("desconnect")) {
				if(so.isConnected())
					so.close();
				return 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
