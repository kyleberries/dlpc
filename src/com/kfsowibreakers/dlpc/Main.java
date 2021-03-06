package com.kfsowibreakers.dlpc;

import static de.robv.android.xposed.XposedHelpers.*;

import java.lang.reflect.Method;

import android.content.ContentValues;
import android.content.res.XResources;
import android.net.Uri;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Main implements IXposedHookLoadPackage, IXposedHookZygoteInit
{
	@Override
	public void initZygote( StartupParam startupParam ) throws Throwable
	{
        log( "initZygote" );
        
        // play store hook
        // hook stock hd7 content provider to fix play store downloads
        try
		{
        	final Class<?> cls = findClass( "android.content.ContentProviderProxy", null );
    		final Method m = findMethodExact( cls, "insert", Uri.class, ContentValues.class );
    		XposedBridge.hookMethod( m, new XC_MethodHook()
    		{
    			@Override
    			protected void beforeHookedMethod( MethodHookParam param ) throws Throwable 
    			{
    				ContentValues values = (ContentValues)param.args[1];
    				//parseContentValues( values );
    				
    				String npackage = values.getAsString( "notificationpackage" );
    				if( npackage != null && npackage.equals( "com.android.vending" ) )
    				{
    					values.put( "allowed_network_types", 3 );
    					values.put( "app_item_id_amz", 0 );
    					//values.put( "req_flags_amz", 193 );
    					values.put( "header_flags_amz", "Content-Length::Content-Type::ETag" );
    					values.put( "allow_metered", true );
    					values.put( "is_visible_in_downloads_ui", true );
    					values.put( "content_type", 1 );
    					values.put( "is_public_api", 1 );
    					values.put( "allow_roaming", true );
    					param.args[1] = values;
    				}
    			}
    		});
    		log( cls.getName()+"."+m.getName()+" hooked" );
		}
    	catch ( Throwable t ) 
    	{
    		log( t );
    	}
        
        // compareSignatures hook
        // allow unsigned apk    
        //try
		//{
        //	final Class<?> cls = findClass("com.android.server.pm.PackageManagerService", null );
    	//	Method m = findMethodExact( cls, "compareSignatures", android.content.pm.Signature[].class, android.content.pm.Signature[].class );
    	//	XposedBridge.hookMethod( m, XC_MethodReplacement.returnConstant( 0 ) );
    	//	log( cls.getName()+"."+m.getName()+" hooked" );
		//}
    	//catch ( Throwable t )
    	//{
    	//	log( t );
    	//}
     

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

	@SuppressWarnings("unused")
	private static void parseContentValues( ContentValues values )
	{
		log( "###############" );
		log( "###############" );
		for( String name : values.keySet() ) 
		{
			log( name + " : " + values.getAsString(name) );
        }
		log( "###############" );
		log( "###############" );
	}

	private static void log( String msg )
    {
    	XposedBridge.log( "ThorHook ===> " + msg );
    }
    
	private static void log( Throwable msg )
    {
    	XposedBridge.log( msg );
    }
}
