# 📚 💰 Booque ver2
<br>

<p align="center"><img width="700" alt="제222" src="https://github.com/epepssp/Booque-ver2/assets/118948099/a11d509d-b989-47a6-aec9-1a0ccdbfce2f"></p>
<br>

## 개요
**인원** 아이티윌 자바 134기 "하찮은 진정혜" 조 6인이 한 번 더 함께한 두번째 팀 프로젝트<br>
**일정** 2023년 1월 11일 ~ 2023년 2월 16일<br>
<br>

## 프로젝트 소개
유저간 중고거래 플랫폼 BOOQUE 장터 및 다양한 기능 추가하여 Booque ver1 빌드업!
 
<br>

## 사용 기술 및 개발환경
- Java
- Spring Boot
- HTML
- CSS
- JavaScript
<br>

## 주요기능 소개(ver1과 비교하여 추가된 기능)
- 부끄장터
  - 중고책 거래 플랫폼
  - Socket을 이용한 유저간 실시간 채팅
  - 판매글 작성 -> 작성중인 글 임시 저장
- 알림
   - 댓글 알림
   - 키워드 알림 
- 외부 경로(로컬 폴더) 이용한 이미지 업로드 

<br>

## 나의 구현 기능
  #### 💡[ 외부 경로 이미지 업로드 - 프로필 사진 변경](#profile)
 + ##### 설정 추가
   ###### &nbsp;&nbsp;◽ SecurityConfig<br><br>&nbsp;&nbsp;◽ application.properties 외부 경로 폴더 위치 추가


 + ##### [프로필 사진 업데이트 버튼 클릭 > File Modal 열림 > fileInput창에서 사진 선택 > btnProfileUpdate 클릭](#pic1)

 
 + ##### [btnProfileUpdate 이벤트 리스너](#pic2)
   ###### &nbsp;&nbsp;◽ fileInput 찾고 fileInput의 file을 가져와 file 변수에 담는다.<br><br>&nbsp;&nbsp;◽ file 데이터를 전송하기 위해 formData 생성하여 file을 formData에 추가한다.<br><br>&nbsp;&nbsp;◽ Axios POST 요청 방식으로 formData를 ImageUploadController의 upload 메서드로 전송한다. 


 + ##### [ImageUploadController의 upload 메서드](#pic3)
   ###### &nbsp;&nbsp;◽ 중복을 방지하기 위해 식별자(UUID) 생성하여 원본 파일 이름 앞에 식별자(UUID)를 추가한 새로운 파일 이름(fileName)을 만든다.<br><br>&nbsp;&nbsp;◽ 파일이 저장될 경로와 새로운 fileName을 담아 File 객체를 생성하고, 그 객체를 지정된 외부 경로에 저장한다.

 + ##### [ImageUpload.js의 getImage() 함수](#pic4)
   ###### &nbsp;&nbsp;◽ 유저 id 전달하고 리턴 받은 fileName을 서버로 전달하여, 서버로부터 로컬 폴더에 저장된 해당 미미지 파일을 반환 받는다.<br><br>&nbsp;&nbsp;◽ 해당 이미지 파일을 프로필 사진 영역에 넣어주면, 프로필 사진 변경 완료! 

 + ##### [ImageUploadController의 viewFile 메서드](#pic5)
   ###### &nbsp;&nbsp;◽ 파일 저장 경로와 전달 받은 fileName으로 실제 file 객체를 생성한다. <br><br>&nbsp;&nbsp;◽ 브라우저가 file을 파악하여 제대로 반환할 수 있도록 HTTP 응답 헤더에 해당 file의 contentType을 찾아서 추가한다.
 <br>
 
  #### 💡[ 알림(Notice)](#notice)
  + ##### [  테이블 생성 및 컬럼 추가](#sec1)
  + ##### [  알림 생성](#sec2)
    ###### &nbsp;&nbsp;◽ 새댓글 알림: 댓글 작성될 때<br><br>&nbsp;&nbsp;◽ 키워드 알림: 알림 받을 키워드 등록 <br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;중고장터 새 글 등록 시, 등록된 키워드 목록에서 새 글 키워드와 일치하는 항목 있는지 체크<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;일치하는 항목 있을 때!
  + ##### 알림 받을 키워드 등록하기
  + ##### [  알림 표시](#sec3)
    ###### &nbsp;&nbsp;◽ 상단바에 알림 버튼 추가<br><br>&nbsp;&nbsp;◽ 뱃지에 알림 갯수 표시<br><br>&nbsp;&nbsp;◽ 로그인 사용자의 전체 알림 리스트 dropdown으로 보여줌</h6>
  + ##### [  알림 확인](#sec4)
    ###### &nbsp;&nbsp;◽ 알림 클릭하여 해당 댓글(판매글)로 이동하면 알림 확인한 것으로 간주하여 알림 삭제<br><br>&nbsp;&nbsp;◽ 새 댓글 백그라운드 컬러 효과</h6>

 <br><br><br>

 
