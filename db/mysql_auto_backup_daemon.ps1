# ================= 后台运行 =================
if (-not $env:PS_DAEMON) {
    $env:PS_DAEMON = "1"
    Start-Process powershell -ArgumentList "-NoProfile -ExecutionPolicy Bypass -File `"$PSCommandPath`"" -WindowStyle Hidden
    exit
}

# ================= 配置 =================
$MySQLBin = "C:\Program Files\MySQL\MySQL Server 8.0\bin"
$User = "root"
$Pwd  = "123456"
$DB   = "property_management"

$BaseDir   = "C:\Users\30567\Desktop\AI开发物业管理\db"
$MinuteDir = "$BaseDir\minute"
$DailyDir  = "$BaseDir\daily"
$LogDir    = "$BaseDir\logs"

New-Item -ItemType Directory -Force -Path $MinuteDir,$DailyDir,$LogDir | Out-Null

$LogFile = "$LogDir\backup.log"

function Log($msg) {
    Add-Content $LogFile "$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')  $msg"
}

function Backup($SqlFile) {
    # 检查 mysqldump 是否存在
    if (!(Test-Path "$MySQLBin\mysqldump.exe")) {
        Log "❌ 错误: 找不到 mysqldump.exe，路径: $MySQLBin"
        return $false
    }

    for ($i=1; $i -le 3; $i++) {
        $ErrorAction = "Stop"
        try {
            $ArgumentList = @(
                "-u$User",
                "-p$Pwd",
                "--default-character-set=utf8mb4",
                "--single-transaction",
                "--quick",
                "--add-drop-table",
                "--set-gtid-purged=OFF",
                $DB
            )

            & "$MySQLBin\mysqldump.exe" $ArgumentList 2>&1 | Out-File -FilePath $SqlFile -Encoding UTF8

            if ($LASTEXITCODE -eq 0) {
                return $true
            } else {
                Log "❌ mysqldump 返回错误码: $LASTEXITCODE (尝试 $i/3)"
            }
        } catch {
            Log "❌ 备份异常: $_"
        }
        Start-Sleep 5
    }
    return $false
}

Log "===== MySQL 备份服务启动 ====="

# 启动时立即执行一次备份
$Dir = "$MinuteDir\$Date"
New-Item -ItemType Directory -Force -Path $Dir | Out-Null
$Sql = "$Dir\pm_startup_$Time.sql"
$Zip = "$Sql.zip"

if (Backup $Sql) {
    Compress-Archive $Sql $Zip -Force
    Remove-Item $Sql
    Log "启动备份成功 $Zip"
} else {
    Log "❌ 启动备份失败"
}

# ================= 主循环 =================
while ($true) {
    $Now = Get-Date
    $Date = $Now.ToString("yyyy-MM-dd")
    $Time = $Now.ToString("yyyyMMdd_HHmmss")

    # ---------- 10 分钟级 ----------
    if ($Now.Minute % 10 -eq 0 -and $Now.Second -lt 5) {
        $Dir = "$MinuteDir\$Date"
        New-Item -ItemType Directory -Force -Path $Dir | Out-Null

        $Sql = "$Dir\pm_$Time.sql"
        $Zip = "$Sql.zip"

        if (Backup $Sql) {
            Compress-Archive $Sql $Zip -Force
            Remove-Item $Sql
            Log "分钟级成功 $Zip"
        } else {
            Log "❌ 分钟级失败"
        }

        # 清理 24 小时前
        Get-ChildItem $MinuteDir -Recurse -File |
          Where-Object { $_.LastWriteTime -lt (Get-Date).AddHours(-24) } |
          Remove-Item -Force
    }

    # ---------- 日级 ----------
    $DailyZip = "$DailyDir\pm_$($Now.ToString('yyyyMMdd')).sql.zip"
    if (!(Test-Path $DailyZip) -and $Now.Hour -eq 0 -and $Now.Minute -lt 10) {
        $Sql = "$DailyDir\pm_$($Now.ToString('yyyyMMdd')).sql"

        if (Backup $Sql) {
            Compress-Archive $Sql $DailyZip -Force
            Remove-Item $Sql
            Log "日级成功 $DailyZip"
        } else {
            Log "❌ 日级失败"
        }
    }

    Start-Sleep 60
}
