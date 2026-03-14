# OpenClaw Mobile - 一键发布到 GitHub

Write-Host "========================================"
Write-Host "  OpenClaw Mobile GitHub Publisher"
Write-Host "========================================"
Write-Host ""

Set-Location $PSScriptRoot

# 检查 Git
Write-Host "[1/5] Checking Git..."
try {
    git --version | Out-Null
    Write-Host "[OK] Git found" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Git not found. Please install Git first." -ForegroundColor Red
    Write-Host "Download: https://git-scm.com/download/win" -ForegroundColor Yellow
    pause
    exit 1
}

# 检查 GitHub CLI
Write-Host "[2/5] Checking GitHub CLI..."
$ghInstalled = $false
try {
    gh --version | Out-Null
    Write-Host "[OK] GitHub CLI found" -ForegroundColor Green
    $ghInstalled = $true
} catch {
    Write-Host "[INFO] GitHub CLI not found (optional)" -ForegroundColor Yellow
}

# 初始化 Git 仓库
Write-Host ""
Write-Host "[3/5] Initializing Git repository..."
if (Test-Path ".git") {
    Write-Host "[INFO] Git repository already exists" -ForegroundColor Yellow
} else {
    git init
    Write-Host "[OK] Git repository initialized" -ForegroundColor Green
}

# 添加文件
Write-Host ""
Write-Host "[4/5] Adding files to Git..."
git add .
$added = (git status --porcelain | Measure-Object).Count
Write-Host "[OK] Added $added files" -ForegroundColor Green

# 提交
Write-Host ""
Write-Host "Creating initial commit..."
$commitMsg = "Initial commit: OpenClaw Mobile v1.0.0"
git commit -m $commitMsg
if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Commit created" -ForegroundColor Green
} else {
    Write-Host "[INFO] Nothing to commit (already committed?)" -ForegroundColor Yellow
}

# 推送到 GitHub
Write-Host ""
Write-Host "[5/5] Pushing to GitHub..."
Write-Host ""
Write-Host "Please enter your GitHub username:" -ForegroundColor Cyan
$githubUsername = Read-Host "Username"
$repoName = "openclaw-mobile"
$repoUrl = "https://github.com/$githubUsername/$repoName"

Write-Host ""
Write-Host "Repository URL: $repoUrl" -ForegroundColor Cyan
Write-Host ""
Write-Host "Options:" -ForegroundColor Yellow
Write-Host "1. Create repository on GitHub website first" -ForegroundColor White
Write-Host "2. Use GitHub CLI (if installed)" -ForegroundColor White
Write-Host "3. Push to existing repository" -ForegroundColor White
Write-Host ""
$choice = Read-Host "Choose option (1/2/3)"

switch ($choice) {
    "1" {
        Write-Host ""
        Write-Host "Please create repository on GitHub:" -ForegroundColor Cyan
        Write-Host "1. Go to: https://github.com/new" -ForegroundColor White
        Write-Host "2. Repository name: $repoName" -ForegroundColor White
        Write-Host "3. Set as Public" -ForegroundColor White
        Write-Host "4. DO NOT check 'Add README' or '.gitignore'" -ForegroundColor Red
        Write-Host "5. Click 'Create repository'" -ForegroundColor White
        Write-Host ""
        Read-Host "Press Enter when done"
        
        git branch -M main
        git remote add origin $repoUrl
        git push -u origin main
    }
    "2" {
        if ($ghInstalled) {
            Write-Host "Creating repository with GitHub CLI..." -ForegroundColor Cyan
            gh repo create $repoName --public --source=. --remote=origin --push
        } else {
            Write-Host "[ERROR] GitHub CLI not installed" -ForegroundColor Red
            Write-Host "Install: winget install GitHub.cli" -ForegroundColor Yellow
        }
    }
    "3" {
        git branch -M main
        git remote add origin $repoUrl
        git push -u origin main
    }
}

Write-Host ""
Write-Host "========================================"
Write-Host "  Publish Complete!" -ForegroundColor Green
Write-Host "========================================"
Write-Host ""
Write-Host "Your repository: $repoUrl" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Add screenshots to 'screenshots/' folder" -ForegroundColor White
Write-Host "2. Configure GitHub Actions in repository Settings" -ForegroundColor White
Write-Host "3. Add topic tags: android, kotlin, llm, ai" -ForegroundColor White
Write-Host "4. Share with the community!" -ForegroundColor White
Write-Host ""
Write-Host "Documentation: GITHUB_PUBLISH_GUIDE.md" -ForegroundColor Gray
Write-Host ""
Start-Sleep -Seconds 5
