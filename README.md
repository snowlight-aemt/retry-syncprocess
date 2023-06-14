# retry-syncprocess

## 스팩
* java ( jdk 17 )
* mybatis
* junit 5
* mockito

## 소소한 개발 : 재처리 대상 관리 파일로 구현하기
* 최범균님 영상에서 소개했던 프로젝트 실제로 구현...

## TODO
* Queue Lock 
  * Queue (Memory, File) 동시성을 위한 락 추가
* Schdule 기능 추가
  * 현재 테스트 샘플용으로 만듬
* 테스트 위치 이동하여 패키지 구조 정리
* 변경 내용을 DB 로 전달

## 상태 다이어그램
![스크린샷 2023-06-14 오후 11 08 53](https://github.com/snowlight-aemt/retry-syncprocess/assets/82430645/c1e2e3b6-939c-431b-a103-4eca3625495d)

## 클래스 다이어그램
![스크린샷 2023-06-14 오후 10 42 02](https://github.com/snowlight-aemt/retry-syncprocess/assets/82430645/3a924d27-ea59-473b-82ae-103c635cf6ed)

## 테스트 코드
![스크린샷 2023-06-14 오후 10 39 31](https://github.com/snowlight-aemt/retry-syncprocess/assets/82430645/535e8f8e-ca1f-4d43-b985-65ed7a3011cc)

## 참고 자료
[최범균님 유튜브](https://www.youtube.com/watch?v=xCe_U1kNmZM)
