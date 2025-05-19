package com.manna.fobe.user.service;

import com.manna.fobe.avatar.service.AvatarService;
import com.manna.fobe.common.exception.BizRuntimeException;
import com.manna.fobe.common.utils.JwtUtil;
import com.manna.fobe.user.dto.LoginRequestDto;
import com.manna.fobe.user.dto.SignupRequestDto;
import com.manna.fobe.user.dto.Tokens;
import com.manna.fobe.user.dto.UpdateUserRequestDto;
import com.manna.fobe.user.entity.User;
import com.manna.fobe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.manna.fobe.common.utils.JwtUtil.REFRESH_TOKEN_EXPIRE_TIME;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AvatarService avatarService;
    private final RedisTemplate<String, Object> redisTemplate;

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
            user.setPassword(passwordEncoder.encode(password));
            user.setNickname("익명" + (userRepository.count() + 1));

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

            if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.get().getPassword())) {
                throw new BizRuntimeException("잘못된 비밀번호입니다.");
            }

            String accessToken = jwtUtil.createToken(user.get().getId(), user.get().getRole().toString());
            String refreshToken = jwtUtil.createRefreshToken(user.get().getId(), user.get().getRole().toString());

            String key = "refreshToken:" + user.get().getId();
            redisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);

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
            if (!jwtUtil.validateToken(refreshToken)) {
                throw new BizRuntimeException("유효하지 않은 리프레시 토큰입니다.");
            }

            int userId = jwtUtil.getUserIdFromToken(refreshToken);
            String role = jwtUtil.getRoleFromToken(refreshToken);

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

    @Override
    public User getMyProfile(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BizRuntimeException("해당 유저를 찾을 수 없습니다. userId=" + userId));
    }

    @Override
    public User updateProfile(int userId, UpdateUserRequestDto updateUserRequestDto) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BizRuntimeException("해당 유저를 찾을 수 없습니다. userId=" + userId));

            if (updateUserRequestDto.getNickname() != null) {
                Optional<User> existingUser = userRepository.findByNickname(updateUserRequestDto.getNickname());
                if (existingUser.isPresent() && existingUser.get().getId() != userId) {
                    throw new BizRuntimeException("이미 사용 중인 닉네임입니다.");
                }
                user.setNickname(updateUserRequestDto.getNickname());
            }

            boolean avatarChanged =
                    (updateUserRequestDto.getHatId() != null && !updateUserRequestDto.getHatId().equals(user.getHatId())) ||
                            (updateUserRequestDto.getHandId() != null && !updateUserRequestDto.getHandId().equals(user.getHandId())) ||
                            (updateUserRequestDto.getSkinId() != null && !updateUserRequestDto.getSkinId().equals(user.getSkinId())) ||
                            (updateUserRequestDto.getTopId() != null && !updateUserRequestDto.getTopId().equals(user.getTopId())) ||
                            (updateUserRequestDto.getBottomId() != null && !updateUserRequestDto.getBottomId().equals(user.getBottomId())) ||
                            (updateUserRequestDto.getFaceId() != null && !updateUserRequestDto.getFaceId().equals(user.getFaceId()));

            if (avatarChanged) {
                String avatarUrl = avatarService.createAvatar(updateUserRequestDto);
                user.setImageUri(avatarUrl);
            }

            if (updateUserRequestDto.getImageUri() != null) user.setImageUri(updateUserRequestDto.getImageUri());
            if (updateUserRequestDto.getHatId() != null) user.setHatId(updateUserRequestDto.getHatId());
            if (updateUserRequestDto.getHandId() != null) user.setHandId(updateUserRequestDto.getHandId());
            if (updateUserRequestDto.getSkinId() != null) user.setSkinId(updateUserRequestDto.getSkinId());
            if (updateUserRequestDto.getTopId() != null) user.setTopId(updateUserRequestDto.getTopId());
            if (updateUserRequestDto.getFaceId() != null) user.setFaceId(updateUserRequestDto.getFaceId());
            if (updateUserRequestDto.getBottomId() != null) user.setBottomId(updateUserRequestDto.getBottomId());
            if (updateUserRequestDto.getBackground() != null) user.setBackground(updateUserRequestDto.getBackground());

            return saveUser(user);
        } catch (DataAccessException e) {
            log.error("프로필 수정 중 데이터베이스 오류 발생", e);
            throw new BizRuntimeException("프로필 수정 중 데이터베이스 오류가 발생했습니다.", e);
        } catch (BizRuntimeException e) {
            log.error("프로필 수정 중 비즈니스 로직 오류 발생", e);
            throw e;
        } catch (Exception e) {
            log.error("프로필 수정 중 예기치 않은 오류 발생", e);
            throw new BizRuntimeException("프로필 수정 중 예기치 않은 오류가 발생했습니다.", e);
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
}