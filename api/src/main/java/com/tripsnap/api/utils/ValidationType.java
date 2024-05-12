package com.tripsnap.api.utils;

import com.google.gson.reflect.TypeToken;
import com.tripsnap.api.domain.dto.AlbumPhotoInsDTO;
import com.tripsnap.api.domain.dto.option.FriendListOption;
import com.tripsnap.api.validator.ValidEnum;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

public enum ValidationType {
    ;

    public enum PrimitiveWrapper {
        Email(EmailType.class),
        LoginPassword(LoginPasswordType.class),
        EntityId(EntityIdType.class),
        FriendListOptionType(FriendListOptionType.class);

        public final Class<?> _class;

        PrimitiveWrapper(Class<?> _class) {
            this._class = _class;
        }
    }

    public enum Collection {
        AlbumPhotoList(NewPhotoListType.class, "albumPhotoList", new TypeToken<List<AlbumPhotoInsDTO>>(){}),
        RemovePhotoList(RemovePhotoListType.class, "removePhotoIds", new TypeToken<List<Long>>(){});

        <T> Collection(Class<?> _class, String property, TypeToken<T> type) {
            this._class = _class;
            this.type = type;
            this.property = property;
        }

        public final Class<?> _class;
        public final TypeToken<?> type;
        public final String property;

    }

    @RequiredArgsConstructor
    private static class EmailType{
        @Email
        final private String value;

        @Override
        public String toString() {
            return this.value;
        }
    };

    @RequiredArgsConstructor
    private static class LoginPasswordType {
        @Size(min=1, max=100) @NotBlank
        final private String password;
        @Override
        public String toString() {
            return this.password;
        }
    }


    @RequiredArgsConstructor
    private static class EntityIdType {
        @Positive @NotNull
        final private Long id;
        @Override
        public String toString() {
            return String.valueOf(id);
        }
    }

    @RequiredArgsConstructor
    private static class FriendListOptionType {
        @ValidEnum(enumClass = FriendListOption.class)
        final private String option;
        @Override
        public String toString() {
            return option;
        }
    }

    @NoArgsConstructor
    private static class NewPhotoListType {
        @Size(max=50,min = 1)
        private List<AlbumPhotoInsDTO> addPhotos;

        private NewPhotoListType(List<String> list) {
            this.addPhotos = list.stream().map(AlbumPhotoInsDTO::new).toList();
        }
    }

    @NoArgsConstructor
    private static class RemovePhotoListType {
        @Size(max=50,min=1)
        private List<Long> removePhotoIds;

        private RemovePhotoListType(List<String> list) {
            this.removePhotoIds = list.stream().map(Long::valueOf).toList();
        }
    }
}
