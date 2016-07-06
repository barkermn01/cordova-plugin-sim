// MCC and MNC codes on Wikipedia
// http://en.wikipedia.org/wiki/Mobile_country_code

// Mobile Network Codes (MNC) for the international identification plan for public networks and subscriptions
// http://www.itu.int/pub/T-SP-E.212B-2014

// class TelephonyManager
// http://developer.android.com/reference/android/telephony/TelephonyManager.html

// permissions
// http://developer.android.com/training/permissions/requesting.html

package com.pbakondy;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

public class Sim extends CordovaPlugin {

  private static final String GET_SIM_INFO = "getSimInfo";
  private static final String HAS_READ_PERMISSION = "hasReadPermission";
  private static final String REQUEST_READ_PERMISSION = "requestReadPermission";

  private CallbackContext callback;
  
  public String getCarrierFromMCCMNC(String Operator){
	  switch(MNC){
		  case "23478":
			return "Wave Telecom Ltd";
		  break;
		  case "23427":
		  case "23491":
		  case "23415":
		  case "23423":
		  case "23403":
			return "Vodafone";
		  break;
		  case "23425":
			return "Truphone";
		  break;
		  case "23409":
			return "Tismi";
		  break;
		  case "23437":
			return "Synectiv Ltd.";
		  break;
		  case "23424":
			return "Stour Marine";
		  break;
		  case "23422":
			return "Routotelecom";
		  break;
		  case "23412":
			return "Railtrack Plc";
		  break;
		  case "23419":
			return "PMN/Teleware";
		  break;
		  case "23416":
			return "Opal Telecom";
		  break;
		  case "23408":
			return "OnePhone";
		  break;
		  case "23410":
		  case "23411":
		  case "23402":
			return "O2 Ltd.";
		  break;
		  case "23428":
			return "Marthon Telecom";
		  break;
		  case "23401":
			return "Mapesbury C. Ltd";
		  break;
		  case "23458":
			return "Manx Telecom";
		  break;
		  case "23426":
			return "Lycamobile";
		  break;
		  case "23435":
			return "JSC Ingenicum";
		  break;
		  case "23450":
		  case "23451":
			return "Jersey Telecom";
		  break;
		  case "23475":
			return "Inquam Telecom Ltd";
		  break;
		  case "23494":
		  case "23420":
			return "Hutchinson 3G";
		  break;
		  case "23414":
			return "HaySystems";
		  break;
		  case "23455":
			return "Guernsey Telecoms";
		  break;
		  case "23417":
			return "FlexTel";
		  break;
		  case "23430":
		  case "23431":
		  case "23432":
			return "EE - T-Mobile";
		  break;
		  case "23433":
		  case "23434":
			return "EE - Orange";
		  break;
		  case "23502":
			return "EE";
		  break;
		  case "23418":
			return "Cloud9";
		  break;
		  case "23436":
		  case "23407":
		  case "23407":
			return "Cable and Wireless";
		  break;
		  case "23477":
		  case "23476":
			return "BT Group";
		  break;
	  }
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    callback = callbackContext;

    if (GET_SIM_INFO.equals(action)) {
      Context context = this.cordova.getActivity().getApplicationContext();

      TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

      String phoneNumber = "";
      String countryCode = manager.getSimCountryIso();
      String simOperator = manager.getSimOperator();
      String carrierName = manager.getSimOperatorName();

      String deviceId = "";
      String deviceSoftwareVersion = "";
      String simSerialNumber = "";
      String subscriberId = "";

      int callState = manager.getCallState();
      int dataActivity = manager.getDataActivity();
      int networkType = manager.getNetworkType();
      int phoneType = manager.getPhoneType();
      int simState = manager.getSimState();

      boolean isNetworkRoaming = manager.isNetworkRoaming();

      if (simPermissionGranted(Manifest.permission.READ_PHONE_STATE)) {
        phoneNumber = manager.getLine1Number();
        deviceId = manager.getDeviceId();
        deviceSoftwareVersion = manager.getDeviceSoftwareVersion();
        simSerialNumber = manager.getSimSerialNumber();
        subscriberId = manager.getSubscriberId();
      }

      String mcc = "";
      String mnc = "";

      if (simOperator.length() >= 3) {
        mcc = simOperator.substring(0, 3);
        mnc = simOperator.substring(3);
      }
	  
	  String networkName = this.getCarrierFromMCCMNC(simOperator);

      JSONObject result = new JSONObject();

      result.put("carrierName", carrierName);
      result.put("networkName", networkName);
      result.put("countryCode", countryCode);
      result.put("mcc", mcc);
      result.put("mnc", mnc);

      result.put("callState", callState);
      result.put("dataActivity", dataActivity);
      result.put("networkType", networkType);
      result.put("phoneType", phoneType);
      result.put("simState", simState);

      result.put("isNetworkRoaming", isNetworkRoaming);

      if (simPermissionGranted(Manifest.permission.READ_PHONE_STATE)) {
        result.put("phoneNumber", phoneNumber);
        result.put("deviceId", deviceId);
        result.put("deviceSoftwareVersion", deviceSoftwareVersion);
        result.put("simSerialNumber", simSerialNumber);
        result.put("subscriberId", subscriberId);
      }

      callbackContext.success(result);

      return true;
    } else if (HAS_READ_PERMISSION.equals(action)) {
      hasReadPermission();
      return true;
    } else if (REQUEST_READ_PERMISSION.equals(action)) {
      requestReadPermission();
      return true;
    } else {
      return false;
    }
  }

  private void hasReadPermission() {
    this.callback.sendPluginResult(new PluginResult(PluginResult.Status.OK,
      simPermissionGranted(Manifest.permission.READ_PHONE_STATE)));
  }

  private void requestReadPermission() {
    requestPermission(Manifest.permission.READ_PHONE_STATE);
  }

  private boolean simPermissionGranted(String type) {
    if (Build.VERSION.SDK_INT < 23) {
      return true;
    }
    return (PackageManager.PERMISSION_GRANTED ==
      ContextCompat.checkSelfPermission(this.cordova.getActivity(), type));
  }

  private void requestPermission(String type) {
    if (!simPermissionGranted(type)) {
      ActivityCompat.requestPermissions(this.cordova.getActivity(), new String[]{type}, 12345);
    }
    this.callback.success();
  }

}
