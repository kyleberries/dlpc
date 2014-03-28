package com.kfsowibreakers.hd7posed;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class pkgcheck implements IXposedHookLoadPackage {
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("Loaded app: " + lpparam.packageName);
    }
}


//looks like it, just need to figure how to implement

// DownloadProvider fix (play store, gmail... downloads)
        // Prevent any call to DownloadProvider.checkInsertPermissions
		if( lpparam.packageName.equals( "com.android.providers.downloads" ) )
    	{
	    	try
			{
	    		final Class<?> cls = findClass( "com.android.providers.downloads.DownloadProvider", lpparam.classLoader );
	    		Method m = findMethodExact( cls, "checkInsertPermissions", ContentValues.class );
	    		XposedBridge.hookMethod( m, XC_MethodReplacement.returnConstant( 0 ) );
	    		log( cls.getName()+"."+m.getName()+" hooked" );  
			}
	    	catch (Throwable t) 
	    	{
	    		log( t );
	    	}
    	}
