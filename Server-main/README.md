# 모델 구성
## YOLOv3
- 직접 학습시키기에는 시간이 너무 오래걸려서
- 약 3,000가지의 음식이 학습된 모델을 AiHub로부터 제공받았습니다. 

## CNN 
- mlforkids 에서 사용하는 CNN 모델만 따왔습니다.
- 평상 시에 자주먹는 음식들 20가지를 선정하여 각 음식 당 100개 이미지를 크롤링해서 학습시켰습니다.
- 각 음식 당, 80개 이미지를 학습시키고 나머지 20개로 테스트해본 결과 약 81퍼센트의 정확도가 나왔습니다.

## 이미지 인식
- YOLOv3가 객체 인식은 어느정도 잘했지만 해당 객체가 어떤 음식인지는 분류못하는 경우가 많았습니다.
- 따라서, YOLOv3로 객체인식해 분리한 다음 CNN모델을 이용해 해당 객체가 어떤 음식인지 분류하는 방법으로 이미지 인식을 하였습니다.

# 사용법
## FLASK
- python FLASK 프레임워크를 이용해 앱과 서버의 통신을 하고 해당 파일은 YOLOv3/fl.py 입니다.


# 이미지 인식 결과
![image jpeg_yolo_out_py](https://user-images.githubusercontent.com/28686334/131852835-9dc33c65-60f6-4d2f-84b4-851103e682d8.jpg)
![Screen Shot 2021-09-02 at 10 32 58 PM](https://user-images.githubusercontent.com/28686334/131852842-6476ceed-b734-49f9-9630-7f4b08d094dc.png)

![KakaoTalk_Photo_2021-09-02-22-28-36](https://user-images.githubusercontent.com/28686334/131852859-7bf4c2be-3305-46fe-a63d-8f8b7a817dd9.jpeg)
![KakaoTalk_Photo_2021-09-02-22-28-42](https://user-images.githubusercontent.com/28686334/131852862-7ee6ecb2-39f6-434c-8cdd-2052b6ca09dc.png)

