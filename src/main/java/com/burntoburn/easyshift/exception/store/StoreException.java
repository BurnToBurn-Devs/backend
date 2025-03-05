package com.burntoburn.easyshift.exception.store;

import com.burntoburn.easyshift.common.exception.BusinessException;
import com.burntoburn.easyshift.common.exception.ErrorCode;

public class StoreException extends BusinessException {

    public StoreException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static StoreException storeAccessDenied() {
        return new StoreException(StoreErrorCode.STORE_ACCESS_DENIED);
    }

    public static StoreException storeNotFound() {
        return new StoreException(StoreErrorCode.STORE_NOT_FOUND);
    }

    public static StoreException userAlreadyJoined() {
        return new StoreException(StoreErrorCode.USER_ALREADY_JOINED);
    }

}