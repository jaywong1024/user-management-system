# STEP 01：Spring Bootプロジェクトの構築

## 1. このSTEPの目標

このSTEPでは業務機能を実装しない。次の内容を自分の言葉で説明できれば完了とする。

1. Mavenが何を管理するのか。
2. `pom.xml`の役割は何か。
3. `@SpringBootApplication`が持つ三つの役割は何か。
4. 組み込みTomcatを使う利点は何か。
5. `application.yml`には何を書くのか。
6. Actuatorのヘルスチェックは何のために使うのか。
7. `@SpringBootTest`は何を確認するのか。

## 2. ディレクトリ構成

```text
user-management-system/
├── pom.xml
├── README.md
├── docs/
│   ├── 基本設計書.md
│   └── 学習ガイド/
├── src/
│   ├── main/
│   │   ├── java/com/huanghanjie/usermanagement/
│   │   │   └── UserManagementApplication.java
│   │   └── resources/application.yml
│   └── test/
│       └── java/com/huanghanjie/usermanagement/
│           └── UserManagementApplicationTests.java
└── target/                 Mavenが生成するためGit管理しない
```

## 3. `pom.xml`の役割

`pom.xml`はMavenプロジェクトの設計情報であり、主に次の内容を管理する。

- プロジェクト名とバージョン
- Javaのバージョン
- 利用するライブラリ
- ビルドとテストの方法
- 実行可能なJARを作成するためのプラグイン

今回利用するStarterは次のとおりである。

| Starter | 役割 |
|---|---|
| `spring-boot-starter-web` | Spring MVC、JSON変換、組み込みTomcat |
| `spring-boot-starter-validation` | DTOの入力チェック |
| `spring-boot-starter-actuator` | ヘルスチェックと運用情報 |
| `spring-boot-starter-test` | JUnit、Mockito、Spring Test |

Starterを利用すると、関連ライブラリのバージョンを一つずつ指定する必要がない。Spring Bootが互換性を考慮した依存関係を管理する。

## 4. `@SpringBootApplication`の役割

`@SpringBootApplication`は、主に次の三つをまとめたアノテーションである。

1. `@SpringBootConfiguration`：Springの設定クラスであることを示す。
2. `@EnableAutoConfiguration`：依存関係と設定値に基づいて自動構成を有効にする。
3. `@ComponentScan`：起動クラスのパッケージ以下からBean候補を検索する。

そのため、起動クラスは通常、アプリケーションのルートパッケージに配置する。

## 5. 今回まだ追加していないもの

### データベース

STEP 02でMySQLとSpring Data JPAを追加し、ユーザー登録APIを段階的に実装する。

### Spring Security

認証の仕組みを理解する前に追加すると、すべてのAPIが保護されて動作確認が複雑になる。そのため、ユーザー登録と例外処理を理解した後で追加する。

### Lombok

Javaのコンストラクタ、アクセサ、`equals`および`hashCode`を復習するため、最初はLombokを使用しない。

## 6. 動作確認

### テスト

```bash
mvn test
```

`BUILD SUCCESS`と表示されれば、Springのアプリケーションコンテキストを構築できている。

### 起動

```bash
mvn spring-boot:run
```

別のターミナルで次を実行する。

```bash
curl http://localhost:8080/actuator/health
```

期待結果：

```json
{"status":"UP"}
```

## 7. 面接用の短い説明

### 日本語

> このプロジェクトでは、Java 21、Spring Boot 3.5、Mavenを使用しています。Spring Bootを採用した理由は、自動構成とStarterによる依存関係管理を利用して、業務機能の実装に集中できるためです。また、組み込みTomcatを利用しているため、実行可能なJARとして単独で起動できます。現在はプロジェクトの基盤構築とMySQL接続まで完了しており、次のSTEPでユーザー登録APIを実装する予定です。

### 中文理解

