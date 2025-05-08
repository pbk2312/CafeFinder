#!/usr/bin/env bash
set -eux

# 1) nginx/html 초기화
rm -rf nginx/html
mkdir -p nginx/html

# 2) HTML 템플릿 복사
cp src/main/resources/templates/*.html nginx/html/