## <div id="profile">💡 외부 경로 이미지 업로드 - 프로필 사진 변경</div>

 <br>
 <div align="center"><img src="https://github.com/epepssp/Booque-ver2/assets/118948099/086e1692-d718-4703-888f-07dd7dbb9766" height="400" alt="프사"></div>
 <br> 

 + #### 설정 추가
 > SecurityConfig.java 추가

 ```java

    @Bean  // 로컬 폴터 이미지 불러오기 위한 config 추가
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()
                    );
        };
    }

 ``` 
 > application.properties 외부경로 폴더 위치 추가

 ``` application.properties
        site.book.upload.path=C:\\study\\images
 ```
 <br>
 
 + #### <div id="pic1">프로필 사진 업데이트 버튼 클릭 > File Modal 열림 > fileInput창에서 사진 선택 > btnProfileUpdate 클릭</div>
 > list.html 일부

 ```html

       <!-- (예진) 프로필 사진 영역-->
       <a th:href="@{ /myPage }">
          <span id="profileImageDiv"></span>
       </a>
    
       <!-- (예진) 프로필 사진 업데이트 버튼 -->
       <span th:if="${ user.username } == ${ #authentication.name }" >
           <img onclick="document.getElementById('imageModal').style.display='block'" src="/images/im.png" />
       </span>


       <!-- file modal 내용 일부 -->
       <div class="modal-body">
          <input type="hidden" id="id" name="id" th:value="${ user.id }"/><input type="file" name="file" id="file"/>
       </div>
       <!-- 프로필 사진 변경 버튼 -->
       <div class="modal-footer"><button type="button" id="btnProfileUpdate" class="btn btn-primary">수정하기</button></div>

 ```
<br>

+ #### <div id="pic2">btnProfileUpdate 이벤트 리스너 처리</div>
> imageUpload.js 
 ```javascript

    // 프로필 사진 변경 버튼 클릭 이벤트 리스너 등록
     btnProfileUpdate.addEventListener('click', e => {  

            // fileInput창을 찾는다.
            const fileInput = document.querySelector('input[name="file"]');

            // fileInput창의 file을 가져와서 file 변수에 담는다.
            const file = fileInput.files[0];  

            // file 데이터를 전송하기 위해 formData 생성하고
            const formData = new FormData();

            // file을 formData에 추가한다.
            formData.append('file', file);   
       
            document.getElementById('imageModal').style.display = 'none';

            // Axios POST 요청으로 formData를 전달하여 upload 메서드 호출
            axios.post('/submit/image', formData)  
                 .then(response => { 
                                 getImage();    
                                 console.log(response);
                  }).catch(err => { console.log(err) })
      });
```
<br>

+ #### <div id="pic3">upload 메서드: 전달받은 이미지 파일을 외부 경로 디렉토리에 저장하는 함수</div>
> ImageUploadController
```java

     @PostMapping("/submit/image")
     public ResponseEntity<Integer> upload(@AuthenticationPrincipal UserSecurityDto userSecurityDto, MultipartFile file) 
                throws IllegalStateException, IOException {

              // 식별자 생성: 파일 이름 중복 방지하기 위해
              UUID uuid = UUID.randomUUID();

              // 원본 파일 이름 앞에 식별자(UUID)를 추가한 새로운 파일 이름(fileName) 생성한다.
              String fileName = uuid + "_" + file.getOriginalFilename();

              // 파일이 저장될 경로와 fileName을 담아 File 객체를 생성한다.
              File saveFile = new File(imageFilePath, fileName);

              // 파일을 지정된 외부 디렉토리에 저장
              file.transferTo(saveFile);

              // 프로필 사진이 변경되었기 때문에, 유저의 프사 fileName도 바꿔준다. 
              User user = userRepository.findById(userSecurityDto.getId()).get();
              user.setFileName(fileName);
              userRepository.save(user);
        
              return ResponseEntity.ok(1);
    }  
```
<br>

