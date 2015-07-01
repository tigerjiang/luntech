package com.luntech.launcher;

public class Utility {
	public static String DEVICE_MODULE_TAIYE = "OTA9901";
	public static String LONGTAI_MODULE_TAIYE = "Q1";
	
	public static int MENU_MYFAVORITE = 0;
	public static int MENU_PLAYHISTORY = 1;
	public static int MENU_FAVPERSON = 2;
	
	public static int DIRECTION_TOP = 0;
	public static int DIRECTION_LEFT = 1;
	public static int DIRECTION_BOTTOM = 2;
	public static int DIRECTION_RIGHT = 3;

	public static final String NETWORK_CONNECT_SUCCESS = "netconnect.success";
	public static final String NETWORK_FAULT = "network.fault";
	public static final String NETWORK_SUCCESS = "network.success";


	public static class NetworkStatus{
		public static int NET_STATUS_DISCONNECT = -1;
		public static int NET_STATUS_WIFI = 1;
		public static int NET_STATUS_ETH = 2;
	}

}
