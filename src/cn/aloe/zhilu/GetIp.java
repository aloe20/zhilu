package cn.aloe.zhilu;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class GetIp {
	// 检查是否有可用网络包括wifi,gprs
		public static boolean checkEnable(Context paramContext) {
			NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext.getSystemService("connectivity"))
					.getActiveNetworkInfo();
			if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable()))
				return true;
			return false;
		}
		
		// 获取手机ip
		@SuppressWarnings("deprecation")
		public String getIp() {
			String ipAddress = "";
			try {
				Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
				// 遍历所用的网络接口
				while (en.hasMoreElements()) {
					NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
					Enumeration<InetAddress> inet = nif.getInetAddresses();
					// 遍历每一个接口绑定的所有ip
					while (inet.hasMoreElements()) {
						InetAddress ip = inet.nextElement();
						if (!ip.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
							return ipAddress = ip.getHostAddress();
						}
					}
				}
			} catch (SocketException e) {
			}
			return ipAddress;
		}
}
