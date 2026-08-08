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

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
