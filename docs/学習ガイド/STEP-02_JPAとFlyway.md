# STEP 02：JPA・Repository・Flyway

## 1. このSTEPの目標

次の起動順序と各部品の責務を説明できるようにする。

```text
application.yml
  → DataSourceを作成
  → FlywayがV1 SQLを実行
  → HibernateがEntityとテーブルを検証
  → Spring Data JPAがRepositoryを生成
  → Spring Boot起動完了
```

## 2. 今回追加した依存関係

| 依存関係 | 役割 |
|---|---|
| `spring-boot-starter-data-jpa` | JPA、Hibernate、Spring Data Repository |
| `postgresql` | PostgreSQLへ接続するJDBC Driver |
| `flyway-core` | Migrationの検出、順序、履歴管理 |
| `flyway-database-postgresql` | PostgreSQL固有のFlyway対応 |
| `h2` | 通常テストを外部VMから独立させるテスト用DB |

## 3. Entityとは

`User`はJavaオブジェクトと`users`テーブルの対応関係を表すEntityである。

| Annotation | 役割 |
|---|---|
| `@Entity` | JPA管理対象のクラスであることを示す |
| `@Table` | 対応するテーブル名を指定する |
| `@Id` | 主キーを示す |
| `@GeneratedValue` | IDの採番方法を指定する |
| `@Column` | カラム名、長さ、NULL可否を指定する |
| `@Enumerated(EnumType.STRING)` | Enumを文字列で保存する |
| `@Version` | 楽観ロック用のバージョンを管理する |
| `@PrePersist` | INSERT直前の処理を行う |
| `@PreUpdate` | UPDATE直前の処理を行う |

EntityはAPIレスポンス用DTOではない。今後ControllerからEntityを直接返さず、Request DTOとResponse DTOを分ける。

## 4. Repositoryとは

```java
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
```

`JpaRepository<User, Long>`の意味：

- 管理するEntityは`User`
- 主キーのJava型は`Long`
- `save`、`findById`、`delete`などの基本処理を継承する

`existsByEmail`と`findByEmail`は、Spring Data JPAがメソッド名を解析してQueryを生成する。現時点では実装クラスを書く必要がない。

## 5. Flywayの核心

- `V1__...sql`をバージョン順に一度だけ実行する。
- 結果とChecksumを`flyway_schema_history`へ記録する。
- 適用済みV1は変更せず、変更時はV2を追加する。
- 開発環境ではSpring Boot起動時、本番では承認済みの独立Jobとして実行する。

## 6. なぜ`ddl-auto: validate`なのか

FlywayがDB構造を作成し、HibernateはEntityとの不一致を検出するだけにするためである。SQL変更履歴をGitで確認でき、Hibernateが意図せず本番テーブルを変更することを防げる。

## 7. テスト環境

通常の`mvn test`は`test` ProfileとH2を利用するため、Linux VMが停止していても実行できる。実際のPostgreSQLとの接続とMigrationは、アプリケーション起動時に別途確認する。

## 8. 次のSTEP

次はユーザー登録APIの最小構成を実装する。

```text
POST /api/v1/users
  → UserController
  → UserService
  → UserRepository
  → PostgreSQL
```

そこでRequest/Response DTO、入力チェック、依存性注入、Serviceの責務および`@Transactional`を一つずつ学ぶ。

## 9. 理解チェック

1. EntityとDTOは何が違いますか。
2. `JpaRepository<User, Long>`の二つの型は何を表しますか。
3. `existsByEmail`の実装を書かなくても動作するのはなぜですか。
4. FlywayとHibernateの責務は何が違いますか。
5. なぜ`ddl-auto: create`を使用しませんか。
6. `@Version`はどのような問題を防止しますか。
7. なぜ通常テストを外部VMへ依存させない設計にしましたか。

