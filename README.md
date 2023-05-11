# 📚Booque ver2

## 개요
**일정** 2023년 1월 11일 ~ 2023년 2월 16일<br>
**인원** 6인 팀 프로젝트

## 사용 기술 및 개발환경
+ Java
+ Spring Boot
+ HTML
+ CSS
+ JavaScript

## 구현 기능(담당)
+ 외부경로 프로필 이미지 업로드

```java
> SecurityConfig.java 추가

 @Bean  // 로컬 폴터 이미지 불러오기 위한 config 추가
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()
                    );
        };
    }
```

> application.properties 외부경로 위치 추가

``` application.properties
site.book.upload.path=E:\\study\\images
```

> PostController.java 일부

```java
    @PostMapping("/profile/imageUpdate")    // (예진) 프로필 이미지 업로드
    public String profileImageUpdate(Integer id, MultipartFile file, HttpServletRequest request) throws Exception{ 
        
        userService.write(id, file);

        // Post, MyPage등 여러 페이지에서 프로필 이미지 업로드 할 수 있음 - 현재 페이지로 리다이렉트 하기 위해
        String referer = request.getHeader("referer");  // 현재 페이지 주소
        String urlTemp = referer.toString().substring(21);  // localhost:8888 뒷 부분만 잘라냄
      
        return "redirect:"+urlTemp;  // 현재 페이지로 리다이렉트 
    }
```

> UserService.java 일부

```java
 @Value("${site.book.upload.path}") // (예진) 절대 경로(외부 경로) 값 주입
    private String imageFilePath;

     public void write(Integer id, MultipartFile file) throws IllegalStateException, IOException {  // (예진) 프로필 이미지 업로드
  
        UUID uuid = UUID.randomUUID();  // 식별자
        String fileName = uuid + "_" + file.getOriginalFilename();

        File saveFile=new File(imageFilePath, fileName); // saveFile: 파일 껍데기(객체) 생성해서 경로+파일이름 저장
        file.transferTo(saveFile);
        
        User user = userRepository.findById(id).get();
        
        user.setFileName(fileName);
        user.setFilePath(imageFilePath+"/"+fileName);
        user.setUserImage("/view/"+fileName);

        userRepository.save(user);
     }
```

> list.html 일부

```html
  <a th:href="@{ /myPage }"> <!-- 프로필 사진 클릭하면 마이페이지로 이동 -->
      <img th:src="${user.userImage}" width=200px; /><!-- (예진) 프로필 이미지-->
  </a>     

   <!-- (예진) 프로필 사진 업데이트 버튼 -->
   <span th:if="${ user.username } == ${ #authentication.name }" >
          <img onclick="imagePop()" src="/images/im.png" width=22px; align="right" />
   </span>

   <!-- 프사 이미지 변경 모달 -->
        <div class="modal" id ="imageModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                     <div class="modal-header">
                          <h5 class="modal-title">프로필 이미지</h5>
                         <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="profileForm" enctype="multipart/form-data" method="post" action="/post/profile/imageUpdate">
                           <input type="hidden" id="id" name="id" th:value="${ user.id }"/>
                           <input type="file" name="file" id="file"/>
                        </form>
                    </div>
                    <div class="modal-footer">
                         <button type="button" id="btnProfileUpdate" class="btn btn-primary">수정하기</button>
                    </div>
                 </div>
            </div>
        </div>
```

> imageUpload.js 

```javascript
window.addEventListener('DOMContentLoaded', () => {
    
     // (예진) 이미지 업데이트 
     
     tempImage();
    
     function tempImage() {
        
        const id = document.querySelector('#id').value;
 
        axios
        .get('/tempView/' + id)  
        .then(response => { viewImage(response.data) } )
        .catch(err => { console.log(err) })

    };
       
       function viewImage(data) {

        const divProfileImage = document.querySelector('#divProfileImage');
            
        axios
        .get('/view/'+ data.fileName)  
        .then(response => { console.log('성공!!') } )
        .catch(err => { console.log(err) })
        
        let img ='';
        if(data.fileName) {
           img +=  `<img src="/view/${data.fileName}" width=200px />`;
        } else {
           // 설정한 프사 없을 경우 디폴트 이미지
           img +=  `<img src="/view/113163657.jpg" width=200px />`;
        }
        divProfileImage.innerHTML = img;
       
    };     
   
    // profile form HTML 요소를 찾음.
    const profileForm = document.querySelector('#profileForm');

    // 프로필 이미지 변경 버튼 찾아서 이벤트 리스너 등록
    const btnProfileUpdate = document.querySelector('#btnProfileUpdate');
    btnProfileUpdate.addEventListener('click', submitForm);
    
    function submitForm(event){
        event.preventDefault();
        
        const result = confirm('프로필 사진을 변경하시겠습니까?');
        if(result) {
            profileForm.action = '/post/profile/imageUpdate';
            profileForm.method= 'post';
            profileForm.submit();
        }     
    tempImage();     
    }
});
```

