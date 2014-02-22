package net.itwister.spectator.model;

public interface AccountModel {

	boolean isSignin();

	void signin(String token) throws Exception;

	void signout();
}