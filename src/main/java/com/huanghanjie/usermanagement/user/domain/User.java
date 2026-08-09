package com.huanghanjie.usermanagement.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * ユーザー情報を表し、usersテーブルと対応付ける永続化Entity。
 *
 * <p>このクラスの役割は、Javaオブジェクトとデータベースのレコードを対応付け、
 * ユーザーの識別情報、認証情報、権限、状態および監査情報を一つの単位として扱うことである。
 * HTTPリクエストを直接受け取るDTOとは役割を分離する。</p>
 */
@Entity
@Table(name = "users")
public class User {

    /** データベースが自動採番するユーザーの一意な識別子。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ログインIDとして使用する正規化済みメールアドレス。 */
    @Column(nullable = false, length = 254, unique = true)
    private String email;

    /** 平文ではなく、一方向ハッシュ化して保存するパスワード。 */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /** 画面やAPIレスポンスで利用するユーザーの表示名。 */
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    /** ユーザーに許可する操作範囲を表す権限。 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRole role;

    /** アカウントが利用可能かどうかを表す状態。 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserStatus status;

    /** レコードを初めて保存した日時。監査情報として使用する。 */
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    /** レコードを最後に更新した日時。監査情報として使用する。 */
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /** 同時更新によるデータの上書きを防止する楽観ロック用の値。 */
    /**
     * 　楽観ロックは、更新時にバージョン番号を確認することで、
     * 他のトランザクションによる更新を検出し、
     * 更新内容の上書きを防止する仕組みです。
     * 　JPAではフィールドに@Versionを付けると、
     * HibernateがUPDATE文のWHERE句に更新前のバージョンを追加し、
     * 更新成功時にバージョンを増加させます。
     * 　更新件数が0件の場合は競合が発生したと判断し、楽観ロック例外が発生します。
     */
    @Version
    @Column(nullable = false)
    private Long version;

    /**
     * JPAがデータベースの値からEntityを復元するために使用する。
     * アプリケーションから無制限に生成されないよう、公開範囲をprotectedにする。
     */
    protected User() {
    }

    /**
     * INSERTの直前に監査日時と初期値を設定する。
     * 呼び出し側の設定漏れがあっても、Entityの初期状態を統一できる。
     */
    @PrePersist
    void initializeBeforeInsert() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        createdAt = now;
        updatedAt = now;

        if (role == null) {
            role = UserRole.USER;
        }
        if (status == null) {
            status = UserStatus.ACTIVE;
        }
    }

    /** UPDATEの直前に最終更新日時を現在時刻へ変更する。 */
    @PreUpdate
    void updateTimestamp() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    /** @return ユーザーを一意に識別するID */
    public Long getId() {
        return id;
    }

    /** @return 正規化済みのメールアドレス */
    public String getEmail() {
        return email;
    }

    /** @return ハッシュ化済みパスワード */
    public String getPasswordHash() {
        return passwordHash;
    }

    /** @return ユーザーの表示名 */
    public String getDisplayName() {
        return displayName;
    }

    /** @return ユーザーに付与された権限 */
    public UserRole getRole() {
        return role;
    }

    /** @return アカウントの利用状態 */
    public UserStatus getStatus() {
        return status;
    }

    /** @return レコードの作成日時 */
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    /** @return レコードの最終更新日時 */
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    /** @return 楽観ロックに使用するバージョン番号 */
    public Long getVersion() {
        return version;
    }
}
