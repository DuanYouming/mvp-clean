
package com.foxcomm.bandon.aispeech.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

public class GrammarHelper {

	private static final String notCnOrNumPattern = "[^\u4e00-\u9fa5\u0030-\u0039]";
	private Context mContext;

	public GrammarHelper(Context mContext) {
		this.mContext = mContext;
	}

	public String getApps() {
		StringBuilder apps = new StringBuilder();
		PackageManager mPackageManager = mContext.getPackageManager();
		Intent mIntent = new Intent(Intent.ACTION_MAIN);
		mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> mApps = mPackageManager.queryIntentActivities(mIntent, 0);
		for (int i = 0; i < mApps.size(); i++) {
			ResolveInfo tempInfo = mApps.get(i);
			String appLabel = mPackageManager.getApplicationLabel(tempInfo.activityInfo.applicationInfo).toString();
			appLabel = appLabel.replaceAll(notCnOrNumPattern, "");
			if (appLabel != null && !appLabel.trim().equals("")) {
				apps.append(appLabel).append("\n").append("|");

			}
		}
		if (apps.length() > 1) {
			return apps.deleteCharAt(apps.length() - 1).toString();
		} else {
			return apps.toString();
		}
	}

	public String importAssets(String contacts, String appName, String filename) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new InputStreamReader(mContext.getAssets().open(filename), Charset.forName("UTF-8")));
			String line ;
			while ((line = br.readLine()) != null) {
				if (line.contains("#CONTACT#")) {
					line = line.replaceAll(" ", "");
					line = line.replaceAll("#CONTACT#;", "");
					sb.append(line);
					sb.append(contacts);
					sb.append(";\n");

				} else if (line.contains("#APPNAME#")) {
					line = line.replaceAll(" ", "");
					line = line.replaceAll("#APPNAME#;", "");
					sb.append(line);
					sb.append(appName);
					sb.append(";\n");

				} else {
					sb.append(line).append("\n");
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}
}
