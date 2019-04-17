@echo off
setlocal

if [%1]==[] (
  echo no test class specified
  goto:eof
)

if [%1]==[index] (
  explorer "build\reports\tests\test\index.html"
  explorer "build\jacoco.html\index.html"
  goto:eof
)

gradle test --tests %*