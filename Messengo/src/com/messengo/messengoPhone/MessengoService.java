package com.messengo.messengoPhone;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MessengoService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
