package com.huanghanjie.usermanagement.security.password;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * パスワードのハッシュ化に必要なオブジェクトをSpring Beanとして登録する設定クラス。
 *
 * <p>Web認証全体はまだ導入せず、このSTEPではパスワードの安全な保存と照合に必要な
 * {@link PasswordEncoder}だけをアプリケーションへ提供する。</p>
 */
@Configuration
public class PasswordConfiguration {

    /**
     * 複数のハッシュ形式を識別できるDelegatingPasswordEncoderを生成する。
     *
     * <p>現在、新しくハッシュ化するパスワードにはBCryptが使用され、保存値には
     * {@code {bcrypt}}という識別子が付与される。識別子により、将来アルゴリズムを
     * 変更した場合でも既存形式を判別して照合できる。</p>
     *
     * @return アプリケーションで共有するPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
