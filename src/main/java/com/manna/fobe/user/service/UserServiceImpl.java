package com.manna.fobe.user.service;

import com.manna.fobe.common.exception.BizRuntimeException;
import com.manna.fobe.common.utils.S3Utils;
import com.manna.fobe.user.dto.LoginRequestDto;
import com.manna.fobe.user.dto.SignupRequestDto;
import com.manna.fobe.user.dto.Tokens;
import com.manna.fobe.user.dto.UpdateUserRequestDto;
import com.manna.fobe.user.entity.User;
import com.manna.fobe.user.repository.UserRepository;
import com.manna.fobe.common.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.springframework.dao.DataAccessException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final S3Utils s3Utils;

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
        System.out.println("updateUserRequestDto = " + updateUserRequestDto);
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
                String avatarUrl = createAvatar(updateUserRequestDto);
                user.setImageUri(avatarUrl);
            }

            if (updateUserRequestDto.getImageUri() != null) {
                user.setImageUri(updateUserRequestDto.getImageUri());
            }
            if (updateUserRequestDto.getHatId() != null) {
                user.setHatId(updateUserRequestDto.getHatId());
            }
            if (updateUserRequestDto.getHandId() != null) {
                user.setHandId(updateUserRequestDto.getHandId());
            }
            if (updateUserRequestDto.getSkinId() != null) {
                user.setSkinId(updateUserRequestDto.getSkinId());
            }
            if (updateUserRequestDto.getTopId() != null) {
                user.setTopId(updateUserRequestDto.getTopId());
            }
            if (updateUserRequestDto.getFaceId() != null) {
                user.setFaceId(updateUserRequestDto.getFaceId());
            }
            if (updateUserRequestDto.getBottomId() != null) {
                user.setBottomId(updateUserRequestDto.getBottomId());
            }
            if (updateUserRequestDto.getBackground() != null) {
                user.setBackground(updateUserRequestDto.getBackground());
            }

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

    private String createAvatar(UpdateUserRequestDto dto) {
        try {
            BufferedImage baseImage = new BufferedImage(230, 230, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = baseImage.createGraphics();
            g2d.setBackground(new Color(255, 255, 255, 255));
            g2d.clearRect(0, 0, 230, 230);

            if (dto.getSkinId() != null && !dto.getSkinId().isEmpty()) {
                String skinUrl = "https://" + s3Utils.getS3Properties().getBucketName() + ".s3." +
                        s3Utils.getS3Properties().getRegion() + ".amazonaws.com/assets/avatar/items/skins/" + dto.getSkinId() + ".svg";
                drawImageFromUrl(g2d, skinUrl);
            }

            String frameUrl = "https://" + s3Utils.getS3Properties().getBucketName() + ".s3." +
                    s3Utils.getS3Properties().getRegion() + ".amazonaws.com/assets/avatar/default/frame.svg";
            drawImageFromUrl(g2d, frameUrl);

            if (dto.getHandId() != null && !dto.getHandId().isEmpty()) {
                String handUrl = "https://" + s3Utils.getS3Properties().getBucketName() + ".s3." +
                        s3Utils.getS3Properties().getRegion() + ".amazonaws.com/assets/avatar/items/hands/" + dto.getHandId() + ".png";
                drawImageFromUrl(g2d, handUrl);
            }
            if (dto.getBottomId() != null && !dto.getBottomId().isEmpty()) {
                String bottomUrl = "https://" + s3Utils.getS3Properties().getBucketName() + ".s3." +
                        s3Utils.getS3Properties().getRegion() + ".amazonaws.com/assets/avatar/items/bottoms/" + dto.getBottomId() + ".svg";
                drawImageFromUrl(g2d, bottomUrl);
            }
            if (dto.getTopId() != null && !dto.getTopId().isEmpty()) {
                String topUrl = "https://" + s3Utils.getS3Properties().getBucketName() + ".s3." +
                        s3Utils.getS3Properties().getRegion() + ".amazonaws.com/assets/avatar/items/tops/" + dto.getTopId() + ".svg";
                drawImageFromUrl(g2d, topUrl);
            }
            if (dto.getFaceId() != null && !dto.getFaceId().isEmpty()) {
                String faceUrl = "https://" + s3Utils.getS3Properties().getBucketName() + ".s3." +
                        s3Utils.getS3Properties().getRegion() + ".amazonaws.com/assets/avatar/items/faces/" + dto.getFaceId() + ".svg";
                drawImageFromUrl(g2d, faceUrl);
            }
            if (dto.getHatId() != null && !dto.getHatId().isEmpty()) {
                String hatUrl = "https://" + s3Utils.getS3Properties().getBucketName() + ".s3." +
                        s3Utils.getS3Properties().getRegion() + ".amazonaws.com/assets/avatar/items/hats/" + dto.getHatId() + ".svg";
                drawImageFromUrl(g2d, hatUrl);
            }

            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(baseImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            MultipartFile multipartFile = new MockMultipartFile(
                    "avatar",
                    "avatar.png",
                    "image/png",
                    imageBytes
            );

            String avatarUrl = s3Utils.uploadFile(multipartFile, "assets/profiles");
            return avatarUrl;
        } catch (IOException e) {
            log.error("아바타 생성 및 S3 업로드 중 IO 오류 발생", e);
            throw new BizRuntimeException("아바타 생성 및 업로드 중 오류가 발생했습니다.", e);
        }
    }

    private void drawImageFromUrl(Graphics2D g2d, String url) throws IOException {
        try {
            if (url.endsWith(".svg")) {
                BufferedImage layerImage = readSvg(url);
                if (layerImage != null) {
                    g2d.drawImage(layerImage, 0, 0, 230, 230, null);
                } else {
                    log.error("SVG 이미지를 로드할 수 없습니다: {}", url);
                }
            } else {
                java.net.URL imageUrl = new java.net.URL(url);
                BufferedImage layerImage = ImageIO.read(imageUrl);
                if (layerImage != null) {
                    g2d.drawImage(layerImage, 0, 0, 230, 230, null);
                } else {
                    log.error("이미지를 로드할 수 없습니다: {}", url);
                }
            }
        } catch (TranscoderException e) {
            log.error("SVG 이미지 변환 실패: {}", url, e);
        } catch (Exception e) {
            log.error("이미지 로드 실패: {}", url, e);
        }
    }

    private BufferedImage readSvg(String url) throws IOException, TranscoderException {
        TranscoderInput input = new TranscoderInput(url);
        BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
        transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, 230f);
        transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, 230f);
        transcoder.transcode(input, null);
        return transcoder.getBufferedImage();
    }

    static class BufferedImageTranscoder extends ImageTranscoder {
        private BufferedImage img;

        @Override
        public BufferedImage createImage(int width, int height) {
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        public void writeImage(BufferedImage img, TranscoderOutput output) {
            this.img = img;
        }

        public BufferedImage getBufferedImage() {
            return img;
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