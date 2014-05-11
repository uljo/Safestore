package se.cenote.safestore;

public class AppContext {
	
	private static AppContext INSTANCE = new AppContext();
	
	private SafeStoreApp app;
	
	private AppContext(){
		app = new SafeStoreApp();
	}
	
	public static AppContext getInstance(){
		return INSTANCE;
	}
	
	public SafeStoreApp getApp(){
		return app;
	}

}
