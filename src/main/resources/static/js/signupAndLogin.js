document.addEventListener('DOMContentLoaded', () => {
    const toggleFormLink = document.getElementById('toggleForm');
    const loginFormElement = document.getElementById('loginForm');
    const signupFormElement = document.getElementById('signupForm');
    const formTitle = document.getElementById('formTitle');
    const formSwitch = document.getElementById('formSwitch');

    // 폼 전환 핸들러
    function toggleAuthForm(event) {
        event.preventDefault();
        if (loginFormElement.classList.contains('hidden')) {
            // 회원가입 폼이 보이면 -> 로그인 폼 보이기
            loginFormElement.classList.remove('hidden');
            signupFormElement.classList.add('hidden');
            formTitle.textContent = '로그인';
            formSwitch.innerHTML = '계정이 없으신가요? <a id="toggleForm" href="#">회원가입</a>';
        } else {
            // 로그인 폼이 보이면 -> 회원가입 폼 보이기
            loginFormElement.classList.add('hidden');
            signupFormElement.classList.remove('hidden');
            formTitle.textContent = '회원가입';
            formSwitch.innerHTML = '이미 계정이 있으신가요? <a id="toggleForm" href="#">로그인</a>';
        }
        document.getElementById('toggleForm').addEventListener('click', toggleAuthForm);
        loginFormElement.reset && loginFormElement.reset();
        signupFormElement.reset && signupFormElement.reset();
    }

    toggleFormLink.addEventListener('click', toggleAuthForm);

    // 로그인 폼 제출 이벤트 처리 (HTTP 상태 코드 활용)
    loginFormElement.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = document.getElementById('loginEmail').value;
        const password = document.getElementById('loginPassword').value;
        try {
            const response = await fetch('/api/member/login', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({email, password}),
            });
            if (!response.ok) {
                let errorData;
                try {
                    errorData = await response.json();
                } catch {
                    errorData = {message: '로그인 실패'};
                }
                // HTTP 상태 코드에 따른 구체적 처리
                if (response.status === 404) {
                    alert(errorData.message || '등록된 회원 정보가 존재하지 않습니다.');
                } else if (response.status === 400) {
                    alert(errorData.message || '비밀번호가 올바르지 않습니다.');
                } else {
                    alert(errorData.message || `로그인 실패: ${response.status}`);
                }
                return;
            }
            const data = await response.json();
            if (data.success) {
                window.location.href = data.data || '/';
            } else {
                alert(data.message || '로그인 실패');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('서버 연결 실패');
        }
    });

    // 회원가입 폼 제출 이벤트 처리 (HTTP 상태 코드 활용)
    signupFormElement.addEventListener('submit', async (e) => {
        e.preventDefault();
        const nickName = document.getElementById('nickName').value;
        const email = document.getElementById('signupEmail').value;
        const password = document.getElementById('password').value;
        const checkPassword = document.getElementById('checkPassword').value;
        const profileImage = document.getElementById('profileImage').files[0];

        if (password !== checkPassword) {
            alert('비밀번호가 일치하지 않습니다.');
            return;
        }

        const formData = new FormData();
        formData.append('nickName', nickName);
        formData.append('email', email);
        formData.append('password', password);
        formData.append('checkPassword', checkPassword);
        if (profileImage) {
            formData.append('profileImage', profileImage);
        }

        try {
            const response = await fetch('/api/member/signUp', {
                method: 'POST',
                body: formData,
            });
            if (!response.ok) {
                let errorData;
                try {
                    errorData = await response.json();
                } catch {
                    errorData = {message: '회원가입 실패'};
                }
                // 상태 코드에 따른 세분화된 처리
                if (response.status === 409) {
                    // MemberAlreadyExistsException 처리
                    alert(errorData.message || '이미 가입된 이메일입니다.');
                } else if (response.status === 400) {
                    // PasswordConfirmationMisMatch 등의 400 에러 처리
                    if (errorData.message && errorData.message.includes('비밀번호 확인')) {
                        alert(errorData.message || '비밀번호 확인이 일치하지 않습니다.');
                    } else {
                        alert(errorData.message || '회원가입에 실패했습니다.');
                    }
                } else {
                    alert(errorData.message || `회원가입 실패: ${response.status}`);
                }
                return;
            }
            const data = await response.json();
            if (data.success) {
                alert('회원가입 성공!');
                // 가입 성공 시 로그인 폼으로 전환
                toggleAuthForm(e);
            } else {
                alert(data.message || '회원가입 실패');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('서버 연결 실패');
        }
    });

    // 이메일 인증 관련 요소
    const sendCodeBtn = document.getElementById('sendCodeBtn');
    const verifyCodeBtn = document.getElementById('verifyCodeBtn');
    const verificationSection = document.getElementById('verificationSection');
    const signupFields = document.getElementById('signupFields');
    const signupSubmitBtn = document.getElementById('signupSubmitBtn');

    // 엔터키로 인한 의도치 않은 폼 제출 방지
    document.getElementById('signupEmail').addEventListener('keydown', (e) => {
        if (e.key === 'Enter') e.preventDefault();
    });
    document.getElementById('verificationCode').addEventListener('keydown', (e) => {
        if (e.key === 'Enter') e.preventDefault();
    });

    // 인증번호 전송
    sendCodeBtn.addEventListener('click', async () => {
        const email = document.getElementById('signupEmail').value.trim();
        if (!validateEmail(email)) {
            alert('유효한 이메일을 입력해주세요.');
            return;
        }
        try {
            const response = await fetch('/api/email/sendCode', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({email}),
            });
            if (response.status === 409) {
                alert('이미 가입된 이메일입니다.');
                return;
            }
            const data = await response.json();
            if (data.success) {
                verificationSection.classList.remove('hidden');
                sendCodeBtn.disabled = true;
            } else {
                alert(data.message || '인증번호 전송 실패');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('서버 연결 실패');
        }
    });

    // 인증번호 확인
    verifyCodeBtn.addEventListener('click', async () => {
        const email = document.getElementById('signupEmail').value;
        const code = document.getElementById('verificationCode').value;
        try {
            const response = await fetch('/api/email/verifyCode', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({email, code}),
            });
            if (!response.ok) {
                alert(`인증번호 확인 실패: ${response.status}`);
                return;
            }
            const data = await response.json();
            if (data.success) {
                signupFields.classList.remove('hidden');
                verifyCodeBtn.disabled = true;
                signupSubmitBtn.disabled = false;
            } else {
                alert('인증번호가 일치하지 않습니다.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('서버 연결 실패');
        }
    });

    // 이메일 유효성 검사 함수
    function validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }

    // 소셜 로그인 핸들러
    window.socialLogin = function (platform) {
        let loginUrl;
        switch (platform) {
            case 'google':
                loginUrl = '/oauth2/authorization/google';
                break;
            case 'kakao':
                loginUrl = '/oauth2/authorization/kakao';
                break;
            case 'naver':
                loginUrl = '/oauth2/authorization/naver';
                break;
            default:
                alert('지원하지 않는 로그인 방식입니다.');
                return;
        }
        window.location.href = loginUrl;
    };
});
