# STEP 03：パスワードの安全な保存

## 1. このSTEPの対象範囲

このSTEPでは、ユーザー登録APIを実装する前に、平文パスワードを安全にハッシュ化し、ログイン時に照合するための基盤を作成した。

実装済みの内容：

- `spring-security-crypto`の導入
- `PasswordEncoder` Beanの登録
- `PasswordHashService`によるハッシュ化と照合
- BCrypt形式、Salt、正常照合および不一致の単体テスト

Controller、ユーザー登録処理、ログイン処理およびJWT認証は、まだ実装していない。

## 2. なぜ暗号化ではなくハッシュ化するのか

暗号化は鍵を使用して元の値へ復号できる。一方、パスワード保存に使用するハッシュ化は一方向変換であり、保存値から平文パスワードへ簡単に戻せない。

```text
暗号化：平文 → 暗号文 → 復号 → 平文
ハッシュ：平文 → ハッシュ値（元の平文へ戻さない）
```

ログイン時にはハッシュを復号しない。利用者が入力した平文パスワードと、データベースに保存されたハッシュを`matches`で照合する。

### 面接での説明例

> パスワードは復号可能な暗号化ではなく、一方向ハッシュとして保存します。ログイン時には保存済みハッシュを復号せず、`PasswordEncoder`の`matches`メソッドを使用して、入力されたパスワードと照合します。

## 3. 用語

| 用語 | 説明 |
|---|---|
| 平文パスワード | 利用者が入力した、ハッシュ化前のパスワード |
| ハッシュ値 | 一方向変換後にデータベースへ保存する値 |
| Salt | 同じパスワードから毎回異なるハッシュを生成するために加えるランダム値 |
| Work Factor | ハッシュ計算に必要な処理量。大きいほど解析しにくいが、サーバー負荷も高くなる |
| 照合（しょうごう） | 入力値と保存値が同じパスワードを表すか確認すること |
| 総当たり攻撃 | 多数の候補を繰り返し試してパスワードを推測する攻撃 |

## 4. 追加した依存関係

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

このSTEPではSpring Security全体ではなく、暗号関連機能を提供する`spring-security-crypto`だけを導入した。そのため、Webリクエストに対する認証Filterやログイン画面はまだ有効にならない。

## 5. `PasswordConfiguration`

### 5.1 クラスの役割

`PasswordConfiguration`は、アプリケーションで共有する`PasswordEncoder`をSpring Beanとして登録する設定クラスである。

> `PasswordConfiguration`は、パスワードのハッシュ化と照合に使用する`PasswordEncoder`をBeanとして登録する設定クラスです。`@Configuration`によって設定クラスであることを示し、`@Bean`メソッドの戻り値をSpringコンテナへ登録します。

### 5.2 メソッド

| メソッド | 日本語での説明 |
|---|---|
| `passwordEncoder()` | 複数のハッシュ形式を識別できる`DelegatingPasswordEncoder`を生成し、Spring Beanとして登録します。 |

## 6. `PasswordHashService`

### 6.1 クラスの役割

`PasswordHashService`は、平文パスワードのハッシュ化と、保存済みハッシュとの照合を担当するServiceである。

呼び出し側がBCryptなどの具体的なアルゴリズムへ直接依存しないように、パスワード処理の入口をこのクラスへ集約する。

> `PasswordHashService`は、パスワードのハッシュ化と照合を担当するServiceです。具体的なハッシュアルゴリズムへの依存をこのクラスに集約することで、呼び出し側の責務を単純にしています。

### 6.2 フィールドとコンストラクタ

| 要素 | 日本語での説明 |
|---|---|
| `passwordEncoder` | 実際のハッシュ化と照合を行うSpring Securityのインターフェースです。 |
| `PasswordHashService(...)` | コンストラクタインジェクションによって`PasswordEncoder`を受け取ります。依存関係が必須であることを明確にし、テスト時にも差し替えやすくします。 |

### 6.3 メソッド

| メソッド | 日本語での説明 |
|---|---|
| `hash(rawPassword)` | 平文パスワードを復元できない形式へ一方向変換し、データベースへ保存する値を返します。 |
| `matches(rawPassword, encodedPassword)` | 入力された平文パスワードが、保存済みハッシュと一致するかを確認します。 |

## 7. DelegatingPasswordEncoder

このプロジェクトでは、次のFactoryメソッドを使用する。

```java
PasswordEncoderFactories.createDelegatingPasswordEncoder();
```

