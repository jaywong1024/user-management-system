package com.huanghanjie.usermanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * アプリケーション全体の最小構成を確認する結合テスト。
 *
 * <p>テスト専用DBへ切り替えず、通常の設定と同じMySQLへ接続する。
 * MySQLへ接続できない場合はテストを失敗させ、環境の問題を明確にする。</p>
 */
@SpringBootTest
class UserManagementApplicationTests {

    /**
     * Springのアプリケーションコンテキストを正常に起動できることを確認する。
     */
    @Test
    void contextLoads() {
    }
}
