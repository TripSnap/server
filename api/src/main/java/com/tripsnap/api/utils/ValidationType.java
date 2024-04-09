package com.tripsnap.api.utils;

import com.google.gson.reflect.TypeToken;
import com.tripsnap.api.domain.dto.AlbumPhotoInsDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

public enum ValidationType {
    ;

    public enum PrimitiveWrapper {
        Email(EmailType.class),
        EntityId(EntityIdType.class);

        public final Class<?> _class;

        PrimitiveWrapper(Class<?> _class) {
            this._class = _class;
        }
    }

    public enum Collection {
        NewPhotoList(NewPhotoListType.class, "addPhotos", new TypeToken<List<AlbumPhotoInsDTO>>(){}),
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
    };

    @RequiredArgsConstructor
    private static class EntityIdType {
        @Positive @NotNull
        final private Long id;
    }

    @NoArgsConstructor
    private static class NewPhotoListType {
        @Size(max=50)
        private List<AlbumPhotoInsDTO> addPhotos;

        private NewPhotoListType(List<String> list) {
            this.addPhotos = list.stream().map(AlbumPhotoInsDTO::new).toList();
        }
    }

    @NoArgsConstructor
    private static class RemovePhotoListType {
        @Size(max=50)
        private List<Long> removePhotoIds;

        private RemovePhotoListType(List<String> list) {
            this.removePhotoIds = list.stream().map(Long::valueOf).toList();
        }
    }
}
