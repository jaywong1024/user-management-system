package com.huanghanjie.usermanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ユーザー管理・認証システムの起動クラス。
 *
 * <p>{@link SpringBootApplication}によって、自動構成、コンポーネントスキャン、
 * Javaベースの設定を有効にする。</p>
 */
@SpringBootApplication
public class UserManagementApplication {

    /**
     * Javaプロセスのエントリーポイント。
     *
     * @param args コマンドラインから渡される起動引数
     */
    public static void main(String[] args) {
        SpringApplication.run(UserManagementApplication.class, args);
    }
}
