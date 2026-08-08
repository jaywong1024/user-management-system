# STEP 02 PostgreSQL環境構築記録

## 1. 構築結果

| 項目 | 内容 |
|---|---|
| 実施日 | 2026-08-08 |
| VM | `192.168.128.200` / CentOS 7 |
| Docker | 19.03.12 |
| PostgreSQL | 17.10 |
| コンテナ | `ums-postgres` |
| DB | `user_management` |
| ポート | `5432` |
| Restart Policy | `unless-stopped` |
| Health | `healthy` |

MacからTCP 5432への接続と、Spring BootからのJDBC接続を確認済みである。

## 2. 既存コンテナの停止

Harbor、YApi、MongoDB、RedisおよびMySQLを含む既存13コンテナに対して、次を実施した。

```bash
docker update --restart=no <確認済みの各コンテナ名>
docker stop <確認済みの各コンテナ名>
```

結果：全13コンテナが`exited`、Restart Policyが`no`になった。コンテナ、イメージおよびVolumeは削除していない。

## 3. PostgreSQL構築

Git管理する構成ファイル：

- `infra/postgresql/compose.yaml`
- `infra/postgresql/init/01-create-roles.sh`
- `infra/postgresql/.env.example`

VMへ配置した場所：

```text
/opt/user-management-system/postgresql/
```

主なコマンド：

```bash
firewall-cmd --permanent --zone=public --add-port=5432/tcp
firewall-cmd --reload
docker-compose up -d
```

秘密情報を保存する`.env`は権限`600`とし、Gitでは管理しない。

## 4. DBユーザー

| ユーザー | 用途 | Superuser |
|---|---|---|
| `ums_admin` | 初期管理 | Yes |
| `user_management_migration` | Flyway・DDL | No |
| `user_management_app` | アプリケーション・DML | No |

アプリケーションユーザーが接続できること、および`CREATE TABLE`を実行できないことを確認済みである。

## 5. 互換性対応

CentOS 7上のDocker 19.03は、Bookwormイメージを古いseccomp設定のまま実行できなかった。そのためPostgreSQLコンテナだけに次を設定した。

```yaml
security_opt:
  - seccomp=unconfined
```

これは学習VM用の暫定対応である。Dockerを更新した場合は削除して再検証する。

## 6. よく使う操作

```bash
cd /opt/user-management-system/postgresql
docker-compose ps
docker-compose logs -f postgres
docker-compose stop
docker-compose up -d
```

