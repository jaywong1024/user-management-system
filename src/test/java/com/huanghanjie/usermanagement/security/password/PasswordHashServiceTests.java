package com.huanghanjie.usermanagement.security.password;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PasswordHashServiceのハッシュ化と照合を確認する単体テスト。
 *
 * <p>Spring ApplicationContextやMySQLを起動せず、パスワード処理だけを独立して検証する。</p>
 */
class PasswordHashServiceTests {

    /** テスト対象となるパスワード処理Service。 */
    private PasswordHashService passwordHashService;

    /** 各テストの前に実際のDelegatingPasswordEncoderを使用してテスト対象を生成する。 */
    @BeforeEach
    void setUp() {
        passwordHashService = new PasswordHashService(
                PasswordEncoderFactories.createDelegatingPasswordEncoder()
        );
    }

    /** 平文とは異なるBCrypt形式の値が生成され、元のパスワードを照合できることを確認する。 */
    @Test
    void hashesRawPasswordAndMatchesIt() {
        String rawPassword = "Correct-Horse-Battery-Staple-2026";

        String encodedPassword = passwordHashService.hash(rawPassword);

        assertThat(encodedPassword)
                .isNotEqualTo(rawPassword)
                .startsWith("{bcrypt}");
        assertThat(passwordHashService.matches(rawPassword, encodedPassword)).isTrue();
    }

    /** Saltが毎回生成されるため、同じ平文でも異なるハッシュになることを確認する。 */
    @Test
    void generatesDifferentHashesForSameRawPassword() {
        String rawPassword = "Same-Password-2026";

        String firstHash = passwordHashService.hash(rawPassword);
        String secondHash = passwordHashService.hash(rawPassword);

        assertThat(firstHash).isNotEqualTo(secondHash);
        assertThat(passwordHashService.matches(rawPassword, firstHash)).isTrue();
        assertThat(passwordHashService.matches(rawPassword, secondHash)).isTrue();
    }

    /** 入力されたパスワードが異なる場合に照合結果がfalseとなることを確認する。 */
    @Test
    void rejectsIncorrectRawPassword() {
        String encodedPassword = passwordHashService.hash("Correct-Password-2026");

        boolean matched = passwordHashService.matches("Wrong-Password-2026", encodedPassword);

        assertThat(matched).isFalse();
    }
}
