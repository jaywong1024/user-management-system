package com.huanghanjie.usermanagement.user.repository;

import com.huanghanjie.usermanagement.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User Entityの保存と検索を担当するRepository。
 *
 * <p>Spring Data JPAがメソッド名から検索処理を生成するため、
 * 基本的なCRUD処理を自分で実装する必要はない。</p>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 指定したメールアドレスが既に登録されているかを確認する。
     *
     * <p>ユーザー登録前の重複チェックに利用する。ただし同時実行を完全には防げないため、
     * データベースの一意制約も併用する。</p>
     *
     * @param email 正規化済みのメールアドレス
     * @return 既に存在する場合はtrue、存在しない場合はfalse
     */
    boolean existsByEmail(String email);

    /**
     * メールアドレスを条件としてユーザーを一件検索する。
     *
     * @param email 正規化済みのメールアドレス
     * @return ユーザーが存在する場合はそのEntity、存在しない場合は空のOptional
     */
    Optional<User> findByEmail(String email);
}
