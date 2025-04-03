TARGET_DIR="/home/spoteditor/Spoteditor-Backend"

cd "$TARGET_DIR" || exit 1

# 현재 디렉터리에서 mysql 폴더와 db-compose.yml을 제외한 모든 파일/디렉터리 삭제
find . -mindepth 1 -maxdepth 1 ! -name 'mysql' ! -name 'db-compose.yml' -exec rm -rf {} +
