Basic Cloud Service
===================
주제 : 단순한 형태의 클라우드 시스템

사용자의 종류(엑터)는 일반 유저 밖에 없음


기능분석
------


### 개인 저장소(좌측 패널)  
~  
> - 유저는 개인 저장소에 파일 업로드  
> 유저는 개인에 파일을 업로드
> - 개인 파일 다운로드  
> 유저는 개인 저장소에 파일을 다운로드  
> - 개인 파일 삭제  
> 유저는 개인 저장소에서 파일을 삭제
> - 파일 코멘트 삽입  
> 유저는 파일에 대한 부가적인 설명, 코멘트 삽입  
> - 유저는 업로드된 파일의 이름 수정  

~   
### 파일 상세정보 패널(우측 패널)
> - 파일 클릭시 우측 패널은 다음과 같은 정보를 출력함  
> 파일 크기   
> 업로드된 날짜  
> 확장자  
> 코멘트  
> 파일 미리보기(이미지의경우 그림 미리보기 + 해상도)  

~  
### 회원 가입(메인 화면)

> - 유저는 ID,PW로 회원가입
> - 유저는 ID,PW로 로그인

~  
할수도 안할수도 있는 기능(안할거같음 아마)
### 공유 저장소  
> - 공유 파일 업로드  
> 유저는 공유 클라우드 공간에 파일을 업로드  
> - 공유 파일 다운로드  
> 유저는 공유 클라우드 공간에 파일을 다운로드
> 유저는 유저 아이디검색, 타인의 공유 저장소에 접근후 업/다운로드  
> - 기간제 파일
> 정해진 기한까지만 다운가능(유효하도록)하도록 설정하고 기간후엔 자동 삭제  

~  
~  

## 예상되는 데이터베이스 attribute  
~  

1. user_id 유저 아이디  유저마다 고유한 식별자 이다.
2. user_ciphertext 암호화된 패스워드  
3. userkey 16자리 개발자가 정해야하는 비밀키  
4. file_id 파일 식별 넘버. 모든 유저들의 파일들 사이에서 파일을 고유 식별한다.
5. file_name 파일 이름. 특정 유저의 개인 저장소내에서는 고유하나 전체유저 사이에서는 고유하지 않다.
6. uploaded_file 업로드된 파일 자체  
7. file_extension 파일 확장자  
8. uploaded_date 업로드된 날짜, 시간  
9.  file_comment 파일의 부연설명  개인 저장소 내에서도 고유하지 않다.
10. file_size 파일 크기  
11. imagesize 이미지의 경우 해상도  

user_info : user_id(PK), ciphertext

user_key : user_id(PK), userkey


PK : user_id + file_id
user_id, file_id, file_name, uploaded_file, file_extension, uploaded_date, file_comment, file_size

file_id -> uploaded_file -> file_extension, file_size

file_id -> file_comment


file_list :
user_id로 인덱스 만들어야 함
file_id(PK), user_id, uploaded_file

file_metadata :
file_id(PK), file_name, file_extention, file_size, uploaded_date, file_comment
