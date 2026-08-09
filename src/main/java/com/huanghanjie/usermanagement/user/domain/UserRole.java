package com.huanghanjie.usermanagement.user.domain;

/**
 * ユーザーに付与する権限を表す。
 */
public enum UserRole {
    /** 一般ユーザーに付与する標準権限。 */
    USER,

    /** ユーザー管理などの管理機能を利用できる権限。 */
    ADMIN
}
