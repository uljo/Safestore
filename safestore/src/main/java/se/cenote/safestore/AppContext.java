package se.cenote.safestore;


/**
 * Singleton helper class for providing central access to main Application <code>SafeStoreApp</code>
 * 
 * @author uffe
 *
 */
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
