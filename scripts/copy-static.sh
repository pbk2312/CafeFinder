#!/usr/bin/env bash
set -eux

# 1) nginx/html 초기화
rm -rf nginx/html
mkdir -p nginx/html/static

# 2) HTML 템플릿 복사
cp src/main/resources/templates/*.html nginx/html/

# 3) 정적 리소스 복사 (CSS, JS, 이미지 등)
cp -r src/main/resources/static/* nginx/html/static/
