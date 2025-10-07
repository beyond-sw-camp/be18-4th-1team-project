package com.domino.smerp.user;

import com.domino.smerp.client.Client;
import com.domino.smerp.client.ClientRepository;
import com.domino.smerp.common.dto.PageResponse;
import com.domino.smerp.common.encrypt.SsnEncryptor;
import com.domino.smerp.common.exception.CustomException;
import com.domino.smerp.common.exception.ErrorCode;
import com.domino.smerp.user.dto.request.CreateUserRequest;
import com.domino.smerp.user.dto.request.UpdateUserRequest;
import com.domino.smerp.user.dto.response.UserListResponse;
import com.domino.smerp.user.dto.response.UserResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final SsnEncryptor ssnEncryptor;

    @Override
    @Transactional
    public void createUser(final CreateUserRequest request) {

        String encryptedSsn = ssnEncryptor.encryptSsn(request.getSsn());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new CustomException(ErrorCode.DUPLICATE_PHONE);
        }
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new CustomException(ErrorCode.DUPLICATE_LOGINID);
        }
        if (userRepository.existsBySsn(encryptedSsn)) {
            throw new CustomException(ErrorCode.DUPLICATE_SSN);
        }

        Client client = null;
        if (request.getCompanyName() != null) {
            client = clientRepository.findByCompanyName(request.getCompanyName())
                                     .orElseThrow(
                                         () -> new CustomException(ErrorCode.CLIENT_NOT_FOUND));
        }

        String empNo = generateEmpNo(request.getHireDate());

        User user = User.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .phone(request.getPhone())
                        .address(request.getAddress())
                        .ssn(encryptedSsn)
                        .loginId(request.getLoginId())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .hireDate(request.getHireDate())
                        .fireDate(
                            request.getFireDate() != null ? request.getFireDate() : null)
                        .deptTitle(request.getDeptTitle())
                        .role(request.getRole())
                        .empNo(empNo)
                        .client(client)
                        .build();

        userRepository.save(user);
    }


    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserListResponse> searchUsers(String name, String deptTitle,
        Pageable pageable) {

        BooleanExpression nameCondition =
            (name != null && !name.isEmpty()) ? QUser.user.name.startsWith(name) : null;
        BooleanExpression deptCondition =
            (deptTitle != null && !deptTitle.isEmpty()) ? QUser.user.deptTitle.startsWith(deptTitle)
                : null;
        BooleanExpression condition = null;

        if (nameCondition != null && deptCondition != null) {
            condition = nameCondition.and(deptCondition);
        } else if (nameCondition != null) {
            condition = nameCondition;
        } else if (deptCondition != null) {
            condition = deptCondition;
        }

        Page<User> page = (condition == null) ? userRepository.findAll(pageable)
            : userRepository.findAll(condition, pageable);

        Page<UserListResponse> pageUser = page.map(user -> UserListResponse.builder()
                                                                           .name(user.getName())
                                                                           .email(user.getEmail())
                                                                           .phone(user.getPhone())
                                                                           .address(user.getAddress())
                                                                           .deptTitle(user.getDeptTitle())
                                                                           .role(user.getRole())
                                                                           .empNo(user.getEmpNo())
                                                                           .build());
        return PageResponse.from(pageUser);
    }

    @Override
    @Transactional
    public void deleteUser(final Long userId) {

        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findUserByEnpNo(final String enpNo) {

        User user = userRepository.findByEmpNo(enpNo)
                                  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Client client = user.getClient();

        String decryptSsn = ssnEncryptor.decryptSsn(user.getSsn());

        return UserResponse.builder()
                           .userId(user.getUserId())
                           .name(user.getName())
                           .email(user.getEmail())
                           .phone(user.getPhone())
                           .address(user.getAddress())
                           .ssn(decryptSsn.substring(0, 8) + "******")
                           .hireDate(user.getHireDate())
                           .fireDate(user.getFireDate())
                           .loginId(user.getLoginId())
                           .deptTitle(user.getDeptTitle())
                           .role(user.getRole())
                           .empNo(user.getEmpNo())
                           .companyName(client != null ? client.getCompanyName() : "거래처 아님")
                           .build();
    }

    @Override
    @Transactional
    public void updateUser(final String enpNo, final UpdateUserRequest request) {

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new CustomException(ErrorCode.DUPLICATE_PHONE);
        }

        User user = userRepository.findByEmpNo(enpNo)
                                  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.updateUser(request);

        if (request.getCompanyName() != null) {
            Client client = clientRepository.findByCompanyName(request.getCompanyName())
                                            .orElseThrow(() -> new CustomException(
                                                ErrorCode.CLIENT_NOT_FOUND));
            user.updateClient(client);
        }
    }

    private String generateEmpNo(LocalDate hireDate) {

        String yearMonth = hireDate.format(DateTimeFormatter.ofPattern("yyyyMM"));

        String lastEmpNo = userRepository.findLastEmpNoByYearMonth(yearMonth);

        int nextSeq = 1;
        if (lastEmpNo != null) {
            nextSeq = Integer.parseInt(lastEmpNo.substring(6)) + 1;
        }

        return String.format("%s%03d", yearMonth, nextSeq);
    }
}