+ #### <div id="pic4">getImage(): 프로필 사진 요청하는 함수</div>
> imageUpload.jsㅇ 
```javascript

     function getImage(){   
          const id = document.querySelector('#id').value;
          const profileImageDiv = document.querySelector('#profileImageDiv');
     
          axios.get('/user/fileName/' + id)  // 유저 id 전송하고, 해당 유저의 프로필 fileName을 요청
               .then(response => {

                      // fileName 전달하여 해당 파일 반환 요청 
                      let img = `<img src="/api/view/${response.data}" width=200px; />`

                      // 반환받은 이미지 파일을 profileImageDiv 추가해서 보여준다.
                      profileImageDiv.innerHTML = img;

              }).catch(err => {  console.log(err)  })              
    }

```
<br>

+ #### <div id="pic5">viewFile 메서드: fileName을 전달받아 로컬 폴더에 저장된 해당 파일을 찾아 반환한다.</div>
> ImageUploadController
```java

    @GetMapping("/user/fileName/{id}")
    public ResponseEntity<String> getProfileImage(@PathVariable Integer id){
            User u =userService.read(id);
            return ResponseEntity.ok(u.getFileName());
    }

    
    @GetMapping("/api/view/{fileName}")
    public ResponseEntity<Resource> viewFile(@PathVariable String fileName) {

            // 파일이 저장된 경로와 전달 받은 fileName을 결합해서 실제 파일 객체를 생성 
            File file = new File(imageFilePath, fileName);
        
            String contentType = null;
            try {
                    // 해당 파일의 MIME 타입(파일 유형)
                    contentType = Files.probeContentType(file.toPath());
            } catch (IOException e) {
                    e.printStackTrace();
            }

            // 해당 파일의 contentType 정보를 HTTP 응답 헤더에 추가한다.
            // 브라우저가 해당 파일을 파악하여 제대로 반환할 수 있도록
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", contentType);
            Resource resource = new FileSystemResource(file);
        
            return ResponseEntity.ok().headers(headers).body(resource);
    }
```
<br><br>   
     
<br>

## <div id="notice">💡 **알림 (Notice)**</div>
**새댓글 알림** 도서 리뷰 포스트에 새 댓글 달리면 알림 받을 수 있음<br>
**키워드 알림** 중고장터에 원하는 키워드가 포함된 새 판매 글이 등록되면 알림 받을 수 있음<br><br>

+ #### <div id="sec1">테이블 생성 및 도메인 컬럼 추가</div>
<img width="650" alt="노티스" src="https://github.com/user-attachments/assets/adfb5f56-863b-4870-ac0e-8b7edd597c1f"><br><br>

