NGINX_CONTAINER_NAME=nginx

echo "nginx 컨테이너 정지"
docker stop $NGINX_CONTAINER_NAME

echo "인증서 갱신"
sudo certbot renew --quiet

echo "nginx 컨테이너 시작"
docker start $NGINX_CONTAINER_NAME

echo "인증서 갱신완료"
