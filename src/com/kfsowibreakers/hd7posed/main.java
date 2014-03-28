package com.kfsowibreakers.hd7posed;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class pkgcheck implements IXposedHookLoadPackage {
//looks like it, just need to figure how to implement

   public void handleLoadPackage( final LoadPackageParam lpparam ) throws Throwable 
    {
		// prevent ota update
		if( lpparam.packageName.equals( "com.android.settings" ) )
    	{
	        try
			{
	        	final Class<?> cls = findClass( "com.android.settings.services.SystemUpdatesService", lpparam.classLoader );
	    		Method m = findMethodBestMatch( cls, "initOTAController" );
	    		XposedBridge.hookMethod( m, XC_MethodReplacement.DO_NOTHING );
	    		log( cls.getName()+"."+m.getName()+" hooked" );  
			}
	    	catch ( Throwable t ) 
	    	{
	    		log( t );
	    	}
    	}

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
	}

