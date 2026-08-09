# STEP 02：JPAによるデータアクセス

## 1. このSTEPの対象範囲

このSTEPでは、MySQLへ接続し、JPAを利用してユーザー情報を永続化するための基盤を作成した。

実装済みの内容：

- MySQL JDBC DriverとSpring Data JPAの導入
- DataSourceとHibernateの設定
- `User` Entity
- `UserRole`と`UserStatus`
- `UserRepository`
- MySQLへ接続するApplicationContextテスト

Controller、Service、ユーザー登録API、パスワードのハッシュ化などは、まだ実装していない。そのため、この文書では説明しない。プロジェクト全体の実装予定は基本設計書で管理する。

## 2. JPAとは何か

JPA（Jakarta Persistence API）は、Javaオブジェクトとリレーショナルデータベースのテーブルを対応付け、データを永続化するための標準仕様である。

JPA自体は具体的な処理を行うライブラリではなく、アノテーションやAPIのルールを定めた仕様である。このプロジェクトでは、JPAの実装としてHibernateを使用し、その上でSpring Data JPAを利用してRepositoryを簡潔に記述している。

```text
アプリケーションコード
    ↓
Spring Data JPA（Repositoryの実装を自動生成）
    ↓
JPA（永続化の標準仕様）
    ↓
Hibernate（JPAの実装）
    ↓
MySQL JDBC Driver
    ↓
MySQL
```

### 面接での説明例

> JPAは、Javaオブジェクトとリレーショナルデータベースをマッピングするための標準仕様です。JPA自体は仕様であり、このプロジェクトでは実装としてHibernateを使用しています。また、Spring Data JPAを利用することで、Repositoryインターフェースから基本的なCRUD処理や検索処理を自動生成しています。

### 重要語彙

- 永続化（えいぞくか）：程序结束后仍将数据保存在数据库中
- 標準仕様（ひょうじゅんしよう）：标准规范
- 実装（じっそう）：规范的具体实现
- 対応付ける（たいおうづける）：建立对象和表之间的映射
- リレーショナルデータベース：关系型数据库
- データアクセス層：访问数据库的程序层

## 3. JPA、Hibernate、Spring Data JPAの違い

| 名称 | 役割 | このプロジェクトでの例 |
|---|---|---|
| JPA | Entity、`EntityManager`、関連付けなどのルールを定める標準仕様 | `@Entity`, `@Id`, `@Column` |
| Hibernate | JPA仕様に従ってSQL生成、状態管理、Dirty Checkingなどを行う実装 | EntityからMySQL用SQLを生成する |
| Spring Data JPA | Repositoryを抽象化し、定型的なデータアクセスコードを減らす | `JpaRepository`, `existsByEmail` |

この三つをすべて「JPA」と呼ぶと説明が曖昧になる。面接では「JPAは仕様、Hibernateは実装、Spring Data JPAはRepositoryの抽象化」と区別すると分かりやすい。

## 4. MyBatisとの違い

MyBatisとJPAは、どちらもデータアクセスを実装するための技術だが、考え方が異なる。

| 観点 | MyBatis | JPA / Hibernate |
|---|---|---|
| 中心となる考え方 | SQLを中心に処理を組み立てる | Entityを中心に状態を管理する |
| SQL | 開発者がXMLまたはアノテーションで明示的に記述する | HibernateがEntityと操作から生成する |
| マッピング | ResultMapなどで検索結果とJavaオブジェクトを対応付ける | アノテーションでEntityとテーブルを対応付ける |
| 更新 | UPDATE文を明示的に呼び出す | 管理状態のEntity変更をDirty Checkingで検出できる |
| 複雑なSQL | SQLを直接制御しやすい | JPQL、Native Query、Specificationなどを選択する |
| 注意点 | SQLやマッピングの重複が増える場合がある | N+1、遅延ロード、永続化コンテキストの理解が必要 |

### MyBatis経験を使った説明例

> 以前はMyBatisを使用していたため、MapperとSQLを明示的に記述する方法に慣れています。JPAではSQLよりEntityの状態を中心に扱い、HibernateがSQLを生成します。単純なCRUDを短く実装できる一方、永続化コンテキスト、Dirty Checking、遅延ロードおよびN+1問題を理解する必要があると考えています。複雑なSQLを細かく制御したい場合はMyBatisが分かりやすく、ドメインモデルを中心に扱う場合はJPAが便利です。要件に応じて選択することが重要です。

JPAが常にMyBatisより優れているわけではない。SQLの複雑さ、既存資産、チームの経験、性能要件によって適切な技術は異なる。