+ 알림

> Notices.java 도메인 생성

```java
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name ="NOTICES")
@SequenceGenerator(name = "NOTICES_SEQ_GEN", sequenceName = "NOTICES_SEQ", initialValue = 1, allocationSize = 1)
public class Notices {
    // (예진) 새 댓글 알림, 키워드 알림 구현하기 위한 도메인

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOTICES_SEQ_GEN")
    private Integer noticeId;
    
    // 새 댓글 알림
    private Integer userId;  // postWriter id; => 알림 받을 사람
    private Integer bookId;
    private Integer postId;
    private Integer replyId;
    
    // usedBookId 알림
    private Integer subscribedBookId;  // 유저가 알람 받기로 등록한 BookId
    private Integer usedBookId;  // usedBookPost의 id
}
```

1. 새 댓글 알림

알림 생성

> NoticeRestController.java 

```java
    // (예진) 포스트에 새 댓글이 달리면 알림(notice) 만들어짐
    // notice create
    @PostMapping("/notice")
    public ResponseEntity<Integer> newNotice(@RequestBody NoticeDto dto){
     
       Integer noticeId = noticeService.create(dto);
       
       return ResponseEntity.ok(noticeId);
    }
```

> NoticeService.java 

```java
  // 새 알림 생성
    public Integer create(NoticeDto dto) {
        
        Notices notice = null;
        if(dto.getUsedBookId() == null) {
             notice = Notices.builder()
                .userId(dto.getUserId())
                .bookId(dto.getBookId())
                .postId(dto.getPostId())
                .replyId(dto.getReplyId()).build();
        } else {
            notice = Notices.builder()
            .userId(dto.getUserId())
            .bookId(dto.getBookId())
            .usedBookId(dto.getUsedBookId()).build();
        }
           
        noticeRepository.save(notice);
        return notice.getNoticeId();
    }   
    
    public List<NoticeDto> readNotices(Integer userId) {  // 알림 받을 userId
        
        List<Notices> list = noticeRepository.findByUserIdOrderByNoticeIdDesc(userId);
        List<NoticeDto> noticeList = new ArrayList<>();
      
        for (Notices n : list) {
            
            if(n.getUsedBookId() == null) {
               PostReply r = replyService.readRep(n.getReplyId());
           
               NoticeDto dto= NoticeDto.builder()
                              .noticeId(n.getNoticeId())
                              .postId(n.getPostId())
                              .bookId(n.getBookId())
                              .userId(n.getUserId())
                              .replyId(n.getReplyId())
                              .userImage(r.getUser().getUserImage())
                              .nickName(r.getUser().getNickName())
                               .build();           
            
                noticeList.add(dto);
                
            } else {
                    UsedBook ub = usedBookService.read(n.getUsedBookId());
                    Book b = bookService.read(n.getBookId());
                
                    NoticeDto dto= NoticeDto.builder()
                        .noticeId(n.getNoticeId())
                        .bookId(n.getBookId())
                        .userId(n.getUserId())
                        .usedBookId(n.getUsedBookId())
                        .title(ub.getTitle())
                        .bookName(b.getBookName())
                        .bookImage(b.getBookImage())
                        .build();
                
                    noticeList.add(dto);
            }
            
        }
        
        return noticeList;
    }
```

> postReply.js 일부

```javascript
 // (예진) 새 댓글 작성시 알림(notice) 만들어짐 - 댓글 작성 함수 then에 newReplyNotion 추가
    function newReplyNotion(data){
      
           axios.post('/notice', data)
           .then(response => {
             
           })         
            .catch(error => {
                    console.log(error);
           });
     
    }
```

유저의 알림 리스트 보여주기
> NoticeRestController.java 

```java
  // (예진) userId(postWriter/subscribedBookId) 알림 리스트(notice list) 불러오기
    @GetMapping("/showNotice/{userId}")
    public ResponseEntity<List<NoticeDto>> showAllNotices(@PathVariable Integer userId) {
 
         List<NoticeDto> list =noticeService.readNotices(userId);
       
        return ResponseEntity.ok(list);
    }  
```

> layout.html 일부

