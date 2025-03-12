package ru.practicum.ewm.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.user.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());

        Page<User> userPage;
        if (ids != null && !ids.isEmpty()) {
            userPage = userRepository.findByIdIn(ids, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        return userPage.getContent().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto createUser(NewUserRequest userDto) {
        User user = userRepository.save(UserMapper.newUserRequestToUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
