#!/bin/bash

# 脚本名称: deploy.sh
# 作用: 自动化部署Docker Compose应用，简化启动流程，并清除所有Docker缓存。

echo "--- 部署脚本开始执行 (执行时间: $(date)) ---"
echo "--- 注意: 脚本以用户 $(whoami) 执行 ---" # 确认当前执行用户

# 切换到部署目录
# 确保 /home/test/ 目录存在且有权限
cd /home/test/ || { echo "错误：无法切换到 /home/test/ 目录。请检查目录是否存在或权限。"; exit 1; }
echo "当前工作目录: $(pwd)"

echo "--- 清理本地所有 Docker 缓存（最高优先级确保拉取正确） ---"
# 停止所有正在运行的容器
echo "正在停止所有正在运行的 Docker 容器..."
docker stop $(docker ps -q) &>/dev/null || echo "警告：停止容器失败或没有正在运行的容器，继续执行。"

# 移除所有容器（包括已停止的）
echo "正在移除所有 Docker 容器..."
docker rm $(docker ps -a -q) &>/dev/null || echo "警告：移除容器失败或没有需要移除的容器，继续执行。"

# 移除所有镜像
echo "正在移除所有 Docker 镜像..."
docker rmi $(docker images -q) &>/dev/null || echo "警告：移除镜像失败或没有需要移除的镜像，继续执行。"

# 清理所有未使用的Docker资源，包括不再使用的卷、网络等。
echo "正在清理所有未使用的 Docker 卷和网络..."
docker system prune -af &>/dev/null || echo "警告：Docker系统清理失败，继续执行。"

echo "--- Docker 环境及缓存清理完成 ---"

echo "--- 清理部署目录旧文件 (保留 package.tgz) ---"
# 查找并删除当前目录（除了 package.tgz）下的所有文件和目录
find . -mindepth 1 ! -name "package.tgz" -exec rm -rf {} + || { echo "错误：清理旧文件失败，可能权限问题。"; exit 1; }
echo "--- 旧文件清理完成 ---"

echo "--- 解压部署包 ---"
# 解压 package.tgz 到当前目录
tar -zxvf package.tgz || { echo "错误：解压 package.tgz 失败。请检查文件是否存在或损坏。"; exit 1; }
echo "--- 部署包解压完成 ---"

echo "--- 设置文件权限 ---"
# 更改解压后的文件和目录的所有者为 root，组为 root (root 用户执行，无需 sudo)
sudo chown -R root:root . || { echo "错误：设置文件所有者失败。请检查 sudo 权限。"; exit 1; }
echo "调试: 已设置文件所有者为 root:root。"
# 设置所有目录的权限为 755 (rwx for owner, rx for group/others)
sudo find . -type d -exec chmod 755 {} + || { echo "错误：设置目录权限失败。"; exit 1; }
echo "调试: 已设置目录权限为 755。"
# 设置所有文件的权限为 644 (rw for owner, r for group/others)
sudo find . -type f -exec chmod 644 {} + || { echo "错误：设置文件权限失败。"; exit 1; }
echo "调试: 已设置文件权限为 644。"
# 确保 deploy.sh 脚本本身有执行权限
sudo chmod +x deploy.sh || { echo "错误：设置 deploy.sh 执行权限失败。"; exit 1; }
echo "调试: 已设置 deploy.sh 执行权限。"
echo "--- 文件权限设置完成 ---"

echo "--- 调整 MySQL 相关目录权限，确保容器内部用户可写 ---"
# 优化点：避免每次都启动临时容器获取UID/GID
# 优先使用常见的默认值，如果后续启动MySQL容器失败，再尝试更精确的获取方式
MYSQL_UID=999 # MySQL默认用户UID
MYSQL_GID=999 # MySQL默认用户GID

# 调整数据目录权限：/var/lib/mysql 对应宿主机的 ./mysql/datadir
if [ -d "mysql/datadir" ]; then
    echo "调试: 尝试设置 mysql/datadir 所有者为 $MYSQL_UID:$MYSQL_GID，权限为 775。"
    sudo chown -R $MYSQL_UID:$MYSQL_GID mysql/datadir && sudo chmod -R 775 mysql/datadir || {
        echo "警告：无法直接设置 mysql/datadir 权限。尝试通过临时容器获取正确的UID/GID。"
        # 仅当第一次尝试失败时才尝试启动临时容器获取UID/GID
        TEMP_MYSQL_UID=$(docker run --rm mysql:8.0 id -u mysql 2>/dev/null)
        TEMP_MYSQL_GID=$(docker run --rm mysql:8.0 id -g mysql 2>/dev/null)
        if [ -n "$TEMP_MYSQL_UID" ] && [ -n "$TEMP_MYSQL_GID" ]; then
            MYSQL_UID=$TEMP_MYSQL_UID
            MYSQL_GID=$TEMP_GID
            echo "调试: 自动获取到 MySQL 容器内部用户ID: $MYSQL_UID，组ID: $MYSQL_GID。"
            sudo chown -R $MYSQL_UID:$MYSQL_GID mysql/datadir && sudo chmod -R 775 mysql/datadir || {
                echo "错误：即使尝试自动获取UID/GID，仍无法设置 mysql/datadir 权限。请手动检查并设置。"
            }
        else
            echo "错误：无法自动获取 MySQL 容器内部用户ID或组ID。请手动确认正确ID并设置 mysql/datadir 权限。"
        fi
    }
else
    echo "警告：mysql/datadir 目录不存在，跳过权限调整。"
fi

# 调整初始化脚本目录权限：/docker-entrypoint-initdb.d 对应宿主机的 ./mysql/source
if [ -d "mysql/source" ]; then
    echo "调试: 尝试设置 mysql/source 所有者为 $MYSQL_UID:$MYSQL_GID，权限为 775。"
    sudo chown -R $MYSQL_UID:$MYSQL_GID mysql/source && sudo chmod -R 775 mysql/source || {
        echo "警告：无法设置 mysql/source 权限。"
    }
else
    echo "警告：mysql/source 目录不存在，跳过权限调整。"
fi

# 调整 mydir 目录权限：/mydir 对应宿主机的 ./mysql/mydir
if [ -d "mysql/mydir" ]; then
    echo "调试: 尝试设置 mysql/mydir 所有者为 $MYSQL_UID:$MYSQL_GID，权限为 775。"
    sudo chown -R $MYSQL_UID:$MYSQL_GID mysql/mydir && sudo chmod -R 775 mysql/mydir || {
        echo "警告：无法设置 mysql/mydir 权限。"
    }
else
    echo "警告：mysql/mydir 目录不存在，跳过权限调整。"
fi

echo "--- MySQL 相关目录权限调整完成 ---"

echo "--- 启动所有 Docker Compose 服务 ---"
# 使用 docker compose up -d --build --pull always 启动所有服务
# --build 确保如果 Dockerfile 有更新则重新构建
# --pull always 确保拉取最新镜像
docker compose up -d --build --pull always || { echo "错误：Docker Compose 服务启动失败。请检查 docker-compose.yml 配置和日志。"; exit 1; }

echo "--- 所有 Docker Compose 服务启动完成 ---"

echo "--- 检查最终容器状态 ---"
docker compose ps
echo "--- 部署脚本执行完毕 (执行时间: $(date)) ---"