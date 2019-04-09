# docker-develop-maven-plugin - 一个帮助开发人员专注于开发而不用浪费时间在打包和发布docker的插件
## 使用前提
使用该插件的开发人员，需要了解linux系统和docker，如果暂时对这两块还没有了解的可以先去了解了先。
## 写在插件开发之前
在进行微服务开发的时候，开发完成进行调试的时候需要将我们开发好的应用部署到开发服务器，这时候需要不断的
打包，上传文件到服务器，停止旧的容器，创建新的镜像，启动新的容器，这一来一回就去了好几分钟，而且有时候
发布可以还是有问题，这时候又要重复这样的操作，这时候就想有没有一种插件可以帮助我们一键搞定这样的重复的
事情，可惜我找了半天还是没有找到我想要的插件，因此就花了点时间写了一个插件，大家只需要执行一个package
的maven命令，去喝杯茶或者上个厕所就可以搞定上面的重复性的工作。
## 插件属性详解
标签 | 说明 | 是否必填
---- | ---- | -------
dockerImagesPath | 服务器存放我们的dockerFile文件和相应的jar包的文件夹的路径 | Y
dockerFilePath | 本地的dockerFile的文件的路径 | Y
jarTargetPath | 本地package命令以后生成的jar包的位置的完整路径 | Y
jarRename | 生成的jar包上传到linux服务器以后需要重命名的名称，，该参数未填则使用dockerImagesPath的jar包的名字|，此处的名称与dockerFile文件夹中的ADD docker-plugin-test.jar /home/app/app.jar的名称一致 | N
imagesHeadName | 镜像的简称名称，主要用于删除所有属于此镜像的版本的镜像 ，该参数未填则使用dockerImagesPath的jar包的名字| N
containerRunName | 启动的容器的名字，该参数未填则直接使用imagesHeadName的参数加run | N
containerRunShare | 该应用启动的时候的文件挂载的位置 | Y
containerRunPorts | 该应用启动的时候的端口映射,多个端口那就对应着启动多少个容器 | Y
containerRunPort | 具体端口映射的配置位置 | Y
options | 服务器的信息的集合 | Y
option | 服务器的ip,账号,密码他们使用以下模板的方式填入： 10.10.10.114,root,1qaz2wsx | Y
## 快速开始
1. 由于该项目没有将maven依赖上传到maven的服务器，因此大家无法正常下载该依赖，大家可以直接把这个工程给git到
本地，然后使用install命令将这个插件安装到本地的maven依赖，或者大家可以直接打开doc文件夹底下的quick_start，
直接将里面的install.bat和docker-developer-maven-plugin-1.1.1-SNAPSHOT.jar拷贝到你本地的随便一个目录，然后直
接双击install.bat【此处只针对window的用户，其他系统用户直接使用命令进行安装】
2. 安装好maven依赖以后大家可以直接打开相应的工程将以下的代码copy到自己的工程的pom.xml的plugins里，然后根据
自己的需求更改以下的配置：
 
              <build>
                      <finalName>docker-maven-plugin-demo</finalName>
                      <plugins>
                          <plugin>
                              <groupId>org.springframework.boot</groupId>
                              <artifactId>spring-boot-maven-plugin</artifactId>
                          </plugin>
              
                          <plugin>
                              <groupId>com.github.lazyBoyl</groupId>
                              <artifactId>docker-maven-plugin</artifactId>
                              <version>1.0-SNAPSHOT</version>
                              <configuration>
                                  <!-- 本地package以后生成的jar包的完整路径 -->
                                  <jarTargetPath>${basedir}/target/docker-maven-plugin-demo.jar</jarTargetPath>
                                  <!-- centos服务器存放我们的dockerFile文件和相应的jar包的文件夹的路径 -->
                                  <dockerImagesPath>/home/app/docker/test/</dockerImagesPath>
                                  <!-- 创建镜像的dockerFile的文件的路径 -->
                                  <dockerFilePath>${basedir}/dockerFile/Dockerfile</dockerFilePath>
                                  <!-- 该工程启动的时候的文件挂载 -->
                                  <containerRunShare>/home/app/logs/docker-plugin-test/log/:/app/www/logs/docker-plugin-test/log/</containerRunShare>
                                  <!-- 该工程启动的时候的端口映射,多个端口那就对应着启动多少个容器 -->
                                  <containerRunPorts>
                                      <containerRunPort>8081:8081</containerRunPort>
                                  </containerRunPorts>
                                  <options>
                                      <!-- 服务器的ip,账号,密码 -->
                                      <option>
                                          10.10.10.125,root,1qaz2wsx
                                      </option>
                                  </options>
                              </configuration>
                              <executions>
                                  <execution>
                                      <!-- 设置该插件的执行的生命周期为package完成以后执行,若不想绑定就将executions这块的代码注释了，后面就不会在打包好以后再去执行docker插件的操作 -->
                                      <phase>package</phase>
                                      <goals>
                                          <!--phase与goal是绑定的关系，当g到达了phase阶段的时候就会去执行goal，此处的值固定为dockerMavenPlugin，因为插件给予他的名字就是dockerMavenPlugin -->
                                          <goal>dockerMavenPlugin</goal>
                                      </goals>
                                  </execution>
                              </executions>
                              <dependencies>
                                  <dependency>
                                      <groupId>ch.ethz.ganymed</groupId>
                                      <artifactId>ganymed-ssh2</artifactId>
                                      <version>262</version>
                                      <scope>compile</scope>
                                  </dependency>
                                  <dependency>
                                      <groupId>commons-io</groupId>
                                      <artifactId>commons-io</artifactId>
                                      <version>2.6</version>
                                      <type>jar</type>
                                      <scope>compile</scope>
                                  </dependency>
                                  <dependency>
                                      <groupId>commons-lang</groupId>
                                      <artifactId>commons-lang</artifactId>
                                      <version>2.6</version>
                                      <type>jar</type>
                                      <scope>compile</scope>
                                  </dependency>
                                  <dependency>
                                      <groupId>org.slf4j</groupId>
                                      <artifactId>jul-to-slf4j</artifactId>
                                      <version>1.7.25</version>
                                      <scope>compile</scope>
                                  </dependency>
                              </dependencies>
                          </plugin>
                      </plugins>
                  </build>
                  
## 视频地址
后续补上使用的视频的地址