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
        console.log("tempImage: id", id);
        
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
