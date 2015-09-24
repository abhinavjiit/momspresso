package com.mycity4kids.interfaces;

import com.mycity4kids.enums.DialogButtonEvent;
import com.mycity4kids.enums.DialogEnum;

public interface IGetRateAndUpdateEvent {

	void getDialogTypeAndEvent(DialogEnum enumType,DialogButtonEvent type);
}
