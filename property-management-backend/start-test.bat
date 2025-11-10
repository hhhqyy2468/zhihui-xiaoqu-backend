@echo off
echo 正在测试智慧小区物业管理系统后端启动...

echo 检查Java环境...
java -version

echo.
echo 检查项目结构...
if exist "src\main\java\com\hyu\PropertyApplication.java" (
    echo 找到启动类
) else (
    echo 未找到启动类，请检查项目结构
    pause
    exit /b 1
)

echo.
echo 项目结构检查完成
echo.
echo 要启动项目，请运行：
echo mvn spring-boot:run
echo.
echo 或者使用IDE运行PropertyApplication.java
echo.
echo 启动后访问：
echo - 接口文档：http://localhost:8080/api/doc.html
echo - 健康检查：http://localhost:8080/api/test/health
echo.
pause