+ #### <div id="sec2">알림 생성</div>
  ##### ◽ 새 댓글 알림
  
  ###### &nbsp;&nbsp;댓글 작성 버튼 클릭 시 생성 됨

  > postReply.js
  ```javaScript

    //댓글 작성 확인 버튼
    const btnReplyRegister = document.querySelector('#btnReplyRegister');
    btnReplyRegister.addEventListener('click', registerNewReply);

    function registerNewReply() {  
          axios.post('/api/reply', data)
               .then(response => {
  
                      // 새 댓글 알림 생성 함수 추가 
                      newReplyNotion(response.data);
   
              }.catch(error => {  console.log(error);  });
        }
  
   ```
   <br><br>

   ##### ◽ 키워드 알림
   ##### 중고책 판매글 작성되면, 알림 받기 등록된 전체 키워드 리스트에서 해당 도서와 일치하는 항목 있는지 확인하고,<br> 있다면 키워드 알림 생성
   ###### 새 글 작성 버튼 이벤트 리스너에 checkBookId(bookId,usedBookId) 함수 추가
     > marketCreate.js
     ```javaScript
       
	btnSubmit.addEventListener('click', function () {   // 새 글 작성 버튼 이벤트 리스너

               const result = confirm('등록하시겠습니까?');
              if (result) {
                  document.querySelector('#formCreate').submit();
                  formCreate.action = '/market/create';
                  formCreate.method = 'post';
                  formCreate.submit();  
  
                  checkBookId(bookId,usedBookId);   // 생성해야 할 키워드 알림이 있는지 체크
              }
    
        });

        function checkBookId(bookId,usedBookId) {
             const data = {     bookId : bookId,             // 새 글의 id(usedBookId)와 판매하는 책의 bookId 전달 
                            usedBookId : usedBookId    }
             axios.post('/notice/check', data)
                  .then(response => {  console.log('성공')  })
                  .catch(err => {  alert(err)  });
        };
     
     ```
     <br>
  
     >  NoticeRestController
     ```java
        
        @PostMapping("/notice/check")
        public ResponseEntity<Integer> checkContainBookId(@RequestBody NoticeDto noticeDto){
        
             List<User> users = userService.read();  // 전수 조사
             for (User u : users) {
                if(u.getNoticeBookId() == noticeDto.getBookId()) { // 일치하는 bookId(키워드)가 있으면 키워드 알림 생성
                      NoticeDto dto = NoticeDto.builder().userId(u.getId()).bookId(noticeDto.getBookId()).usedBookId(noticeDto.getUsedBookId()).build();
                      return ResponseEntity.ok(noticeService.create(dto));
                } 
             } 
             return ResponseEntity.ok(1);
        }
     ```


     ###### &nbsp;&nbsp; 알림 받을 키워드 등록
     ###### &nbsp;&nbsp;&nbsp; 검색 결과 페이지에 사용자가 입력한 키워드가 포함된 추천 도서 리스트를 제공하여 키워드 알림 등록을 유도한다.  
     ###### &nbsp;&nbsp;&nbsp; 추천 리스트에서 원하는 항목을 클릭하면 알림 받을 키워드(bookId)로 등록 할 수 있다.

     > MarketController
     ```java
        @GetMapping("/mainSearch") 
        public void mainSearch(@AuthenticationPrincipal UserSecurityDto userDto ,String region, String mainKeyword,
                  Model model, String orderSlt , String status) {

                  // 이런 중고책 찾으세요? 키워드 추천 리스트 제공
                  List<Book> list4 = bookService.searchByBookName(mainKeyword); 
                  model.addAttribute("list4", list4);      
        }    
     ```
     
     > BookRepository
     ```java
              // 제목에 사용자가 검색한 키워드가 포함된 책 4권만 반환하는 리스트
              List<Book> findTop4ByBookNameIgnoreCaseContaining(String Keyword);
     ```
     <br>
  
     ###### 이런 책 찾으세요?
   이미지로

     > marketSearch.html
     ```html
               <div style="display: inline-block;"><!-- 키워드 등록 버튼 --> 
                  <span class="m-1" onclick="register(event);"><i class="bi bi-hand-index-fill" style="font-size: 21px;"></i></span>
               </div>
            

               <script>
                   function register(event) {  // 키워드 등록 함수
                       const bookId = document.querySelector('#b-Id').value;
                       axios.get('/register/notice/'+bookId)
                            .then(response => {  alert('알림 등록 완료!');  })
                            .catch(err =>{  console.log(err);  });
                    }
               </script>
     ```
     <br>
     <br><br>
 
 + #### <div id="sec3">알림 표시</div>

   ##### ◽ 상단바에 알림 버튼 추가
   > layout.html
   ```html
     
       <!-- 상단바 로그인 한 상태 -->
       <th:block sec:authorize="isAuthenticated()">
       <input type="hidden" id="userId" th:value="${ userId }"/>

          <!-- 알림 버튼 -->
          <div class="w3-dropdown-hover w3-bar-item w3-right">
             <button class="w3-button" id="btnAlarm" style="color:white; margin-top:8px; margin-right:30px;">
   
                 <!-- 알림 버튼 우상단 빨간 뱃지 -->
                 <i class="fa fa-bell-o"></i>
                 <span class="position-absolute top-10 right-10 translate-middle badge rounded-pill bg-danger" style="width: 24px;  height: 24px;">
                    <!-- 뱃지 가운데 알림 갯수 카운트하여 표시 -->
                    <span id="noticeCount" class="position-absolute top-50 start-50 translate-middle" style="transform: translate(-50%, -50%); font-size: 15px;"></span>
                 </span>
   
             </button>
             <div class="w3-dropdown-content w3-card-4 w3-bar-block mb-2" style="top:66px; right:46px;">
                 <!-- 로그인 유저 알림 리스트 보여줄 영역 -->
                 <div id="divNotices" class="notices"></div>
             </div>
          </div>
            
        </th:block>

        <!-- 노티스 자바스크립트 -->
        <script th:src="@{ /js/notice.js }"></script>
   
     ```
     <br>
     
      ##### ◽ 로그인 사용자의 알림 갯수 카운트해서 알림 버튼 우상단 뱃지에 알림 갯수 표시
      ##### ◽ 로그인 사용자의 전체 알림 리스트를 drop-down 보여줌
     
     > notice.js
     ```javaScript

        // 로그인 한 유저가 있으면, 유저의 id를 전달하여 해당 유저의 알림 리스트를 리턴 받는다.
      
        function updateNoticeList(data){
     
             // 알림 갯수 count -> 뱃지에 알림 갯수 표시
             const noticeCount = document.querySelector('#noticeCount');
             let count = '';
             count += '<span style="color: white;">'+ data.length +'</span>';
             noticeCount.innerHTML = count;


             // 로그인 한 사용자의 전체 알림 리스트 - drop down으로 보여줄 리스트
             const divNotices = document.querySelector('#divNotices');
             let str ='';
        
             for (let x of data){
     
                if(x.replyId) {  // 새 댓글 알림인 경우
                   str +=`<div><a style="font-size: 17px; text-align:left; padding-top:15px; color:#708090;" class="w3-bar-item w3-button"`
                       <!-- 새 댓글 알림 클릭하면 해당 댓글로 이동. 알림 클릭시 확인한 것으로 간주하여 알림 삭제. -->
                       + `onclick="deleteNotice();" a href="/post/detail?postId=${ x.postId }&bookId=${ x.bookId }&replyId=${ x.replyId }">`
                       + '<input type="hidden" id="noticeId"  value="'+ x.noticeId +'" />'
                       + '내블로그) <img class="rounded-circle m-1" width="30" height="30" src="' + x.userImage + '" />'
                       + `<span class="under-line"><span class="fw-bold">${x.nickName}</span>님의 새 댓글!</span>`
                       + '</a></div>';
                }
                if(x.usedBookId {  // 키워드 알림인 경우
                   str +=`<div><a style="font-size: 17px; text-align:left; padding-top:15px; color:#708090;" class="w3-bar-item w3-button"`
                       <!-- 새 글 키워드 알림 클릭하면 해당 글로 이동. 알림 클릭시 확인한 것으로 간주하여 알림 삭제. -->
                       + `onclick="deleteNotice();" a href=" /market/detail?usedBookId=${ x.usedBookId }">`
                       + '<input type="hidden" id="noticeId"  value="'+ x.noticeId +'" />'
                       + '부끄장터) <img class="rounded-circle m-1" width="30" height="30" src="' + x.bookImage + '" />'
                       + `<span class="fw-bold">${x.bookName}</span> 새 판매글!`
                       + '</a></div>';
                }
     
             }
             divNotices.innerHTML = str;
        }
     ```
     <br>

     > NoticeRestController
     ```java
        // (예진) userId(postWriter/subscribedBookId) 알림 리스트(notice list) 불러오기
        @GetMapping("/showNotice/{userId}")
        public ResponseEntity<List<NoticeDto>> showAllNotices(@PathVariable Integer userId) {
        
             List<NoticeDto> list =noticeService.readNotices(userId);
             return ResponseEntity.ok(list);
       }
     ```
     <br>
     
     > NoticeService
     ```java
        public List<NoticeDto> readNotices(Integer userId) {  // 알림 받을 userId
             List<Notices> list = noticeRepository.findByUserIdOrderByNoticeIdDesc(userId);
             List<NoticeDto> noticeList = new ArrayList<>();
      
             for (Notices n : list) {
                 if(n.getUsedBookId() == null) {
                    PostReply r = replyService.readRep(n.getReplyId());
           
                    NoticeDto dto= NoticeDto.builder().noticeId(n.getNoticeId())
                         .postId(n.getPostId()).bookId(n.getBookId()).userId(n.getUserId()).replyId(n.getReplyId())
                         .userImage(r.getUser().getUserImage()).nickName(r.getUser().getNickName()).build();           
            
                    noticeList.add(dto);
                 } else {
                    UsedBook ub = usedBookService.read(n.getUsedBookId());
                    Book b = bookService.read(n.getBookId());
                
                    NoticeDto dto= NoticeDto.builder().noticeId(n.getNoticeId())
                         .bookId(n.getBookId()).userId(n.getUserId()).usedBookId(n.getUsedBookId())
                         .title(ub.getTitle()).bookName(b.getBookName()).bookImage(b.getBookImage()).build();
                
                    noticeList.add(dto);
                 }
             }
             return noticeList;   // 생성된 시간 순서대로 하나의 알람 리스트 만들어서 넘김
        } 
     ```

     <br>
     
 + #### <div id="sec4">알림 확인 (= 알림 삭제)</div>
   <h5>알림 클릭 -> 해당 댓글 또는 판매 글로 이동 -> 알림 확인한 것으로 간주하고 알림 삭제</h5>  
   <h6>새 댓글 알림 클릭해서 이동하면, 해당 댓글 백그라운드 컬러로 표시 -> 댓글 배경 클릭시 백그라운드 컬러 효과 사라짐</h6>

     > notice.js
     ```javaScript
        function updateNoticeList(data){
           for (let x of data){
               if(x.replyId) {  
                   str += `onclick="deleteNotice();" a href="/post/detail?postId=${ x.postId }&bookId=${ x.bookId }&replyId=${ x.replyId }">`
                if(x.usedBookId {  
                   str += `onclick="deleteNotice();" a href=" /market/detail?usedBookId=${ x.usedBookId }">`
                }
            }
        }
  
   
        // (예진) 알림 클릭하면 알림 확인한 것으로 보고 notice 삭제
        function deleteNotice() {
             const noticeId = document.querySelector('#noticeId').value;
             axios.delete('/notice/delete/'+ noticeId)
                  .then(response => { console.log('삭제성공'); })
                  .catch(err =>{ console.log(err); });
        };
     ```

     > postReply.js
     ```javaScript
     
        // 댓글 목록 함수
        function readAllReplies(){
            axios.get('/api/reply/all/' + postId)  
                 .then(response => { updateReplyList(response.data) })
                 .catch(err => { console.log(err) });
        }    

    
        function updateReplyList(data){
            
            if(r.replyId == repId) {  // 새 댓글 -> 백그라운드 컬러 효과
               str +='<div class="bgColor" id="bgColorBtn" style="background-color: #e6f2ff;">';  
            }
            if(r.replyId != repId) {   // 새 댓글이 아닌
               str +='<div class="bgColor" id="bgColorBtn">';
            }      
                    // (중략)
      

            // 새 댓글에 준 백그라운드 컬러 -> 한 번 클릭하면 없어짐
            const bg = document.querySelector('.bgColor');
        
            bg.addEventListener('click', function(){
              const divBg = document.getElementById('bgColorBtn');
              divBg.style.backgroundColor = 'white';
              divBg.removeAttribute('class');
        });
      
     ```

     

 + #### 움짤
 <div align="center"><img src="https://github.com/epepssp/Booque-ver2/assets/118948099/a81cd3f7-308d-4b45-abe3-a84ad1c32283" height="400" alt="댓글알림"></div>

 <br>
 <div align="center"><img src="https://github.com/epepssp/Booque-ver2/assets/118948099/ca090a59-a4e5-47f5-bf17-c18ada6ceba1" height="400" alt="키워드알림"></div>


