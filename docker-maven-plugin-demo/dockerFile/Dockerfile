FROM base-jdk8
   MAINTAINER linzf <282245889@qq.com>
   RUN mkdir -p /app/www/logs/docker-plugin-test/log/
   RUN mkdir -p /home/app/
   ADD docker-maven-plugin-demo.jar /home/app/app.jar
   #设置CMD的形式开机启动程序
   CMD java -jar /home/app/app.jar