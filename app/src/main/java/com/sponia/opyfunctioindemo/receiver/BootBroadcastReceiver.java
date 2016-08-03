package com.sponia.opyfunctioindemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sponia.opyfunctioindemo.service.TestService;

/**
 * @author linguokun
 * @packageName com.sponia.opyfunctioindemo.receiver
 * @description
 * @date 16/7/27
 */
public class BootBroadcastReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction().toString();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            System.out.println("收到开机广播");


            // u can start your service here
//            Toast.makeText(context, "boot completed action has got", Toast.LENGTH_LONG).show();

//            Intent ootStartIntent=new Intent(context,SurfaceActivity.class);
//            ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            ootStartIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//            context.startActivity(ootStartIntent);

//            Intent service = new Intent(context,TestService.class);
//            context.startService(service);
            TestService.trigger(context);
            System.out.println("开机启动服务");
        }

    }
}
