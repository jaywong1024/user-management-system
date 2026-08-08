# DB設計書

## usersテーブル

| カラム | 型 | 制約・用途 |
|---|---|---|
| `id` | BIGINT | PK、IDENTITY |
| `email` | VARCHAR(254) | NOT NULL、UNIQUE、正規化済み |
| `password_hash` | VARCHAR(255) | NOT NULL、平文保存禁止 |
| `display_name` | VARCHAR(100) | NOT NULL、空白のみ禁止 |
| `role` | VARCHAR(30) | `USER` / `ADMIN` |
| `status` | VARCHAR(30) | `ACTIVE` / `LOCKED` / `DISABLED` |
| `created_at` | TIMESTAMPTZ | 作成日時 |
| `updated_at` | TIMESTAMPTZ | 更新日時 |
| `version` | BIGINT | 楽観ロック用、0以上 |

実際のDDLは次のMigrationファイルを正とする。

- `src/main/resources/db/migration/V1__create_users_table.sql`

## 設計上の要点

- アプリケーションの事前確認とDBの一意制約の両方でメール重複を防止する。
- パスワードはハッシュ値だけを保存する。
- `@Version`と`version`カラムで楽観ロックを利用できるようにする。
- Hibernateは`ddl-auto: validate`とし、テーブル作成はFlywayに任せる。
- DB変更が必要な場合、適用済みV1を編集せずV2を追加する。