新しいパスワードには現在BCryptが使用され、保存形式は次のようになる。

```text
{bcrypt}$2a$10$...
```

- `{bcrypt}`：使用したPasswordEncoderを識別するID
- `$2a$`：BCryptの形式情報
- `10`：BCryptのCost
- 残りの部分：Saltとハッシュ値を含む

アルゴリズム名は秘密情報ではない。`{bcrypt}`を保存することで、将来新しいアルゴリズムへ変更した後も、既存データがどの形式で作成されたか判別できる。

### 面接での説明例

> `DelegatingPasswordEncoder`は、保存値の先頭にある識別子を基に、対応する`PasswordEncoder`へ処理を委譲します。現在は新しいパスワードをBCryptでハッシュ化し、保存値には`{bcrypt}`という識別子が付きます。これにより、将来アルゴリズムを変更した場合でも、既存のハッシュ形式を判別して照合できます。

## 8. Saltと照合方法

BCryptはハッシュ化のたびにランダムなSaltを生成する。そのため、同じ平文パスワードを2回ハッシュ化しても、結果は異なる。

```text
同じパスワード + Salt A → ハッシュA
同じパスワード + Salt B → ハッシュB
```

したがって、次の比較方法は使用できない。

```text
hash(入力パスワード) == DBのハッシュ
```

新しいSaltによって異なるハッシュが生成されるためである。必ず次のように照合する。

```java
passwordEncoder.matches(rawPassword, encodedPassword);
```

`matches`は保存済みハッシュに含まれるアルゴリズム情報、Cost、Saltを読み取り、入力されたパスワードを同じ条件で検証する。

## 9. BCryptとWork Factor

BCryptは、パスワード解析を困難にするため、意図的に計算時間を必要とする適応型一方向関数である。

Costを大きくすると攻撃者の総当たり処理を遅くできるが、正常なログイン処理のサーバー負荷も増加する。本番環境では、実際のサーバー性能を測定して調整する必要がある。このSTEPではSpring Securityの標準設定を使用し、独自のCost調整はまだ行わない。

## 10. テスト設計

`PasswordHashServiceTests`はSpring ApplicationContextとMySQLを使用しない単体テストである。パスワード処理だけを独立して確認する。

| テスト | 確認内容 |
|---|---|
| `hashesRawPasswordAndMatchesIt` | ハッシュが平文と異なり、`{bcrypt}`形式で、正しいパスワードを照合できること |
| `generatesDifferentHashesForSameRawPassword` | 同じ平文でもSaltによって異なるハッシュが生成され、どちらも照合できること |
| `rejectsIncorrectRawPassword` | 誤ったパスワードの照合結果がfalseになること |

テスト結果：

```text
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 11. セキュリティ上のルール

- 平文パスワードをデータベースへ保存しない。
- 平文パスワードとハッシュ値をログへ出力しない。
- ハッシュ値をAPIレスポンスへ含めない。
- ハッシュ値同士を文字列として比較しない。
- 独自のハッシュアルゴリズムを作成しない。
- パスワードの入力チェックは、Request DTOを実装するSTEPで追加する。
- ログイン成功後はパスワードを毎回照合せず、短期間有効な認証情報を利用する。

## 12. 面接回答

> パスワードは平文や復号可能な暗号文ではなく、Saltを含む適応型一方向ハッシュとして保存します。このプロジェクトではSpring Securityの`DelegatingPasswordEncoder`を使用し、新しいパスワードをBCryptでハッシュ化します。ログイン時はハッシュ値を復号したり、文字列として比較したりせず、`matches`メソッドで照合します。また、平文パスワードとハッシュ値をログやAPIレスポンスへ出力しないようにします。

## 13. 理解チェック

1. パスワードを復号可能な暗号化ではなく、一方向ハッシュとして保存する理由は何ですか。
2. Saltは何のために使用しますか。
3. 同じパスワードから毎回異なるハッシュが生成されるのはなぜですか。
4. ハッシュ値同士を直接比較できないのはなぜですか。
5. `PasswordEncoder`の`encode`と`matches`は、それぞれ何を行いますか。
6. `DelegatingPasswordEncoder`の`{bcrypt}`は何を表しますか。
7. BCryptのCostを大きくした場合、セキュリティと性能へどのような影響がありますか。
8. なぜパスワード処理を`PasswordHashService`へ集約しましたか。
9. なぜこのSTEPではSpring Security全体ではなく、`spring-security-crypto`だけを導入しましたか。
