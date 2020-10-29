package vn.aptech.springboot.amazingtoy.service;

import org.springframework.web.multipart.MultipartFile;
import vn.aptech.springboot.amazingtoy.dto.model.user.UserDto;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface UserService {

    UserDto findByEmail(String email);
    UserDto findById(Long id);
    List<UserDto> findAll();
    UserDto register(UserDto userDto, MultipartFile profilePicture) throws IOException;
    UserDto update(UserDto userDto, MultipartFile filePicture) throws IOException;
    void delete(Long id);

}
