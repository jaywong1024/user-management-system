# User Management System

日本IT企業のJava面接に向けて、実装・設計・テスト・日本語説明を一つにつなげて学習するためのプロジェクトです。

このリポジトリは完成品を一度に作るのではなく、小さな単位で次の流れを繰り返します。

```text
基本設計 → 実装 → テスト → 技術説明 → 面接練習 → ブログ
```

## 現在の進捗

### STEP 01：Spring Bootプロジェクトの構築

- [x] Java 21・Mavenプロジェクトの作成
- [x] Spring Bootアプリケーションの起動クラス
- [x] Actuatorによるヘルスチェック
- [x] アプリケーションコンテキストのテスト
- [x] 基本設計書の初版
- [x] STEP 02 PostgreSQL・Flyway環境構築
- [x] Linux VMの既存コンテナ調査と停止
- [x] PostgreSQL 17.10の構築
- [x] Flywayによる初期Migration
- [x] User EntityとUserRepository
- [ ] ユーザー登録API
- [x] PostgreSQL接続
- [ ] メールアドレスの重複防止
- [ ] パスワードのハッシュ化
- [ ] 統一例外処理
- [ ] 認証・認可

## 技術スタック

| 分類 | 技術 | 選定理由 |
|---|---|---|
| 言語 | Java 21 | LTS版であり、モダンJavaの機能を復習できるため |
| フレームワーク | Spring Boot 3.5 | Spring Framework 6系とJakarta名前空間を学習するため |
| ビルド | Maven | Java業務システムで広く利用され、依存関係を明示しやすいため |
| Web | Spring MVC | REST APIを実装するため |
| 監視 | Spring Boot Actuator | ヘルスチェックと運用の基礎を学ぶため |
| テスト | JUnit 5 | Spring Boot標準のテスト基盤を利用するため |
| DB | PostgreSQL 17.10 | トランザクション、制約、SQLを実践的に復習するため |
| DB Migration | Flyway | SQLのバージョン、適用順序および変更履歴を管理するため |

## 起動方法

```bash
mvn spring-boot:run
```

起動後、別のターミナルでヘルスチェックを確認します。

```bash
curl http://localhost:8080/actuator/health
```

正常な場合は次のレスポンスが返ります。

```json
{"status":"UP"}
```

## テスト方法

```bash
mvn test
```

## ドキュメント

- [基本設計書](docs/基本設計書.md)
- [STEP 01 学習ガイド](docs/学習ガイド/STEP-01_Spring-Bootプロジェクト構築.md)
- [STEP 02 PostgreSQL環境構築記録](docs/環境構築/STEP-02_PostgreSQL環境構築.md)
- [データベース設計書](docs/データベース/DB設計書.md)
- [データベース変更履歴](docs/データベース/DB変更履歴.md)
- [STEP 02 JPA・Repository・Flyway学習ガイド](docs/学習ガイド/STEP-02_JPAとFlyway.md)

## 学習上のルール

- コメントはコードの逐語訳ではなく、設計理由や注意点を日本語で書く。
- 実装した機能は、まず中国語で説明し、その後に日本語で説明する。
- 動作確認とテストが完了した内容だけをブログにまとめる。
- 理解できないまま次のSTEPへ進まない。