## 5. `User` Entity

### 5.1 クラスの役割

`User`はユーザー情報をJavaオブジェクトとして表現し、MySQLの`users`テーブルと対応付けるEntityである。

> `User`クラスはユーザー情報を表すEntityです。`@Entity`によってJPAの永続化対象であることを示し、`@Table`によって対応するテーブル名を指定しています。このクラスでは、識別情報、認証情報、権限、アカウント状態および監査情報を管理します。

### 5.2 主なアノテーション

| アノテーション | 役割 |
|---|---|
| `@Entity` | このクラスがJPAの永続化対象であることを示す |
| `@Table(name = "users")` | 対応するテーブル名を指定する |
| `@Id` | 主キーとなるフィールドを示す |
| `@GeneratedValue` | 主キーをデータベース側で自動採番する |
| `@Column` | カラム名、NULL可否、文字数、一意制約などを指定する |
| `@Enumerated(EnumType.STRING)` | Enumの名前を文字列として保存する |
| `@Version` | 楽観ロック用のバージョンを管理する |
| `@PrePersist` | INSERT直前に呼ばれるライフサイクルコールバックを指定する |
| `@PreUpdate` | UPDATE直前に呼ばれるライフサイクルコールバックを指定する |

### 5.3 フィールド

| フィールド | 日本語での説明 |
|---|---|
| `id` | ユーザーを一意に識別する主キーです。値はデータベース側で自動採番します。 |
| `email` | ログインIDとして使用するメールアドレスです。データベースでは重複を許可しません。 |
| `passwordHash` | 一方向ハッシュ化したパスワードを保存するための項目です。平文パスワードは保存しません。 |
| `displayName` | 画面やAPIレスポンスに表示するユーザー名です。 |
| `role` | ユーザーに許可する操作範囲を表します。 |
| `status` | アカウントが利用可能かどうかを表します。 |
| `createdAt` | レコードを初めて保存した日時です。 |
| `updatedAt` | レコードを最後に更新した日時です。 |
| `version` | 同時更新による上書きを検出するための楽観ロック用バージョンです。 |

### 5.4 メソッド

| メソッド | 日本語での説明 |
|---|---|
| `User()` | JPAがデータベースの値からEntityを復元するために使用する引数なしコンストラクタです。 |
| `initializeBeforeInsert()` | INSERT直前に作成日時、更新日時、権限および状態の初期値を設定します。 |
| `updateTimestamp()` | UPDATE直前に最終更新日時を現在時刻へ変更します。 |
| Getter | Entityの状態を外部から参照するために使用します。無制限な変更を防ぐため、Setterは公開していません。 |

## 6. Entityの状態と永続化コンテキスト

JPAでは、Entityが現在どの状態にあるかが重要である。

| 状態 | 意味 |
|---|---|
| Transient | Javaオブジェクトとして生成されたが、まだJPAに管理されていない |
| Managed | 永続化コンテキストに管理され、変更が追跡されている |
| Detached | 以前は管理されていたが、現在は永続化コンテキストから外れている |
| Removed | 削除対象として管理されている |

永続化コンテキストは、Entityを管理する作業領域である。同じ主キーのEntityを同一コンテキスト内で一つのインスタンスとして扱う第一レベルキャッシュや、変更内容を検出するDirty Checkingなどの機能を持つ。

### Dirty Checking

管理状態のEntityをトランザクション内で変更すると、Hibernateは変更前後の状態を比較し、必要なUPDATE文を生成する。この仕組みをDirty Checkingという。

> Dirty Checkingとは、永続化コンテキストが管理しているEntityの変更を検出し、トランザクションの終了時などにUPDATE文へ反映する仕組みです。そのため、管理状態のEntityでは、変更のたびにRepositoryの更新メソッドを呼ぶ必要はありません。

現在の`User`には業務上の更新メソッドがまだないため、Dirty Checkingを利用する処理も未実装である。

### FlushとCommit

- Flush：永続化コンテキストの変更内容をSQLとしてデータベースへ送る
- Commit：トランザクションを確定する

Flushしただけでは必ずしもトランザクションが確定したとは限らない。後でRollbackされる可能性がある。

## 7. `UserRepository`

`UserRepository`は、`User` Entityの保存と検索を担当するデータアクセス層である。

```java
public interface UserRepository extends JpaRepository<User, Long>
```

- `User`：操作対象のEntity型
- `Long`：主キーの型

