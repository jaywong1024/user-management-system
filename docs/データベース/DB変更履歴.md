# DB変更履歴

## 環境初期化

| 項目 | 内容 |
|---|---|
| 実施日 | 2026-08-08 |
| SQL | `infra/postgresql/init/01-create-roles.sh` |
| 内容 | Migration用・アプリケーション用ロールの作成と権限分離 |
| 結果 | 成功 |

実行したSQLのパスワード部分は環境変数から渡し、文書では`***`として扱う。

## Flyway Migration

| Version | ファイル | 適用日時 | Checksum | 結果 |
|---|---|---|---:|---|
| V1 | `V1__create_users_table.sql` | 2026-08-08 16:15 JST | -487744905 | 成功 |

V1で実行した内容：

- `users`テーブル作成
- 主キー、一意制約、CHECK制約作成
- テーブルとカラムのコメント追加
- アプリケーションユーザーへのDML権限付与

確認結果：

```text
flyway_schema_history: version=1, success=true
public.users: 作成済み
user_management_app: SELECT可能、DDL不可
```

