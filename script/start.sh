# 编译
# mvn clean install -Dmaven.test.skip=true

# 部署运行

#
SERVER_NAME="ONTAuth-1.0.0.RELEASE.jar"

#
ps -ef | grep $SERVER_NAME | grep -v grep | awk '{print $2}' | xargs kill -9

#
nohup java -jar target/$SERVER_NAME --spring.profiles.active=dev >/dev/null 2>&1 &

#
LOG_LOCATION="/Users/leewi9/Downloads/ONTAuth/log/all.log"
tail -f $LOG_LOCATION