`JpaRepository`を継承すると、`save`、`findById`、`findAll`、`deleteById`などの基本操作を利用できる。Spring Data JPAが実行時にRepositoryの実装を生成するため、自分で実装クラスを書く必要はない。

### Query Method

```java
boolean existsByEmail(String email);
Optional<User> findByEmail(String email);
```

Spring Data JPAは、`existsByEmail`や`findByEmail`というメソッド名を解析し、対応する検索処理を生成する。この仕組みをQuery MethodまたはDerived Queryという。

> `existsByEmail`は、指定したメールアドレスが存在するかを確認するQuery Methodです。Spring Data JPAがメソッド名を解析してクエリを生成するため、単純な検索であれば実装クラスやSQLを記述する必要はありません。

## 8. 遅延ロードとN+1問題

関連Entityを必要になるまで取得しない仕組みを遅延ロードという。不要なデータ取得を避けられる一方、ループ内で関連データへアクセスすると追加SQLが繰り返し実行され、N+1問題が発生する場合がある。

現在の`User`には他のEntityとの関連がないため、遅延ロードもN+1問題もまだ発生しない。関連を追加するSTEPで、実際のSQLを確認しながら学習する。

`open-in-view: false`にしている理由：

> Controllerまで永続化コンテキストを開いたままにすると、画面用データの変換中に意図しないSQLが実行される可能性があります。そのため、このプロジェクトではデータ取得とDTOへの変換をService層のトランザクション内で完了させる方針です。

## 9. `ddl-auto: update`

この学習段階では、Hibernateの`ddl-auto: update`を利用し、Entityに合わせてテーブルを作成・更新する。

> `ddl-auto: update`は学習環境を簡単にするために使用しています。Entityの定義を基にHibernateがテーブル構造を更新します。ただし、変更内容の事前確認や履歴管理が難しいため、本番環境で安易に使用する設定ではありません。

## 10. Lombokを使用していない理由

Lombokは日本で使われていないわけではない。採用するかどうかは、国ではなく、プロジェクトの方針、Javaのバージョン、IDEやビルド環境、保守性に対する考え方によって決まる。

このSTEPでLombokを導入していない理由：

1. Javaのコンストラクタ、アクセス修飾子、Getterを実際のコードとして確認するため。
2. 各フィールドとメソッドの役割を日本語で説明する練習をするため。
3. Entityに`@Data`を付けると、不要なSetterまで公開されるため。
4. 自動生成された`toString`に`passwordHash`が含まれる危険を避けるため。
5. Entityの`equals`と`hashCode`は主キーや関連を考慮して慎重に設計する必要があるため。

現在の`User` Entityでは、学習内容を明示するためにLombokを使用しない。今後、新しいクラスを実際に追加するSTEPで、Lombok、通常のJavaコード、Java 21の`record`を比較してから採用を判断する。Entityへ`@Data`を機械的に付けない方針は維持する。

### 面接での説明例

> Lombokはボイラープレートコードを削減できる便利なライブラリですが、Entityに`@Data`を付けると、不要なSetter、`toString`、`equals`および`hashCode`まで自動生成されます。特にパスワードなどの機密情報や遅延ロードとの関係に注意が必要です。そのため、必要なアノテーションだけを選択して使用する方針です。

## 11. 現在の理解チェック

1. JPA、Hibernate、Spring Data JPAの違いを説明してください。
2. MyBatisとJPAでは、データアクセスの考え方がどのように違いますか。
3. `@Entity`と`@Table`はそれぞれ何を表しますか。
4. JPAが引数なしコンストラクタを必要とする理由は何ですか。
5. `JpaRepository<User, Long>`の二つの型は何を表しますか。
6. `existsByEmail`の実装を書かなくても動作するのはなぜですか。
7. Managed状態とDetached状態の違いを説明してください。
8. Dirty Checkingとは何ですか。
9. FlushとCommitは何が違いますか。
10. `@Enumerated(EnumType.STRING)`を使用する利点は何ですか。
11. `@Version`はどのような問題を防止しますか。
12. EntityへLombokの`@Data`を安易に付けない理由は何ですか。
---
1. JPAは標準の定義で、Hibernateは具体的な実装で、Spring Data JPAは抽象的なカプセル化で、例えばRepositoryです。
2. MyBatisはSQLを中心にデータを永続化する技術で、JPAはEntityを中心にデータを永続化する技術です。例えば、MyBatisは主に具体的なSQLがXMLファイルに明示的に書くことで、JPAは主にEntityクラスをメンテナンスすることです。
3. クラスは@Entityを付けると、このクラスは永続化対象という意味で、＠Tableは対応するtable name,catalog,schemaを指定できるアノテーションです。
4. JPAの実装するHibernateはリフレクションを利用したインスタンス化のが必要です。
5. EntityのクラスとEntityのIDのタイプです。
6. Spring Data JPAはメソッド名を解析し、対応する検索処理を生成する。
7. Managed状態は永続化コンテキストに管理され、Detached状態は以前は管理されいたが、現在は永続化コンテキストから外れている。
8. Dirty CheckingというのはManaged状態のEntityをトランザクション中で変更すると、Hibernateは変更前後の状態を比べて、必要なUPDATE文を生成する。
9. Flushはただ変更内容をSQLとしてDatabaseへ送って、Commitはトランザクションを確定する。
10. @Enumerated(EnumType.STRING)を使用する利点は可読性（かどくせい）にあります。
11. @Versionは楽観ロックを使って、二人を同じデータを変わることを防止することです。
12. 学習のために、今Lombokを使わない方がいいと思います。なぜなら、引数なしコンストラクタの必要性とか、toSting, equals, hashCodeなどのメソッドを考慮して慎重に設計する必要があるため。

