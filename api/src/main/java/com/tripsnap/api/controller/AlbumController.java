package com.tripsnap.api.controller;

import com.tripsnap.api.controller.api.AlbumApi;
import com.tripsnap.api.domain.dto.*;
import com.tripsnap.api.service.AlbumService;
import com.tripsnap.api.service.PhotoService;
import com.tripsnap.api.utils.ParameterUtil;
import com.tripsnap.api.utils.ValidationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/album", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AlbumController implements AlbumApi {
    private final AlbumService albumService;
    private final PhotoService photoService;

    @PostMapping("/list")
    @Override
    public ResponseEntity<ResultDTO.SimpleWithPageData<List<GroupAlbumDTO>>> getAlbums(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> param) {
        PageDTO pageDTO = ParameterUtil.validationAndConvert(param, PageDTO.class);
        Long groupId = ParameterUtil.validationAndConvert(String.valueOf(param.get("groupId")), ValidationType.PrimitiveWrapper.EntityId, Long.class);

        return ResponseEntity.ok(albumService.getAlbums(user.getUsername(), pageDTO, groupId));
    }

    @PostMapping
    @Override
    public ResponseEntity<?> addAlbum(@AuthenticationPrincipal User user, @Valid @RequestBody GroupAlbumInsDTO param) {
        return ResponseEntity.ok(albumService.createAlbum(user.getUsername(), param));
    }

    @PostMapping("/remove")
    @Override
    public ResponseEntity<?> removeAlbum(@AuthenticationPrincipal User user, @Valid @RequestBody GroupAlbumParamDTO paramDTO) {
        return ResponseEntity.ok(albumService.deleteAlbum(user.getUsername(), paramDTO));
    }

    @PostMapping("/photo/list")
    @Override
    public ResponseEntity<ResultDTO.SimpleWithPageData<List<AlbumPhotoDTO>>> photos(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> param) {
        PageDTO pageDTO = ParameterUtil.validationAndConvert(param, PageDTO.class);
        GroupAlbumParamDTO groupAlbumParamDTO = ParameterUtil.validationAndConvert(param, GroupAlbumParamDTO.class);

        return ResponseEntity.ok(photoService.getPhotos(user.getUsername(), pageDTO, groupAlbumParamDTO));
    }

    @PostMapping("/{album-id:\\d+}/upload-authority")
    @Override
    public ResponseEntity<?> photoUploadAuthority(
            @AuthenticationPrincipal User user,
            @PathVariable("album-id") Long albumId,
            @Valid @RequestBody RequestPresignedUrlDTO param) {
        ParameterUtil.validation(albumId, ValidationType.PrimitiveWrapper.EntityId);
        return ResponseEntity.ok(photoService.getPresignedURL(user.getUsername(), albumId, param));
    }


    @PostMapping("/photo")
    @Override
    public ResponseEntity<?> addPhotos(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> param) {
        GroupAlbumParamDTO groupAlbumParamDTO = ParameterUtil.validationAndConvert(param, GroupAlbumParamDTO.class);
        ParameterUtil.validation(param, ValidationType.Collection.AlbumPhotoList._class);
        List<AlbumPhotoInsDTO> albumPhotoList = (List<AlbumPhotoInsDTO>) ParameterUtil.convert(
                param.get(ValidationType.Collection.AlbumPhotoList.property),
                ValidationType.Collection.AlbumPhotoList.type
        );

        return ResponseEntity.ok(photoService.addPhotos(user.getUsername(),groupAlbumParamDTO, albumPhotoList));
    }

    @PostMapping("/photo/remove")
    @Override
    public ResponseEntity<?> removePhotos(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> param) {
        GroupAlbumParamDTO groupAlbumParamDTO = ParameterUtil.validationAndConvert(param, GroupAlbumParamDTO.class);
        ParameterUtil.validation(param, ValidationType.Collection.RemovePhotoList._class);
        List<Long> removeIds = (List<Long>) ParameterUtil.convert(
                param.get(ValidationType.Collection.RemovePhotoList.property),
                ValidationType.Collection.RemovePhotoList.type
        );

        return ResponseEntity.ok(photoService.deletePhotos(user.getUsername(),groupAlbumParamDTO, removeIds));
    }
}
