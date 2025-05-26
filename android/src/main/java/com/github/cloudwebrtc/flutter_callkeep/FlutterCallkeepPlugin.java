package com.github.cloudwebrtc.flutter_callkeep;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.wazo.callkeep.CallKeepModule;

/** FlutterCallkeepPlugin */
public class FlutterCallkeepPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private CallKeepModule callKeep;

  private void setActivity(@NonNull Activity activity) {
    if (callKeep != null) {
      callKeep.setActivity(activity);
    }
  }

  private void startListening(final Context context, BinaryMessenger messenger) {
    channel = new MethodChannel(messenger, "FlutterCallKeep.Method");
    channel.setMethodCallHandler(this);
    callKeep = new CallKeepModule(context, messenger);
  }

  private void stopListening() {
    if (channel != null) {
      channel.setMethodCallHandler(null);
      channel = null;
    }
    if (callKeep != null) {
      callKeep.dispose();
      callKeep = null;
    }
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    startListening(flutterPluginBinding.getApplicationContext(), flutterPluginBinding.getBinaryMessenger());
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (callKeep != null && !callKeep.HandleMethodCall(call, result)) {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    stopListening();
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    if (callKeep != null) {
      callKeep.setActivity(binding.getActivity());
    }
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    if (callKeep != null) {
      callKeep.setActivity(null);
    }
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    if (callKeep != null) {
      callKeep.setActivity(binding.getActivity());
    }
  }

  @Override
  public void onDetachedFromActivity() {
    if (callKeep != null) {
      callKeep.setActivity(null);
    }
  }
}