```html
  <!-- 알림 버튼 -->
            <div class="w3-dropdown-hover w3-bar-item w3-right">
                <button class="w3-button" id="btnAlarm" style="color:white; margin-top:8px; margin-right:30px;">
                
                    <!-- (예진) 알림 아이콘 오른쪽 상단에 알림 갯수 뜨도록 -->
                    <i class="fa fa-bell-o"></i><span class="position-absolute top-60 start-85 translate-middle badge rounded-pill bg-danger m-1" id="noticeCount"></span>
                </button>

                <input type="hidden" id="userId" th:value="${ userId }"/>
                
                 <div class="w3-dropdown-content w3-card-4 w3-bar-block mb-2" style="top:66px; right:46px;">

                     <!-- (예진) 댓글 알림 리스트 보여줄 영역 -->
                     <div id="divNotices" class="notices"></div>
                 </div>
            </div>
````

> notice.js

```javascript
    const userId = document.querySelector('#userId2').innerText;
    
    if(userId){
         console.log(userId);
         showNotice();      // 로그인 한 유저의 알림 리스트
    }
   
    function showNotice(){
        console.log(userId);
       
        axios
        .get('/showNotice/' + userId)  
        .then(response => { 
            updateNoticeList(response.data) } )
        .catch(err => { console.log(err) });
        
    }    
```

알림 확인 및 삭제

> notice.js

```javascript

    function updateNoticeList(data){
        
         const noticeCount = document.querySelector('#noticeCount');
         let count = '';
         count += '<span style="color: white;">'+ data.length +'</span>';
         noticeCount.innerHTML = count;
        
        const divNotices = document.querySelector('#divNotices');
        let str ='';
        
         for (let x of data){ 
             
            if(x.replyId) {
              str +=`<div><a style="font-size: 17px; text-align:left; padding-top:15px; color:#708090;" class="w3-bar-item w3-button"`
                   // 알림 클릭하면 해당 댓글로 이동함. 
                  + `onclick="deleteNotice();" a href="/post/detail?postId=${ x.postId }&bookId=${ x.bookId }&replyId=${ x.replyId }">`
                  + '<input type="hidden" id="noticeId"  value="'+ x.noticeId +'" />'
                  + '내블로그) <img class="rounded-circle m-1" width="30" height="30" src="' + x.userImage + '" />'
                  + `<span class="under-line"><span class="fw-bold">${x.nickName}</span>님의 새 댓글!</span>`
                  + '</a></div>';
            }

           if(x.usedBookId){
             str +=`<div><a style="font-size: 17px; text-align:left; padding-top:15px; color:#708090;" class="w3-bar-item w3-button"`
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

> postReply.js

```javascript
  function updateReplyList(data){
        const divReplies = document.querySelector('#replies');
        let str = '';

       for (let r of data){
            str += '<div class="card border-dark mb-3 w-100" style="text-align: left;">';
       
            if(r.replyId == repId) {   // (예진) 클릭한 알림에 해당하는 댓글 -> 하늘색 백그라운드 효과로 표시
               str +='<div class="bgColor" id="bgColorBtn" style="background-color: #e6f2ff;">';
            }

            if(r.replyId != repId) {  
               str +='<div class="bgColor" id="bgColorBtn">';
            }      
            str +=`<div class="flex-shrink-0"><a href="/post/list?postWriter=${r.replyWriter}">`
                + '<img class="rounded-circle m-2" width="45" height="45" src="' + r.userImage + '" alt="..." />'
                + `<span class="fw-bold m-2">${r.nickName}</span></a></div>`
                + '<div class="card-body text-dark">'
                + '<p class="card-text">' + r.replyContent + '</p>'
                + '<div><small style="color:gray;"> 작성시간: ' + '<span id="commentDate">' + r.createdTime + '</span>' + '</small></div>'
    //            + '<div><small style="color:gray;"> 수정시간: ' + r.modifiedTime + '</small></div>'
                + '</div>';
                
            if(r.replyWriter == loginUser){
               str += '<div class="card-footer">'
                   + `<button type="button" class="btnModifies btn btn-outline-primary" data-rid="${r.replyId}">수정</button>`
                   + '</div>';
            }
            
            str += '</div>';

            str +='</div>';
         }
        
        divReplies.innerHTML = str;
        
        
        // (예진) 새 댓글에 하늘색 백그라운드 컬러 효과 -> 댓글 클릭시 효과 사라짐
        const bg = document.querySelector('.bgColor');
        
        bg.addEventListener('click', function(){
        const divBg = document.getElementById('bgColorBtn');
        divBg.style.backgroundColor = 'white';
        divBg.removeAttribute('class');
        
    });
```

> NoticeRestController.java

```java
@DeleteMapping("/notice/delete/{noticeId}")
    public void deleteNotice(@PathVariable Integer noticeId){ 
       //알림 클릭하면 확인한 것으로 보고 알림 삭제 

        noticeService.delete(noticeId);
        noticeRepository.deleteById(noticeId);    
   }
```
