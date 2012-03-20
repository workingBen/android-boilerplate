package util;

import com.bpellow.android.boilerplate.activity.BaseActivity;

/**
 * 
 * @author bpellow
 * 
 * This utility class is used to collect RunnableFactories for the app
 *
 */

public class RunnableUtils {
	/**
	 * StartActivity RunnableFactory
	 */
	public static Runnable startActivityRunnableFactory(final BaseActivity ctx, final Class goToClass, final boolean clearTop) {
	    return new Runnable() {
	    	public void run() {
	    		ctx.goToActivity(goToClass, clearTop);
	    	}
	    };
	}
}