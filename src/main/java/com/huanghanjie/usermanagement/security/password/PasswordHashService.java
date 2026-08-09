package com.huanghanjie.usermanagement.security.password;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 平文パスワードのハッシュ化と、入力されたパスワードの照合を担当するService。
 *
 * <p>呼び出し側がハッシュアルゴリズムの詳細へ依存しないようにし、
 * パスワードに関する処理の入口をこのクラスへ集約する。</p>
 */
@Service
public class PasswordHashService {

    /** 実際のハッシュ化と照合を行うSpring Securityのインターフェース。 */
    private final PasswordEncoder passwordEncoder;

    /**
     * コンストラクタインジェクションによってPasswordEncoderを受け取る。
     *
     * @param passwordEncoder ハッシュ化と照合を行うPasswordEncoder
     */
    public PasswordHashService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 平文パスワードを復元できない形式へ一方向変換する。
     *
     * @param rawPassword 利用者が入力した平文パスワード
     * @return データベースへ保存するハッシュ化済みパスワード
     */
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 入力された平文パスワードが保存済みハッシュと一致するかを確認する。
     *
     * <p>同じパスワードでも毎回異なるSaltが使用されるため、文字列同士を直接比較せず、
     * PasswordEncoderのmatchesメソッドを使用する。</p>
     *
     * @param rawPassword 利用者が入力した平文パスワード
     * @param encodedPassword データベースに保存されているハッシュ化済みパスワード
     * @return 同じパスワードである場合はtrue、それ以外はfalse
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
