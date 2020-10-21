package com.example.photoappapiusers.users.service;

import com.example.photoappapiusers.users.data.AlbumsServiceClient;
import com.example.photoappapiusers.users.data.UserEntity;
import com.example.photoappapiusers.users.data.UsersRepository;
import com.example.photoappapiusers.users.shared.UserDto;
import com.example.photoappapiusers.users.ui.models.AlbumResponseModel;
import feign.FeignException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UsersServiceImpl implements UsersService {

    UsersRepository usersRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    // RestTemplate restTemplate;
    Environment environment;
    AlbumsServiceClient albumsServiceClient;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public UsersServiceImpl(UsersRepository usersRepository,
                            BCryptPasswordEncoder bCryptPasswordEncoder,
                            AlbumsServiceClient albumsServiceClient, Environment environment) {
        this.usersRepository = usersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.albumsServiceClient = albumsServiceClient;
        this.environment = environment;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        userDto.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);

        usersRepository.save(userEntity);

        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = usersRepository.findByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);
        return new ModelMapper().map(userEntity, UserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = usersRepository.findByEmail(username);
        if (userEntity == null) throw new UsernameNotFoundException(username);
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
                true, true, true, true, new ArrayList<>());
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = usersRepository.findByUserId(userId);
        if (userEntity == null) throw new UsernameNotFoundException("User not found.");
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
//        String albumsUrl = String.format(environment.getProperty("albums.url"), userId);
//        ResponseEntity<List<AlbumResponseModel>> albumsListResponse = restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {});
//        List<AlbumResponseModel> albumsList = albumsListResponse.getBody();
        logger.info("Before calling albums MicroService");
        List<AlbumResponseModel> albumsList = albumsServiceClient.getAlbums(userId);
        logger.info("After calling albums MicroService");

        userDto.setAlbums(albumsList);

        return userDto;
    }
}
