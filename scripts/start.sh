echo "--------------- 서버 배포 시작 -----------------"
cd /home/spoteditor/Spoteditor-Backend

echo "--------------- 기존 도커 컨테이너 다운 -----------------"
docker compose down || true
echo "--------------- 도커 컴포즈 이미지 업 -----------------"
docker compose up -d --build
echo "--------------- 서버 배포 끝 -----------------"