这个项目使用Java 21、Spring Boot 3.5和Maven。选择Spring Boot是因为可以利用自动配置和Starter依赖管理，把精力集中在业务功能上。同时使用内嵌Tomcat，可以将应用作为可执行JAR独立启动。目前已经完成项目基础搭建和MySQL连接，下一步会实现用户注册API。

## 8. 理解チェック

不看文档，尝试回答以下问题：

1. 为什么选择Maven，而不是手动下载JAR？
2. `spring-boot-starter-web`带来了哪些主要功能？
3. 为什么启动类应该放在根包？
4. 自动配置是不是意味着完全不需要配置？
5. Actuator和普通业务Controller有什么区别？
6. 为什么第一步没有立刻加入数据库和Spring Security？
7. 如果把端口从8080改成8081，应该修改哪里？

能够用中文回答后，再尝试使用上面的日语关键词回答。

1. Mavenを採用すると、ライブライの管理するのは便利で、ビルドとテストも簡単になり、プラグインも豊富です。
2. pomファイルを見ると、spring-boot-starter-webはspring-boot,json,tomcat,web,web-mvnが含めた。主にspring-boot項目を構築し、web機能の基盤を提供することです。
3. 起動クラスのアノテーション@SpringBootApplicationは@ComponentScanが含めて、このアノテーションの機能がルートディレクトリからBean候補を検索する。
4. 自動設定はディフォルトルールで、設定が全く不要と言う意味ではなく、実際には、特に複数の環境の場合、多くの設定が必要あります。
5. Actuatorはヘルスチェックと運用情報のため採用して、Controllerただ普通に業務用です。
6. 一度全て完璧にしようとするのではなく、徐々に復習したいです。
7. application.ymlファイルのserver.portを変更すると、訪問portが変わってなる。
---
修正：
1. Mavenを採用すると、ライブラリや依存関係を一元管理できます。また、ビルドやテストを同じコマンドで実行でき、豊富なプラグインも利用できます。手動でJARを管理する場合と比べて、バージョンの不整合や依存関係の漏れを防ぎやすいです。
2. spring-boot-starter-webには、Spring MVC、JSON変換を行うJackson、組み込みTomcatなどが含まれています。主にREST APIやWebアプリケーションを開発するための基盤を提供します。
3. @SpringBootApplicationには@ComponentScanが含まれており、起動クラスが属するパッケージと、そのサブパッケージからBean候補を検索します。そのため、起動クラスは通常、ルートパッケージに配置します。
4. 自動構成は、依存関係や設定値に基づいてデフォルトの構成を自動的に行う仕組みです。ただし、設定がまったく不要という意味ではありません。複数環境への対応や独自要件がある場合は、明示的な設定が必要です。
5. Actuatorは、ヘルスチェックやメトリクスなど、アプリケーションの監視と運用に必要な情報を提供します。一方、通常のControllerは、ユーザー登録などの業務処理をHTTP APIとして提供します。
6. 最初からデータベースやSpring Securityを追加すると、設定や問題の切り分けが複雑になります。そのため、まずSpring Bootの基本構成と起動方法を確認し、その後、機能を段階的に追加する方針にしました。
7. application.ymlのserver.portを8081に変更します。変更後は、アプリケーションの待受ポートとアクセス先のポートが8081になります。

「6」这里值得背的词：
段階的に追加する：分阶段添加
問題を切り分ける：定位、隔离问题
構成が複雑になる：配置变复杂

「7」不要使用 訪問port，日语 IT 工作中一般说：
アクセス先のポート
待受ポート
ポート番号

## 9. 下一STEP之前需要你完成的事情

- 在IntelliJ IDEA中打开`pom.xml`并等待Maven同步完成。
- 执行一次`mvn test`。
- 启动应用并访问`/actuator/health`。
- 阅读`基本設計書.md`的第1～8节。
- 尝试回答“理解チェック”的7个问题。

确认理解后，再进入STEP 02：JPAによるデータアクセス
