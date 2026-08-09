package com.huanghanjie.usermanagement.user.domain;

/**
 * ユーザーアカウントの利用状態を表す。
 */
public enum UserStatus {
    /** ログインおよび通常機能を利用できる状態。 */
    ACTIVE,

    /** ログイン失敗などにより一時的に利用を制限した状態。 */
    LOCKED,

    /** 退会や管理操作などにより利用を停止した状態。 */
    DISABLED
}
