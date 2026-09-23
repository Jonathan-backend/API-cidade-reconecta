@echo off
title Reconecta Cidades - API
set "SCRIPT_DIR=%~dp0"
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT_DIR%executar.ps1" %*
if errorlevel 1 (
  echo.
  echo A API nao conseguiu iniciar. Veja a mensagem acima.
  pause
)
