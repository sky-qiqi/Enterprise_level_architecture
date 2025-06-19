#!/bin/bash

# 脚本名称: deploy.sh
# 作用: 自动化部署Docker Compose应用，
# 确保端口彻底释放、启动顺序正确，并优化镜像管理。

echo "--- 部署脚本开始执行 (执行时间: $(date)) ---"
echo "--- 注意: 脚本以用户 $(whoami) 执行 ---" # 确认当前执行用户

# 切换到部署目录
# 确保 /home/test/ 目录存在且有权限
cd /home/test/ || { echo "错误：无法切换到 /home/test/ 目录。请检查目录是否存在或权限。"; exit 1; }
echo "当前工作目录: $(pwd)"

echo "--- 清理旧的 Docker 服务及对象（最高优先级确保端口释放） ---"
# 第一步：停止并移除由当前 docker-compose.yml 定义的所有容器及相关卷，
# 这是最彻底地释放Docker占用端口的方法。
# 增加错误检查，避免因 compose 文件不存在而报错中断
if docker compose ps &>/dev/null; then # 检查是否有 compose 服务在运行
    echo "发现正在运行的 Docker Compose 服务，正在执行 down..."
    docker compose down --volumes --remove-orphans || echo "警告：旧的Docker Compose服务清理失败，继续执行。"
else
    echo "提示：未找到正在运行的Docker Compose服务，跳过 'docker compose down'。"
fi
echo "--- Docker Compose 服务清理完成 ---"

# 第二步：清理所有未使用的Docker资源，包括不再使用的镜像、容器、卷、网络等，
# 进一步确保环境干净，避免潜在冲突。
docker system prune -af || echo "警告：Docker系统清理失败，继续执行。"
echo "--- Docker 系统清理完成 ---"

# 增加短暂停顿，给系统时间完全释放端口
echo "--- 暂停 2 秒，确保端口完全释放 ---"
sleep 2

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
# 获取 MySQL 容器内部 "mysql" 用户的 UID 和 GID。
# 这里通过启动一个临时容器来查询。
# 如果此命令失败（例如，没有 docker 命令或镜像拉取失败），会使用默认值。
# 将标准错误重定向到 /dev/null 防止命令本身报错污染输出
# 注意：如果此处无法自动获取UID/GID，请根据实际情况手动设定 MYSQL_UID 和 MYSQL_GID
MYSQL_UID=$(docker run --rm mysql:8.0 id -u mysql 2>/dev/null) # <-- 这里使用 mysql:8.0 来获取UID/GID
MYSQL_GID=$(docker run --rm mysql:8.0 id -g mysql 2>/dev/null) # <-- 这里使用 mysql:8.0 来获取UID/GID

if [ -z "$MYSQL_UID" ] || [ -z "$MYSQL_GID" ]; then
    echo "警告：未能自动获取 MySQL 容器内部用户ID或组ID。使用常见默认值 999:999。如果仍有问题，请手动确认正确ID。"
    MYSQL_UID=999 # 默认 UID
    MYSQL_GID=999 # 默认 GID
else
    echo "调试: 自动获取到 MySQL 容器内部用户ID: $MYSQL_UID，组ID: $MYSQL_GID。"
fi

# 调整数据目录权限：/var/lib/mysql 对应宿主机的 ./mysql/datadir
if [ -d "mysql/datadir" ]; then
    sudo chown -R $MYSQL_UID:$MYSQL_GID mysql/datadir || { echo "警告：无法设置 mysql/datadir 所有者。"; }
    sudo chmod -R 775 mysql/datadir || { echo "警告：无法设置 mysql/datadir 权限。"; }
    echo "调试: 已设置 mysql/datadir 所有者为 $MYSQL_UID:$MYSQL_GID，权限为 775。"
else
    echo "警告：mysql/datadir 目录不存在，跳过权限调整。"
fi

# 调整初始化脚本目录权限：/docker-entrypoint-initdb.d 对应宿主机的 ./mysql/source
if [ -d "mysql/source" ]; then
    sudo chown -R $MYSQL_UID:$MYSQL_GID mysql/source || { echo "警告：无法设置 mysql/source 所有者。"; }
    sudo chmod -R 775 mysql/source || { echo "警告：无法设置 mysql/source 权限。"; }
    echo "调试: 已设置 mysql/source 所有者为 $MYSQL_UID:$MYSQL_GID，权限为 775。"
else
    echo "警告：mysql/source 目录不存在，跳过权限调整。"
fi