关于n+1问题的回答：
N+1問題とは、一覧データを一回のSQLで取得した後、各Entityの関連データを遅延ロードすることで、追加のSQLがN回実行される問題です。Fetch Join、EntityGraph、DTO Projection、Batch Fetchingなどを、取得要件に応じて使い分けます。

### 理解チェックの修正版

1. JPAは、Javaオブジェクトとリレーショナルデータベースを対応付けるための標準仕様です。HibernateはJPA仕様の代表的な実装であり、SQL生成やEntityの状態管理を行います。Spring Data JPAはRepositoryを抽象化し、定型的なデータアクセスコードを削減する仕組みです。
2. MyBatisはSQLを中心にデータアクセスを実装する技術であり、SQLをXMLファイルやアノテーションに明示的に記述します。一方、JPAはEntityを中心にデータを管理し、HibernateがEntityの状態や操作に基づいてSQLを生成します。
3. クラスに`@Entity`を付けると、そのクラスがJPAの永続化対象であることを示します。`@Table`は、Entityに対応するテーブル名、スキーマ、カタログなどを指定するためのアノテーションです。
4. JPAの実装であるHibernateが、リフレクションなどを利用してEntityを生成し、データベースの値から状態を復元するために、引数なしコンストラクタが必要です。
5. 第一型引数の`User`は操作対象となるEntityの型を表し、第二型引数の`Long`は主キーの型を表します。
6. Spring Data JPAがメソッド名を解析し、対応するクエリとRepositoryの実装を実行時に生成するため、自分で実装クラスを書く必要はありません。
7. Managed状態のEntityは永続化コンテキストによって管理されており、変更内容が追跡されます。Detached状態のEntityは、以前は管理されていたものの、現在は永続化コンテキストの管理対象から外れているため、変更しても自動的にはデータベースへ反映されません。
8. Dirty Checkingとは、トランザクション内でManaged状態のEntityを変更した場合に、Hibernateが変更前後の状態を比較し、必要なUPDATE文を自動的に生成する仕組みです。
9. Flushは永続化コンテキストの変更内容をSQLとしてデータベースへ送る処理です。Commitはトランザクションを確定する処理です。Flush後であっても、Commitされる前に例外が発生すればRollbackされる可能性があります。
10. `EnumType.STRING`を使用すると、Enumの名前が文字列として保存されるため、データベース上の値を理解しやすくなります。また、`ORDINAL`と異なり、Enum定数の定義順序を変更しても既存データとの対応関係が壊れにくいという利点があります。
11. `@Version`は楽観ロックを実現するために使用します。複数のトランザクションが同じデータを更新した場合に競合を検出し、後から実行された更新が先に確定した変更を上書きすることを防止します。
12. EntityにLombokの`@Data`を付けると、すべてのフィールドに対するSetter、`toString`、`equals`および`hashCode`が自動生成されます。その結果、Entityの状態を無制限に変更できたり、`passwordHash`などの機密情報がログへ出力されたりする可能性があります。また、関連EntityやHibernateのProxyを含むメソッドは慎重に設計する必要があります。そのため、Entityでは必要なLombokアノテーションだけを選択して使用します。

### N+1問題の修正版

> N+1問題とは、一覧データを1回のSQLで取得した後、各Entityの関連データを遅延ロードすることで、追加のSQLがN回実行される問題です。Fetch Join、EntityGraph、DTO Projection、Batch Fetchingなどを、取得要件に応じて使い分けます。
