package com.tripsnap.api.controller;

import com.tripsnap.api.controller.api.AlbumApi;
import com.tripsnap.api.domain.dto.*;
import com.tripsnap.api.service.AlbumService;
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

    @DeleteMapping
    @Override
    public ResponseEntity<?> removeAlbum(@AuthenticationPrincipal User user, @Valid @RequestBody GroupAlbumParamDTO paramDTO) {
        return ResponseEntity.ok(albumService.deleteAlbum(user.getUsername(), paramDTO));
    }

    @PostMapping("/photo/list")
    @Override
    public ResponseEntity<ResultDTO.SimpleWithPageData<List<AlbumPhotoDTO>>> photos(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> param) {
        PageDTO pageDTO = ParameterUtil.validationAndConvert(param, PageDTO.class);
        GroupAlbumParamDTO groupAlbumParamDTO = ParameterUtil.validationAndConvert(param, GroupAlbumParamDTO.class);

        return ResponseEntity.ok(albumService.getPhotos(user.getUsername(), pageDTO, groupAlbumParamDTO));
    }

    @PostMapping("/photo")
    @Override
    public ResponseEntity<?> addPhotos(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> param) {
        GroupAlbumParamDTO groupAlbumParamDTO = ParameterUtil.validationAndConvert(param, GroupAlbumParamDTO.class);
        ParameterUtil.validation(param, ValidationType.Collection.NewPhotoList._class);
        List<AlbumPhotoInsDTO> removeIds = (List<AlbumPhotoInsDTO>) ParameterUtil.convert(
                param.get(ValidationType.Collection.NewPhotoList.property),
                ValidationType.Collection.NewPhotoList.type
        );

        return ResponseEntity.ok(albumService.addPhotos(user.getUsername(),groupAlbumParamDTO, removeIds));
    }

    @DeleteMapping("/photo")
    @Override
    public ResponseEntity<?> removePhotos(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> param) {
        GroupAlbumParamDTO groupAlbumParamDTO = ParameterUtil.validationAndConvert(param, GroupAlbumParamDTO.class);
        ParameterUtil.validation(param, ValidationType.Collection.RemovePhotoList._class);
        List<Long> removeIds = (List<Long>) ParameterUtil.convert(
                param.get(ValidationType.Collection.RemovePhotoList.property),
                ValidationType.Collection.RemovePhotoList.type
        );

        return ResponseEntity.ok(albumService.deletePhotos(user.getUsername(),groupAlbumParamDTO, removeIds));
    }
}