# 调整 mydir 目录权限：/mydir 对应宿主机的 ./mysql/mydir
if [ -d "mysql/mydir" ]; then
    sudo chown -R $MYSQL_UID:$MYSQL_GID mysql/mydir || { echo "警告：无法设置 mysql/mydir 所有者。"; }
    sudo chmod -R 775 mysql/mydir || { echo "警告：无法设置 mysql/mydir 权限。"; }
    echo "调试: 已设置 mysql/mydir 所有者为 $MYSQL_UID:$MYSQL_GID，权限为 775。"
else
    echo "警告：mysql/mydir 目录不存在，跳过权限调整。"
fi

echo "--- MySQL 相关目录权限调整完成 ---"


echo "--- 启动 Docker 服务并检查/拉取最新镜像 ---"
MAX_RETRIES=3  # 最大重试次数
RETRY_DELAY=5  # 每次重试前等待秒数

# 首次拉取镜像
echo "--- 正在拉取 Docker 镜像 (docker compose pull) ---"
if docker compose pull; then
    echo "所有镜像已成功拉取。"
else
    echo "警告：镜像拉取失败。可能会在 'docker compose up' 阶段重试拉取。"
fi
echo "--- 镜像拉取完成 ---"

# --- 关键修改：将端口清理放在镜像拉取后，启动前 ---
echo "--- 再次执行端口清理（镜像拉取后，启动前） ---"
PORTS=(8080 6379 3306)
for port in "${PORTS[@]}"; do
    echo "检查端口 $port..."
    PIDS=""
    LSOF_PIDS=$(sudo lsof -i :$port -t 2>/dev/null | sort -u)
    if [ -n "$LSOF_PIDS" ]; then
        PIDS="$LSOF_PIDS"
        echo "调试: lsof 在端口 $port 发现 PID(s): $PIDS"
    else
        NETSTAT_PIDS=$(sudo netstat -tulnp 2>/dev/null | grep ":$port " | awk '{print $7}' | cut -d'/' -f1 | sort -u)
        if [ -n "$NETSTAT_PIDS" ]; then
            PIDS="$NETSTAT_PIDS"
            echo "调试: netstat 在端口 $port 发现 PID(s): $PIDS"
        fi
    fi

    if [ -n "$PIDS" ]; then
        echo "端口 $port 发现被以下PID占用: $PIDS"
        for pid in $PIDS; do
            if sudo kill -0 "$pid" 2>/dev/null; then
                echo "尝试终止 PID $pid..."
                if sudo kill -9 "$pid" 2>/dev/null; then
                    echo "成功终止 PID $pid。"
                else
                    echo "警告：无法终止 PID $pid。请检查进程状态或手动终止。"
                fi
            else
                echo "PID $pid 不存在或已终止，跳过终止。"
            fi
        done
        sleep 1 # 等待进程完全释放端口
    else
        echo "端口 $port 未被占用。"
    fi
done
echo "--- 实时端口清理完成 (第二次) ---"
# --- 关键修改结束 ---


for i in $(seq 1 $MAX_RETRIES); do
    echo "尝试启动 Docker 服务 (第 $i/$MAX_RETRIES 次尝试)..."
    # 以后台模式启动 Docker Compose 定义的所有服务。
    # 这里不需要 --pull always 了，因为我们已经在前面单独 pull 过了。
    if docker compose up -d; then
        echo "Docker 服务启动成功！"
        break # 如果成功则跳出循环
    else
        echo "Docker 服务启动失败 (尝试 $i/$MAX_RETRIES)。"
        if [ $i -lt $MAX_RETRIES ]; then
            echo "--- 强制重启 Docker Daemon 以解决潜在的问题 ---"
            if command -v systemctl &> /dev/null; then
                sudo systemctl restart docker || echo "警告：无法重启 Docker Daemon，请手动检查或确保权限。"
                echo "调试: Docker Daemon 重启完成，等待 $RETRY_DELAY 秒..."
            else
                echo "警告：systemctl 未找到，无法自动重启 Docker Daemon。请手动检查并重启 Docker。"
            fi
            sleep $RERY_DELAY # 等待 Docker 重启和给定的延迟
        else
            echo "错误：Docker 服务启动失败，已达到最大重试次数。请检查服务配置或日志。"
            exit 1 # 达到最大重试次数后退出
        fi
    fi
done
echo "--- Docker 服务启动完成 ---"

echo "--- 检查容器状态 ---"
# 显示所有服务的运行状态
docker compose ps
echo "--- 部署脚本执行完毕 (执行时间: $(date)) ---"