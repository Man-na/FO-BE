package com.manna.fobe.user.service;

import com.manna.fobe.common.exception.BizRuntimeException;
import com.manna.fobe.user.dto.LoginRequestDto;
import com.manna.fobe.user.dto.SignupRequestDto;
import com.manna.fobe.user.dto.Tokens;
import com.manna.fobe.user.entity.User;
import com.manna.fobe.user.repository.UserRepository;
import com.manna.fobe.user.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User signup(SignupRequestDto createUserRequestDto) {
        try {
            if (userRepository.findByEmail(createUserRequestDto.getEmail()).isPresent()) {
                throw new BizRuntimeException("이미 사용중인 이메일입니다.");
            }

            String password = createUserRequestDto.getPassword();
            if (!isPasswordValid(password)) {
                throw new BizRuntimeException("정책에 어긋난 비밀번호입니다.");
            }

            User user = new User();
            user.setEmail(createUserRequestDto.getEmail());
            user.setPassword(passwordEncoder.encode(createUserRequestDto.getPassword()));

            return saveUser(user);
        } catch (DataAccessException e) {
            log.error("회원가입 처리 중 데이터베이스 오류 발생", e);
            throw new BizRuntimeException("회원가입 처리 중 데이터베이스 오류가 발생했습니다.", e);
        } catch (BizRuntimeException e) {
            log.error("회원가입 처리 중 비즈니스 로직 오류 발생", e);
            throw e;
        } catch (Exception e) {
            log.error("회원가입 처리 중 예기치 않은 오류 발생", e);
            throw new BizRuntimeException("회원가입 처리 중 예기치 않은 오류가 발생했습니다.", e);
        }
    }

    @Override
    public Tokens login(LoginRequestDto loginRequestDto) {
        try {
            Optional<User> user = userRepository.findByEmail(loginRequestDto.getEmail());
            if (user.isEmpty()) {
                throw new BizRuntimeException("존재하지 않는 유저입니다.");
            }

            // 패스워드 매칭
            if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.get().getPassword())) {
                throw new BizRuntimeException("잘못된 비밀번호입니다.");
            }

            // AccessToken, RefreshToken 생성
            String accessToken = jwtUtil.createToken(user.get().getUserId(), user.get().getRole().toString());
            String refreshToken = jwtUtil.createRefreshToken(user.get().getUserId(), user.get().getRole().toString());

            // Tokens DTO 로 묶어서 반환
            return new Tokens(accessToken, refreshToken);

        } catch (DataAccessException e) {
            log.error("로그인 처리 중 데이터베이스 오류 발생", e);
            throw new BizRuntimeException("로그인 처리 중 데이터베이스 오류가 발생했습니다.", e);
        } catch (BizRuntimeException e) {
            log.error("로그인 처리 중 비즈니스 로직 오류 발생", e);
            throw e;
        } catch (Exception e) {
            log.error("로그인 처리 중 예기치 않은 오류 발생", e);
            throw new BizRuntimeException("로그인 처리 중 예기치 않은 오류가 발생했습니다.", e);
        }
    }

    @Override
    public Tokens refresh(String refreshToken) {
        try {
            // refreshToken 유효성 검증
            if (!jwtUtil.validateToken(refreshToken)) {
                throw new BizRuntimeException("유효하지 않은 리프레시 토큰입니다.");
            }

            // 토큰에서 userId, role 파싱
            int userId = jwtUtil.getUserIdFromToken(refreshToken);
            String role = jwtUtil.getRoleFromToken(refreshToken);

            // 새 AccessToken 발급
            String newAccessToken = jwtUtil.createToken(userId, role);
            String newRefreshToken = jwtUtil.createRefreshToken(userId, role);

            return new Tokens(newAccessToken, newRefreshToken);

        } catch (BizRuntimeException e) {
            log.error("토큰 재발급 중 비즈니스 로직 오류 발생", e);
            throw e;
        } catch (Exception e) {
            log.error("토큰 재발급 중 예기치 않은 오류 발생", e);
            throw new BizRuntimeException("토큰 재발급 중 예기치 않은 오류가 발생했습니다.", e);
        }
    }


    private boolean isPasswordValid(String password) {
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*?_])[A-Za-z\\d!@#$%^&*?_]{8,16}$";
        return password.matches(passwordPattern);
    }

    private User saveUser(User user) {
        try {
            return userRepository.save(user);
        } catch (DataAccessException e) {
            log.error("회원 저장 중 데이터베이스 오류 발생", e);
            throw new BizRuntimeException("회원 저장에 실패했습니다.", e);
        } catch (Exception e) {
            log.error("회원 저장 중 예기치 않은 오류 발생", e);
            throw new BizRuntimeException("회원 저장 중 예기치 않은 오류가 발생했습니다.", e);
        }
    }

    /**
     * userId로 DB에서 유저 조회
     */
    public User getMyProfile(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BizRuntimeException("해당 유저를 찾을 수 없습니다. userId=" + userId));
    }
}
