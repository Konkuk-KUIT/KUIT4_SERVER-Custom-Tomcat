package Controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.io.IOException;
import java.util.Map;

import static Constant.HttpMethodType.*;
import static Constant.Url.INDEX_HTML;
import static Constant.UserQueryKey.*;
import static Constant.UserQueryKey.EMAIL;

public class SignUpController implements Controller{
    private final MemoryUserRepository userRepository = MemoryUserRepository.getInstance();
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if(httpRequest.getMethod().equals(GET.getMessage())){
            handleSignUpGet(httpRequest, httpResponse);
        }
        if(httpRequest.getMethod().equals(POST.getMessage())){
            handleSignUpPost(httpRequest, httpResponse);
        }

    }
    private void handleSignUpGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String requestUrl = httpRequest.getPath();
        String queryString = requestUrl.split("\\?")[1];
        Map<String, String> queryParams = HttpRequestUtils.parseQueryParameter(queryString);        //주어진 쿼리 문자열을 파싱해여 map 형태로 반환

        User user = User.from(queryParams);            //User 객체 생성

        if (userRepository.findUserById(user.getUserId()) == null){
            userRepository.addUser(user);
        }

        httpResponse.redirect(INDEX_HTML.getMessage());
    }

    private void handleSignUpPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
       // User user = User.from(httpRequest);   //httpRequest의 맴버 변수를 꺼내서 User의 생성자로 넣어주는게 좋다는 의미인가?
                                                //파라미터로 httpRequest 하나 넘겨주는게 깔끔한것 같았는데 좋지 않은 방향인듯
        User user = User.from(httpRequest.getBody(USERID.getValue()), httpRequest.getBody(PASSWORD.getValue()),
                                httpRequest.getBody(NAME.getValue()), httpRequest.getBody(EMAIL.getValue()));

        if (userRepository.findUserById(user.getUserId()) == null) {
            userRepository.addUser(user);
        }

        httpResponse.redirect(INDEX_HTML.getMessage());
    }
}
